package com.tom.fabriclibs.hooks.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.EntityJoinWorldEvent;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

	protected ClientWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey,
			RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler,
			boolean bl, boolean bl2, long l) {
		super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
	}

	@Inject(at = @At("HEAD"), method = "addEntityPrivate(ILnet/minecraft/entity/Entity;)V", cancellable = true)
	public void onEntityAdd(int id, Entity ent, CallbackInfo cbi) {
		EntityJoinWorldEvent evt = new EntityJoinWorldEvent(ent, this);
		Events.EVENT_BUS.post(evt);
		if(evt.isCanceled()) {
			cbi.cancel();
		}
	}
}
