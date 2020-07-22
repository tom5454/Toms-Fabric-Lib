package com.tom.fabriclibs.client;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

public interface DynamicBakedModel extends BakedModel, IBakedModelExt {

	@Override
	public default List<BakedQuad> getQuads(BlockState paramBlockState, Direction paramDirection, Random paramRandom) {
		return getQuads(paramBlockState, paramDirection, paramRandom, IModelData.EMPTY);
	}

	@Override
	List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData);

}
