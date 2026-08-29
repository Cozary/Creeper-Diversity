package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.GhostCreeperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class GhostCreeperRenderer extends MobRenderer<GhostCreeperEntity, CreeperRenderState, CreeperModel> {
    private static final Identifier GHOST_CREEPER_LOCATION = Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "textures/entity/creeper/ghost_creeper.png");

    public GhostCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), 0.0F);
    }

    @Override
    public CreeperRenderState createRenderState() {
        return new CreeperRenderState();
    }

    @Override
    public void extractRenderState(GhostCreeperEntity entity, CreeperRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.swelling = entity.getSwelling(partialTick);
        state.isPowered = entity.isPowered();
    }

    @Nullable
    @Override
    protected RenderType getRenderType(CreeperRenderState state, boolean showBody, boolean translucent, boolean appearsGlowing) {
        return RenderTypes.entityTranslucent(this.getTextureLocation(state));
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
        return GHOST_CREEPER_LOCATION;
    }
}
