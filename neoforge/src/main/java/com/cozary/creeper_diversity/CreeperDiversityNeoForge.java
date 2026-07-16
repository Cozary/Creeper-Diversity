package com.cozary.creeper_diversity;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreeperDiversity.MOD_ID)
public class CreeperDiversityNeoForge {

    public CreeperDiversityNeoForge(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        CreeperDiversity.LOG.info("Hello NeoForge world!");
        CreeperDiversity.init();

    }
}