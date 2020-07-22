package com.tom.fabriclibs.hooks.mixin;

import java.util.OptionalInt;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.PlayerOpenContainerEvent;
import com.tom.fabriclibs.ext.IEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements IEntity {

	public ServerPlayerEntityMixin(World world, BlockPos blockPos, GameProfile gameProfile) {
		super(world, blockPos, gameProfile);
	}

	@Inject(at = @At(value = "RETURN", ordinal = 2), method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;")
	public void onOpenHandledScreen(NamedScreenHandlerFactory nshf, CallbackInfoReturnable<OptionalInt> cbi) {
		Events.EVENT_BUS.post(new PlayerOpenContainerEvent(this, currentScreenHandler));
	}
}
