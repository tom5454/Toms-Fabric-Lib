package com.tom.fabriclibs.events.client;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;

import com.tom.fabriclibs.event.Event;

public class RenderWorldLastEvent extends Event {
	private final WorldRenderer context;
	private final MatrixStack mat;
	private final float partialTicks;

	public RenderWorldLastEvent(WorldRenderer context, MatrixStack mat, float partialTicks) {
		this.context = context;
		this.mat = mat;
		this.partialTicks = partialTicks;
	}

	public WorldRenderer getContext() {
		return context;
	}

	public MatrixStack getMatrixStack() {
		return mat;
	}

	public float getPartialTicks() {
		return partialTicks;
	}
}
