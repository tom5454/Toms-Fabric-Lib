package com.tom.fabriclibs.events.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerBreakSpeedEvent extends EntityEvent {
	private final BlockState state;
	private final float originalSpeed;
	private float newSpeed = 0.0f;

	public PlayerBreakSpeedEvent(PlayerEntity player, BlockState state, float original) {
		super(player);
		this.state = state;
		this.originalSpeed = original;
		this.setNewSpeed(original);
	}

	public BlockState getState() { return state; }
	public float getOriginalSpeed() { return originalSpeed; }
	public float getNewSpeed() { return newSpeed; }
	public void setNewSpeed(float newSpeed) { this.newSpeed = newSpeed; }
}
