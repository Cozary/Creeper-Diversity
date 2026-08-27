package com.cozary.creeper_diversity.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class FrozenLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    private static final Identifier ICE_TEXTURE_LOCATION = Identifier.fromNamespaceAndPath("minecraft", "textures/block/ice.png");

    public FrozenLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, S state, float yRot, float xRot) {
        if (state instanceof FrozenRenderStateAccessor accessor && accessor.creeperDiversity$isEffectFrozen()) {
            if (!state.isInvisible) {
                int overlay = LivingEntityRenderer.getOverlayCoords(state, 0.0F);
                nodeCollector.order(1).submitModel(
                        this.getParentModel(),
                        state,
                        poseStack,
                        RenderTypes.entityTranslucent(ICE_TEXTURE_LOCATION),
                        packedLight,
                        overlay,
                        0,
                        null
                );
            }
        }
    }
}
