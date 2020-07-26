package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import com.tom.fabriclibs.PlayerReachAttribute;

@Mixin(Item.class)
public class ItemMixin {

	@ModifyConstant(method = "rayTrace(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/RayTraceContext$FluidHandling;)Lnet/minecraft/util/hit/BlockHitResult;", constant = @Constant(doubleValue = 5.0D))
	private static double getReach(double oldVal, World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling) {
		float attrib = (float)player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		return player.abilities.creativeMode ? attrib : attrib - 0.5F;
	}
}
