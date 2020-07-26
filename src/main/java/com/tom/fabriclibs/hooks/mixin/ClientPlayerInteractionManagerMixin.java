package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;

import com.tom.fabriclibs.PlayerReachAttribute;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Shadow MinecraftClient client;
	@Shadow GameMode gameMode;

	@Inject(method = "getReachDistance()F", at = @At("HEAD"), cancellable = true)
	public void onGetReachDistance(CallbackInfoReturnable<Float> cbi) {
		float attrib = (float)client.player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		cbi.setReturnValue(this.gameMode.isCreative() ? attrib : attrib - 0.5F);
	}
}
