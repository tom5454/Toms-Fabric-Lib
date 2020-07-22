package com.tom.fabriclibs.hooks.mixin;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.EntityJoinWorldEvent;
import com.tom.fabriclibs.events.init.WorldStartEvent;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

	protected ServerWorldMixin(MutableWorldProperties mutableWorldProperties, RegistryKey<World> registryKey,
			RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, Supplier<Profiler> profiler,
			boolean bl, boolean bl2, long l) {
		super(mutableWorldProperties, registryKey, registryKey2, dimensionType, profiler, bl, bl2, l);
	}

	@Shadow abstract boolean checkUuid(Entity entity);
	@Shadow abstract void loadEntityUnchecked(Entity entity);

	@Inject(at = @At("RETURN"), method = "<init>*")
	public void onInit(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, WorldGenerationProgressListener generationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2, CallbackInfo cbi) {
		Events.EVENT_BUS.post(new WorldStartEvent(this));
	}

	/*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;"), method = "addEntity(Lnet/minecraft/entity/Entity;)Z", require = 1, cancellable = true)
	public void onAddEntity(Entity ent, CallbackInfoReturnable<Boolean> cbi) {
		EntityJoinWorldEvent evt = new EntityJoinWorldEvent(ent, this);
		Events.EVENT_BUS.post(evt);
		if(evt.isCanceled()) {
			cbi.setReturnValue(false);
		}
	}*/

	@Overwrite
	private boolean addEntity(Entity entity) {
		if (entity.removed) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
			return false;
		}
		if (checkUuid(entity)) {
			return false;
		}
		EntityJoinWorldEvent evt = new EntityJoinWorldEvent(entity, this);
		Events.EVENT_BUS.post(evt);
		if(evt.isCanceled()) {
			return false;
		}
		Chunk chunk = getChunk(MathHelper.floor(entity.getX() / 16.0D), MathHelper.floor(entity.getZ() / 16.0D), ChunkStatus.FULL, entity.teleporting);
		if (!(chunk instanceof WorldChunk)) {
			return false;
		}
		chunk.addEntity(entity);
		loadEntityUnchecked(entity);
		return true;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;loadEntityUnchecked(Lnet/minecraft/entity/Entity;)V"), method = "loadEntity(Lnet/minecraft/entity/Entity;)Z", require = 1, cancellable = true)
	public void onLoadEntity(Entity ent, CallbackInfoReturnable<Boolean> cbi) {
		EntityJoinWorldEvent evt = new EntityJoinWorldEvent(ent, this);
		Events.EVENT_BUS.post(evt);
		if(evt.isCanceled()) {
			cbi.setReturnValue(false);
		}
	}

	@Inject(at = @At(value = "HEAD"), method = "addPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;)V", require = 1, cancellable = true)
	public void onAddPlayer(ServerPlayerEntity ent, CallbackInfo cbi) {
		EntityJoinWorldEvent evt = new EntityJoinWorldEvent(ent, this);
		Events.EVENT_BUS.post(evt);
		if(evt.isCanceled()) {
			cbi.cancel();
		}
	}
}
