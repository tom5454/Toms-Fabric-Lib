package com.tom.fabriclibs.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import com.tom.fabriclibs.event.Event;

public class BlockHarvestDropsEvent extends Event {
	private final WorldAccess world;
	private final BlockPos pos;
	private final BlockState state;
	private final PlayerEntity harvester; // May be null for non-player harvesting such as explosions or machines
	private final DefaultedList<ItemStack> drops;

	public BlockHarvestDropsEvent(WorldAccess world, BlockPos pos, BlockState state, DefaultedList<ItemStack> drops, PlayerEntity harvester)
	{
		this.pos = pos;
		this.world = world;
		this.state = state;
		this.harvester = harvester;
		this.drops = drops;
	}

	public WorldAccess getWorld()
	{
		return world;
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public BlockState getState()
	{
		return state;
	}
	public DefaultedList<ItemStack> getDrops() {
		return drops;
	}

	public PlayerEntity getHarvester() {
		return harvester;
	}
}
