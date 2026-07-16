package com.cozary.creeper_diversity.init;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface RegistryObject<T> extends Supplier<T> {

    ResourceKey<T> getResourceKey();

    Identifier getId();

    @Override
    T get();

    Holder<T> asHolder();

}
