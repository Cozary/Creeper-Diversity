package com.cozary.creeper_diversity.init;

import com.cozary.creeper_diversity.CreeperDiversity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> SOUL_DETACHED = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "soul_detached")
    );

    public static DamageSource soulDetached(Level level) {
        return new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(SOUL_DETACHED));
    }
}
