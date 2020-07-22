package com.tom.fabriclibs.ext;

import net.minecraft.util.Identifier;

import com.tom.fabriclibs.Events;

public interface IRegistryEntry<T> extends IRegistered {
	default Identifier getRegistryNameInt() {throw new AbstractMethodError("Mixin failed");}
	default T setRegistryName(Identifier name) {throw new AbstractMethodError("Mixin failed");}

	default T setRegistryName(String name) {
		return setRegistryName(new Identifier(Events.getModid(), name));
	}

	default T setRegistryName(String modid, String name) {
		return setRegistryName(new Identifier(modid, name));
	}
}
