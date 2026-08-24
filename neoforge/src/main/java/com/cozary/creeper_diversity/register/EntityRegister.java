package com.cozary.creeper_diversity.register;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.CactusCreeperEntity;
import com.cozary.creeper_diversity.entity.MiniCreeperEntity;
import com.cozary.creeper_diversity.init.ModEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = CreeperDiversity.MOD_ID)
public class EntityRegister {

    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.MINI_CREEPER.get(), MiniCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.CACTUS_CREEPER.get(), CactusCreeperEntity.createAttributes().build());
    }
}
