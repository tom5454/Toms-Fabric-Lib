package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class LivingHurtEvent extends EntityEvent {
	private final DamageSource source;
	private float amount;
	public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount)
	{
		super(entity);
		this.source = source;
		this.amount = amount;
	}

	public DamageSource getSource() { return source; }

	public float getAmount() { return amount; }

	public void setAmount(float amount) { this.amount = amount; }
}
