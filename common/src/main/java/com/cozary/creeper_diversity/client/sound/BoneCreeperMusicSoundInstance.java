package com.cozary.creeper_diversity.client.sound;

import com.cozary.creeper_diversity.entity.BoneCreeperEntity;
import com.cozary.creeper_diversity.entity.BonePileEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BoneCreeperMusicSoundInstance extends AbstractTickableSoundInstance {
    public static final float BASE_PITCH = 0.70F;
    public static final float WOBBLE_SPEED = 0.08F;
    public static final float WOBBLE_AMOUNT = 0.06F;

    public static float calculatePlaybackSpeed(float ticks, float swelling) {
        float base = BASE_PITCH + Mth.sin(ticks * WOBBLE_SPEED) * WOBBLE_AMOUNT;
        return base + (swelling * swelling) * 1.5F;
    }

    private BoneCreeperEntity creeper;
    private BonePileEntity bonePile;
    private int ticks = 0;
    private int pileCheckGraceTicks = 0;

    public BoneCreeperMusicSoundInstance(BoneCreeperEntity creeper, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.RECORDS, RandomSource.create());
        this.creeper = creeper;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.85F;
        this.pitch = BASE_PITCH;
        this.x = creeper.getX();
        this.y = creeper.getY();
        this.z = creeper.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return (this.creeper != null && !this.creeper.isSilent()) || this.bonePile != null;
    }

    public boolean isTracking(BoneCreeperEntity entity) {
        if (this.creeper == entity) return true;
        if (this.isStopped()) return false;
        double dx = this.x - entity.getX();
        double dy = this.y - entity.getY();
        double dz = this.z - entity.getZ();
        return (dx * dx + dy * dy + dz * dz) <= 9.0D;
    }

    @Override
    public void tick() {
        // 1. Active Bone Creeper phase
        if (this.creeper != null && !this.creeper.isRemoved() && this.creeper.isAlive() && this.creeper.hasDisc()) {
            this.x = this.creeper.getX();
            this.y = this.creeper.getY();
            this.z = this.creeper.getZ();
            this.volume = 0.85F;
            this.ticks++;
            float swelling = this.creeper.getSwelling(0.0F);
            this.pitch = calculatePlaybackSpeed(this.ticks, swelling);
            this.bonePile = null;
            this.pileCheckGraceTicks = 0;
            return;
        }

        // 2. Creeper was removed / exploded: check for Bone Pile
        if (this.creeper != null && (this.creeper.isRemoved() || !this.creeper.isAlive())) {
            if (this.bonePile == null || this.bonePile.isRemoved() || !this.bonePile.isAlive()) {
                // Search for bone pile at last known location
                AABB searchBox = new AABB(this.x - 2.5D, this.y - 2.5D, this.z - 2.5D, this.x + 2.5D, this.y + 2.5D, this.z + 2.5D);
                List<BonePileEntity> piles = this.creeper.level().getEntitiesOfClass(BonePileEntity.class, searchBox, p -> p.isAlive() && !p.isRemoved());
                if (!piles.isEmpty()) {
                    this.bonePile = piles.get(0);
                    this.creeper = null;
                } else {
                    this.pileCheckGraceTicks++;
                    if (this.pileCheckGraceTicks > 10) {
                        this.stop();
                        return;
                    }
                }
            }
        }

        // 3. Bone Pile phase: mute sound while waiting for resurrection
        if (this.bonePile != null) {
            if (this.bonePile.isRemoved() || !this.bonePile.isAlive()) {
                // Bone pile just ended: check if revived Bone Creeper spawned
                AABB searchBox = new AABB(this.x - 2.5D, this.y - 2.5D, this.z - 2.5D, this.x + 2.5D, this.y + 2.5D, this.z + 2.5D);
                List<BoneCreeperEntity> revived = this.bonePile.level().getEntitiesOfClass(BoneCreeperEntity.class, searchBox, c -> c.isAlive() && !c.isRemoved() && c.hasDisc());
                if (!revived.isEmpty()) {
                    this.creeper = revived.get(0);
                    this.bonePile = null;
                    this.volume = 0.85F;
                    this.x = this.creeper.getX();
                    this.y = this.creeper.getY();
                    this.z = this.creeper.getZ();
                    return;
                } else {
                    // Pile destroyed by player or discarded without resurrection
                    this.stop();
                    return;
                }
            } else {
                // Still in pile phase: completely mute sound while audio continues progressing
                this.volume = 0.0F;
                this.x = this.bonePile.getX();
                this.y = this.bonePile.getY();
                this.z = this.bonePile.getZ();
            }
        }
    }
}
