package com.cozary.creeper_diversity;

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
import com.cozary.creeper_diversity.init.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreeperDiversityFabric implements ModInitializer {

    private static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "creeper_diversity"));

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, FabricCreativeModeTab.builder()
                .title(Component.translatable("itemGroup.creeper_diversity"))
                .icon(() -> new ItemStack(ModItems.MINI_CREEPER_SPAWN_EGG.get()))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.MINI_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.CACTUS_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.SPORE_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.MUD_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.ICE_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.PINK_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.GHOST_CREEPER_SPAWN_EGG.get());
                    output.accept(ModItems.BONE_CREEPER_SPAWN_EGG.get());
                })
                .build()
        );

        CreeperDiversity.init();

        FabricDefaultAttributeRegistry.register(ModEntityTypes.MINI_CREEPER.get(), MiniCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.CACTUS_CREEPER.get(), CactusCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.SPORE_CREEPER.get(), SporeCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.MUD_CREEPER.get(), MudCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.ICE_CREEPER.get(), IceCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.PINK_CREEPER.get(), PinkCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.GHOST_CREEPER.get(), GhostCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.BONE_CREEPER.get(), BoneCreeperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntityTypes.BONE_PILE.get(), BonePileEntity.createAttributes());
    }
}
