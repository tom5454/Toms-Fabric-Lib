package com.tom.fabriclibs;

import net.minecraft.util.Identifier;

import com.tom.fabriclibs.ext.IRegistryEntry;

public class RegistryEntry<T> implements IRegistryEntry<T> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		return regName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T setRegistryName(Identifier name) {
		regName = name;
		return (T) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
