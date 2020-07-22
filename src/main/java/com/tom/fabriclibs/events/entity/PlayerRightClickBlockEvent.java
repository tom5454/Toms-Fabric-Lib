package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.tom.fabriclibs.event.EventResult;

public class PlayerRightClickBlockEvent extends EntityEvent {
	private final Hand hand;
	private final BlockPos pos;
	private ActionResult result;
	private EventResult useBlock;

	public PlayerRightClickBlockEvent(PlayerEntity player, Hand hand, BlockPos pos) {
		super(player);
		this.hand = hand;
		this.pos = pos;
	}

	public World getWorld()  {
		return getPlayer().getEntityWorld();
	}

	public ItemStack getItemStack()  {
		return getPlayer().getStackInHand(hand);
	}

	public BlockPos getPos() {
		return pos;
	}

	public Hand getHand() {
		return hand;
	}

	public void setCancellationResult(ActionResult success) {
		this.result = success;
	}

	public ActionResult getCancellationResult() {
		return result;
	}

	public void setUseBlock(EventResult result) {
		useBlock = result;
	}

	public EventResult getUseBlock() {
		return useBlock;
	}
}
