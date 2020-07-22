package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class LivingEntityUseItemTickEvent extends EntityEvent {
	private final ItemStack item;
	private int duration;

	public LivingEntityUseItemTickEvent(LivingEntity entity, ItemStack item, int duration) {
		super(entity);
		this.item = item;
		this.setDuration(duration);
	}

	public ItemStack getItem()
	{
		return item;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}
}
