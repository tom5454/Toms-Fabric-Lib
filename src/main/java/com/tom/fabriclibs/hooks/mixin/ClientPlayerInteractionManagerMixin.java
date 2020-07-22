package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.world.GameMode;

import com.tom.fabriclibs.PlayerReachAttribute;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

	@Shadow MinecraftClient client;
	@Shadow GameMode gameMode;

	@Overwrite
	public float getReachDistance() {
		float attrib = (float)client.player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		return this.gameMode.isCreative() ? attrib : attrib - 0.5F;
	}
}
