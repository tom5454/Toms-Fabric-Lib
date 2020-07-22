package com.tom.fabriclibs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;

import com.tom.fabriclibs.event.EntityPacketManager;
import com.tom.fabriclibs.event.ObjectHolderRegistry;
import com.tom.fabriclibs.events.ServerResourceManagerEvent;
import com.tom.fabriclibs.events.init.ClientSetupEvent;
import com.tom.fabriclibs.events.init.CommonSetupEvent;
import com.tom.fabriclibs.events.init.Register;
import com.tom.fabriclibs.mixinapi.AfterInvoke2;
import com.tom.fabriclibs.network.NetworkHandler;

public class FabricLibs implements PreLaunchEntrypoint, ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger();

	public FabricLibs() {
		InjectionPoint.register(AfterInvoke2.class);
		//DistExecutor.runWhenOn(EnvType.CLIENT, () -> LoadWindow::show);
	}

	@Override
	public void onPreLaunch() {
		LOGGER.info("Hello Fabric world!");
		Events.registerFabricListeners();
	}

	@Override
	public void onInitialize() {
		Blocks.ACACIA_BUTTON.asItem();
		Items.ACACIA_BOAT.asItem();
		PlayerReachAttribute.register();
		ObjectHolderRegistry.forEachRegistry(r -> {
			LOGGER.info("Registry: " + ObjectHolderRegistry.getType(r));
			Events.INIT_BUS.post(new Register<>(r));
			ObjectHolderRegistry.apply(r);
		});
		Events.INIT_BUS.post(new CommonSetupEvent());
		DistExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			Events.INIT_BUS.post(new ClientSetupEvent());
		});
		EntityPacketManager.init();
		NetworkHandler.init();
		Events.setModid("fabriclibs");
		Events.EVENT_BUS.addListener(ServerResourceManagerEvent.class, CustomRecipeLoader::serverStart);
		Events.setModid(null);
		//new TerrainRenderContext();
		//new IntegratedPlayerManager(null, null, null);
		//throw new RuntimeException("Exit");
	}
}
