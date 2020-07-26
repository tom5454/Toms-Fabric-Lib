package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IItem;

@Mixin(value = Item.class, priority = 1001)
public abstract class ItemMixin implements IItem, Vanishable {
	private Identifier regName;

	@Shadow abstract Item getRecipeRemainder();

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.ITEM.getId((Item) (Object) this);
		return regName;
	}

	@Override
	public Item setRegistryName(Identifier name) {
		regName = name;
		return (Item) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}

	@Override
	public ItemStack getConatinerItem(ItemStack old) {
		Item item2 = getRecipeRemainder();
		return (item2 == null) ? ItemStack.EMPTY : new ItemStack(item2);
	}
}
