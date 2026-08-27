package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow
    protected boolean jumping;

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void preventJumpFromGroundWhenFrozen(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModMobEffects.FROZEN.asHolder())) {
            ci.cancel();
        }
    }

    @Inject(method = "jumpInLiquid", at = @At("HEAD"), cancellable = true)
    private void preventJumpInLiquidWhenFrozen(TagKey<Fluid> fluidTag, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModMobEffects.FROZEN.asHolder())) {
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void cancelJumpingFlagWhenFrozen(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(ModMobEffects.FROZEN.asHolder())) {
            this.jumping = false;
        }
    }
}
