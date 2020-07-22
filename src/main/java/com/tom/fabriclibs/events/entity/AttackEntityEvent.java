package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AttackEntityEvent extends EntityEvent {
	private final Entity target;
	public AttackEntityEvent(PlayerEntity player, Entity target) {
		super(player);
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}
}
