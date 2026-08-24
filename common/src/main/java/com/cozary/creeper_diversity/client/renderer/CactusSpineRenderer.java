package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.CactusSpineEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class CactusSpineRenderer extends ArrowRenderer<CactusSpineEntity, ArrowRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "textures/entity/projectiles/cactus_spine.png");

    public CactusSpineRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public Identifier getTextureLocation(ArrowRenderState state) {
        return TEXTURE;
    }
}
