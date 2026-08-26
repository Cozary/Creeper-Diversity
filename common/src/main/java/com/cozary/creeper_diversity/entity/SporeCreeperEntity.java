package com.cozary.creeper_diversity.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SporeCreeperEntity extends Creeper {

    private static final List<Holder<MobEffect>> SPORE_EFFECTS = List.of(
            MobEffects.NIGHT_VISION,
            MobEffects.JUMP_BOOST,
            MobEffects.WEAKNESS,
            MobEffects.BLINDNESS,
            MobEffects.POISON,
            MobEffects.REGENERATION,
            MobEffects.SATURATION,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WITHER,
            MobEffects.NAUSEA
    );

    public SporeCreeperEntity(EntityType<? extends SporeCreeperEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            this.dead = true;


            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(this.isPowered() ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);

            int minMushrooms = this.isPowered() ? 5 : 3;
            int maxMushrooms = this.isPowered() ? 9 : 6;
            int mushroomsToPlace = minMushrooms + this.random.nextInt(maxMushrooms - minMushrooms + 1);
            placeMushroomsOnExplode(mushroomsToPlace);

            spawnSporeLingeringCloud();

            this.discard();
        }
    }

    private void spawnSporeLingeringCloud() {
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        float radius = this.isPowered() ? 4.5F : 3.0F;
        int duration = this.isPowered() ? 900 : 600;

        cloud.setRadius(radius);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(duration);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        cloud.setCustomParticle(ParticleTypes.MYCELIUM);

        List<Holder<MobEffect>> availableEffects = new ArrayList<>(SPORE_EFFECTS);
        Collections.shuffle(availableEffects);

        int effectCount = this.isPowered() ? 2 : 1;
        int amplifier = this.isPowered() ? 1 : 0;
        int effectDuration = this.isPowered() ? 240 : 160;

        for (int i = 0; i < effectCount && i < availableEffects.size(); i++) {
            cloud.addEffect(new MobEffectInstance(availableEffects.get(i), effectDuration, amplifier));
        }

        this.level().addFreshEntity(cloud);
    }

    private void placeMushroomsOnExplode(int count) {
        BlockPos basePos = this.blockPosition();
        List<BlockPos> posList = new ArrayList<>();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -2; dy <= 3; dy++) {
                    BlockPos pos = basePos.offset(dx, dy, dz);
                    if (this.level().getBlockState(pos).isAir()) {
                        posList.add(pos);
                    }
                }
            }
        }

        Util.shuffle(posList, this.random);

        int placed = 0;
        for (BlockPos pos : posList) {
            if (placed >= count) {
                break;
            }

            Block mushroomBlock = this.random.nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM;
            BlockState mushroomState = mushroomBlock.defaultBlockState();

            if (this.level().getBlockState(pos).isAir() && mushroomState.canSurvive(this.level(), pos)) {
                this.level().setBlock(pos, mushroomState, 3);
                placed++;
            }
        }
    }
}
