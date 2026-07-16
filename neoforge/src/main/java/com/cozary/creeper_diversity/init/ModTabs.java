package com.cozary.creeper_diversity.init;

import com.cozary.creeper_diversity.CreeperDiversity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreeperDiversity.MOD_ID);

    public static final Supplier<CreativeModeTab> CREEPER_DIVERSITY_TAB = CREATIVE_MODE_TAB.register("creeper_diversity", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.creeper_diversity"))
            .icon(() -> new ItemStack(ModItems.MINI_CREEPER_SPAWN_EGG.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.MINI_CREEPER_SPAWN_EGG.get());
            })
            .build());

    public static void init(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
