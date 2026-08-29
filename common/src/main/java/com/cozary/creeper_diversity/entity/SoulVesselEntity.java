package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModDamageTypes;
import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SoulVesselEntity extends Entity {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoulVesselEntity.class);
    public static final int DEFAULT_MAX_TICKS = 200;

    private static final EntityDataAccessor<String> DATA_OWNER_UUID = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_OWNER_NAME = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_TICKS_REMAINING = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MAX_TICKS = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_CROUCHING = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_HEAD_Y_ROT = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEAD_X_ROT = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<ItemStack> DATA_HELMET = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_CHESTPLATE = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_LEGGINGS = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_BOOTS = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_MAINHAND = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<ItemStack> DATA_OFFHAND = SynchedEntityData.defineId(SoulVesselEntity.class, EntityDataSerializers.ITEM_STACK);

    private NonNullList<ItemStack> savedInventory = null;
    private boolean inventoryRestored = false;
    private float savedHealth = 20.0F;

    public SoulVesselEntity(EntityType<? extends SoulVesselEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public SoulVesselEntity(EntityType<? extends SoulVesselEntity> entityType, Level level, ServerPlayer owner) {
        this(entityType, level);
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        this.setYRot(owner.getYRot());
        this.setXRot(owner.getXRot());
        this.setOwnerUUID(owner.getUUID());
        this.setOwnerName(owner.getName().getString());
        this.setCustomName(owner.getDisplayName());
        this.setCustomNameVisible(true);

        this.savedHealth = owner.getHealth();

        this.entityData.set(DATA_CROUCHING, owner.isCrouching());
        this.entityData.set(DATA_HEAD_Y_ROT, owner.getYHeadRot());
        this.entityData.set(DATA_HEAD_X_ROT, owner.getXRot());

        this.entityData.set(DATA_HELMET, owner.getItemBySlot(EquipmentSlot.HEAD).copy());
        this.entityData.set(DATA_CHESTPLATE, owner.getItemBySlot(EquipmentSlot.CHEST).copy());
        this.entityData.set(DATA_LEGGINGS, owner.getItemBySlot(EquipmentSlot.LEGS).copy());
        this.entityData.set(DATA_BOOTS, owner.getItemBySlot(EquipmentSlot.FEET).copy());
        this.entityData.set(DATA_MAINHAND, owner.getItemBySlot(EquipmentSlot.MAINHAND).copy());
        this.entityData.set(DATA_OFFHAND, owner.getItemBySlot(EquipmentSlot.OFFHAND).copy());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_OWNER_UUID, "");
        builder.define(DATA_OWNER_NAME, "");
        builder.define(DATA_TICKS_REMAINING, DEFAULT_MAX_TICKS);
        builder.define(DATA_MAX_TICKS, DEFAULT_MAX_TICKS);
        builder.define(DATA_CROUCHING, false);
        builder.define(DATA_HEAD_Y_ROT, 0.0F);
        builder.define(DATA_HEAD_X_ROT, 0.0F);

        builder.define(DATA_HELMET, ItemStack.EMPTY);
        builder.define(DATA_CHESTPLATE, ItemStack.EMPTY);
        builder.define(DATA_LEGGINGS, ItemStack.EMPTY);
        builder.define(DATA_BOOTS, ItemStack.EMPTY);
        builder.define(DATA_MAINHAND, ItemStack.EMPTY);
        builder.define(DATA_OFFHAND, ItemStack.EMPTY);
    }

    public void setMaxTicks(int maxTicks) {
        this.entityData.set(DATA_MAX_TICKS, maxTicks);
        this.entityData.set(DATA_TICKS_REMAINING, maxTicks);
    }

    public int getMaxTicks() {
        return this.entityData.get(DATA_MAX_TICKS);
    }

    public void capturePlayerInventory(ServerPlayer player) {
        int size = player.getInventory().getContainerSize();
        this.savedInventory = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            this.savedInventory.set(i, player.getInventory().getItem(i).copy());
        }
        player.getInventory().clearContent();
        player.inventoryMenu.broadcastChanges();
    }

    public void restorePlayerInventory(ServerPlayer player) {
        if (this.savedInventory != null && !this.inventoryRestored) {
            this.inventoryRestored = true;

            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack current = player.getInventory().getItem(i);
                if (!current.isEmpty()) {
                    Containers.dropItemStack(this.level(), player.getX(), player.getY(), player.getZ(), current.copy());
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }

            for (int i = 0; i < this.savedInventory.size(); i++) {
                if (i < player.getInventory().getContainerSize()) {
                    player.getInventory().setItem(i, this.savedInventory.get(i).copy());
                }
            }
            this.savedInventory.clear();
            player.inventoryMenu.broadcastChanges();
        }
    }

    public boolean isCrouchingVessel() {
        return this.entityData.get(DATA_CROUCHING);
    }

    public float getHeadYRot() {
        return this.entityData.get(DATA_HEAD_Y_ROT);
    }

    public float getHeadXRot() {
        return this.entityData.get(DATA_HEAD_X_ROT);
    }

    public ItemStack getVesselItem(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> this.entityData.get(DATA_HELMET);
            case CHEST -> this.entityData.get(DATA_CHESTPLATE);
            case LEGS -> this.entityData.get(DATA_LEGGINGS);
            case FEET -> this.entityData.get(DATA_BOOTS);
            case MAINHAND -> this.entityData.get(DATA_MAINHAND);
            case OFFHAND -> this.entityData.get(DATA_OFFHAND);
            default -> ItemStack.EMPTY;
        };
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID, uuid != null ? uuid.toString() : "");
    }

    public String getOwnerUUIDString() {
        return this.entityData.get(DATA_OWNER_UUID);
    }

    public UUID getOwnerUUID() {
        String uuidStr = this.getOwnerUUIDString();
        if (uuidStr == null || uuidStr.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setOwnerName(String name) {
        this.entityData.set(DATA_OWNER_NAME, name != null ? name : "");
    }

    public String getOwnerName() {
        return this.entityData.get(DATA_OWNER_NAME);
    }

    public int getTicksRemaining() {
        return this.entityData.get(DATA_TICKS_REMAINING);
    }

    public float getProgress(float partialTick) {
        float remaining = (float) this.getTicksRemaining() - partialTick;
        int max = Math.max(1, this.getMaxTicks());
        return Math.max(0.0F, Math.min(1.0F, remaining / (float) max));
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return true;
    }

    @Override
    public int getTeamColor() {
        return 0x8B0000;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            int remaining = this.getTicksRemaining() - 1;
            this.entityData.set(DATA_TICKS_REMAINING, remaining);

            if (this.level() instanceof ServerLevel serverLevel) {
                UUID ownerUuid = this.getOwnerUUID();
                if (ownerUuid == null) {
                    this.discard();
                    return;
                }

                ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(ownerUuid);
                if (player == null || !player.isAlive()) {
                    if (player != null) {
                        restorePlayerInventory(player);
                    }
                    this.discard();
                    return;
                }

                spawnTetherParticles(serverLevel, player);

                if (this.tickCount >= 30) {
                    double distSq = this.distanceToSqr(player);
                    if (distSq <= 2.5D) {

                        restorePlayerInventory(player);
                        player.removeEffect(ModMobEffects.SOUL_DETACHED.asHolder());

                        player.setHealth(Math.min(player.getMaxHealth(), Math.max(1.0F, this.savedHealth)));
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2));
                        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0));
                        player.sendSystemMessage(Component.translatable("message.creeper_diversity.soul_reunited"), true);

                        serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.9D, this.getZ(), 20, 0.4D, 0.6D, 0.4D, 0.08D);
                        serverLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY() + 1.2D, this.getZ(), 5, 0.3D, 0.3D, 0.3D, 0.02D);
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.2F, 1.6F);
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BREATH, SoundSource.PLAYERS, 1.5F, 1.0F);

                        this.discard();
                        return;
                    }
                }

                if (remaining <= 0) {
                    restorePlayerInventory(player);
                    player.teleportTo(this.getX(), this.getY(), this.getZ());
                    player.removeEffect(ModMobEffects.SOUL_DETACHED.asHolder());
                    player.invulnerableTime = 0;
                    player.setHealth(Math.min(player.getMaxHealth(), Math.max(1.0F, this.savedHealth)));
                    player.hurtServer(serverLevel, ModDamageTypes.soulDetached(serverLevel), 6.0F);
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120, 0));
                    player.sendSystemMessage(Component.translatable("message.creeper_diversity.soul_shock"), true);

                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.9D, this.getZ(), 25, 0.5D, 0.8D, 0.5D, 0.1D);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.5F, 0.8F);
                    serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.5F);

                    this.discard();
                }
            }
        } else {
            double px = this.getX() + (this.random.nextDouble() - 0.5D) * 0.6D;
            double py = this.getY() + this.random.nextDouble() * 1.6D;
            double pz = this.getZ() + (this.random.nextDouble() - 0.5D) * 0.6D;
            this.level().addParticle(ParticleTypes.SOUL, px, py, pz, 0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.inventoryRestored && this.savedInventory != null && !this.level().isClientSide()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                UUID ownerUuid = this.getOwnerUUID();
                ServerPlayer player = ownerUuid != null ? serverLevel.getServer().getPlayerList().getPlayer(ownerUuid) : null;
                if (player != null && player.isAlive()) {
                    restorePlayerInventory(player);
                } else {
                    for (ItemStack stack : this.savedInventory) {
                        if (!stack.isEmpty()) {
                            Containers.dropItemStack(this.level(), this.getX(), this.getY(), this.getZ(), stack);
                        }
                    }
                }
            }
        }
        super.remove(reason);
    }

    private void spawnTetherParticles(ServerLevel serverLevel, ServerPlayer player) {
        Vec3 vesselPos = new Vec3(this.getX(), this.getY() + 0.8D, this.getZ());
        serverLevel.sendParticles(
            new TrailParticleOption(vesselPos, 0x34EBE5, 12),
            player.getX() + (this.random.nextDouble() - 0.5D) * 0.4D,
            player.getY() + 0.5D + (this.random.nextDouble() - 0.5D) * 0.4D,
            player.getZ() + (this.random.nextDouble() - 0.5D) * 0.4D,
            2,
            0.05D, 0.05D, 0.05D, 0.0D
        );
    }

    public static void triggerSoulShockOnDisconnect(ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        UUID ownerUuid = player.getUUID();
        SoulVesselEntity targetVessel = null;

        for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof SoulVesselEntity vessel && ownerUuid.equals(vessel.getOwnerUUID())) {
                    targetVessel = vessel;
                    break;
                }
            }
            if (targetVessel != null) {
                break;
            }
        }

        if (targetVessel != null) {
            targetVessel.restorePlayerInventory(player);
            player.setPos(targetVessel.getX(), targetVessel.getY(), targetVessel.getZ());
            targetVessel.discard();
        }

        player.removeEffect(ModMobEffects.SOUL_DETACHED.asHolder());
        player.invulnerableTime = 0;
        player.setHealth(Math.min(player.getMaxHealth(), Math.max(1.0F, targetVessel.savedHealth)));
        player.hurtServer(serverLevel, ModDamageTypes.soulDetached(serverLevel), 6.0F);
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120, 0));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.entityData.set(DATA_OWNER_UUID, input.getString("OwnerUUID").orElse(""));
        this.entityData.set(DATA_OWNER_NAME, input.getString("OwnerName").orElse(""));
        this.savedHealth = input.getFloatOr("SavedHealth", 20.0F);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putString("OwnerUUID", this.getOwnerUUIDString());
        output.putString("OwnerName", this.getOwnerName());
        output.putFloat("SavedHealth", this.savedHealth);
    }
}
