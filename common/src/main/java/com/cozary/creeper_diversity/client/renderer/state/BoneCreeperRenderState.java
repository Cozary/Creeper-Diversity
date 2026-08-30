package com.cozary.creeper_diversity.client.renderer.state;

import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class BoneCreeperRenderState extends CreeperRenderState {
    public final ItemStackRenderState discRenderState = new ItemStackRenderState();
    public boolean hasDisc;
}
