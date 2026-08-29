package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MixinMob {

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void ignoreSoulDetachedPlayers(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target != null && target.hasEffect(ModMobEffects.SOUL_DETACHED.asHolder())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventTargetingSoulDetachedPlayers(@Nullable LivingEntity target, CallbackInfo ci) {
        if (target != null && target.hasEffect(ModMobEffects.SOUL_DETACHED.asHolder())) {
            ci.cancel();
        }
    }
}
