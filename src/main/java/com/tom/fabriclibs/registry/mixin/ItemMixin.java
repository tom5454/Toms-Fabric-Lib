package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import com.tom.fabriclibs.PlayerReachAttribute;
import com.tom.fabriclibs.ext.IItem;

@Mixin(value = Item.class, priority = 1)
public abstract class ItemMixin implements IItem, Vanishable {
	private Identifier regName;

	@Shadow abstract Item getRecipeRemainder();

	@Override
	public Identifier getRegistryName() {
		if(regName == null)regName = Registry.ITEM.getId((Item) (Object) this);
		return regName;
	}

	@Override
	public Item setRegistryName(Identifier name) {
		regName = name;
		return (Item) (Object) this;
	}

	@Override
	public Identifier getRegistryNameInt() {
		return regName;
	}

	@Overwrite
	public static BlockHitResult rayTrace(World world, PlayerEntity player, RayTraceContext.FluidHandling fluidHandling) {
		float f = player.pitch;
		float g = player.yaw;

		Vec3d vec3d = player.getCameraPosVec(1.0F);


		float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
		float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
		float j = -MathHelper.cos(-f * 0.017453292F);
		float k = MathHelper.sin(-f * 0.017453292F);

		float l = i * j;
		float m = k;
		float n = h * j;

		double d = getReach(player);
		Vec3d vec3d2 = vec3d.add(l * d, m * d, n * d);

		return world.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.OUTLINE, fluidHandling, player));
	}

	private static float getReach(PlayerEntity player) {
		float attrib = (float)player.getAttributeInstance(PlayerReachAttribute.REACH).getValue();
		return player.abilities.creativeMode ? attrib : attrib - 0.5F;
	}

	@Override
	public ItemStack getConatinerItem(ItemStack old) {
		Item item2 = getRecipeRemainder();
		return (item2 == null) ? ItemStack.EMPTY : new ItemStack(item2);
	}
}
