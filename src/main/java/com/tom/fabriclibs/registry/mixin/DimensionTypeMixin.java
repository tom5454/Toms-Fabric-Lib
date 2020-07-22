package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.dimension.DimensionType;

import com.tom.fabriclibs.MinecraftServerInstance;
import com.tom.fabriclibs.ext.IRegistryEntry;
import com.tom.fabriclibs.ext.IServer;

@Mixin(DimensionType.class)
public class DimensionTypeMixin implements IRegistryEntry<DimensionType> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null) {
			if(MinecraftServerInstance.SERVER != null) {
				RegistryTracker.Modifiable reg = ((IServer) MinecraftServerInstance.SERVER).getDimensionTracker();
				regName = reg.getDimensionTypeRegistry().getId((DimensionType) (Object) this);
			}
		}
		return regName;
	}

	@Override
	public DimensionType setRegistryName(Identifier name) {
		regName = name;
		return (DimensionType) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}

}
