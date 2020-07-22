package com.tom.fabriclibs.events.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import com.tom.fabriclibs.event.EventResult;

public class CriticalHitEvent extends EntityEvent {
	private float damageModifier;
	private final float oldDamageModifier;
	private final Entity target;
	private final boolean vanillaCritical;
	private EventResult result = EventResult.DEFAULT;

	public CriticalHitEvent(PlayerEntity player, Entity target, float damageModifier, boolean vanillaCritical)
	{
		super(player);
		this.target = target;
		this.damageModifier = damageModifier;
		this.oldDamageModifier = damageModifier;
		this.vanillaCritical = vanillaCritical;
	}

	/**
	 * The Entity that was damaged by the player.
	 */
	public Entity getTarget()
	{
		return target;
	}

	/**
	 * This set the damage multiplier for the hit.
	 * If you set it to 0, then the particles are still generated but damage is not done.
	 */
	public void setDamageModifier(float mod)
	{
		this.damageModifier = mod;
	}

	/**
	 * The damage modifier for the hit.<br>
	 * This is by default 1.5F for ciritcal hits and 1F for normal hits .
	 */
	public float getDamageModifier()
	{
		return this.damageModifier;
	}

	/**
	 * The orignal damage modifier for the hit wthout any changes.<br>
	 * This is 1.5F for ciritcal hits and 1F for normal hits .
	 */
	public float getOldDamageModifier()
	{
		return this.oldDamageModifier;
	}

	/**
	 * Returns true if this hit was critical by vanilla
	 */
	public boolean isVanillaCritical()
	{
		return vanillaCritical;
	}

	public void setResult(EventResult r) {
		this.result = r;
	}

	public EventResult getResult() {
		return result;
	}
}
