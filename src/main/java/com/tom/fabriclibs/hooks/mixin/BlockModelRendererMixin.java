package com.tom.fabriclibs.hooks.mixin;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockModelRenderer.AmbientOcclusionCalculator;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import com.tom.fabriclibs.client.IBakedModelExt;
import com.tom.fabriclibs.client.IModelData;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {

	@Overwrite
	public boolean renderSmooth(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long seed, int overlay) {
		boolean bl = false;

		float[] fs = new float[Direction.values().length * 2];
		BitSet bitSet = new BitSet(3);
		AmbientOcclusionCalculator ambientOcclusionCalculator = ((BlockModelRenderer)(Object)this).new AmbientOcclusionCalculator();

		IBakedModelExt ext = (IBakedModelExt) model;
		IModelData data = ext.getModelData(world, pos, state);

		for (Direction direction : Direction.values()) {
			random.setSeed(seed);
			List<BakedQuad> list = ext.getQuads(state, direction, random, data);
			if (!list.isEmpty()) {
				if (!cull || Block.shouldDrawSide(state, world, pos, direction)) {
					renderQuadsSmooth(world, state, pos, buffer, vertexConsumer, list, fs, bitSet, ambientOcclusionCalculator, overlay);
					bl = true;
				}
			}
		}
		random.setSeed(seed);
		List<BakedQuad> list2 = ext.getQuads(state, null, random, data);
		if (!list2.isEmpty()) {
			renderQuadsSmooth(world, state, pos, buffer, vertexConsumer, list2, fs, bitSet, ambientOcclusionCalculator, overlay);
			bl = true;
		}

		return bl;
	}

	@Overwrite
	public boolean renderFlat(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack buffer, VertexConsumer vertexConsumer, boolean cull, Random random, long l, int i) {
		boolean bl = false;

		BitSet bitSet = new BitSet(3);

		IBakedModelExt ext = (IBakedModelExt) model;
		IModelData data = ext.getModelData(world, pos, state);

		for (Direction direction : Direction.values()) {
			random.setSeed(l);
			List<BakedQuad> list = ext.getQuads(state, direction, random, data);
			if (!list.isEmpty()) {
				if (!cull || Block.shouldDrawSide(state, world, pos, direction)) {
					int j = WorldRenderer.getLightmapCoordinates(world, state, pos.offset(direction));

					renderQuadsFlat(world, state, pos, j, i, false, buffer, vertexConsumer, list, bitSet);
					bl = true;
				}
			}
		}
		random.setSeed(l);
		List<BakedQuad> list2 = ext.getQuads(state, null, random, data);
		if (!list2.isEmpty()) {
			renderQuadsFlat(world, state, pos, -1, i, true, buffer, vertexConsumer, list2, bitSet);
			bl = true;
		}

		return bl;
	}

	@Shadow abstract void renderQuadsSmooth(BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, List<BakedQuad> quads, float[] box, BitSet flags, AmbientOcclusionCalculator ambientOcclusionCalculator, int overlay);
	@Shadow abstract void renderQuadsFlat(BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, BitSet flags);
}
