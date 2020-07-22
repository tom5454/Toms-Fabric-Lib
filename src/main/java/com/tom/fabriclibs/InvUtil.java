package com.tom.fabriclibs;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class InvUtil {

	public static ListTag inv2Tag(Inventory inv) {
		ListTag l = new ListTag();
		for(int i = 0;i<inv.size();i++) {
			if(!inv.getStack(i).isEmpty()) {
				CompoundTag tag = new CompoundTag();
				inv.getStack(i).toTag(tag);
				tag.putByte("Slot", (byte) i);
				l.add(tag);
			}
		}
		return l;
	}

	public static void loadTag2Inv(Inventory inv, ListTag l) {
		inv.clear();
		for(int i = 0;i<l.size();i++) {
			CompoundTag tag = l.getCompound(i);
			byte slot = tag.getByte("Slot");
			if(slot >= 0 && inv.size() > slot) {
				inv.setStack(slot, ItemStack.fromTag(tag));
			}
		}
	}
}
