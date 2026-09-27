package com.betterwhips.registry;

import com.betterwhips.BetterWhipsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModSounds {
    private ModSounds() {}

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterWhipsMod.MOD_ID);

    public static final RegistryObject<SoundEvent> LANDING_QUAKE_1 =
            SOUND_EVENTS.register("landing_quake_1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "landing_quake_1")));
    public static final RegistryObject<SoundEvent> LANDING_QUAKE_2 =
            SOUND_EVENTS.register("landing_quake_2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "landing_quake_2")));
    public static final RegistryObject<SoundEvent> SHOCKWAVE =
            SOUND_EVENTS.register("shockwave", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "shockwave")));
    public static final RegistryObject<SoundEvent> LIGHTNING_ARC =
            SOUND_EVENTS.register("lightning_arc", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "lightning_arc")));
    public static final RegistryObject<SoundEvent> WHIP_SWING =
            SOUND_EVENTS.register("whip_swing", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "whip_swing")));
    public static final RegistryObject<SoundEvent> WHIP_CRACK =
            SOUND_EVENTS.register("whip_crack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "whip_crack")));
    public static final RegistryObject<SoundEvent> CRYSTAL_WHIP_HIT =
            SOUND_EVENTS.register("crystal_whip_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "crystal_whip_hit")));
    public static final RegistryObject<SoundEvent> LIGHTNING_WHIP_HIT =
            SOUND_EVENTS.register("lightning_whip_hit", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BetterWhipsMod.MOD_ID, "lightning_whip_hit")));
}
