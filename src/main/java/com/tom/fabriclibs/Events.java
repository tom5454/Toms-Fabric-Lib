package com.tom.fabriclibs;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import com.tom.fabriclibs.event.EventBus;
import com.tom.fabriclibs.event.EventResult;
import com.tom.fabriclibs.events.entity.AttackEntityEvent;
import com.tom.fabriclibs.events.entity.PlayerRightClickBlockEvent;

public class Events {
	public static final EventBus EVENT_BUS = new EventBus();
	public static final EventBus INIT_BUS = new EventBus();
	private static String modid;
	public static String getModid() {
		return modid;
	}

	public static void setModid(String modid) {
		Events.modid = modid;
	}

	public static void throwIfNull() {
		if(modid == null)throw new IllegalStateException("Modid is null");
	}

	public static void registerFabricListeners() {
		AttackEntityCallback.EVENT.register(new AttackEntityCallback() {

			@Override
			public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity,
					EntityHitResult hitResult) {
				Events.EVENT_BUS.post(new AttackEntityEvent(player, entity));
				return ActionResult.PASS;
			}
		});
		UseBlockCallback.EVENT.register(new UseBlockCallback() {

			@Override
			public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
				PlayerRightClickBlockEvent event = new PlayerRightClickBlockEvent(player, hand, hitResult.getBlockPos());
				EVENT_BUS.post(event);
				if (event.isCanceled()) return event.getCancellationResult();
				if(event.getUseBlock() == EventResult.ALLOW)return ActionResult.SUCCESS;
				return ActionResult.PASS;
			}
		});
	}
}
