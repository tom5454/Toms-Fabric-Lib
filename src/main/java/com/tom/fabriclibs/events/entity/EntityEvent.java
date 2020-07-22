package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.tom.fabriclibs.event.Event;

public abstract class EntityEvent extends Event {

	private final Entity entity;

	public EntityEvent(Entity entity) {
		this.entity = entity;
	}

	public LivingEntity getEntityLiving() {
		return (LivingEntity) entity;
	}

	public PlayerEntity getPlayer() {
		return (PlayerEntity) entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
