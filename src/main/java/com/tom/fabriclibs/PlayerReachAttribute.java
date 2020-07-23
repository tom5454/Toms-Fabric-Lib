package com.tom.fabriclibs;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.loader.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PlayerReachAttribute {
	public static EntityAttribute REACH;
	private static boolean isReachEntityAttributesPresent;

	public static void register() {
		isReachEntityAttributesPresent = FabricLoader.INSTANCE.isModLoaded(
				"reach-entity-attributes");
		if(isReachEntityAttributesPresent) {
			try {
				Class.forName("com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes").getDeclaredFields();
			} catch (ClassNotFoundException e) {
			}
			REACH = Registry.ATTRIBUTE.get(new Identifier("reach-entity-attributes:reach"));
		} else {
			REACH = new ClampedEntityAttribute("generic.reachDistance", 5.0D, 0.0D, 1024.0D).setTracked(true);
			Registry.register(Registry.ATTRIBUTE, new Identifier("fabriclibs", "reach"), REACH);
			DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY = new HashMap<>(DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY);
			DefaultAttributeContainer cont = DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY.get(EntityType.PLAYER);
			cont.instances = new HashMap<>(cont.instances);
			cont.instances.put(REACH, new EntityAttributeInstance(REACH, i -> {
				throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getId(PlayerReachAttribute.REACH));
			}));
		}
	}

	public static Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> getRegistry() {
		return DefaultAttributeRegistry.DEFAULT_ATTRIBUTE_REGISTRY;
	}
}
