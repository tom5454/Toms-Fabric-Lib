package com.tom.fabriclibs.events.client;

import java.util.Map;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;

import com.tom.fabriclibs.event.Event;

public class ModelBakeEvent extends Event {
	private final Map<Identifier, BakedModel> models;

	public ModelBakeEvent(Map<Identifier, BakedModel> models) {
		this.models = models;
	}

	public Map<Identifier, BakedModel> getModelRegistry() {
		return models;
	}
}
