package com.tom.fabriclibs.hooks.mixin;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

import com.tom.fabriclibs.client.IBakedModelExt;
import com.tom.fabriclibs.client.IModelData;

@Mixin(BakedModel.class)
public interface BakedModelMixin extends IBakedModelExt {

	@Overwrite
	public default List<BakedQuad> getQuads(BlockState paramBlockState, Direction paramDirection, Random paramRandom) {
		return getQuads(paramBlockState, paramDirection, paramRandom, IModelData.EMPTY);
	}

	@Override
	public default List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData modelData) {
		return getQuads(state, side, rand);
	}
}
