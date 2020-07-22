package com.tom.fabriclibs.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GuiRegistry {
	public static final Map<Identifier, ExtenedTooltipRenderer> tooltipRenderers = new HashMap<>();
	public static final ExtenedTooltipRenderer DUMMY = new ExtenedTooltipRenderer() {

		@Override
		public List<Text> renderTooltip(List<Text> tooltipIn, MatrixStack matrices, ItemStack stack, int x, int y, TooltipRenderer tooltipRenderer) {
			return tooltipIn;
		}
	};

	public static <M extends ScreenHandler, U extends Screen & ScreenHandlerProvider<M>> void registerGui(ScreenHandlerType<? extends M> type, Factory<M, U> provider) {
		HandledScreens.register(type, provider::create);
	}

	@FunctionalInterface
	public static interface Factory<T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T>> {
		U create(T param1T, PlayerInventory param1PlayerInventory, Text param1Text);
	}
}
