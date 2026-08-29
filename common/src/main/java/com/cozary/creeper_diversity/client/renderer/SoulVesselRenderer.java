package com.cozary.creeper_diversity.client.renderer;

import com.cozary.creeper_diversity.client.renderer.state.SoulVesselRenderState;
import com.cozary.creeper_diversity.entity.SoulVesselEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.UUID;

public class SoulVesselRenderer extends EntityRenderer<SoulVesselEntity, SoulVesselRenderState> {
    private static final Identifier DEFAULT_SKIN = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

    private final PlayerModel wideModel;
    private final PlayerModel slimModel;

    public SoulVesselRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.wideModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false);
        this.slimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public SoulVesselRenderState createRenderState() {
        return new SoulVesselRenderState();
    }

    @Override
    public void extractRenderState(SoulVesselEntity entity, SoulVesselRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.progress = entity.getProgress(partialTick);
        state.yRot = entity.getYRot();
        state.headYRot = entity.getHeadYRot();
        state.headXRot = entity.getHeadXRot();
        state.isCrouching = entity.isCrouchingVessel();
        state.isGlowing = entity.isCurrentlyGlowing();

        state.helmet = entity.getVesselItem(EquipmentSlot.HEAD);
        state.chestplate = entity.getVesselItem(EquipmentSlot.CHEST);
        state.leggings = entity.getVesselItem(EquipmentSlot.LEGS);
        state.boots = entity.getVesselItem(EquipmentSlot.FEET);
        state.mainHand = entity.getVesselItem(EquipmentSlot.MAINHAND);
        state.offHand = entity.getVesselItem(EquipmentSlot.OFFHAND);

        UUID uuid = entity.getOwnerUUID();
        PlayerSkin resolvedSkin = null;

        if (uuid != null) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null && mc.player.getUUID().equals(uuid)) {
                resolvedSkin = mc.player.getSkin();
            }

            if (resolvedSkin == null && mc.level != null) {
                Player p = mc.level.getPlayerByUUID(uuid);
                if (p instanceof AbstractClientPlayer clientPlayer) {
                    resolvedSkin = clientPlayer.getSkin();
                }
            }

            if (resolvedSkin == null && mc.getConnection() != null) {
                PlayerInfo info = mc.getConnection().getPlayerInfo(uuid);
                if (info != null) {
                    resolvedSkin = info.getSkin();
                }
            }
        }

        if (resolvedSkin == null) {
            resolvedSkin = DefaultPlayerSkin.get(uuid != null ? uuid : UUID.randomUUID());
        }

        state.isSlim = resolvedSkin.model() != null && "SLIM".equalsIgnoreCase(resolvedSkin.model().name());
        state.skinTexture = resolveTextureLocation(resolvedSkin.body() != null ? resolvedSkin.body().id() : null);
        state.capeTexture = resolvedSkin.cape() != null ? resolveTextureLocation(resolvedSkin.cape().id()) : null;
        state.elytraTexture = resolvedSkin.elytra() != null ? resolveTextureLocation(resolvedSkin.elytra().id()) : null;
    }

    private static Identifier resolveTextureLocation(Identifier id) {
        if (id == null) {
            return DEFAULT_SKIN;
        }
        String path = id.getPath();
        if (!path.startsWith("textures/")) {
            path = "textures/" + path;
        }
        if (!path.endsWith(".png")) {
            path = path + ".png";
        }
        return id.withPath(path);
    }

    @Override
    public void submit(SoulVesselRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.yRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501D, 0.0D);

        PlayerModel activeModel = state.isSlim ? this.slimModel : this.wideModel;

        float netHeadYaw = state.headYRot - state.yRot;
        activeModel.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
        activeModel.head.xRot = (state.headXRot * ((float) Math.PI / 180.0F)) * 0.5F + 0.25F;
        activeModel.head.zRot = 0.0F;

        if (state.isCrouching) {
            activeModel.body.xRot = 0.5F;
            activeModel.body.yRot = 0.0F;
            activeModel.body.zRot = 0.0F;
            activeModel.rightArm.xRot = 0.4F;
            activeModel.leftArm.xRot = 0.4F;
            activeModel.rightArm.yRot = 0.0F;
            activeModel.leftArm.yRot = 0.0F;
            activeModel.rightArm.zRot = 0.0F;
            activeModel.leftArm.zRot = 0.0F;
            activeModel.rightLeg.xRot = -1.4F;
            activeModel.leftLeg.xRot = -1.4F;
            activeModel.rightLeg.yRot = 0.3F;
            activeModel.leftLeg.yRot = -0.3F;
            activeModel.head.y = 4.2F;
            activeModel.body.y = 3.2F;
        } else {
            activeModel.body.xRot = 0.0F;
            activeModel.body.yRot = 0.0F;
            activeModel.body.zRot = 0.0F;
            activeModel.leftArm.xRot = 0.0F;
            activeModel.rightArm.xRot = 0.0F;
            activeModel.leftArm.yRot = 0.0F;
            activeModel.rightArm.yRot = 0.0F;
            activeModel.leftArm.zRot = 0.0F;
            activeModel.rightArm.zRot = 0.0F;
            activeModel.leftLeg.xRot = 0.0F;
            activeModel.rightLeg.xRot = 0.0F;
            activeModel.leftLeg.yRot = 0.0F;
            activeModel.rightLeg.yRot = 0.0F;
            activeModel.head.y = 0.0F;
            activeModel.body.y = 0.0F;
        }

        activeModel.hat.xRot = activeModel.head.xRot;
        activeModel.hat.yRot = activeModel.head.yRot;
        activeModel.hat.zRot = activeModel.head.zRot;
        activeModel.hat.y = activeModel.head.y;

        activeModel.jacket.xRot = activeModel.body.xRot;
        activeModel.jacket.yRot = activeModel.body.yRot;
        activeModel.jacket.zRot = activeModel.body.zRot;
        activeModel.jacket.y = activeModel.body.y;

        activeModel.leftSleeve.xRot = activeModel.leftArm.xRot;
        activeModel.leftSleeve.yRot = activeModel.leftArm.yRot;
        activeModel.leftSleeve.zRot = activeModel.leftArm.zRot;

        activeModel.rightSleeve.xRot = activeModel.rightArm.xRot;
        activeModel.rightSleeve.yRot = activeModel.rightArm.yRot;
        activeModel.rightSleeve.zRot = activeModel.rightArm.zRot;

        activeModel.leftPants.xRot = activeModel.leftLeg.xRot;
        activeModel.leftPants.yRot = activeModel.leftLeg.yRot;
        activeModel.leftPants.zRot = activeModel.leftLeg.zRot;

        activeModel.rightPants.xRot = activeModel.rightLeg.xRot;
        activeModel.rightPants.yRot = activeModel.rightLeg.yRot;
        activeModel.rightPants.zRot = activeModel.rightLeg.zRot;

        activeModel.hat.visible = true;
        activeModel.jacket.visible = true;
        activeModel.leftSleeve.visible = true;
        activeModel.rightSleeve.visible = true;
        activeModel.leftPants.visible = true;
        activeModel.rightPants.visible = true;

        Identifier texture = state.skinTexture != null
                ? state.skinTexture
                : Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

        nodeCollector.order(0).submitModelPart(
                activeModel.root(),
                poseStack,
                RenderTypes.entityCutout(texture),
                15728880,
                OverlayTexture.NO_OVERLAY,
                null,
                0xFFFFFFFF,
                null
        );

        if (state.isGlowing) {
            nodeCollector.order(1).submitModelPart(
                    activeModel.root(),
                    poseStack,
                    RenderTypes.outline(texture),
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    null,
                    0xFF8B0000,
                    null
            );
        }

        poseStack.popPose();
        super.submit(state, poseStack, nodeCollector, cameraRenderState);
    }
}
