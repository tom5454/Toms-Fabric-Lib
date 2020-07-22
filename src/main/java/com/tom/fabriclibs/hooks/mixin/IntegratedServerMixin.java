package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.init.ServerStartingEvent;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

	/*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;loadWorld()V", ordinal = 1), method = "setupServer()Z", require = 1)
	public void onInit1(CallbackInfoReturnable<Boolean> cbi) {

	}*/

	@Inject(at = @At(value = "RETURN"), method = "setupServer()Z")
	public void onInit2(CallbackInfoReturnable<Boolean> cbi) {
		Events.EVENT_BUS.post(new ServerStartingEvent((MinecraftServer)(Object)this));
	}
}
