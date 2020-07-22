package com.tom.fabriclibs.hooks.mixin;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.impl.client.indigo.renderer.RenderMaterialImpl.Value;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.FabricLibsAccess;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainFallbackConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

import com.tom.fabriclibs.client.IBakedModelExt;
import com.tom.fabriclibs.client.IModelData;
import com.tom.fabriclibs.mixinapi.IndigoPatchPlugin.To;

@Mixin(value = TerrainFallbackConsumer.class, priority = 1)
public abstract class TerrainFallbackConsumerMixin {

	@Shadow static Value MATERIAL_FLAT;
	@Shadow static Value MATERIAL_SHADED;

	@Shadow abstract void renderQuad(BakedQuad quad, Direction cullFace, Value defaultMaterial);

	//@Inject(at = @At("HEAD"), method = "accept(Lnet/minecraft/client/render/model/BakedModel;)V", cancellable = true)
	@To
	public void acceptModel(BakedModel model) {
		BlockRenderInfo blockInfo = FabricLibsAccess.blockInfo(this);
		final Supplier<Random> random = blockInfo.randomSupplier;
		final Value defaultMaterial = FabricLibsAccess.defaultAo(blockInfo) && model.useAmbientOcclusion() ? MATERIAL_SHADED : MATERIAL_FLAT;
		final BlockState blockState = blockInfo.blockState;

		IBakedModelExt ext = (IBakedModelExt) model;
		IModelData data = ext.getModelData(blockInfo.blockView, blockInfo.blockPos, blockState);

		for (int i = 0; i < 6; i++) {
			Direction face = ModelHelper.faceFromIndex(i);
			List<BakedQuad> quads = ext.getQuads(blockState, face, random.get(), data);
			final int count = quads.size();

			if (count != 0) {
				for (int j = 0; j < count; j++) {
					BakedQuad q = quads.get(j);
					renderQuad(q, face, defaultMaterial);
				}
			}
		}

		List<BakedQuad> quads = ext.getQuads(blockState, null, random.get(), data);
		final int count = quads.size();

		if (count != 0) {
			for (int j = 0; j < count; j++) {
				BakedQuad q = quads.get(j);
				renderQuad(q, null, defaultMaterial);
			}
		}
	}
}
