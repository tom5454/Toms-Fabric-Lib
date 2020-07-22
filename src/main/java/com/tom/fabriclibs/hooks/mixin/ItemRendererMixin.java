package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

import com.tom.fabriclibs.ext.IItem;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"))
	public boolean shouldRenderDamageBar(ItemStack stack) {
		return ((IItem)stack.getItem()).shouldRenderDamageBar(stack);
	}

	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I"))
	public int getDamage(ItemStack stack) {
		return ((IItem)stack.getItem()).getDamageBarValue(stack);
	}

	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"))
	public int getMaxDamage(ItemStack stack) {
		return ((IItem)stack.getItem()).getDamageBarMaxValue(stack);
	}
}
