package com.tom.fabriclibs.events;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import com.tom.fabriclibs.event.Event;

public class ItemTooltipEvent extends Event {
	private final ItemStack stack;
	private final List<Text> lines;

	public ItemTooltipEvent(ItemStack stack, List<Text> lines) {
		this.stack = stack;
		this.lines = lines;
	}

	public ItemStack getItemStack() {
		return stack;
	}

	public List<Text> getToolTip() {
		return lines;
	}
}
