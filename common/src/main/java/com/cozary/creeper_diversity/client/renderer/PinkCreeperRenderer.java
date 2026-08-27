package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.PinkCreeperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class PinkCreeperRenderer extends MobRenderer<PinkCreeperEntity, PinkCreeperRenderState, CreeperModel> {
    private static final Identifier PINK_CREEPER_LOCATION = Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "textures/entity/creeper/pink_creeper.png");

    public PinkCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
    }

    @Override
    public PinkCreeperRenderState createRenderState() {
        return new PinkCreeperRenderState();
    }

    @Override
    public void extractRenderState(PinkCreeperEntity entity, PinkCreeperRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.swelling = entity.getSwelling(partialTick);
        state.isPowered = entity.isPowered();
        state.isPranking = entity.isPranking();
        state.prankProgress = entity.getPrankProgress(partialTick);
    }

    @Override
    public void submit(PinkCreeperRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        if (state.isPranking) {
            poseStack.pushPose();
            float progress = state.prankProgress;
            float yOffset = progress * 1.5F;
            float scale = 1.6F + Mth.sin(progress * (float) Math.PI) * 0.3F;

            poseStack.translate(0.0D, 0.5D + yOffset, 0.0D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(progress * 720.0F));
            poseStack.scale(scale, scale, scale);

            ModelPart head = this.getModel().root().getChild("head");
            head.xRot = 0.0F;
            head.yRot = 0.0F;
            head.zRot = 0.0F;

            nodeCollector.order(0).submitModelPart(
                    head,
                    poseStack,
                    RenderTypes.entityCutout(PINK_CREEPER_LOCATION),
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    null
            );

            poseStack.popPose();
        } else {
            super.submit(state, poseStack, nodeCollector, cameraRenderState);
        }
    }

    @Override
    protected void scale(PinkCreeperRenderState state, PoseStack poseStack) {
        float f = state.swelling;
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    protected float getWhiteOverlayProgress(PinkCreeperRenderState state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTextureLocation(PinkCreeperRenderState state) {
        return PINK_CREEPER_LOCATION;
    }
}
