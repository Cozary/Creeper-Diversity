package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModBlocks;
import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;

public class IceCreeperEntity extends Creeper {

    public IceCreeperEntity(EntityType<? extends IceCreeperEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            if (this.onGround() || this.isInWater()) {
                applyFrostWalker();
            }
        } else {
            if (this.random.nextFloat() < 0.65F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 1.2D;
                double py = this.getY() + this.random.nextDouble() * 1.8D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 1.2D;
                this.level().addParticle(ParticleTypes.SNOWFLAKE, px, py, pz, 0.0D, -0.04D, 0.0D);
            }
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.0001D && this.random.nextFloat() < 0.3F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D;
                double py = this.getY() + 0.1D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()), px, py, pz, 0.0D, 0.05D, 0.0D);
            }
        }
    }

    private void applyFrostWalker() {
        int radius = this.isPowered() ? 3 : 2;
        BlockPos centerPos = this.blockPosition();
        BlockState frostedIce = Blocks.FROSTED_ICE.defaultBlockState();

        for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-radius, -1, -radius), centerPos.offset(radius, 0, radius))) {
            if (pos.closerToCenterThan(this.position(), radius + 0.5D)) {
                BlockPos abovePos = pos.above();
                FluidState fluidState = this.level().getFluidState(pos);

                if (fluidState.is(Fluids.WATER) && fluidState.isSource() && this.level().getBlockState(abovePos).isAir()) {
                    if (frostedIce.canSurvive(this.level(), pos) && this.level().isUnobstructed(frostedIce, pos, CollisionContext.empty())) {
                        this.level().setBlockAndUpdate(pos, frostedIce);
                        this.level().scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(this.random, 60, 120));
                    }
                }
            }
        }
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            this.dead = true;
            boolean powered = this.isPowered();

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(powered ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.FROSTED_ICE.defaultBlockState()), this.getX(), this.getY() + 0.5D, this.getZ(), powered ? 120 : 70, 2.0D, 1.5D, 2.0D, 0.15D);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()), this.getX(), this.getY() + 0.5D, this.getZ(), powered ? 100 : 60, 1.8D, 1.2D, 1.8D, 0.15D);
            }

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 4.0F, 0.9F + this.random.nextFloat() * 0.2F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_HURT_FREEZE, SoundSource.HOSTILE, 3.0F, 1.0F);

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), powered ? 2.0F : 1.2F, Level.ExplosionInteraction.NONE);

            double radius = powered ? 8.5D : 6.0D;
            int duration = powered ? 240 : 140;
            int fatigue = powered ? 3 : 2;

            AABB blastBox = this.getBoundingBox().inflate(radius);
            List<LivingEntity> affected = this.level().getEntitiesOfClass(LivingEntity.class, blastBox, entity -> entity != this && entity.isAlive() && this.distanceTo(entity) <= radius);

            for (LivingEntity entity : affected) {
                entity.addEffect(new MobEffectInstance(ModMobEffects.FROZEN.asHolder(), duration, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, duration, fatigue));
                entity.setTicksFrozen(entity.getTicksRequiredToFreeze() + duration);
                entity.clearFire();
            }

            int explosionRadius = powered ? 5 : 3;
            createFrostedIceOrb(explosionRadius);

            this.discard();
        }
    }

    private void createFrostedIceOrb(int radius) {
        BlockPos centerPos = this.blockPosition().above();
        BlockState fragileIce = ModBlocks.FRAGILE_FROSTED_ICE.get().defaultBlockState();
        double innerRadiusSq = Math.max(0.0D, (radius - 1.2D) * (radius - 1.2D));
        double outerRadiusSq = (radius + 0.5D) * (radius + 0.5D);

        for (int dx = -radius - 1; dx <= radius + 1; dx++) {
            for (int dy = -radius - 1; dy <= radius + 1; dy++) {
                for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq <= outerRadiusSq && distSq >= innerRadiusSq) {
                        BlockPos targetPos = centerPos.offset(dx, dy, dz);
                        BlockState currentState = this.level().getBlockState(targetPos);
                        FluidState fluidState = this.level().getFluidState(targetPos);

                        if (currentState.isAir() || fluidState.is(Fluids.WATER) || currentState.canBeReplaced()) {
                            this.level().setBlockAndUpdate(targetPos, fragileIce);
                            this.level().scheduleTick(targetPos, ModBlocks.FRAGILE_FROSTED_ICE.get(), Mth.nextInt(this.random, 60, 160));
                        }
                    }
                }
            }
        }
    }
}
