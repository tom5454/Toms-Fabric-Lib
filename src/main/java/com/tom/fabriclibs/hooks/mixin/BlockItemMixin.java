package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import com.tom.fabriclibs.ext.IBlock;
import com.tom.fabriclibs.ext.IItem;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item implements IItem {

	public BlockItemMixin(Settings settings) {
		super(settings);
	}

	@Shadow abstract Block getBlock();

	@Override
	public Identifier getExtenedTooltipRenderer() {
		return ((IBlock)getBlock()).getExtenedTooltipRenderer();
	}

	@Override
	public void tickAsItemEntity(ItemStack stack, ItemEntity item) {
		((IBlock)getBlock()).tickAsItemEntity(stack, item);
	}

	@Override
	public ItemStack getConatinerItem(ItemStack old) {
		ItemStack stack = ((IBlock)getBlock()).getConatinerItem(old);
		if(stack != null)return stack;
		Item item2 = getRecipeRemainder();
		return (item2 == null) ? ItemStack.EMPTY : new ItemStack(item2);
	}
}
