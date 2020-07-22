package com.tom.fabriclibs.events.init;

import net.minecraft.world.World;

import com.tom.fabriclibs.event.Event;

public class WorldStartEvent extends Event {
	private final World world;

	public WorldStartEvent(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}
}
