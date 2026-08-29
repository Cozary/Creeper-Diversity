package com.cozary.creeper_diversity.client.renderer.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class SoulVesselRenderState extends EntityRenderState {
    public float progress;
    public float yRot;
    public float headYRot;
    public float headXRot;
    public boolean isCrouching;
    public boolean isGlowing;
    public boolean isSlim;
    public Identifier skinTexture;
    public Identifier capeTexture;
    public Identifier elytraTexture;

    public ItemStack helmet = ItemStack.EMPTY;
    public ItemStack chestplate = ItemStack.EMPTY;
    public ItemStack leggings = ItemStack.EMPTY;
    public ItemStack boots = ItemStack.EMPTY;
    public ItemStack mainHand = ItemStack.EMPTY;
    public ItemStack offHand = ItemStack.EMPTY;
}
