package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.CreeperDiversity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        
        CreeperDiversity.LOG.info("This line is printed by an example mod mixin from Fabric!");
        CreeperDiversity.LOG.info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }
}