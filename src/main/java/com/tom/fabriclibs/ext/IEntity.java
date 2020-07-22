package com.tom.fabriclibs.ext;

import java.util.List;

import net.minecraft.entity.ItemEntity;
import net.minecraft.nbt.CompoundTag;

public interface IEntity {
	CompoundTag getPersistentData();
	List<ItemEntity> captureDrops();
	List<ItemEntity> captureDrops(List<ItemEntity> v);
}
