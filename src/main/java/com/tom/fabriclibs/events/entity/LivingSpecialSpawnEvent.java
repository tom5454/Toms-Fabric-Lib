package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.WorldAccess;

public class LivingSpecialSpawnEvent extends EntityEvent {
	private final WorldAccess world;
	private final double x;
	private final double y;
	private final double z;
	private final MobSpawnerLogic spawner;
	private final SpawnReason spawnReason;

	public LivingSpecialSpawnEvent(MobEntity entity, WorldAccess world, double x, double y, double z, MobSpawnerLogic spawner, SpawnReason spawnReason)  {
		super(entity);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.spawner = spawner;
		this.spawnReason = spawnReason;
	}

	public WorldAccess getWorld() { return world; }
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }

	public MobSpawnerLogic getSpawner()
	{
		return spawner;
	}

	public SpawnReason getSpawnReason()
	{
		return spawnReason;
	}
}
