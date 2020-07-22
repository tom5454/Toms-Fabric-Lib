package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.client.RenderWorldLastEvent;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow MinecraftClient client;

	@Inject(at = @At(value = "INVOKE_AFTER", target = "Lnet/minecraft/client/render/WorldRenderer;render("
			+ "Lnet/minecraft/client/util/math/MatrixStack;"
			+ "FJZ"
			+ "Lnet/minecraft/client/render/Camera;"
			+ "Lnet/minecraft/client/render/GameRenderer;"
			+ "Lnet/minecraft/client/render/LightmapTextureManager;"
			+ "Lnet/minecraft/util/math/Matrix4f;"
			+ ")V"), method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V")
	public void renderLast(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo cbi) {
		this.client.getProfiler().swap("fabriclibs:renderLast");
		Events.EVENT_BUS.post(new RenderWorldLastEvent(this.client.worldRenderer, matrix, tickDelta));
	}
}
