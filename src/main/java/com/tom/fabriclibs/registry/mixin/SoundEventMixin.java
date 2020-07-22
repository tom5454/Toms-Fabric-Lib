package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(SoundEvent.class)
public class SoundEventMixin implements IRegistryEntry<SoundEvent> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.SOUND_EVENT.getId((SoundEvent) (Object) this);
		return regName;
	}

	@Override
	public SoundEvent setRegistryName(Identifier name) {
		regName = name;
		return (SoundEvent) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
