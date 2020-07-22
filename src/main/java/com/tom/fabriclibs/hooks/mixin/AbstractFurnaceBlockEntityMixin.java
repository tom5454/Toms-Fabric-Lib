package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import com.tom.fabriclibs.ext.IItem;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

	@Shadow DefaultedList<ItemStack> inventory;

	@Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
	public Object handleFuelContainerItem(DefaultedList inv, int slot, Object in) {
		return ItemStack.EMPTY;
	}

	@Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
	public void handleFuelContainerItem(ItemStack stack, int i) {
		if (stack.getCount() == 1) {
			ItemStack containerItem = ((IItem)stack.getItem()).getConatinerItem(stack);
			inventory.set(1, containerItem);
		} else {
			stack.decrement(1);
		}
	}
}
