package com.cozary.creeper_diversity.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class CactusCreeperEntity extends Creeper {

    private int touchCooldown = 0;

    public CactusCreeperEntity(EntityType<? extends CactusCreeperEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            if (this.touchCooldown > 0) {
                this.touchCooldown--;
            } else {
                AABB touchBox = this.getBoundingBox().inflate(0.1D);
                List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, touchBox, e -> e != this && e.isAlive());
                for (LivingEntity entity : nearby) {
                    entity.hurt(this.damageSources().cactus(), 1.0F);
                    entity.setStingerCount(entity.getStingerCount() + 1);
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, SoundSource.HOSTILE, 1.0F, 1.0F);
                    this.touchCooldown = 10;
                    break;
                }
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean result = super.hurtServer(level, source, amount);
        if (result && source.getDirectEntity() instanceof LivingEntity attacker && attacker != this) {
            if (this.distanceToSqr(attacker) <= 9.0D && !source.is(DamageTypes.THORNS) && !source.is(DamageTypes.CACTUS)) {
                attacker.hurt(this.damageSources().cactus(), 1.0F);
                attacker.setStingerCount(attacker.getStingerCount() + 1);
            }
        }
        return result;
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            float power = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.5F * power, Level.ExplosionInteraction.NONE);

            // Place cactus
            int minCount = this.isPowered() ? 3 : 2;
            int maxCount = this.isPowered() ? 5 : 4;
            int cactiToPlace = minCount + this.random.nextInt(maxCount - minCount + 1);
            placeCactiOnExplode(cactiToPlace);

            // Spawn spines
            int spineCount = this.isPowered() ? 24 : 12;
            for (int i = 0; i < spineCount; i++) {
                float yaw = (float) (i * (360.0D / spineCount));
                float pitch = (i % 3 == 0) ? -20.0F : ((i % 3 == 1) ? 0.0F : 25.0F);

                float pitchRad = pitch * ((float) Math.PI / 180.0F);
                float yawRad = -yaw * ((float) Math.PI / 180.0F);

                float vx = -Mth.sin(yawRad) * Mth.cos(pitchRad);
                float vy = -Mth.sin(pitchRad);
                float vz = Mth.cos(yawRad) * Mth.cos(pitchRad);

                CactusSpineEntity spine = new CactusSpineEntity(this.level(), this.getX(), this.getY() + 0.8D, this.getZ(), this);
                spine.shoot(vx, vy, vz, 1.2F, 1.5F);
                this.level().addFreshEntity(spine);
            }

            this.discard();
            this.spawnLingeringCloud();
        }
    }

    private void placeCactiOnExplode(int count) {
        BlockPos basePos = this.blockPosition();
        List<BlockPos> candidatePositions = new ArrayList<>();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -2; dy <= 3; dy++) {
                    BlockPos pos = basePos.offset(dx, dy, dz);
                    if (isValidCactusLocation(pos)) {
                        candidatePositions.add(pos);
                    }
                }
            }
        }

        Util.shuffle(candidatePositions, this.random);

        int placed = 0;
        for (BlockPos pos : candidatePositions) {
            if (placed >= count) {
                break;
            }

            if (isValidCactusLocation(pos)) {
                int targetHeight = 1 + this.random.nextInt(3);
                this.level().setBlock(pos, Blocks.CACTUS.defaultBlockState(), 3);

                for (int h = 1; h < targetHeight; h++) {
                    BlockPos stackPos = pos.above(h);
                    if (isValidCactusLocation(stackPos)) {
                        this.level().setBlock(stackPos, Blocks.CACTUS.defaultBlockState(), 3);
                    } else {
                        break;
                    }
                }

                placed++;
            }
        }
    }

    private boolean isValidCactusLocation(BlockPos pos) {
        return this.level().getBlockState(pos).isAir() && Blocks.CACTUS.defaultBlockState().canSurvive(this.level(), pos);
    }
}
