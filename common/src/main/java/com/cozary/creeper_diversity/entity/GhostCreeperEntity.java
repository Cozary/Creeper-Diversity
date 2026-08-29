package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModEntityTypes;
import com.cozary.creeper_diversity.init.ModMobEffects;
import com.cozary.creeper_diversity.mixin.CreeperAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class GhostCreeperEntity extends Creeper {

    public GhostCreeperEntity(EntityType<? extends GhostCreeperEntity> type, Level level) {
        super(type, level);
        this.moveControl = new GhostMoveControl(this);
        this.setNoGravity(true);

        CreeperAccessor accessor = (CreeperAccessor) this;
        accessor.setMaxSwell(30);
        accessor.setExplosionRadius(0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GhostSwellGoal(this));
        this.goalSelector.addGoal(2, new GhostChargeGoal(this));
        this.goalSelector.addGoal(3, new GhostFloatAroundGoal(this));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new GhostTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.FLYING_SPEED, 0.45D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return true;
    }

    @Override
    public int getTeamColor() {
        return 0xD8F8FF;
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (this.getSwellDir() > 0) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.65D));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            AABB touchBox = this.getBoundingBox().inflate(0.1D);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, touchBox, p -> !p.isSpectator() && !p.isCreative() && p.isAlive());
            for (Player player : players) {
                player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 140, 0, false, true, true));
                if (this.tickCount % 20 == 0) {
                    this.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 0.8F, 1.2F);
                }
            }
        } else {
            if (this.random.nextFloat() < 0.3F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.7D;
                double py = this.getY() + this.random.nextDouble() * 1.5D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.7D;
                this.level().addParticle(ParticleTypes.SOUL, px, py, pz, 0.0D, 0.02D, 0.0D);
            }
            if (this.random.nextFloat() < 0.15F) {
                double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D;
                double py = this.getY() + this.random.nextDouble() * 1.4D;
                double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D;
                this.level().addParticle(ParticleTypes.SMOKE, px, py, pz, 0.0D, 0.01D, 0.0D);
            }
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() || !this.level().isClientSide()) {
            this.moveRelative(0.02F, travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.80D));
        }
        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {}

    @Override
    public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource source) {
        if (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.FALL) || source.is(DamageTypeTags.IS_FALL)) {
            return true;
        }
        return super.isInvulnerableTo(serverLevel, source);
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {}

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {}

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            this.dead = true;
            boolean powered = this.isPowered();

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(powered ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.5D, this.getZ(), powered ? 100 : 50, 1.2D, 1.2D, 1.2D, 0.12D);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.5D, this.getZ(), powered ? 70 : 35, 1.0D, 1.0D, 1.0D, 0.08D);

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 4.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 3.0F, 0.7F);

                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 0.0F, Level.ExplosionInteraction.NONE);

                double radius = powered ? 10.0D : 7.0D;
                int duration = powered ? 200 : 160;
                AABB blastBox = this.getBoundingBox().inflate(radius);
                List<LivingEntity> affected = this.level().getEntitiesOfClass(LivingEntity.class, blastBox, entity -> entity != this && entity.isAlive() && this.distanceTo(entity) <= radius);

                for (LivingEntity entity : affected) {
                    if (entity instanceof ServerPlayer player && !player.isSpectator()) {

                        if (!player.hasEffect(ModMobEffects.SOUL_DETACHED.asHolder())) {
                            SoulVesselEntity vessel = new SoulVesselEntity(ModEntityTypes.SOUL_VESSEL.get(), serverLevel, player);
                            vessel.capturePlayerInventory(player);

                            float maxHealth = player.getMaxHealth();
                            player.setHealth(maxHealth);
                            int soulDuration = (int) Math.ceil(maxHealth / 2.0F) * 20;
                            vessel.setMaxTicks(soulDuration);

                            serverLevel.addFreshEntity(vessel);
                            player.addEffect(new MobEffectInstance(ModMobEffects.SOUL_DETACHED.asHolder(), soulDuration, 0));

                            Vec3 fromCenter = player.position().subtract(this.position());
                            double dist = fromCenter.length();
                            Vec3 knockDir = dist > 0.001D ? fromCenter.normalize() : new Vec3(0, 1, 0);
                            Vec3 knockMotion = knockDir.scale(powered ? 4.0D : 2.5D).add(0.0D, 0.60D, 0.0D);
                            player.setDeltaMovement(knockMotion);
                            player.hurtMarked = true;
                        }
                    } else {
                        entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, duration, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, 1));
                    }
                }
            }

            this.discard();
        }
    }

    static class GhostMoveControl extends MoveControl {
        private final GhostCreeperEntity ghost;

        public GhostMoveControl(GhostCreeperEntity ghost) {
            super(ghost);
            this.ghost = ghost;
        }

        @Override
        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 targetVec = new Vec3(this.wantedX - this.ghost.getX(), this.wantedY - this.ghost.getY(), this.wantedZ - this.ghost.getZ());
                double dist = targetVec.length();
                if (dist < 0.15D) {
                    this.operation = Operation.WAIT;
                    this.ghost.setDeltaMovement(this.ghost.getDeltaMovement().scale(0.5D));
                } else {
                    double targetSpeed = this.speedModifier * this.ghost.getAttributeValue(Attributes.FLYING_SPEED);
                    if (dist < 2.0D) {
                        targetSpeed *= (dist / 2.0D);
                    }
                    Vec3 targetVelocity = targetVec.scale(targetSpeed / dist);
                    this.ghost.setDeltaMovement(this.ghost.getDeltaMovement().lerp(targetVelocity, 0.35D));

                    Vec3 motion = this.ghost.getDeltaMovement();
                    if (motion.horizontalDistanceSqr() > 0.001D) {
                        this.ghost.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * (180.0F / (float) Math.PI));
                        this.ghost.yBodyRot = this.ghost.getYRot();
                    }
                }
            } else if (this.operation == Operation.WAIT) {
                this.ghost.setDeltaMovement(this.ghost.getDeltaMovement().scale(0.6D));
            }
        }
    }

    static class GhostChargeGoal extends Goal {
        private final GhostCreeperEntity ghost;

        public GhostChargeGoal(GhostCreeperEntity ghost) {
            this.ghost = ghost;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.ghost.getTarget();
            return target != null && target.isAlive() && this.ghost.distanceToSqr(target) > 2.0D;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.ghost.getTarget();
            return target != null && target.isAlive() && this.ghost.distanceToSqr(target) > 2.0D;
        }

        @Override
        public void tick() {
            LivingEntity target = this.ghost.getTarget();
            if (target != null) {
                this.ghost.getMoveControl().setWantedPosition(target.getX(), target.getEyeY() - 0.2D, target.getZ(), 1.0D);
            }
        }
    }

    static class GhostFloatAroundGoal extends Goal {
        private final GhostCreeperEntity ghost;

        public GhostFloatAroundGoal(GhostCreeperEntity ghost) {
            this.ghost = ghost;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MoveControl moveControl = this.ghost.getMoveControl();
            if (!moveControl.hasWanted()) {
                return true;
            } else {
                double dx = moveControl.getWantedX() - this.ghost.getX();
                double dy = moveControl.getWantedY() - this.ghost.getY();
                double dz = moveControl.getWantedZ() - this.ghost.getZ();
                double distSq = dx * dx + dy * dy + dz * dz;
                return distSq < 1.0D || distSq > 3600.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            double targetX = this.ghost.getX() + (double) ((this.ghost.getRandom().nextFloat() * 2.0F - 1.0F) * 10.0F);
            double targetY = this.ghost.getY() + (double) ((this.ghost.getRandom().nextFloat() * 2.0F - 1.0F) * 5.0F);
            double targetZ = this.ghost.getZ() + (double) ((this.ghost.getRandom().nextFloat() * 2.0F - 1.0F) * 10.0F);
            this.ghost.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 0.7D);
        }
    }

    static class GhostSwellGoal extends Goal {
        private final GhostCreeperEntity ghost;
        private LivingEntity target;

        public GhostSwellGoal(GhostCreeperEntity ghost) {
            this.ghost = ghost;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = this.ghost.getTarget();
            return this.ghost.getSwellDir() > 0 || (livingEntity != null && this.ghost.distanceToSqr(livingEntity) < 16.0D);
        }

        @Override
        public void start() {
            this.target = this.ghost.getTarget();
        }

        @Override
        public void stop() {
            this.target = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.target == null || !this.target.isAlive()) {
                this.ghost.setSwellDir(-1);
            } else if (this.ghost.distanceToSqr(this.target) > 36.0D) {
                this.ghost.setSwellDir(-1);
            } else {
                this.ghost.setSwellDir(1);
            }
        }
    }

    static class GhostTargetGoal extends TargetGoal {
        private final GhostCreeperEntity ghost;
        private Player targetPlayer;

        public GhostTargetGoal(GhostCreeperEntity ghost) {
            super(ghost, false);
            this.ghost = ghost;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            double detectionRadius = this.getFollowDistance() * 0.65D;
            this.targetPlayer = this.ghost.level().getNearestPlayer(
                this.ghost.getX(),
                this.ghost.getY(),
                this.ghost.getZ(),
                detectionRadius,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR
            );
            return this.targetPlayer != null && this.targetPlayer.isAlive();
        }

        @Override
        public void start() {
            this.ghost.setTarget(this.targetPlayer);
            super.start();
        }
    }
}
