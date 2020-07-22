package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(Biome.class)
public class BiomeMixin implements IRegistryEntry<Biome> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.BIOME.getId((Biome) (Object) this);
		return regName;
	}

	@Override
	public Biome setRegistryName(Identifier name) {
		regName = name;
		return (Biome) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
