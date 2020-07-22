package com.tom.fabriclibs.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;

public class ObjectHolderRegistry {
	private static final Map<String, List<Class<?>>> holders = new HashMap<>();
	private static final Map<String, Map<Class<?>, Map<String, String>>> holdersDeobf = new HashMap<>();
	private static final Map<Class<?>, Registry<?>> registries = new HashMap<>();
	private static final Map<Registry<?>, Class<?>> regToClazz = new HashMap<>();
	private static final List<Registry<?>> registryInitList = new ArrayList<>();

	static {
		registerRegistry(Block.class, Registry.BLOCK);
		registerRegistry(Item.class, Registry.ITEM);
		registerRegistry(SoundEvent.class, Registry.SOUND_EVENT);
		registerRegistry(BlockEntityType.class, Registry.BLOCK_ENTITY_TYPE);
		registerRegistry(StatusEffect.class, Registry.STATUS_EFFECT);
		registerRegistry(Potion.class, Registry.POTION);
		registerRegistry(EntityType.class, Registry.ENTITY_TYPE);
		registerRegistry(Enchantment.class, Registry.ENCHANTMENT);
		registerRegistry(ScreenHandlerType.class, Registry.SCREEN_HANDLER);
		registerRegistry(Feature.class, Registry.FEATURE);
		registerRegistry(RecipeSerializer.class, Registry.RECIPE_SERIALIZER);
		registerRegistry(EntityAttribute.class, Registry.ATTRIBUTE);
		registerRegistry(Biome.class, Registry.BIOME);
		register("minecraft", Blocks.class);
		register("minecraft", Items.class);
		registerDeobf("minecraft", Blocks.class, "/blocks.obf");
		registerDeobf("minecraft", Items.class, "/items.obf");
	}

	public static void register(String modid, Class<?> clazz) {
		holders.computeIfAbsent(modid, k -> new ArrayList<>()).add(clazz);
	}

	private static void registerDeobf(String modid, Class<?> clazz, String obfFile) {
		Map<String, String> map = holdersDeobf.computeIfAbsent(modid, k -> new HashMap<>()).computeIfAbsent(clazz, k -> new HashMap<>());
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(ObjectHolderRegistry.class.getResourceAsStream(obfFile)))){
			String ln;
			while((ln = rd.readLine()) != null) {
				String[] sp = ln.split("\t");
				map.put(sp[0], sp[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> void apply(Registry<T> reg) {
		Field modifiers;
		try {
			modifiers = Field.class.getDeclaredField("modifiers");
		} catch (NoSuchFieldException | SecurityException e2) {
			throw new RuntimeException(e2);
		}
		modifiers.setAccessible(true);
		Class<T> type = (Class<T>) regToClazz.get(reg);
		for (Entry<String, List<Class<?>>> e : holders.entrySet()) {
			String modid = e.getKey();
			for (Class<?> clazz : e.getValue()) {
				for (Field field : clazz.getDeclaredFields()) {
					if(Modifier.isStatic(field.getModifiers())) {
						Identifier id;
						if(field.isAnnotationPresent(ObjectHolder.class)) {
							id = new Identifier(field.getAnnotation(ObjectHolder.class).value());
						} else {
							id = new Identifier(modid, field.getName().toLowerCase());
							Map<Class<?>, Map<String, String>> deobf = holdersDeobf.get(modid);
							if(deobf != null) {
								Map<String, String> map = deobf.get(clazz);
								if(map != null) {
									id = new Identifier(modid, map.getOrDefault(field.getName(), field.getName()).toLowerCase());
								}
							}
						}
						if(type.isAssignableFrom(field.getType())) {
							if(reg != null && reg.getOrEmpty(id).isPresent()) {
								Object entry = reg.get(id);
								if(entry != null) {
									try {
										modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
										field.set(null, entry);
									} catch (IllegalArgumentException | IllegalAccessException e1) {
										throw new RuntimeException(e1);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static <T> void registerRegistry(Class<T> type, Registry<? extends T> registry) {
		registries.put(type, registry);
		regToClazz.put(registry, type);
		registryInitList.add(registry);
	}

	@SuppressWarnings("unchecked")
	public static <T> void removeRegistry(Class<T> type) {
		Registry<? extends T> registry = (Registry<? extends T>) registries.remove(type);
		regToClazz.remove(registry);
		registryInitList.remove(registry);
	}

	public static void forEachRegistry(Consumer<Registry<?>> consumer) {
		registryInitList.forEach(consumer);
	}

	public static Class<?> getType(Registry<?> r) {
		return regToClazz.get(r);
	}

	/*@SuppressWarnings("unchecked")
	public static <T> Class findType(Registry<T> r) {
		TypeVariable persistentClass = (TypeVariable) (((ParameterizedType) r.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0]);
		System.out.println(persistentClass.getGenericDeclaration());
		return null;
	}*/
}
