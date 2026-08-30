package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.client.model.BonePileModel;
import com.cozary.creeper_diversity.client.renderer.state.BonePileRenderState;
import com.cozary.creeper_diversity.entity.BonePileEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class BonePileRenderer extends MobRenderer<BonePileEntity, BonePileRenderState, BonePileModel> {
    private static final Identifier BONE_CREEPER_LOCATION = Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "textures/entity/creeper/bone_creeper.png");

    public BonePileRenderer(EntityRendererProvider.Context context) {
        super(context, new BonePileModel(context.bakeLayer(ModelLayers.CREEPER_HEAD)), 0.3F);
    }

    @Override
    public BonePileRenderState createRenderState() {
        return new BonePileRenderState();
    }

    @Override
    public void extractRenderState(BonePileEntity entity, BonePileRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.remainingTicks = entity.getRemainingTicks();
    }

    @Override
    public Identifier getTextureLocation(BonePileRenderState state) {
        return BONE_CREEPER_LOCATION;
    }
}
