package com.tom.fabriclibs.ext;

import net.minecraft.util.registry.RegistryTracker;

public interface IServer {
	RegistryTracker.Modifiable getDimensionTracker();
}
