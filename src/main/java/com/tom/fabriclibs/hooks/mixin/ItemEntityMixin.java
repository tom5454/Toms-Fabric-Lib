package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

import com.tom.fabriclibs.ext.IItem;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

	@Shadow abstract ItemStack getStack();

	@Inject(at = @At(value = "RETURN", ordinal = 1), method = "tick()V")
	public void onTick(CallbackInfo cbi) {
		ItemStack stack = getStack();
		((IItem)stack.getItem()).tickAsItemEntity(stack, (ItemEntity) (Object) this);
	}
}
