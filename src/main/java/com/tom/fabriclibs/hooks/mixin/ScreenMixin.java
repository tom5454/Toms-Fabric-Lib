package com.tom.fabriclibs.hooks.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

import com.tom.fabriclibs.client.ExtenedTooltipRenderer.TooltipRenderer;
import com.tom.fabriclibs.client.GuiRegistry;
import com.tom.fabriclibs.ext.IItem;

@Mixin(Screen.class)
public abstract class ScreenMixin implements TooltipRenderer {

	@Shadow int width;
	@Shadow int height;

	@Shadow abstract List<Text> getTooltipFromItem(ItemStack stack);
	@Shadow abstract void renderTooltip(MatrixStack matrices, List<? extends StringRenderable> lines, int x, int y);

	@Overwrite
	public void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
		List<Text> text = getTooltipFromItem(stack);
		text = GuiRegistry.tooltipRenderers.getOrDefault(((IItem)stack.getItem()).getExtenedTooltipRenderer(), GuiRegistry.DUMMY).renderTooltip(text, matrices, stack, x, y, this);
		if(text != null && !text.isEmpty())renderTooltip(matrices, text, x, y);
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
