package com.tom.fabriclibs.events;

import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ServerResourceManager;

import com.tom.fabriclibs.event.Event;

public class ServerResourceManagerEvent extends Event {
	private final ServerResourceManager manager;

	public ServerResourceManagerEvent(ServerResourceManager manager) {
		this.manager = manager;
	}

	public ServerResourceManager getManager() {
		return manager;
	}

	public ReloadableResourceManagerImpl getResourceManager() {
		return (ReloadableResourceManagerImpl) manager.getResourceManager();
	}
}
