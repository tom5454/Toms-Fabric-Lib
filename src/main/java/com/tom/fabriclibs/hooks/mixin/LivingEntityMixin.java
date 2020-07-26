package com.tom.fabriclibs.hooks.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.LivingDropsEvent;
import com.tom.fabriclibs.events.entity.LivingEntityUseItemTickEvent;
import com.tom.fabriclibs.events.entity.LivingExperienceDropEvent;
import com.tom.fabriclibs.events.entity.LivingHurtEvent;
import com.tom.fabriclibs.ext.IEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IEntity {

	@Shadow abstract int getCurrentExperience(PlayerEntity e);

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

	@Redirect(method = "dropXp()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getCurrentExperience(Lnet/minecraft/entity/player/PlayerEntity;)I"))
	public int onGetXP(LivingEntity this$0, PlayerEntity e) {
		LivingExperienceDropEvent evt = new LivingExperienceDropEvent((LivingEntity) (Object) this, e, getCurrentExperience(e));
		Events.EVENT_BUS.post(evt);
		if(evt.isCanceled())return 0;
		return evt.getDroppedExperience();
	}

	@Inject(method = "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"), cancellable = true)
	public void onApplyDamage(DamageSource source, float amount, CallbackInfo cbi) {
		if (amount <= 0)cbi.cancel();
	}

	@ModifyArg(method = "applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
	public float onApplyDamageGetAmount(DamageSource source, float amount) {
		LivingHurtEvent evt = new LivingHurtEvent((LivingEntity) (Object) this, source, amount);
		Events.EVENT_BUS.post(evt);
		amount = evt.getAmount();
		if (amount <= 0)return 0;
		else return amount;
	}

	@Inject(method = "applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F", at = @At("HEAD"), cancellable = true)
	public void onApplyArmorToDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cbi) {
		if(amount <= 0)cbi.setReturnValue(0f);
	}

	@Redirect(method = "tickActiveItemStack()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;usageTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V"))
	public void onTickActiveItemStack(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		LivingEntityUseItemTickEvent evt = new LivingEntityUseItemTickEvent(user, stack, itemUseTimeLeft);
		Events.EVENT_BUS.post(evt);
		itemUseTimeLeft = evt.getDuration();
		if(itemUseTimeLeft > 0)
			stack.usageTick(world, user, remainingUseTicks);
	}
}
