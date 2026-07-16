package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.mixin.CreeperAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class MiniCreeperEntity extends Creeper {

    public MiniCreeperEntity(EntityType<? extends MiniCreeperEntity> type, Level level) {
        super(type, level);
        CreeperAccessor accessor = (CreeperAccessor) this;
        accessor.setMaxSwell(15);
        accessor.setExplosionRadius(1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    public void explodeCreeper() {
        if (!this.level().isClientSide()) {
            float f = this.isPowered() ? 2.0F : 1.0F;
            this.dead = true;
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.5F * f, Level.ExplosionInteraction.NONE);
            this.discard();
            this.spawnLingeringCloud();
        }
    }
}
