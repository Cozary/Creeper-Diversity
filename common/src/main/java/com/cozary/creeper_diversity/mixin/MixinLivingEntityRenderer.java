package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.client.renderer.layer.FrozenLayer;
import com.cozary.creeper_diversity.client.renderer.state.ModLivingEntityRenderStateAccessor;
import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow
    public abstract boolean addLayer(RenderLayer<S, M> layer);

    @Shadow
    public abstract Identifier getTextureLocation(S state);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initFrozenLayer(EntityRendererProvider.Context context, M model, float shadowRadius, CallbackInfo ci) {
        this.addLayer(new FrozenLayer<>(this));
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void extractSoulDetachedRenderState(T entity, S state, float partialTick, CallbackInfo ci) {
        if (state instanceof ModLivingEntityRenderStateAccessor accessor) {
            accessor.creeperDiversity$setEffectFrozen(entity.hasEffect(ModMobEffects.FROZEN.asHolder()));
            accessor.creeperDiversity$setSoulDetached(entity.hasEffect(ModMobEffects.SOUL_DETACHED.asHolder()));
        }
    }

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void getSoulDetachedRenderType(S state, boolean showBody, boolean translucent, boolean appearsGlowing, CallbackInfoReturnable<RenderType> cir) {
        if (state instanceof ModLivingEntityRenderStateAccessor accessor && accessor.creeperDiversity$isSoulDetached()) {
            cir.setReturnValue(RenderTypes.entityTranslucent(this.getTextureLocation(state)));
        }
    }

    @Inject(method = "getModelTint", at = @At("HEAD"), cancellable = true)
    private void getSoulDetachedModelTint(S state, CallbackInfoReturnable<Integer> cir) {
        if (state instanceof ModLivingEntityRenderStateAccessor accessor && accessor.creeperDiversity$isSoulDetached()) {
            cir.setReturnValue(0x77FFFFFF);
        }
    }
}
