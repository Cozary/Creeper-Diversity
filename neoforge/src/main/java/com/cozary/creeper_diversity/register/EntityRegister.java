package com.cozary.creeper_diversity.register;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.BoneCreeperEntity;
import com.cozary.creeper_diversity.entity.BonePileEntity;
import com.cozary.creeper_diversity.entity.CactusCreeperEntity;
import com.cozary.creeper_diversity.entity.GhostCreeperEntity;
import com.cozary.creeper_diversity.entity.IceCreeperEntity;
import com.cozary.creeper_diversity.entity.MiniCreeperEntity;
import com.cozary.creeper_diversity.entity.MudCreeperEntity;
import com.cozary.creeper_diversity.entity.PinkCreeperEntity;
import com.cozary.creeper_diversity.entity.SporeCreeperEntity;
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
        event.put(ModEntityTypes.SPORE_CREEPER.get(), SporeCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.MUD_CREEPER.get(), MudCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.ICE_CREEPER.get(), IceCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.PINK_CREEPER.get(), PinkCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.GHOST_CREEPER.get(), GhostCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.BONE_CREEPER.get(), BoneCreeperEntity.createAttributes().build());
        event.put(ModEntityTypes.BONE_PILE.get(), BonePileEntity.createAttributes().build());
    }
}
