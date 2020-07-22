package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(Feature.class)
public class FeatureMixin implements IRegistryEntry<Feature<?>> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.FEATURE.getId((Feature<?>) (Object) this);
		return regName;
	}

	@Override
	public Feature<?> setRegistryName(Identifier name) {
		regName = name;
		return (Feature<?>) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
