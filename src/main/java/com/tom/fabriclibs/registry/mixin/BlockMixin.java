package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import com.tom.fabriclibs.ext.IBlock;

@Mixin(Block.class)
public class BlockMixin implements IBlock {
	private Identifier regName;

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.BLOCK.getId((Block) (Object) this);
		return regName;
	}

	@Override
	public Block setRegistryName(Identifier name) {
		regName = name;
		return (Block) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, BlockView world, BlockPos pos) {
		return (((Block) (Object) this) == Blocks.BOOKSHELF) ? 1.0F : 0.0F;
	}
}
