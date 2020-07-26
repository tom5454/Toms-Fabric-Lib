package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LivingExperienceDropEvent extends EntityEvent {
	private final PlayerEntity attackingPlayer;
	private final int originalExperiencePoints;

	private int droppedExperiencePoints;

	public LivingExperienceDropEvent(LivingEntity entity, PlayerEntity attackingPlayer, int originalExperience)
	{
		super(entity);

		this.attackingPlayer = attackingPlayer;
		this.originalExperiencePoints = this.droppedExperiencePoints = originalExperience;
	}

	public boolean isModified() {
		return originalExperiencePoints != droppedExperiencePoints;
	}

	public int getDroppedExperience()
	{
		return droppedExperiencePoints;
	}

	public void setDroppedExperience(int droppedExperience)
	{
		this.droppedExperiencePoints = droppedExperience;
	}

	public PlayerEntity getAttackingPlayer()
	{
		return attackingPlayer;
	}

	public int getOriginalExperience()
	{
		return originalExperiencePoints;
	}
}
