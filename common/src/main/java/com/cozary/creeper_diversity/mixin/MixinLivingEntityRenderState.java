package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.client.renderer.FrozenRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class MixinLivingEntityRenderState implements FrozenRenderStateAccessor {

    @Unique
    private boolean creeperDiversity$effectFrozen;

    @Override
    public boolean creeperDiversity$isEffectFrozen() {
        return this.creeperDiversity$effectFrozen;
    }

    @Override
    public void creeperDiversity$setEffectFrozen(boolean effectFrozen) {
        this.creeperDiversity$effectFrozen = effectFrozen;
    }
}
