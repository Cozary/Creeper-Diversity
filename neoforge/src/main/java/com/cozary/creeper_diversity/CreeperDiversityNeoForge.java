package com.cozary.creeper_diversity;

import com.cozary.creeper_diversity.init.ModTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(CreeperDiversity.MOD_ID)
public class CreeperDiversityNeoForge {

    public CreeperDiversityNeoForge(IEventBus eventBus) {
        CreeperDiversity.init();
        ModTabs.init(eventBus);
    }
}