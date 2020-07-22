package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityJoinWorldEvent extends EntityEvent {
	private final World world;

	public EntityJoinWorldEvent(Entity entity, World world) {
		super(entity);
		this.world = world;
	}

	public World getWorld()
	{
		return world;
	}
}
