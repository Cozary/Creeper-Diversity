package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModEntityTypes;
import com.cozary.creeper_diversity.mixin.CreeperAccessor;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BoneCreeperEntity extends Creeper {

    private static final EntityDataAccessor<ItemStack> DATA_DISC = SynchedEntityData.defineId(BoneCreeperEntity.class, EntityDataSerializers.ITEM_STACK);

    public BoneCreeperEntity(EntityType<? extends BoneCreeperEntity> type, Level level) {
        super(type, level);

        CreeperAccessor accessor = (CreeperAccessor) this;
        accessor.setMaxSwell(15);
        accessor.setExplosionRadius(0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DISC, ItemStack.EMPTY);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        RandomSource random = level.getRandom();
        BuiltInRegistries.ITEM.getRandomElementOf(ItemTags.CREEPER_DROP_MUSIC_DISCS, random)
                .ifPresentOrElse(
                        holder -> this.setDiscItem(new ItemStack(holder.value())),
                        () -> this.setDiscItem(new ItemStack(Items.MUSIC_DISC_13))
                );
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    public ItemStack getDiscItem() {
        return this.entityData.get(DATA_DISC);
    }

    public void setDiscItem(ItemStack stack) {
        this.entityData.set(DATA_DISC, stack);
    }

    public boolean hasDisc() {
        return !this.getDiscItem().isEmpty();
    }

    public void setHasDisc(boolean hasDisc) {
        if (!hasDisc) {
            this.setDiscItem(ItemStack.EMPTY);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("music_disc", ItemStack.CODEC, this.getDiscItem());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("music_disc", ItemStack.CODEC).ifPresent(this::setDiscItem);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            if (this.hasDisc() && this.random.nextFloat() < 0.12F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.5D;
                double py = this.getY() + 0.8D + this.random.nextDouble() * 0.4D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.5D;
                this.level().addParticle(ParticleTypes.NOTE, px, py, pz, this.random.nextDouble(), 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            this.dead = true;
            boolean powered = this.isPowered();

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(powered ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 0.5D, this.getZ(), powered ? 80 : 40, 1.5D, 0.8D, 1.5D, 0.1D);
                serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL, this.getX(), this.getY() + 0.5D, this.getZ(), 50, 1.0D, 0.8D, 1.0D, 0.15D);

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, 1.0F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SKELETON_DEATH, SoundSource.HOSTILE, 2.0F, 0.8F);

                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, Level.ExplosionInteraction.NONE);

                double blastRadius = powered ? 8.0D : 5.5D;
                AABB blastBox = this.getBoundingBox().inflate(blastRadius);
                List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, blastBox, e -> e != this && e.isAlive() && this.distanceTo(e) <= blastRadius);
                for (LivingEntity target : targets) {
                    Vec3 fromCenter = target.position().subtract(this.position());
                    double dist = fromCenter.length();
                    Vec3 knockDir = dist > 0.001D ? fromCenter.normalize() : new Vec3(0, 1, 0);
                    Vec3 knockMotion = knockDir.scale(powered ? 2.0D : 1.5D).add(0.0D, 0.45D, 0.0D);
                    target.setDeltaMovement(knockMotion);
                    target.hurtMarked = true;
                    target.hurtServer(serverLevel, this.damageSources().mobAttack(this), powered ? 8.0F : 4.0F);
                }

                int fertilizeRadius = powered ? 6 : 4;
                BlockPos centerPos = this.blockPosition();
                for (int x = -fertilizeRadius; x <= fertilizeRadius; x++) {
                    for (int y = -2; y <= 2; y++) {
                        for (int z = -fertilizeRadius; z <= fertilizeRadius; z++) {
                            if (x * x + z * z <= fertilizeRadius * fertilizeRadius) {
                                BlockPos targetPos = centerPos.offset(x, y, z);
                                BlockState state = serverLevel.getBlockState(targetPos);
                                if (state.getBlock() instanceof BonemealableBlock bonemealable && bonemealable.isValidBonemealTarget(serverLevel, targetPos, state)) {
                                    if (bonemealable.isBonemealSuccess(serverLevel, serverLevel.getRandom(), targetPos, state)) {
                                        bonemealable.performBonemeal(serverLevel, serverLevel.getRandom(), targetPos, state);
                                        serverLevel.levelEvent(1505, targetPos, 15);
                                    }
                                }
                            }
                        }
                    }
                }

                BonePileEntity pile = new BonePileEntity(ModEntityTypes.BONE_PILE.get(), serverLevel);
                pile.setPos(this.getX(), this.getY(), this.getZ());
                pile.setYRot(this.getYRot());
                pile.setDiscItem(this.getDiscItem());
                serverLevel.addFreshEntity(pile);
            }

            this.discard();
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.hasDisc()) {
                this.spawnAtLocation(serverLevel, this.getDiscItem().copy());
            }
        }
    }
}
