package com.tom.fabriclibs.events.init;

import net.minecraft.server.MinecraftServer;

import com.tom.fabriclibs.event.Event;

public class ServerStartingEvent extends Event {
	private final MinecraftServer server;

	public ServerStartingEvent(MinecraftServer server) {
		this.server = server;
	}

	public MinecraftServer getServer() {
		return server;
	}
}
