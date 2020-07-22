package com.tom.fabriclibs.events.init;

import net.minecraft.server.MinecraftServer;

import com.tom.fabriclibs.event.Event;

public class ServerAboutToStartEvent extends Event {
	private final MinecraftServer server;

	public ServerAboutToStartEvent(MinecraftServer server) {
		this.server = server;
	}

	public MinecraftServer getServer() {
		return server;
	}
}
