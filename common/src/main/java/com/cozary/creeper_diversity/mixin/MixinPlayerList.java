package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.entity.SoulVesselEntity;
import com.cozary.creeper_diversity.init.ModMobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Shadow
    protected abstract void save(ServerPlayer player);

    @Inject(method = "remove", at = @At("HEAD"))
    private void handleDisconnectWhenSoulDetached(ServerPlayer player, CallbackInfo ci) {
        if (player.hasEffect(ModMobEffects.SOUL_DETACHED.asHolder())) {
            SoulVesselEntity.triggerSoulShockOnDisconnect(player);
            this.save(player);
        }
    }
}
