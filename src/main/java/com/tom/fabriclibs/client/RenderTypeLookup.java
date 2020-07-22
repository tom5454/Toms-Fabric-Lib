package com.tom.fabriclibs.client;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;

public class RenderTypeLookup {

	public static void setRenderLayer(Block block, RenderLayer layer) {
		RenderLayers.BLOCKS.put(block, layer);
	}

}
