package com.tom.fabriclibs.registry.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.tom.fabriclibs.ext.IItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

	@Shadow abstract Item getItem();

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V")
	public void onBreak(ItemStack stack, int val, int dmg, LivingEntity entity, Consumer breakCB) {
		((IItem)getItem()).onBroken(stack, entity);
	}
}
