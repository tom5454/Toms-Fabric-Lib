package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin implements IRegistryEntry<BlockEntityType<?>> {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.BLOCK_ENTITY_TYPE.getId((BlockEntityType<?>) (Object) this);
		return regName;
	}

	@Override
	public BlockEntityType<?> setRegistryName(Identifier name) {
		regName = name;
		return (BlockEntityType<?>) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}
}
