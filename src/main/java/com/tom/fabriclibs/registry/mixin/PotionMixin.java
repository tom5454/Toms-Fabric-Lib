package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(Potion.class)
public class PotionMixin implements IRegistryEntry<Potion> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.POTION.getId((Potion) (Object) this);
		return regName;
	}

	@Override
	public Potion setRegistryName(Identifier name) {
		regName = name;
		return (Potion) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
