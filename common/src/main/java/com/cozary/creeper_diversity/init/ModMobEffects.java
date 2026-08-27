package com.cozary.creeper_diversity.init;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.effect.FrozenMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModMobEffects {

    public static final RegistrationProvider<MobEffect> MOB_EFFECTS = RegistrationProvider.get(Registries.MOB_EFFECT, CreeperDiversity.MOD_ID);

    public static final RegistryObject<MobEffect> FROZEN = MOB_EFFECTS.register("frozen", () ->
            new FrozenMobEffect(MobEffectCategory.HARMFUL, 0x89CFF0)
    );

    public static void loadClass() {
    }
}
