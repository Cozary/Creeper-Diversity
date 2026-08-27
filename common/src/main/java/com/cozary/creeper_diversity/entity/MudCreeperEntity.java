package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class MudCreeperEntity extends Creeper {

    public MudCreeperEntity(EntityType<? extends MudCreeperEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.0001D || this.tickCount % 10 == 0) {
                placeMudTrail();
            }
        } else {
            if (this.getDeltaMovement().horizontalDistanceSqr() > 0.0001D && this.random.nextFloat() < 0.4F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D;
                double py = this.getY() + 0.1D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D;
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState()),
                        px, py, pz, 0.0D, 0.05D, 0.0D);
            }
        }
    }

    private void placeMudTrail() {
        BlockState mudLayerState = ModBlocks.MUD_LAYER.get().defaultBlockState();
        for (int l = 0; l < 4; ++l) {
            int i = Mth.floor(this.getX() + (double) ((float) (l % 2 * 2 - 1) * 0.25F));
            int j = Mth.floor(this.getY());
            int k = Mth.floor(this.getZ() + (double) ((float) (l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockpos = new BlockPos(i, j, k);
            if (this.level().getBlockState(blockpos).isAir() && mudLayerState.canSurvive(this.level(), blockpos)) {
                this.level().setBlockAndUpdate(blockpos, mudLayerState);
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
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState()), this.getX(), this.getY() + 0.5D, this.getZ(), powered ? 90 : 50, 1.8D, 1.0D, 1.8D, 0.15D);
            }

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.MUD_BREAK, SoundSource.HOSTILE, 3.0F, 0.8F + this.random.nextFloat() * 0.4F);

            this.level().explode(this, this.getX(), this.getY(), this.getZ(), powered ? 2.0F : 1.2F, Level.ExplosionInteraction.NONE);

            double radius = powered ? 8.5D : 6.0D;
            int slowAmplifier = powered ? 3 : 2;
            int weaknessAmplifier = powered ? 1 : 0;
            int duration = powered ? 300 : 200;

            AABB blastBox = this.getBoundingBox().inflate(radius);
            List<LivingEntity> affected = this.level().getEntitiesOfClass(LivingEntity.class, blastBox, entity -> entity != this && entity.isAlive() && this.distanceTo(entity) <= radius);

            for (LivingEntity entity : affected) {
                entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, slowAmplifier));
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, weaknessAmplifier));
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, powered ? 40 : 20, 0));
            }

            int layerRadius = powered ? 5 : 4;
            int layerCount = powered ? (25 + this.random.nextInt(15)) : (15 + this.random.nextInt(10));
            placeMudLayersOnExplode(layerCount, layerRadius);

            int mudBlockCount = powered ? (6 + this.random.nextInt(5)) : (3 + this.random.nextInt(4));
            placeMudOnExplode(mudBlockCount);

            this.discard();
        }
    }

    private void placeMudLayersOnExplode(int count, int radius) {
        BlockPos basePos = this.blockPosition();
        BlockState mudLayerState = ModBlocks.MUD_LAYER.get().defaultBlockState();
        List<BlockPos> candidatePositions = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    for (int dy = -2; dy <= 2; dy++) {
                        BlockPos pos = basePos.offset(dx, dy, dz);
                        if (this.level().getBlockState(pos).isAir() && mudLayerState.canSurvive(this.level(), pos)) {
                            candidatePositions.add(pos);
                            break;
                        }
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
            if (this.level().getBlockState(pos).isAir() && mudLayerState.canSurvive(this.level(), pos)) {
                this.level().setBlockAndUpdate(pos, mudLayerState);
                placed++;
            }
        }
    }

    private void placeMudOnExplode(int count) {
        BlockPos basePos = this.blockPosition();
        List<BlockPos> candidatePositions = new ArrayList<>();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos pos = basePos.offset(dx, dy, dz);
                    if (isConvertibleToMud(pos)) {
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

            if (isConvertibleToMud(pos)) {
                this.level().setBlock(pos, Blocks.MUD.defaultBlockState(), 3);
                placed++;
            }
        }
    }

    private boolean isConvertibleToMud(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.ROOTED_DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.MUDDY_MANGROVE_ROOTS)) {
            return this.level().getBlockState(pos.above()).isAir() || !this.level().getBlockState(pos.above()).isSolid();
        }
        return false;
    }
}
