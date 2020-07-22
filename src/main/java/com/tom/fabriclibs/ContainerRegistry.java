package com.tom.fabriclibs;

import java.util.function.BiFunction;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ContainerRegistry {
	public static <T extends ScreenHandler> ScreenHandlerType<T> create(BiFunction<Integer, PlayerInventory, T> factory) {
		return new ScreenHandlerType<>(factory::apply);
	}
}
