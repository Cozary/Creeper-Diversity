package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.mixin.CreeperAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class PinkCreeperEntity extends Creeper {

    public static final byte EVENT_TOTEM_PRANK = 67;
    public static final int PRANK_TOTAL_TICKS = 60;

    private static final EntityDataAccessor<Boolean> DATA_PRANKING = SynchedEntityData.defineId(PinkCreeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PRANK_TICKS = SynchedEntityData.defineId(PinkCreeperEntity.class, EntityDataSerializers.INT);

    public PinkCreeperEntity(EntityType<? extends PinkCreeperEntity> type, Level level) {
        super(type, level);
        CreeperAccessor accessor = (CreeperAccessor) this;
        accessor.setMaxSwell(20);
        accessor.setExplosionRadius(8);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PRANKING, false);
        builder.define(DATA_PRANK_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public boolean isPranking() {
        return this.entityData.get(DATA_PRANKING);
    }

    public float getPrankProgress(float partialTick) {
        if (!this.isPranking()) {
            return 0.0F;
        }
        int remaining = this.entityData.get(DATA_PRANK_TICKS);
        float progress = 1.0F - (Math.max(0.0F, remaining - partialTick) / (float) PRANK_TOTAL_TICKS);
        return Mth.clamp(progress, 0.0F, 1.0F);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isPranking()) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            if (!this.level().isClientSide()) {
                int remaining = this.entityData.get(DATA_PRANK_TICKS) - 1;
                this.entityData.set(DATA_PRANK_TICKS, remaining);

                if (remaining <= 0) {
                    this.dead = true;
                    boolean powered = this.isPowered();

                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(powered ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    }

                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);

                    float explosionPower = powered ? 16.0F : 8.0F;
                    this.level().explode(this, this.getX(), this.getY() + 0.6D, this.getZ(), explosionPower, Level.ExplosionInteraction.BLOCK);

                    this.discard();
                }
            }
        } else {
            if (this.level().isClientSide()) {
                if (this.random.nextFloat() < 0.25F) {
                    double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.8D;
                    double py = this.getY() + this.random.nextDouble() * 1.6D;
                    double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.8D;
                    this.level().addParticle(ParticleTypes.CHERRY_LEAVES, px, py, pz, (this.random.nextDouble() - 0.5D) * 0.02D, -0.01D, (this.random.nextDouble() - 0.5D) * 0.02D);
                }
                if (this.random.nextFloat() < 0.015F) {
                    double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.8D;
                    double py = this.getY() + 1.2D + this.random.nextDouble() * 0.5D;
                    double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.8D;
                    this.level().addParticle(ParticleTypes.HEART, px, py, pz, 0.0D, 0.02D, 0.0D);
                }
            }
        }
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            if (!this.isPranking()) {
                this.entityData.set(DATA_PRANKING, true);
                this.entityData.set(DATA_PRANK_TICKS, PRANK_TOTAL_TICKS);
                this.setInvulnerable(true);
                this.setDeltaMovement(0.0D, 0.0D, 0.0D);

                this.level().broadcastEntityEvent(this, EVENT_TOTEM_PRANK);

                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.5D, this.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.05D);
                    serverLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.0D, this.getZ(), 4, 0.3D, 0.4D, 0.3D, 0.1D);
                }

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 2.0F, 1.25F);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return !this.isPranking() && super.isPushable();
    }

    @Override
    public boolean attackable() {
        return !this.isPranking() && super.attackable();
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource source) {
        if (this.isPranking()) {
            return true;
        }
        return super.isInvulnerableTo(serverLevel, source);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isPranking()) {
            return false;
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (this.isPranking()) {
            if (!this.level().isClientSide()) {
                float explosionPower = this.isPowered() ? 16.0F : 8.0F;
                this.level().explode(this, this.getX(), this.getY() + 0.6D, this.getZ(), explosionPower, Level.ExplosionInteraction.BLOCK);
            }
        }
        super.die(damageSource);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_TOTEM_PRANK) {
            if (this.level().isClientSide()) {
                double x = this.getX();
                double y = this.getY();
                double z = this.getZ();

                this.level().playLocalSound(x, y, z, SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 1.8F, 1.25F, false);

                for (int i = 0; i < 4; i++) {
                    double speedX = (this.random.nextDouble() - 0.5D) * 0.3D;
                    double speedY = this.random.nextDouble() * 0.3D;
                    double speedZ = (this.random.nextDouble() - 0.5D) * 0.3D;
                    this.level().addParticle(ParticleTypes.HEART, x, y + 1.0D, z, speedX, speedY, speedZ);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }
    }
}
