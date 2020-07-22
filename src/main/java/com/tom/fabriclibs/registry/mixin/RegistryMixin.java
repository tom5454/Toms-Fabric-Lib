package com.tom.fabriclibs.registry.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AirBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.tom.fabriclibs.FabricLibs;
import com.tom.fabriclibs.ext.IRegistry;
import com.tom.fabriclibs.ext.IRegistryEntry;

@Mixin(Registry.class)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RegistryMixin implements IRegistry<IRegistryEntry> {

	@Override
	public IRegistryEntry register(IRegistryEntry object) {
		Identifier id = object.getRegistryNameInt();
		if(object instanceof BlockItem && ((BlockItem)object).getBlock() instanceof AirBlock)throw new IllegalArgumentException("Registering air block item");
		if(object instanceof AirBlock)throw new IllegalArgumentException("Registering air block");
		if(id == null)throw new IllegalArgumentException("RegistryName == null");
		FabricLibs.LOGGER.info("Registering: " + id);
		Registry registry = (Registry)(Object)this;
		/*if(registry.getOrEmpty(id).isPresent()) {
			Object old = registry.get(id);
			int oldID = registry.getRawId(old);
			FabricLibs.LOGGER.info("Overwriting: " + old + " " + oldID);
			((SimpleRegistry)registry).set(oldID, RegistryKey.of(registry.registryKey, id), object);
		} else {*/
		Registry.register(registry, id, object);
		//int rid = registry.getRawId(object);
		//}
		return object;
	}

	@Override
	public void registerAll(IRegistryEntry... objects) {
		for (int i = 0; i < objects.length; i++) {
			register(objects[i]);
		}
	}
}
