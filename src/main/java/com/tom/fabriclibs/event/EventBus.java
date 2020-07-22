package com.tom.fabriclibs.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.objectweb.asm.Opcodes.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.ITypedEvent;

public class EventBus {
	@Retention(value = RUNTIME)
	@Target(value = METHOD)
	public static @interface SubscribeEvent {
		EventPriority priority() default EventPriority.NORMAL;
	}

	public static interface IEventListener {
		void invoke(Event event);
	}
	private static final HashMap<Method, Class<?>> cache = Maps.newHashMap();
	private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<>();
	private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
	private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);
	private static final ASMClassLoader LOADER = new ASMClassLoader();
	private Map<Class<?>, List<IEventListener>> listenersByEvent = new HashMap<>();
	public void register(Object target) {
		Events.throwIfNull();
		if (listeners.containsKey(target))  {
			return;
		}
		boolean isStatic = target.getClass() == Class.class;

		@SuppressWarnings("unchecked")
		Set<? extends Class<?>> supers = isStatic ? Sets.newHashSet((Class<?>)target) : TypeToken.of(target.getClass()).getTypes().rawTypes();
		for (Method method : (isStatic ? (Class<?>)target : target.getClass()).getMethods()) {
			if (isStatic && !Modifier.isStatic(method.getModifiers()))
				continue;
			else if (!isStatic && Modifier.isStatic(method.getModifiers()))
				continue;

			for (Class<?> cls : supers) {
				try
				{
					Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
					if (real.isAnnotationPresent(SubscribeEvent.class))
					{
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length != 1)
						{
							throw new IllegalArgumentException(
									"Method " + method + " has @SubscribeEvent annotation, but requires " + parameterTypes.length +
									" arguments.  Event handler methods must require a single argument."
									);
						}

						Class<?> eventType = parameterTypes[0];

						if (!Event.class.isAssignableFrom(eventType))
						{
							throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but takes a argument that is not an Event " + eventType);
						}

						register(eventType, target, real);
						break;
					}
				}
				catch (NoSuchMethodException e) {
					; // Eat the error, this is not unexpected
				}
			}
		}
	}

	private void register(Class<?> eventType, Object target, Method real) {
		try {
			IEventListener handler;
			if (Modifier.isStatic(real.getModifiers()))
				handler = (IEventListener)createWrapper(real).newInstance();
			else
				handler = (IEventListener)createWrapper(real).getConstructor(Object.class).newInstance(target);

			if(ITypedEvent.class.isAssignableFrom(eventType)) {
				ParameterizedType type = (ParameterizedType) real.getGenericParameterTypes()[0];
				java.lang.reflect.Type filter = type.getActualTypeArguments()[0];
				if (filter instanceof ParameterizedType) {// Unlikely that nested generics will ever be relevant for event filtering, so discard them
					filter = ((ParameterizedType)filter).getRawType();
				}
				handler = new TypedListener(Events.getModid(), handler, filter);
			} else {
				handler = new SimpleListener(Events.getModid(), handler);
			}
			listenersByEvent.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static class SimpleListener implements IEventListener {
		private final String modid;
		private final IEventListener handler;
		public SimpleListener(String modid, IEventListener handler) {
			this.modid = modid;
			this.handler = handler;
		}

		@Override
		public void invoke(Event event) {
			Events.setModid(modid);
			handler.invoke(event);
			Events.setModid(null);
		}
	}
	private static class TypedListener implements IEventListener {
		private final IEventListener handler;
		private final java.lang.reflect.Type filter;
		private final String modid;
		public TypedListener(String modid, IEventListener handler, java.lang.reflect.Type type) {
			this.handler = handler;
			this.filter = type;
			this.modid = modid;
		}
		@SuppressWarnings("rawtypes")
		@Override
		public void invoke(Event event) {
			if(filter == ((ITypedEvent)event).getType()) {
				Events.setModid(modid);
				handler.invoke(event);
				Events.setModid(null);
			}
		}
	}

	private static class ConsumerListener<T extends Event> implements IEventListener {
		private final String modid;
		private final Consumer<T> consumer;
		public ConsumerListener(String modid, Consumer<T> consumer) {
			this.modid = modid;
			this.consumer = consumer;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void invoke(Event event) {
			Events.setModid(modid);
			consumer.accept((T) event);
			Events.setModid(null);
		}
	}

	public void unregister(Object target) {
		ArrayList<IEventListener> l = listeners.remove(target);
		if(l != null) {
			listenersByEvent.values().forEach(e -> e.removeAll(l));
		}
	}

	private static Class<?> createWrapper(Method callback) {
		if (cache.containsKey(callback)) {
			return cache.get(callback);
		}

		ClassWriter cw = new ClassWriter(0);
		MethodVisitor mv;

		boolean isStatic = Modifier.isStatic(callback.getModifiers());
		String name = getUniqueName(callback);
		String desc = name.replace('.',  '/');
		String instType = Type.getInternalName(callback.getDeclaringClass());
		String eventType = Type.getInternalName(callback.getParameterTypes()[0]);

		cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[]{ HANDLER_DESC });

		cw.visitSource(".dynamic", null);
		{
			if (!isStatic)
				cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", isStatic ? "()V" : "(Ljava/lang/Object;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			if (!isStatic)
			{
				mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
			}
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			if (!isStatic)
			{
				mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
				mv.visitTypeInsn(CHECKCAST, instType);
			}
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(CHECKCAST, eventType);
			mv.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, instType, callback.getName(), Type.getMethodDescriptor(callback), false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 2);
			mv.visitEnd();
		}
		cw.visitEnd();
		Class<?> ret = LOADER.define(name, cw.toByteArray());
		cache.put(callback, ret);
		return ret;
	}
	public void post(Event e) {
		String modid = Events.getModid();
		List<IEventListener> l = listenersByEvent.get(e.getClass());
		if(l != null)l.forEach(c -> c.invoke(e));
		Events.setModid(modid);
	}
	private static int IDs = 0;
	private static String getUniqueName(Method callback)
	{
		return String.format("ASMEventListener_%d_%s_%s_%s", IDs++,
				callback.getDeclaringClass().getSimpleName(),
				callback.getName(),
				callback.getParameterTypes()[0].getSimpleName());
	}

	private static class ASMClassLoader extends ClassLoader
	{
		private ASMClassLoader()
		{
			super(ASMClassLoader.class.getClassLoader());
		}

		public Class<?> define(String name, byte[] data)
		{
			return defineClass(name, data, 0, data.length);
		}
	}

	public <T extends Event> void addListener(Class<T> clazz, Consumer<T> consumer) {
		if(clazz == null)throw new NullPointerException();
		Events.throwIfNull();
		listenersByEvent.computeIfAbsent(clazz, k -> new ArrayList<>()).add(new ConsumerListener<>(Events.getModid(), consumer));
	}
}
