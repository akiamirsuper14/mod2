package com.example.backrooms94.worldgen;

import com.example.backrooms94.Backrooms94;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Backrooms94.MOD_ID);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> TOWN_FEATURE =
            FEATURES.register("town_feature", () -> new TownFeature(NoneFeatureConfiguration.CODEC));

    private ModFeatures() {}
}
