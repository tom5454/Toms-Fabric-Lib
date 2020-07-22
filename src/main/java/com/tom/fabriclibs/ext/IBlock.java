package com.tom.fabriclibs.ext;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface IBlock extends IRegistryEntry<Block> {
	default float getEnchantPowerBonus(BlockState state, BlockView world, BlockPos pos) {
		throw new AbstractMethodError("Mixin failed");
	}
	default Identifier getExtenedTooltipRenderer() {
		return null;
	}
	default void tickAsItemEntity(ItemStack stack, ItemEntity item) {
	}
	default ItemStack getConatinerItem(ItemStack old) {
		return null;
	}
}
