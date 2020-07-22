package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import com.tom.fabriclibs.PlayerReachAttribute;
import com.tom.fabriclibs.mixinapi.ReachMixinPlugin.MixinInject;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

	@Shadow ServerPlayerEntity player;

	@MixinInject(value = 64.0D, obf = "method_12046", normal = "onPlayerInteractBlock")
	private double getReach(double val) {
		float attrib = (float)player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		float r = (player.abilities.creativeMode ? attrib : attrib - 0.5F) + 4;
		return r * r;
	}
}
