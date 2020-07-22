package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.HitResult;

public class ArrowImpactEvent extends EntityEvent {
	private final HitResult result;

	public ArrowImpactEvent(PersistentProjectileEntity entity, HitResult result) {
		super(entity);
		this.result = result;
	}

	public PersistentProjectileEntity getArrow() {
		return (PersistentProjectileEntity) getEntity();
	}

	public HitResult getHitResult() {
		return result;
	}
}
