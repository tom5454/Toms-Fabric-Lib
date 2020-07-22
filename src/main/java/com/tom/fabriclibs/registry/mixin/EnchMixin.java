package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(Enchantment.class)
public class EnchMixin implements IRegistryEntry<Enchantment> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.ENCHANTMENT.getId((Enchantment) (Object) this);
		return regName;
	}

	@Override
	public Enchantment setRegistryName(Identifier name) {
		regName = name;
		return (Enchantment) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
