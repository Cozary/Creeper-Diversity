package com.cozary.creeper_diversity.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FragileFrostedIceBlock extends FrostedIceBlock {

    public static final MapCodec<FragileFrostedIceBlock> CODEC = simpleCodec(FragileFrostedIceBlock::new);

    public FragileFrostedIceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<FrostedIceBlock> codec() {
        return (MapCodec<FrostedIceBlock>) (MapCodec<?>) CODEC;
    }

    @Override
    protected void melt(BlockState state, Level level, BlockPos pos) {
        level.removeBlock(pos, false);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState()),
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 6, 0.2D, 0.2D, 0.2D, 0.05D);
        }
    }
}
