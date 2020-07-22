package com.tom.fabriclibs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

public class EnumHelper {
	private static Object reflectionFactory      = null;
	private static Method newConstructorAccessor = null;
	private static Method newInstance            = null;
	private static Method newFieldAccessor       = null;
	private static Method fieldAccessorSet       = null;
	private static boolean isSetup               = false;

	private static void setup()
	{
		if (isSetup)
		{
			return;
		}

		try
		{
			Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
			reflectionFactory      = getReflectionFactory.invoke(null);
			newConstructorAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class);
			newInstance            = Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class);
			newFieldAccessor       = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
			fieldAccessorSet       = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
		}
		catch (Exception e)
		{
			System.err.println("Error setting up EnumHelper.");
			e.printStackTrace();
		}

		isSetup = true;
	}

	/*
	 * Everything below this is found at the site below, and updated to be able to compile in Eclipse/Java 1.6+
	 * Also modified for use in decompiled code.
	 * Found at: http://niceideas.ch/roller2/badtrash/entry/java_create_enum_instances_dynamically
	 */
	private static Object getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws Exception
	{
		Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
		parameterTypes[0] = String.class;
		parameterTypes[1] = int.class;
		System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
		return newConstructorAccessor.invoke(reflectionFactory, enumClass.getDeclaredConstructor(parameterTypes));
	}

	private static < T extends Enum<? >> T makeEnum(Class<T> enumClass,  String value, int ordinal, Class<?>[] additionalTypes,  Object[] additionalValues) throws Exception
	{
		int additionalParamsCount = additionalValues == null ? 0 : additionalValues.length;
		Object[] params = new Object[additionalParamsCount + 2];
		params[0] = value;
		params[1] = ordinal;
		if (additionalValues != null)
		{
			System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
		}
		return enumClass.cast(newInstance.invoke(getConstructorAccessor(enumClass, additionalTypes), new Object[] {params}));
	}

	public static void setFailsafeFieldValue(Field field,  Object target,  Object value) throws Exception
	{
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
		fieldAccessorSet.invoke(fieldAccessor, target, value);
	}

	private static void blankField(Class<?> enumClass, String fieldName) throws Exception
	{
		for (Field field : Class.class.getDeclaredFields())
		{
			if (field.getName().contains(fieldName))
			{
				field.setAccessible(true);
				setFailsafeFieldValue(field, enumClass, null);
				break;
			}
		}
	}

	private static void cleanEnumCache(Class<?> enumClass) throws Exception
	{
		blankField(enumClass, "enumConstantDirectory");
		blankField(enumClass, "enumConstants");
		//Open J9
		blankField(enumClass, "enumVars");
	}


	/*private static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Object... paramValues)
	{
		setup();
		return addEnum(commonTypes, enumType, enumName, paramValues);
	}*/


	protected static <T extends Enum<? >> T addEnum(Class<?>[][] map, Class<T> enumType, String enumName, Object... paramValues)
	{
		for (Class<?>[] lookup : map)
		{
			if (lookup[0] == enumType)
			{
				Class<?>[] paramTypes = new Class<?>[lookup.length - 1];
				if (paramTypes.length > 0)
				{
					System.arraycopy(lookup, 1, paramTypes, 0, paramTypes.length);
				}
				return addEnum(enumType, enumName, paramTypes, paramValues);
			}
		}
		return null;
	}

	//Tests an enum is compatible with these args, throws an error if not.
	public static void testEnum(Class<? extends Enum<?>> enumType, Class<?>[] paramTypes)
	{
		addEnum(true, enumType, null, paramTypes, (Object[])null);
	}


	public static <T extends Enum<? >> T addEnum(Class<T> enumType, String enumName, Class<?>[] paramTypes, Object... paramValues)
	{
		return addEnum(false, enumType, enumName, paramTypes, paramValues);
	}

	@SuppressWarnings({ "unchecked" })

	private static <T extends Enum<? >> T addEnum(boolean test, final Class<T> enumType,  String enumName, final Class<?>[] paramTypes,  Object[] paramValues)
	{
		if (!isSetup)
		{
			setup();
		}

		Field valuesField = null;
		Field[] fields = enumType.getDeclaredFields();

		for (Field field : fields)
		{
			String name = field.getName();
			if (name.equals("$VALUES") || name.equals("ENUM$VALUES")) //Added 'ENUM$VALUES' because Eclipse's internal compiler doesn't follow standards
			{
				valuesField = field;
				break;
			}
		}

		int flags = Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
		if (valuesField == null)
		{
			String valueType = String.format("[L%s;", enumType.getName().replace('.', '/'));

			for (Field field : fields)
			{
				if ((field.getModifiers() & flags) == flags &&
						field.getType().getName().replace('.', '/').equals(valueType)) //Apparently some JVMs return .'s and some don't..
				{
					valuesField = field;
					break;
				}
			}
		}

		if (valuesField == null)
		{
			final List<String> lines = Lists.newArrayList();
			lines.add(String.format("Could not find $VALUES field for enum: %s", enumType.getName()));
			lines.add(String.format("Flags: %s", String.format("%16s", Integer.toBinaryString(flags)).replace(' ', '0')));
			lines.add(              "Fields:");
			for (Field field : fields)
			{
				String mods = String.format("%16s", Integer.toBinaryString(field.getModifiers())).replace(' ', '0');
				lines.add(String.format("       %s %s: %s", mods, field.getName(), field.getType().getName()));
			}

			for (String line : lines)
				System.err.println(line);

			if (test)
			{
				throw new RuntimeException("Could not find $VALUES field for enum: " + enumType.getName());
			}
			return null;
		}

		if (test)
		{
			Object ctr = null;
			Exception ex = null;
			try
			{
				ctr = getConstructorAccessor(enumType, paramTypes);
			}
			catch (Exception e)
			{
				ex = e;
			}
			if (ctr == null || ex != null)
			{
				System.err.println("Target Arguments:");
				System.err.println("    java.lang.String, int, " + toString(paramTypes));
				System.err.println("Found Constructors:");
				for (Constructor<?> ctr0 : enumType.getDeclaredConstructors())
				{
					System.err.println("    " + toString(ctr0.getParameterTypes()));
				}
				throw new RuntimeException(String.format("Could not find constructor for Enum %s", enumType.getName()), ex);
			}
			return null;
		}

		valuesField.setAccessible(true);

		try
		{
			T[] previousValues = (T[])valuesField.get(enumType);
			T newValue = makeEnum(enumType, enumName, previousValues.length, paramTypes, paramValues);
			setFailsafeFieldValue(valuesField, null, ArrayUtils.add(previousValues, newValue));
			cleanEnumCache(enumType);

			return newValue;
		}
		catch (Exception e)
		{
			System.err.println("Error adding enum with EnumHelper.");
			throw new RuntimeException(e);
		}
	}
	private static String toString(Class<?>[] cls)
	{
		StringBuilder b = new StringBuilder();
		for (int x = 0; x < cls.length; x++)
		{
			b.append(cls[x].getName());
			if (x != cls.length - 1)
				b.append(", ");
		}
		return b.toString();
	}
	static
	{
		if (!isSetup)
		{
			setup();
		}
	}
}
