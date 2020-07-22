package com.tom.fabriclibs.event;

import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.CriticalHitEvent;
import com.tom.fabriclibs.events.entity.HarvestCheckEvent;
import com.tom.fabriclibs.events.entity.LivingSpecialSpawnEvent;
import com.tom.fabriclibs.events.entity.PlayerBreakSpeedEvent;

public class Hooks {

	public static boolean onCropsGrowPre(ServerWorld worldIn, BlockPos pos, BlockState state, boolean b) {
		return b;
	}

	public static void onCropsGrowPost(ServerWorld worldIn, BlockPos pos, BlockState state) {

	}

	public static boolean canEntitySpawnSpawner(MobEntity mobentity, World world, float x, float y, float z,
			MobSpawnerLogic spawnerLogicExt) {
		return true;
	}

	public static boolean doSpecialSpawn(MobEntity entity, World world, float x, float y, float z,
			MobSpawnerLogic spawnerLogicExt, SpawnReason spawner) {
		LivingSpecialSpawnEvent evt = new LivingSpecialSpawnEvent(entity, world, x, y, z, spawnerLogicExt, spawner);
		Events.EVENT_BUS.post(evt);
		return evt.isCanceled();
	}

	public static ActionResult onPlaceItemIntoWorld(ItemUsageContext context) {
		ItemStack is = context.getStack();
		PlayerEntity playerEntity = context.getPlayer();
		BlockPos blockPos = context.getBlockPos();
		CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(context.getWorld(), blockPos, false);
		if (playerEntity != null && !playerEntity.abilities.allowModifyWorld && !is.canPlaceOn(context.getWorld().getTagManager(), cachedBlockPosition)) {
			return ActionResult.PASS;
		}

		Item item = is.getItem();
		ActionResult actionResult = item.useOnBlock(context);
		if (playerEntity != null && actionResult == ActionResult.SUCCESS) {
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
		}
		return actionResult;
	}

	public static CriticalHitEvent getCriticalHit(PlayerEntity player, Entity target, boolean vanillaCritical, float damageModifier) {
		CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
		Events.EVENT_BUS.post(hitResult);
		if (hitResult.getResult() == EventResult.ALLOW || (vanillaCritical && hitResult.getResult() == EventResult.DEFAULT)) {
			return hitResult;
		}
		return null;
	}

	public static float getBreakSpeed(PlayerEntity playerEntity, BlockState state, float f) {
		PlayerBreakSpeedEvent e = new PlayerBreakSpeedEvent(playerEntity, state, f);
		Events.EVENT_BUS.post(e);
		return e.getNewSpeed();
	}

	public static boolean doPlayerHarvestCheck(PlayerEntity player, BlockState state, boolean success) {
		HarvestCheckEvent event = new HarvestCheckEvent(player, state, success);
		Events.EVENT_BUS.post(event);
		return event.canHarvest();
	}
}
