package com.cozary.creeper_diversity.init;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.entity.CactusCreeperEntity;
import com.cozary.creeper_diversity.entity.CactusSpineEntity;
import com.cozary.creeper_diversity.entity.IceCreeperEntity;
import com.cozary.creeper_diversity.entity.MiniCreeperEntity;
import com.cozary.creeper_diversity.entity.MudCreeperEntity;
import com.cozary.creeper_diversity.entity.PinkCreeperEntity;
import com.cozary.creeper_diversity.entity.SporeCreeperEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {

    public static final RegistrationProvider<EntityType<?>> ENTITY_TYPES = RegistrationProvider.get(Registries.ENTITY_TYPE, CreeperDiversity.MOD_ID);

    public static final RegistryObject<EntityType<MiniCreeperEntity>> MINI_CREEPER = ENTITY_TYPES.register("mini_creeper", () ->
            EntityType.Builder.of(MiniCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.3F, 0.85F)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "mini_creeper")))
    );

    public static final RegistryObject<EntityType<CactusCreeperEntity>> CACTUS_CREEPER = ENTITY_TYPES.register("cactus_creeper", () ->
            EntityType.Builder.of(CactusCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "cactus_creeper")))
    );

    public static final RegistryObject<EntityType<CactusSpineEntity>> CACTUS_SPINE = ENTITY_TYPES.register("cactus_spine", () ->
            EntityType.Builder.<CactusSpineEntity>of(CactusSpineEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "cactus_spine")))
    );

    public static final RegistryObject<EntityType<SporeCreeperEntity>> SPORE_CREEPER = ENTITY_TYPES.register("spore_creeper", () ->
            EntityType.Builder.of(SporeCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "spore_creeper")))
    );

    public static final RegistryObject<EntityType<MudCreeperEntity>> MUD_CREEPER = ENTITY_TYPES.register("mud_creeper", () ->
            EntityType.Builder.of(MudCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "mud_creeper")))
    );

    public static final RegistryObject<EntityType<IceCreeperEntity>> ICE_CREEPER = ENTITY_TYPES.register("ice_creeper", () ->
            EntityType.Builder.of(IceCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "ice_creeper")))
    );

    public static final RegistryObject<EntityType<com.cozary.creeper_diversity.entity.PinkCreeperEntity>> PINK_CREEPER = ENTITY_TYPES.register("pink_creeper", () ->
            EntityType.Builder.of(com.cozary.creeper_diversity.entity.PinkCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "pink_creeper")))
    );

    public static void loadClass() {
    }
}
