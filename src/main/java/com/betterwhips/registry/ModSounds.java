package com.betterwhips.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "better_whips");

    public static final RegistryObject<SoundEvent> LANDING_QUAKE_1 = register("landing_quake_1");
    public static final RegistryObject<SoundEvent> LANDING_QUAKE_2 = register("landing_quake_2");
    public static final RegistryObject<SoundEvent> SHOCKWAVE = register("shockwave");
    public static final RegistryObject<SoundEvent> LIGHTNING_ARC = register("lightning_arc");
    public static final RegistryObject<SoundEvent> WHIP_SWING = register("whip_swing");
    public static final RegistryObject<SoundEvent> WHIP_CRACK = register("whip_crack");
    public static final RegistryObject<SoundEvent> CRYSTAL_WHIP_HIT = register("crystal_whip_hit");
    public static final RegistryObject<SoundEvent> LIGHTNING_WHIP_HIT = register("lightning_whip_hit");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation("better_whips", name)));
    }

    private ModSounds() {}
}
