package com.tom.fabriclibs.registry.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

import com.tom.fabriclibs.ext.IEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {
	private CompoundTag lib$persistentData = new CompoundTag();
	private List<ItemEntity> lib$captureDrops;

	@Shadow abstract double getZ();
	@Shadow abstract double getY();
	@Shadow abstract double getX();

	@Shadow World world;

	@Override
	public CompoundTag getPersistentData() {
		return lib$persistentData;
	}

	@Inject(at = @At("HEAD"), method = "toTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;")
	public void onToTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cbi) {
		tag.put("persistentData", lib$persistentData);
	}

	@Inject(at = @At("HEAD"), method = "fromTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void onFromTag(CompoundTag tag, CallbackInfo cbi) {
		lib$persistentData = tag.getCompound("persistentData");
	}

	@Override
	public List<ItemEntity> captureDrops() {
		return lib$captureDrops;
	}

	@Override
	public List<ItemEntity> captureDrops(List<ItemEntity> v) {
		List<ItemEntity> ret = lib$captureDrops;
		lib$captureDrops = v;
		return ret;
	}

	@Overwrite
	public ItemEntity dropStack(ItemStack stack, float yOffset) {
		if (stack.isEmpty()) {
			return null;
		}

		if (this.world.isClient) {
			return null;
		}
		ItemEntity itemEntity = new ItemEntity(this.world, getX(), getY() + yOffset, getZ(), stack);
		itemEntity.setToDefaultPickupDelay();
		if(lib$captureDrops != null) {
			lib$captureDrops.add(itemEntity);
			return itemEntity;
		}
		this.world.spawnEntity(itemEntity);
		return itemEntity;
	}
}
