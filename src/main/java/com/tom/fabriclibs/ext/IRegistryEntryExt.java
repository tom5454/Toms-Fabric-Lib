package com.tom.fabriclibs.ext;

import net.minecraft.util.Identifier;

public interface IRegistryEntryExt<T> extends IRegistryEntry<T> {
	@Override
	default Identifier getRegistryNameInt() {
		return getRegistryName();
	}

	@Override
	Identifier getRegistryName();

	void setRegistryNameInt(Identifier name);

	@SuppressWarnings("unchecked")
	@Override
	default T setRegistryName(Identifier name) {
		setRegistryNameInt(name);
		return (T) this;
	}
}
