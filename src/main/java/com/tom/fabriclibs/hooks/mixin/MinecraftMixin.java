package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.client.ClientSetupLastEvent;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {

	@Inject(at = @At(value = "RETURN"), method = "<init>")
	private void fabriclibs_init(CallbackInfo info) {
		Events.INIT_BUS.post(new ClientSetupLastEvent());
	}
}
