package com.tom.fabriclibs.events.entity;

import net.minecraft.server.network.ServerPlayerEntity;

import com.tom.fabriclibs.event.Event;

public class PlayerJoinedEvent extends Event {
	public final ServerPlayerEntity player;

	public PlayerJoinedEvent(ServerPlayerEntity player) {
		this.player = player;
	}

}
