package com.cozary.creeper_diversity.client;

import com.cozary.creeper_diversity.client.renderer.CactusCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.CactusSpineRenderer;
import com.cozary.creeper_diversity.client.renderer.MiniCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.SporeCreeperRenderer;
import com.cozary.creeper_diversity.init.ModEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderers;

@Environment(EnvType.CLIENT)
public class CreeperDiversityFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(ModEntityTypes.MINI_CREEPER.get(), MiniCreeperRenderer::new);
        EntityRenderers.register(ModEntityTypes.CACTUS_CREEPER.get(), CactusCreeperRenderer::new);
        EntityRenderers.register(ModEntityTypes.CACTUS_SPINE.get(), CactusSpineRenderer::new);
        EntityRenderers.register(ModEntityTypes.SPORE_CREEPER.get(), SporeCreeperRenderer::new);
    }
}
