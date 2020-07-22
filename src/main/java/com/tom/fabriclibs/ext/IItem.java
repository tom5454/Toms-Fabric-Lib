package com.tom.fabriclibs.ext;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public interface IItem extends IRegistryEntry<Item> {
	default boolean isShield(ItemStack stack) {
		return stack.getItem() == Items.SHIELD;
	}
	default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment ench) {
		return ench.type.isAcceptableItem(stack.getItem());
	}
	default void onBroken(ItemStack stack, LivingEntity entity) {
		stack.decrement(1);
	}
	default ItemStack getConatinerItem(ItemStack old) {
		throw new AbstractMethodError("Mixin failed");
	}
	default boolean shouldRenderDamageBar(ItemStack stack) {
		return stack.isDamaged();
	}
	default int getDamageBarValue(ItemStack stack) {
		return stack.getDamage();
	}
	default int getDamageBarMaxValue(ItemStack stack) {
		return stack.getMaxDamage();
	}
	default void tickAsItemEntity(ItemStack stack, ItemEntity item) {
	}
	default Identifier getExtenedTooltipRenderer() {
		return null;
	}
	default boolean canBeRepaired() {
		return true;
	}
}
