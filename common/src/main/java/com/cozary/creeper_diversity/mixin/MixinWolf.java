package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.entity.BoneCreeperEntity;
import com.cozary.creeper_diversity.entity.BonePileEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public abstract class MixinWolf extends TamableAnimal {

    protected MixinWolf(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void creeperDiversity$registerBoneGoals(CallbackInfo ci) {
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, BoneCreeperEntity.class, false));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, BonePileEntity.class, false));
    }
}
