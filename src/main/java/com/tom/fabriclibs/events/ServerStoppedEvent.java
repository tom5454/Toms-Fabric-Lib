package com.tom.fabriclibs.events;

import net.minecraft.server.MinecraftServer;

import com.tom.fabriclibs.event.Event;

public class ServerStoppedEvent extends Event {
	private final MinecraftServer server;

	public ServerStoppedEvent(MinecraftServer server) {
		this.server = server;
	}

	public MinecraftServer getServer() {
		return server;
	}
}
