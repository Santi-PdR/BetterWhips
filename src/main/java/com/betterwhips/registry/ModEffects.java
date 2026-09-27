package com.betterwhips.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "better_whips");
    public static final RegistryObject<MobEffect> WATER_PROTECTION_READY =
            MOB_EFFECTS.register("water_protection_ready",
                    () -> new MarkerEffect(MobEffectCategory.BENEFICIAL, 4579316));
    public static final RegistryObject<MobEffect> WATER_PROTECTION_COOLDOWN =
            MOB_EFFECTS.register("water_protection_cooldown",
                    () -> new MarkerEffect(MobEffectCategory.NEUTRAL, 4944258));

    private static final class MarkerEffect extends MobEffect {
        private MarkerEffect(MobEffectCategory category, int color) {
            super(category, color);
        }
    }

    private ModEffects() {}
}
