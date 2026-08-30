package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.CreeperDiversity;
import com.cozary.creeper_diversity.client.renderer.layer.BoneCreeperDiscLayer;
import com.cozary.creeper_diversity.client.renderer.state.BoneCreeperRenderState;
import com.cozary.creeper_diversity.entity.BoneCreeperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

import com.cozary.creeper_diversity.client.sound.BoneCreeperMusicSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

import java.util.Map;
import java.util.WeakHashMap;

public class BoneCreeperRenderer extends MobRenderer<BoneCreeperEntity, BoneCreeperRenderState, CreeperModel> {
    private static final Identifier BONE_CREEPER_LOCATION = Identifier.fromNamespaceAndPath(CreeperDiversity.MOD_ID, "textures/entity/creeper/bone_creeper.png");
    private static final Map<BoneCreeperEntity, BoneCreeperMusicSoundInstance> PLAYING_SOUNDS = new WeakHashMap<>();

    private final ItemModelResolver itemModelResolver;

    public BoneCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.itemModelResolver = context.getItemModelResolver();
        this.addLayer(new BoneCreeperDiscLayer(this));
    }

    @Override
    public BoneCreeperRenderState createRenderState() {
        return new BoneCreeperRenderState();
    }

    @Override
    public void extractRenderState(BoneCreeperEntity entity, BoneCreeperRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.swelling = entity.getSwelling(partialTick);
        state.isPowered = entity.isPowered();
        state.hasDisc = entity.hasDisc();

        if (state.hasDisc) {
            this.itemModelResolver.updateForTopItem(state.discRenderState, entity.getDiscItem(), ItemDisplayContext.FIXED, entity.level(), entity, entity.getId());

            BoneCreeperMusicSoundInstance current = PLAYING_SOUNDS.get(entity);
            if (current == null || current.isStopped()) {
                PLAYING_SOUNDS.values().removeIf(sound -> sound == null || sound.isStopped());
                boolean alreadyPlaying = false;
                for (BoneCreeperMusicSoundInstance active : PLAYING_SOUNDS.values()) {
                    if (active != null && !active.isStopped() && active.isTracking(entity)) {
                        PLAYING_SOUNDS.put(entity, active);
                        alreadyPlaying = true;
                        break;
                    }
                }

                if (!alreadyPlaying) {
                    JukeboxSong.fromStack(entity.getDiscItem()).ifPresent(songHolder -> {
                        SoundEvent sound = songHolder.value().soundEvent().value();
                        BoneCreeperMusicSoundInstance newInstance = new BoneCreeperMusicSoundInstance(entity, sound);
                        PLAYING_SOUNDS.put(entity, newInstance);
                        Minecraft.getInstance().getSoundManager().play(newInstance);
                    });
                }
            }
        } else {
            state.discRenderState.clear();
        }
    }

    @Override
    protected void scale(BoneCreeperRenderState state, PoseStack poseStack) {
        float f = state.swelling;
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    protected float getWhiteOverlayProgress(BoneCreeperRenderState state) {
        float f = state.swelling;
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTextureLocation(BoneCreeperRenderState state) {
        return BONE_CREEPER_LOCATION;
    }
}
