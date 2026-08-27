package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.client.renderer.FrozenLayer;
import com.cozary.creeper_diversity.client.renderer.FrozenRenderStateAccessor;
import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow
    public abstract boolean addLayer(RenderLayer<S, M> layer);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initFrozenLayer(EntityRendererProvider.Context context, M model, float shadowRadius, CallbackInfo ci) {
        this.addLayer(new FrozenLayer<>(this));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void extractFrozenRenderState(T entity, S state, float partialTick, CallbackInfo ci) {
        if (state instanceof FrozenRenderStateAccessor accessor) {
            accessor.creeperDiversity$setEffectFrozen(entity.hasEffect(ModMobEffects.FROZEN.asHolder()));
        }
    }
}
