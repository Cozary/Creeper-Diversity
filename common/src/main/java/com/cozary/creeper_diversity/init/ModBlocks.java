package com.cozary.creeper_diversity.init;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.block.FragileFrostedIceBlock;
import com.cozary.creeper_diversity.block.MudLayerBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {

    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, CreeperDiversity.MOD_ID);

    public static final RegistryObject<Block> MUD_LAYER = BLOCKS.register("mud_layer", () ->
            new MudLayerBlock(
                    BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "mud_layer")))
                            .replaceable()
                            .noCollision()
                            .strength(0.1F)
                            .sound(SoundType.MUD)
                            .randomTicks()
                            .noOcclusion()
                            .pushReaction(PushReaction.DESTROY)
            )
    );

    public static final RegistryObject<Block> FRAGILE_FROSTED_ICE = BLOCKS.register("fragile_frosted_ice", () ->
            new FragileFrostedIceBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.FROSTED_ICE)
                            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "fragile_frosted_ice")))
                            .mapColor(MapColor.ICE)
                            .friction(0.98F)
                            .strength(0.5F)
                            .sound(SoundType.GLASS)
                            .randomTicks()
                            .noOcclusion()
                            .pushReaction(PushReaction.DESTROY)
            )
    );

    public static void loadClass() {
    }
}
