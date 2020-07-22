package com.tom.fabriclibs.events.entity;

import java.util.Collection;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class LivingDropsEvent extends EntityEvent {
	private final DamageSource source;
	private final Collection<ItemEntity> drops;

	public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops) {
		super(entity);
		this.source = source;
		this.drops = drops;
	}

	public DamageSource getSource() {
		return source;
	}

	public Collection<ItemEntity> getDrops() {
		return drops;
	}
}
