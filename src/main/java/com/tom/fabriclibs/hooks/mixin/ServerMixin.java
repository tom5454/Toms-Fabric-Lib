package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.util.registry.RegistryTracker.Modifiable;
import net.minecraft.world.dimension.DimensionType;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.MinecraftServerInstance;
import com.tom.fabriclibs.VillagerTradingManager;
import com.tom.fabriclibs.event.ObjectHolderRegistry;
import com.tom.fabriclibs.events.ServerStoppedEvent;
import com.tom.fabriclibs.events.init.Register;
import com.tom.fabriclibs.events.init.ServerAboutToStartEvent;
import com.tom.fabriclibs.ext.IServer;

@Mixin(MinecraftServer.class)
public class ServerMixin implements IServer {

	@Shadow RegistryTracker.Modifiable dimensionTracker;

	@Inject(at = @At("HEAD"), method = "loadWorld()V")
	public void onLoadWorld(CallbackInfo cbi) {
		System.out.println("Server about to start");
		MinecraftServerInstance.SERVER = ((MinecraftServer)(Object)this);
		ObjectHolderRegistry.registerRegistry(DimensionType.class, dimensionTracker.registry);
		Events.INIT_BUS.post(new Register<>(dimensionTracker.registry));
		ObjectHolderRegistry.apply(dimensionTracker.registry);
		Events.EVENT_BUS.post(new ServerAboutToStartEvent((MinecraftServer)(Object)this));
		VillagerTradingManager.postWandererEvent();
	}

	@Inject(at = @At("RETURN"), method = "shutdown()V")
	public void onShutdown(CallbackInfo cbi) {
		ObjectHolderRegistry.removeRegistry(DimensionType.class);
		Events.EVENT_BUS.post(new ServerStoppedEvent((MinecraftServer)(Object)this));
		MinecraftServerInstance.SERVER = null;
	}

	@Override
	public Modifiable getDimensionTracker() {
		return dimensionTracker;
	}
}
