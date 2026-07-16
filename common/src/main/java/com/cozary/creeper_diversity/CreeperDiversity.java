package com.cozary.creeper_diversity;

import com.cozary.creeper_diversity.init.ModEntityTypes;
import com.cozary.creeper_diversity.init.ModItems;
import com.cozary.creeper_diversity.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreeperDiversity {

    public static final String MOD_ID = "creeper_diversity";
    public static final String MOD_NAME = "CreeperDiversity";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        ModEntityTypes.loadClass();
        ModItems.loadClass();

        if (Services.PLATFORM.isModLoaded("creeper_diversity")) {
            LOG.info("Hello to creeper_diversity");
        }
    }
}
