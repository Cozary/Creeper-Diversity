package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModEntityTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class BonePileEntity extends PathfinderMob {

    public static final int RESURRECTION_TICKS = 100;

    private static final EntityDataAccessor<Integer> DATA_REMAINING_TICKS = SynchedEntityData.defineId(BonePileEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> DATA_DISC = SynchedEntityData.defineId(BonePileEntity.class, EntityDataSerializers.ITEM_STACK);

    public BonePileEntity(EntityType<? extends BonePileEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_TICKS, RESURRECTION_TICKS);
        builder.define(DATA_DISC, ItemStack.EMPTY);
    }

    public int getRemainingTicks() {
        return this.entityData.get(DATA_REMAINING_TICKS);
    }

    public void setRemainingTicks(int ticks) {
        this.entityData.set(DATA_REMAINING_TICKS, ticks);
    }

    public ItemStack getDiscItem() {
        return this.entityData.get(DATA_DISC);
    }

    public void setDiscItem(ItemStack disc) {
        this.entityData.set(DATA_DISC, disc);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("remaining_ticks", Codec.INT, this.getRemainingTicks());
        output.store("music_disc", ItemStack.CODEC, this.getDiscItem());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setRemainingTicks(input.read("remaining_ticks", Codec.INT).orElse(RESURRECTION_TICKS));
        input.read("music_disc", ItemStack.CODEC).ifPresent(this::setDiscItem);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    public void travel(Vec3 travelVector) {
        this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
        super.travel(Vec3.ZERO);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            int ticks = this.getRemainingTicks() - 1;
            this.setRemainingTicks(ticks);
            if (ticks <= 0) {
                this.resurrect();
            } else if (ticks < 50) {
                if (ticks == 49) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SKELETON_AMBIENT, SoundSource.HOSTILE, 1.0F, 0.8F);
                }
                if (ticks % 8 == 0) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BONE_BLOCK_HIT, SoundSource.HOSTILE, 0.7F, 1.2F + this.random.nextFloat() * 0.4F);
                }
            }
        } else {
            int ticks = this.getRemainingTicks();
            if (ticks < 50 && this.random.nextFloat() < 0.35F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D;
                double py = this.getY() + 0.1D + this.random.nextDouble() * 0.3D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D;
                this.level().addParticle(ParticleTypes.SOUL, px, py, pz, 0.0D, 0.03D, 0.0D);
            }
        }
    }

    private void resurrect() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.5D, this.getZ(), 20, 0.4D, 0.4D, 0.4D, 0.05D);
            serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.5D, this.getZ(), 30, 0.5D, 0.5D, 0.5D, 0.08D);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 1.5F, 1.2F);

            BoneCreeperEntity revived = new BoneCreeperEntity(ModEntityTypes.BONE_CREEPER.get(), serverLevel);
            revived.setPos(this.getX(), this.getY(), this.getZ());
            revived.setYRot(this.getYRot());
            revived.setDiscItem(this.getDiscItem());
            revived.setHasDisc(!this.getDiscItem().isEmpty());
            revived.setHealth(revived.getMaxHealth());
            serverLevel.addFreshEntity(revived);

            this.discard();
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getY() + 0.2D, this.getZ(), 25, 0.3D, 0.2D, 0.3D, 0.1D);
            if (!this.getDiscItem().isEmpty()) {
                this.spawnAtLocation(serverLevel, this.getDiscItem().copy());
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }
}
