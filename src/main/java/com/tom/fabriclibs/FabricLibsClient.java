package com.tom.fabriclibs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;

import com.tom.fabriclibs.events.ItemTooltipEvent;

public class FabricLibsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((a, b, c) -> {
			Events.EVENT_BUS.post(new ItemTooltipEvent(a, c));
		});
	}

}
