package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.ServerResourceManagerEvent;

@Mixin(ServerResourceManager.class)
public class ServerResourceManagerMixin {

	@Inject(at = @At("RETURN"), method = "<init>*")
	public void onInit(CommandManager.RegistrationEnvironment registrationEnvironment, int i, CallbackInfo cbi) {
		Events.EVENT_BUS.post(new ServerResourceManagerEvent((ServerResourceManager)(Object)this));
	}
}
