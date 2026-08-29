package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.client.renderer.state.ModLivingEntityRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public class MixinLivingEntityRenderState implements ModLivingEntityRenderStateAccessor {

    @Unique
    private boolean creeperDiversity$effectFrozen;

    @Unique
    private boolean creeperDiversity$soulDetached;

    @Override
    public boolean creeperDiversity$isEffectFrozen() {
        return this.creeperDiversity$effectFrozen;
    }

    @Override
    public void creeperDiversity$setEffectFrozen(boolean effectFrozen) {
        this.creeperDiversity$effectFrozen = effectFrozen;
    }

    @Override
    public boolean creeperDiversity$isSoulDetached() {
        return this.creeperDiversity$soulDetached;
    }

    @Override
    public void creeperDiversity$setSoulDetached(boolean soulDetached) {
        this.creeperDiversity$soulDetached = soulDetached;
    }
}
