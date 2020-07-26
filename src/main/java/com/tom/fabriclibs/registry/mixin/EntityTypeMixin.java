package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import com.tom.fabriclibs.event.Hooks;
import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements IRegistryEntry<EntityType<?>>{
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.ENTITY_TYPE.getId((EntityType<?>) (Object) this);
		return regName;
	}

	@Override
	public EntityType<?> setRegistryName(Identifier name) {
		regName = name;
		return (EntityType<?>) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), method = "spawn(Lnet/minecraft/world/World;Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/text/Text;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;", require = 1, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void onSpawn(World world, CompoundTag itemTag, Text name, PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<Entity> cbi, Entity ent) {
		if(ent instanceof MobEntity) {
			if(Hooks.doSpecialSpawn((MobEntity) ent, world, pos.getX(), pos.getY(), pos.getZ(), null, spawnReason))
				cbi.setReturnValue(null);
		}
	}

	@Shadow abstract Entity create(World world, CompoundTag itemTag, Text name, PlayerEntity player, BlockPos pos,
			SpawnReason spawnReason, boolean alignPosition, boolean invertY);
}
