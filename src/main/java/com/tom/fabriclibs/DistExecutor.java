package com.tom.fabriclibs;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class DistExecutor {

	public static <T> T callWhenOn(EnvType env, Supplier<Supplier<T>> object) {
		if(FabricLoader.getInstance().getEnvironmentType() == env) {
			return object.get().get();
		}
		return null;
	}

	public static void runWhenOn(EnvType env, Supplier<Runnable> object) {
		if(FabricLoader.getInstance().getEnvironmentType() == env) {
			object.get().run();
		}
	}

	public static <T> T runForDist(Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return client.get().get();
		} else {
			return server.get().get();
		}
	}
}
