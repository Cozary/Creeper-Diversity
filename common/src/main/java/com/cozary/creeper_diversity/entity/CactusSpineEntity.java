package com.cozary.creeper_diversity.entity;

import com.cozary.creeper_diversity.init.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class CactusSpineEntity extends AbstractArrow {

    public CactusSpineEntity(EntityType<? extends CactusSpineEntity> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(1.5D);
    }

    public CactusSpineEntity(Level level, double x, double y, double z, LivingEntity shooter) {
        super(ModEntityTypes.CACTUS_SPINE.get(), x, y, z, level, new ItemStack(Items.AIR), null);
        if (shooter != null) {
            this.setOwner(shooter);
        }
        this.setBaseDamage(1.5D);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.AIR);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (hitResult.getEntity() instanceof LivingEntity living) {
            living.setStingerCount(living.getStingerCount() + 1);
            if (!this.level().isClientSide()) {
                living.hurt(this.damageSources().cactus(), 2.0F);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}
