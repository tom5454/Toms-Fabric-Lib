package com.tom.fabriclibs.hooks.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.client.ModelBakeEvent;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

	@Shadow Map<Identifier, BakedModel> models;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"), method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
	public void onLoad(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo cbi) {
		Events.INIT_BUS.post(new ModelBakeEvent(models));
	}
}
