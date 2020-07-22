package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenHandler;

public class PlayerOpenContainerEvent extends EntityEvent {
	private final ScreenHandler container;

	public PlayerOpenContainerEvent(Entity entity, ScreenHandler container) {
		super(entity);
		this.container = container;
	}

	public ScreenHandler getContainer() {
		return container;
	}

}
