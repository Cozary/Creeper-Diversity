package com.cozary.creeper_diversity.register;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.client.renderer.BoneCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.BonePileRenderer;
import com.cozary.creeper_diversity.client.renderer.CactusCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.CactusSpineRenderer;
import com.cozary.creeper_diversity.client.renderer.GhostCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.IceCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.MiniCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.MudCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.PinkCreeperRenderer;
import com.cozary.creeper_diversity.client.renderer.SoulVesselRenderer;
import com.cozary.creeper_diversity.client.renderer.SporeCreeperRenderer;
import com.cozary.creeper_diversity.init.ModEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreeperDiversity.MOD_ID, value = Dist.CLIENT)
public class RendererRegister {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.MINI_CREEPER.get(), MiniCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CACTUS_CREEPER.get(), CactusCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CACTUS_SPINE.get(), CactusSpineRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SPORE_CREEPER.get(), SporeCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.MUD_CREEPER.get(), MudCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ICE_CREEPER.get(), IceCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PINK_CREEPER.get(), PinkCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.GHOST_CREEPER.get(), GhostCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.SOUL_VESSEL.get(), SoulVesselRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BONE_CREEPER.get(), BoneCreeperRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BONE_PILE.get(), BonePileRenderer::new);
    }
}
