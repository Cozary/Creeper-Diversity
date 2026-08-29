package com.cozary.creeper_diversity.client.renderer.state;

public interface ModLivingEntityRenderStateAccessor {
    boolean creeperDiversity$isEffectFrozen();
    void creeperDiversity$setEffectFrozen(boolean effectFrozen);

    boolean creeperDiversity$isSoulDetached();
    void creeperDiversity$setSoulDetached(boolean soulDetached);
}
