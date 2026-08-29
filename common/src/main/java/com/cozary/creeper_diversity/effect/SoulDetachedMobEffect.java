package com.cozary.creeper_diversity.effect;

import com.cozary.creeper_diversity.init.ModDamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SoulDetachedMobEffect extends MobEffect {

    public SoulDetachedMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof ServerPlayer player) {

            if (player.tickCount % 20 == 0) {
                player.hurtServer(level, ModDamageTypes.soulDetached(level), 2.0F);
            }

            if (player.tickCount % 20 == 0) {
                player.sendSystemMessage(Component.translatable("message.creeper_diversity.soul_detached"), true);
            }

            if (player.getRandom().nextFloat() < 0.2F) {
                double px = player.getX() + (player.getRandom().nextDouble() - 0.5D) * 0.8D;
                double py = player.getY() + player.getRandom().nextDouble() * 1.8D;
                double pz = player.getZ() + (player.getRandom().nextDouble() - 0.5D) * 0.8D;
                level.sendParticles(ParticleTypes.SOUL, px, py, pz, 1, 0.0D, 0.02D, 0.0D, 0.01D);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
