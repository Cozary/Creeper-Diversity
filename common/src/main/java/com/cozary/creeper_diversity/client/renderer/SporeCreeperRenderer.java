package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.SporeCreeperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SporeCreeperRenderer extends MobRenderer<SporeCreeperEntity, CreeperRenderState, CreeperModel> {
    private static final Identifier SPORE_CREEPER_LOCATION = Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "textures/entity/creeper/spore_creeper.png");

    public SporeCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
    }

    @Override
    public CreeperRenderState createRenderState() {
        return new CreeperRenderState();
    }

    @Override
    public void extractRenderState(SporeCreeperEntity entity, CreeperRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.swelling = entity.getSwelling(partialTick);
        state.isPowered = entity.isPowered();
    }

    @Override
    protected void scale(CreeperRenderState state, PoseStack poseStack) {
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
    protected float getWhiteOverlayProgress(CreeperRenderState state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTextureLocation(CreeperRenderState state) {
        return SPORE_CREEPER_LOCATION;
    }
}
