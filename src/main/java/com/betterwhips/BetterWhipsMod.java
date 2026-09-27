package com.betterwhips;

import com.betterwhips.registry.ModCreativeTabs;
import com.betterwhips.registry.ModEffects;
import com.betterwhips.registry.ModItems;
import com.betterwhips.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BetterWhipsMod.MOD_ID)
public final class BetterWhipsMod {
    public static final String MOD_ID = "better_whips";

    public BetterWhipsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        ModSounds.SOUND_EVENTS.register(modBus);
        ModEffects.MOB_EFFECTS.register(modBus);
    }
}
