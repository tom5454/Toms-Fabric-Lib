package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.init.ServerStartingEvent;

@Mixin(MinecraftDedicatedServer.class)
public class DedicatedServerMixin {

	@Inject(at = @At(value = "RETURN"), method = "setupServer()Z")
	public void onInit2(CallbackInfoReturnable<Boolean> cbi) {
		Events.EVENT_BUS.post(new ServerStartingEvent((MinecraftServer)(Object)this));
	}
}
