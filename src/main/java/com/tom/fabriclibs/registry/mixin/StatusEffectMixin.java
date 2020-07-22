package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(StatusEffect.class)
public class StatusEffectMixin implements IRegistryEntry<StatusEffect> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.STATUS_EFFECT.getId((StatusEffect) (Object) this);
		return regName;
	}

	@Override
	public StatusEffect setRegistryName(Identifier name) {
		regName = name;
		return (StatusEffect) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
