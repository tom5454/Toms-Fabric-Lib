package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.tom.fabriclibs.PlayerReachAttribute;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

	@Shadow ServerPlayerEntity player;

	@ModifyConstant(method = "onPlayerInteractBlock(Lnet/minecraft/network/packet/c2s/play/PlayerInteractBlockC2SPacket;)V", constant = @Constant(doubleValue = 64.0D))
	private double getReach(double val) {
		float attrib = (float)player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		float r = (player.abilities.creativeMode ? attrib : attrib - 0.5F) + 4;
		return r * r;
	}
}
