package com.tom.fabriclibs.client;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

public interface IBakedModelExt {
	List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData);

	public default IModelData getModelData(BlockRenderView world, BlockPos pos, BlockState state) {
		return IModelData.EMPTY;
	}
}
