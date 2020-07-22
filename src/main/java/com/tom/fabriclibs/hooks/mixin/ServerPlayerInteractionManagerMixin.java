package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;

import com.tom.fabriclibs.PlayerReachAttribute;
import com.tom.fabriclibs.mixinapi.ReachMixinPlugin.MixinInject;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

	@Shadow ServerPlayerEntity player;
	@Shadow GameMode gameMode;

	@MixinInject(value = 36.0D, obf = "method_14263", normal = "processBlockBreakingAction")
	private double getReach(double def) {
		float attrib = (float)player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		float r = (this.gameMode.isCreative() ? attrib : attrib - 0.5F) + 2;
		return r * r;
	}
}
