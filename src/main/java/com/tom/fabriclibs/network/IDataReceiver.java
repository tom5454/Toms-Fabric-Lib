package com.tom.fabriclibs.network;

import net.minecraft.nbt.CompoundTag;

public interface IDataReceiver {
	void receive(CompoundTag tag);
}
