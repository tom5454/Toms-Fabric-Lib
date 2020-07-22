package com.tom.fabriclibs;

import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NBTIngredient extends Ingredient
{
	private final ItemStack stack;
	public NBTIngredient(ItemStack stack)
	{
		super(Stream.of(new Ingredient.StackEntry(stack)));
		this.stack = stack;
	}

	public static Ingredient of(ItemStack stack) {
		return new NBTIngredient(stack);
	}

	@Override
	public boolean test(ItemStack input)
	{
		if (input == null)
			return false;
		//Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
		return this.stack.getItem() == input.getItem() && this.stack.getDamage() == input.getDamage() && ItemStack.areTagsEqual(stack, input);
	}

	@Override
	public JsonElement toJson()
	{
		JsonObject json = new JsonObject();
		json.addProperty("type", "nbt");
		json.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
		json.addProperty("count", stack.getCount());
		if (stack.hasTag())
			json.addProperty("nbt", stack.getTag().toString());
		return json;
	}

	/*public static class Serializer implements IIngredientSerializer<NBTIngredient>
	{
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public NBTIngredient parse(PacketByteBuf buffer) {
			return new NBTIngredient(buffer.readItemStack());
		}

		@Override
		public NBTIngredient parse(JsonObject json) {
			return new NBTIngredient(CraftingHelper.getItemStack(json, true));
		}

		@Override
		public void write(PacketByteBuf buffer, NBTIngredient ingredient) {
			buffer.writeItemStack(ingredient.stack);
		}
	}*/
}
