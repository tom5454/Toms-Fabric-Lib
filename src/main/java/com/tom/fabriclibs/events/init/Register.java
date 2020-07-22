package com.tom.fabriclibs.events.init;

import java.lang.reflect.Type;

import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.event.Event;
import com.tom.fabriclibs.event.ObjectHolderRegistry;
import com.tom.fabriclibs.events.ITypedEvent;
import com.tom.fabriclibs.ext.IRegistry;

public class Register<T> extends Event implements ITypedEvent<T> {
	private final IRegistry<T> registry;

	@SuppressWarnings("unchecked")
	public Register(Registry<T> reg) {
		this.registry = (IRegistry<T>) reg;
	}

	public IRegistry<T> getRegistry() {
		return registry;
	}

	@Override
	public Type getType() {
		return ObjectHolderRegistry.getType((Registry<?>) registry);
	}

}
