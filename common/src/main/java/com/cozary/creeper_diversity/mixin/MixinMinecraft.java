package com.cozary.creeper_diversity.mixin;

import com.cozary.creeper_diversity.CreeperDiversity;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    
    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        
        CreeperDiversity.LOG.info("This line is printed by an example mod common mixin!");
        CreeperDiversity.LOG.info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }
}