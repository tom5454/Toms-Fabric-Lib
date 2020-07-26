package com.tom.fabriclibs.hooks.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

import com.google.common.collect.ImmutableList;

import com.tom.fabriclibs.client.ExtenedTooltipRenderer.TooltipRenderer;
import com.tom.fabriclibs.client.GuiRegistry;
import com.tom.fabriclibs.ext.IItem;

@Mixin(Screen.class)
public abstract class ScreenMixin implements TooltipRenderer {

	@Shadow int width;
	@Shadow int height;

	@Shadow abstract List<Text> getTooltipFromItem(ItemStack stack);
	@Shadow abstract void renderTooltip(MatrixStack matrices, List<? extends StringRenderable> lines, int x, int y);

	@Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", cancellable = true)
	public void onRenderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo cbi) {
		List<Text> text = ImmutableList.copyOf(getTooltipFromItem(stack));
		List<Text> text1 = GuiRegistry.tooltipRenderers.getOrDefault(((IItem)stack.getItem()).getExtenedTooltipRenderer(), GuiRegistry.DUMMY).renderTooltip(text, matrices, stack, x, y, this);
		if(text1 == null || !text.equals(text1)) {
			cbi.cancel();
			if(text1 != null && !text1.isEmpty())
				renderTooltip(matrices, text1, x, y);
		}
	}

	@Override
	public void renderHoverTooltip(MatrixStack matrices, List<? extends StringRenderable> lines, int x, int y) {
		renderTooltip(matrices, lines, x, y);
	}

	@Override
	public int getWWidth() {
		return width;
	}

	@Override
	public int getWHeight() {
		return height;
	}
}
