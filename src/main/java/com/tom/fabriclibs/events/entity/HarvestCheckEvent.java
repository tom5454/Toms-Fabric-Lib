package com.tom.fabriclibs.events.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class HarvestCheckEvent extends EntityEvent {
	private final BlockState state;
	private boolean success;

	public HarvestCheckEvent(PlayerEntity player, BlockState state, boolean success) {
		super(player);
		this.state = state;
		this.success = success;
	}

	public BlockState getTargetBlock() { return this.state; }
	public boolean canHarvest() { return this.success; }
	public void setCanHarvest(boolean success){ this.success = success; }
}
