package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(ScreenHandlerType.class)
public class ScreenHandlerTypeMixin implements IRegistryEntry<ScreenHandlerType<?>> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.SCREEN_HANDLER.getId((ScreenHandlerType<?>) (Object) this);
		return regName;
	}

	@Override
	public ScreenHandlerType<?> setRegistryName(Identifier name) {
		regName = name;
		return (ScreenHandlerType<?>) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
