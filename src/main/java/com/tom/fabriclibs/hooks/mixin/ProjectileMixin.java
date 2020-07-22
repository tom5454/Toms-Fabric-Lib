package com.tom.fabriclibs.hooks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import com.tom.fabriclibs.Events;
import com.tom.fabriclibs.events.entity.ArrowImpactEvent;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileMixin extends Entity {

	public ProjectileMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at = @At(value = "HEAD"), method = "onCollision(Lnet/minecraft/util/hit/HitResult;)V")
	public void onImpact(HitResult result, CallbackInfo cbi) {
		ProjectileEntity this$0 = (ProjectileEntity) (Object) this;
		if(this$0 instanceof PersistentProjectileEntity) {
			ArrowImpactEvent e = new ArrowImpactEvent((PersistentProjectileEntity) (Object) this, result);
			Events.EVENT_BUS.post(e);
		}
	}
}
