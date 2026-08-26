package com.cozary.creeper_diversity.init;

import com.cozary.creeper_diversity.CreeperDiversity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class ModItems {

    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, CreeperDiversity.MOD_ID);

    public static final RegistryObject<Item> MINI_CREEPER_SPAWN_EGG = ITEMS.register("mini_creeper_spawn_egg", () ->
            new SpawnEggItem(
                    new Item.Properties()
                            .spawnEgg(ModEntityTypes.MINI_CREEPER.get())
                            .setId(ResourceKey.create(Registries.ITEM,
                                    Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "mini_creeper_spawn_egg")))
            )
    );

    public static final RegistryObject<Item> CACTUS_CREEPER_SPAWN_EGG = ITEMS.register("cactus_creeper_spawn_egg", () ->
            new SpawnEggItem(
                    new Item.Properties()
                            .spawnEgg(ModEntityTypes.CACTUS_CREEPER.get())
                            .setId(ResourceKey.create(Registries.ITEM,
                                    Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "cactus_creeper_spawn_egg")))
            )
    );

    public static final RegistryObject<Item> SPORE_CREEPER_SPAWN_EGG = ITEMS.register("spore_creeper_spawn_egg", () ->
            new SpawnEggItem(
                    new Item.Properties()
                            .spawnEgg(ModEntityTypes.SPORE_CREEPER.get())
                            .setId(ResourceKey.create(Registries.ITEM,
                                    Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "spore_creeper_spawn_egg")))
            )
    );

    public static void loadClass() {
    }
}
