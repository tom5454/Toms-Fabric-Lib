package com.tom.fabriclibs.hooks.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.world.World;

import com.tom.fabriclibs.ext.IItem;

@Mixin(RepairItemRecipe.class)
public class RepairItemRecipeMixin {

	@Inject(at = @At("RETURN"), method = "matches(Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/world/World;)Z", locals = LocalCapture.CAPTURE_FAILHARD)
	public void matches(CraftingInventory craftingInventory, World world, CallbackInfoReturnable<Boolean> cbi, List<ItemStack> list) {
		for (ItemStack itemStack : list) {
			if(!((IItem)itemStack.getItem()).canBeRepaired()) {
				cbi.setReturnValue(false);
			}
		}
	}
}
