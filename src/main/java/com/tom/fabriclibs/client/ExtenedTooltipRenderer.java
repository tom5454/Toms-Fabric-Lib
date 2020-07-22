package com.tom.fabriclibs.client;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class ExtenedTooltipRenderer extends DrawableHelper {
	public abstract List<Text> renderTooltip(List<Text> tooltipIn, MatrixStack matrices, ItemStack stack, int x, int y, TooltipRenderer tooltipRenderer);

	public static interface TooltipRenderer {
		void renderHoverTooltip(MatrixStack matrices, List<? extends StringRenderable> lines, int x, int y);
		int getWWidth();
		int getWHeight();
	}
}
