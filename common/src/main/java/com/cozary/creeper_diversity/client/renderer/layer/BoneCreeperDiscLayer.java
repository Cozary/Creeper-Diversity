package com.cozary.creeper_diversity.client.renderer.layer;

import com.cozary.creeper_diversity.client.renderer.state.BoneCreeperRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import com.cozary.creeper_diversity.client.sound.BoneCreeperMusicSoundInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class BoneCreeperDiscLayer extends RenderLayer<BoneCreeperRenderState, CreeperModel> {

    public BoneCreeperDiscLayer(RenderLayerParent<BoneCreeperRenderState, CreeperModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, BoneCreeperRenderState state, float yRot, float xRot) {
        if (!state.hasDisc || state.discRenderState.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.60D, 0.0D);

        float musicSpeed = BoneCreeperMusicSoundInstance.calculatePlaybackSpeed(state.ageInTicks, state.swelling);
        float spinSpeed = (6.0F + (state.swelling * state.swelling) * 32.0F) * musicSpeed;
        float spinRotation = (state.ageInTicks * spinSpeed) % 360.0F;
        float wobbleRotation = (state.ageInTicks * 1.2F) % 360.0F;
        float tilt = 4.5F + Mth.sin(state.ageInTicks * 0.15F) * 1.5F + (state.swelling * 2.0F);

        poseStack.mulPose(Axis.YP.rotationDegrees(wobbleRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F - tilt));
        poseStack.mulPose(Axis.ZP.rotationDegrees(spinRotation));

        poseStack.scale(0.38F, 0.38F, 0.38F);

        state.discRenderState.submit(poseStack, nodeCollector, packedLight, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
