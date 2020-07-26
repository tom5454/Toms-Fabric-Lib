package com.tom.fabriclibs.hooks.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.tom.fabriclibs.event.Hooks;
import com.tom.fabriclibs.events.entity.CriticalHitEvent;

@Mixin(value = PlayerEntity.class, priority = 1)
public abstract class PlayerEntityMixin extends LivingEntity {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract float getAttackCooldownProgress(float baseTime);
	@Shadow public abstract void resetLastAttackedTicks();
	@Shadow public abstract void spawnSweepAttackParticles();
	@Shadow public abstract void addCritParticles(Entity target);
	@Shadow public abstract void addEnchantedHitParticles(Entity target);
	@Shadow public abstract void increaseStat(Identifier stat, int amount);
	@Shadow public abstract void addExhaustion(float exhaustion);

	@Shadow public PlayerInventory inventory;

	//TODO find a way to do this with injections
	@Overwrite
	public void attack(Entity target) {
		float h;
		if (!target.isAttackable()) {
			return;
		}
		if (target.handleAttack(this)) {
			return;
		}

		float f = (float)getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);


		if (target instanceof LivingEntity) {
			h = EnchantmentHelper.getAttackDamage(getMainHandStack(), ((LivingEntity)target).getGroup());
		} else {
			h = EnchantmentHelper.getAttackDamage(getMainHandStack(), EntityGroup.DEFAULT);
		}



		float i = getAttackCooldownProgress(0.5F);
		f *= (0.2F + i * i * 0.8F);
		h *= i;

		resetLastAttackedTicks();

		if (f > 0.0F || h > 0.0F) {
			boolean bl = (i > 0.9F);

			boolean bl2 = false;
			int j = 0;
			j += EnchantmentHelper.getKnockback(this);

			if (isSprinting() && bl) {
				this.world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, getSoundCategory(), 1.0F, 1.0F);
				j++;
				bl2 = true;
			}

			boolean bl3 = (bl && this.fallDistance > 0.0F && !this.onGround && !isClimbing() && !isTouchingWater() && !hasStatusEffect(StatusEffects.BLINDNESS) && !hasVehicle() && target instanceof LivingEntity);

			bl3 = (bl3 && !isSprinting());

			CriticalHitEvent hitResult = Hooks.getCriticalHit((PlayerEntity)(Object)this, target, bl3, bl3 ? 1.5F : 1.0F);//
			bl3 = hitResult != null;//

			if (bl3) {
				f *= hitResult.getDamageModifier();
			}
			f += h;

			boolean bl4 = false;


			double d = (this.horizontalSpeed - this.prevHorizontalSpeed);
			if (bl && !bl3 && !bl2 && this.onGround && d < getMovementSpeed()) {

				ItemStack itemStack = getStackInHand(Hand.MAIN_HAND);
				if (itemStack.getItem() instanceof net.minecraft.item.SwordItem) {
					bl4 = true;
				}
			}

			float k = 0.0F;
			boolean bl5 = false;
			int l = EnchantmentHelper.getFireAspect(this);

			if (target instanceof LivingEntity) {
				k = ((LivingEntity)target).getHealth();


				if (l > 0 && !target.isOnFire()) {
					bl5 = true;
					target.setOnFireFor(1);
				}
			}

			Vec3d vec3d = target.getVelocity();

			boolean bl6 = target.damage(DamageSource.player((PlayerEntity)(Object)this), f);
			if (bl6) {
				if (j > 0) {
					if (target instanceof LivingEntity) {
						((LivingEntity)target).takeKnockback(j * 0.5F, MathHelper.sin(this.yaw * 0.017453292F), -MathHelper.cos(this.yaw * 0.017453292F));
					} else {
						target.addVelocity((-MathHelper.sin(this.yaw * 0.017453292F) * j * 0.5F), 0.1D, (MathHelper.cos(this.yaw * 0.017453292F) * j * 0.5F));
					}
					setVelocity(getVelocity().multiply(0.6D, 1.0D, 0.6D));
					setSprinting(false);
				}
				if (bl4) {
					float m = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;
					List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0D, 0.25D, 1.0D));
					for (LivingEntity livingEntity : list) {
						if (livingEntity == this || livingEntity == target || isTeammate(livingEntity)) {
							continue;
						}

						if (livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker()) {
							continue;
						}

						if (squaredDistanceTo(livingEntity) < 9.0D) {
							livingEntity.takeKnockback(0.4F, MathHelper.sin(this.yaw * 0.017453292F), -MathHelper.cos(this.yaw * 0.017453292F));
							livingEntity.damage(DamageSource.player((PlayerEntity)(Object)this), m);
						}
					}
					this.world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, getSoundCategory(), 1.0F, 1.0F);
					spawnSweepAttackParticles();
				}

				if (target instanceof ServerPlayerEntity && target.velocityModified) {
					((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
					target.velocityModified = false;
					target.setVelocity(vec3d);
				}

				if (bl3) {
					this.world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, getSoundCategory(), 1.0F, 1.0F);
					addCritParticles(target);
				}

				if (!bl3 && !bl4) {
					if (bl) {
						this.world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, getSoundCategory(), 1.0F, 1.0F);
					} else {
						this.world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, getSoundCategory(), 1.0F, 1.0F);
					}
				}

				if (h > 0.0F) {
					addEnchantedHitParticles(target);
				}

				onAttacking(target);

				if (target instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged((LivingEntity)target, this);
				}
				EnchantmentHelper.onTargetDamaged(this, target);

				ItemStack itemStack2 = getMainHandStack();
				Entity entity = target;
				if (target instanceof EnderDragonPart) {
					entity = ((EnderDragonPart)target).owner;
				}
				if (!this.world.isClient && !itemStack2.isEmpty() && entity instanceof LivingEntity) {
					itemStack2.postHit((LivingEntity)entity, (PlayerEntity)(Object)this);


					if (itemStack2.isEmpty()) {
						setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
					}
				}
				if (target instanceof LivingEntity) {
					float n = k - ((LivingEntity)target).getHealth();

					increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));

					if (l > 0) {
						target.setOnFireFor(l * 4);
					}


					if (this.world instanceof ServerWorld && n > 2.0F) {
						int o = (int)(n * 0.5D);
						((ServerWorld)this.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5D), target.getZ(), o, 0.1D, 0.0D, 0.1D, 0.2D);
					}
				}

				addExhaustion(0.1F);
			}
			else {
				this.world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, getSoundCategory(), 1.0F, 1.0F);

				if (bl5) {
					target.extinguish();
				}
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void onGetBlockBreakingSpeed(BlockState state, CallbackInfoReturnable<Float> cbi, float f) {
		cbi.setReturnValue(Hooks.getBreakSpeed((PlayerEntity)(Object)this, state, f));
	}

	@Inject(method = "isUsingEffectiveTool(Lnet/minecraft/block/BlockState;)Z", at = @At("RETURN"), cancellable = true)
	public void onIsUsingEffectiveTool(BlockState state, CallbackInfoReturnable<Boolean> cbi) {
		cbi.setReturnValue(Hooks.doPlayerHarvestCheck((PlayerEntity)(Object)this, state, cbi.getReturnValueZ()));
	}
}
