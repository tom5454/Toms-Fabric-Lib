package com.tom.fabriclibs.hooks.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.LivingDropsEvent;
import com.tom.fabriclibs.events.entity.LivingEntityUseItemTickEvent;
import com.tom.fabriclibs.events.entity.LivingExperienceDropEvent;
import com.tom.fabriclibs.events.entity.LivingHurtEvent;
import com.tom.fabriclibs.ext.IEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IEntity {

	@Shadow abstract boolean shouldAlwaysDropXp();
	@Shadow abstract boolean canDropLootAndXp();
	@Shadow abstract int getCurrentExperience(PlayerEntity e);
	@Shadow abstract DamageTracker getDamageTracker();
	@Shadow abstract void setHealth(float f);
	@Shadow abstract void setAbsorptionAmount(float f);
	@Shadow abstract float getAbsorptionAmount();
	@Shadow abstract float applyEnchantmentsToDamage(DamageSource source, float amount);
	@Shadow abstract float applyArmorToDamage(DamageSource source, float amount);
	@Shadow abstract float getHealth();
	@Shadow abstract ItemStack getStackInHand(Hand activeHand);
	@Shadow abstract Hand getActiveHand();
	@Shadow abstract int getItemUseTimeLeft();
	@Shadow abstract void spawnConsumptionEffects(ItemStack activeItemStack2, int i);
	@Shadow abstract boolean shouldSpawnConsumptionEffects();
	@Shadow abstract void clearActiveItem();
	@Shadow abstract void consumeItem();
	@Shadow abstract boolean isUsingItem();

	@Shadow int playerHitTimer;
	@Shadow PlayerEntity attackingPlayer;
	@Shadow ItemStack activeItemStack;
	@Shadow int itemUseTimeLeft;

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at = @At("HEAD"), method = "drop(Lnet/minecraft/entity/damage/DamageSource;)V")
	public void onDropStart(DamageSource source, CallbackInfo cbi) {
		captureDrops(new ArrayList<>());
	}

	@Inject(at = @At("RETURN"), method = "drop(Lnet/minecraft/entity/damage/DamageSource;)V")
	public void onDropReturn(DamageSource source, CallbackInfo cbi) {
		List<ItemEntity> drops = captureDrops(null);
		LivingDropsEvent e = new LivingDropsEvent((LivingEntity) (Object) this, source, drops);
		Events.EVENT_BUS.post(e);
		if(!e.isCanceled()) {
			e.getDrops().forEach(i -> world.spawnEntity(i));
		}
	}

	@Overwrite
	public void dropXp() {
		if (!this.world.isClient && (shouldAlwaysDropXp() || (this.playerHitTimer > 0 && canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)))) {
			int i = getCurrentExperience(this.attackingPlayer);
			LivingExperienceDropEvent evt = new LivingExperienceDropEvent((LivingEntity) (Object) this, attackingPlayer, i);
			Events.EVENT_BUS.post(evt);
			if(evt.isCanceled())return;
			i = evt.getDroppedExperience();
			while (i > 0) {
				int j = ExperienceOrbEntity.roundToOrbSize(i);
				i -= j;
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, getX(), getY(), getZ(), j));
			}
		}
	}

	@Overwrite
	public void applyDamage(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) {
			return;
		}
		LivingHurtEvent evt = new LivingHurtEvent((LivingEntity) (Object) this, source, amount);
		Events.EVENT_BUS.post(evt);
		amount = evt.getAmount();
		if (amount <= 0) return;

		amount = applyArmorToDamage(source, amount);
		amount = applyEnchantmentsToDamage(source, amount);

		float f = amount;
		amount = Math.max(amount - getAbsorptionAmount(), 0.0F);
		setAbsorptionAmount(getAbsorptionAmount() - f - amount);

		float g = f - amount;
		if (g > 0.0F && g < 3.4028235E37F && source.getAttacker() instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0F));
		}

		if (amount == 0.0F) {
			return;
		}

		float h = getHealth();
		setHealth(h - amount);
		getDamageTracker().onDamage(source, h, amount);
		setAbsorptionAmount(getAbsorptionAmount() - amount);
	}

	@Overwrite
	private void tickActiveItemStack() {
		if (isUsingItem())
		{
			if (ItemStack.areItemsEqual(getStackInHand(getActiveHand()), this.activeItemStack)) {
				this.activeItemStack = getStackInHand(getActiveHand());
				LivingEntityUseItemTickEvent evt = new LivingEntityUseItemTickEvent((LivingEntity) (Object) this, activeItemStack, itemUseTimeLeft);
				Events.EVENT_BUS.post(evt);
				itemUseTimeLeft = evt.getDuration();
				if(itemUseTimeLeft > 0)
					this.activeItemStack.usageTick(this.world, (LivingEntity) (Object) this, getItemUseTimeLeft());
				if (shouldSpawnConsumptionEffects()) {
					spawnConsumptionEffects(this.activeItemStack, 5);
				}
				if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !this.activeItemStack.isUsedOnRelease()) {
					consumeItem();
				}
			} else {
				clearActiveItem();
			}
		}
	}
}
