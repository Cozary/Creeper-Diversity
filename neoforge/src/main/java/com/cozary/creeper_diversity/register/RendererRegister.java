package com.cozary.creeper_diversity.register;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.client.renderer.MiniCreeperRenderer;
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
    }
}
