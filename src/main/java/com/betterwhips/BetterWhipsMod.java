package com.betterwhips;

import com.betterwhips.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BetterWhipsMod.MOD_ID)
public final class BetterWhipsMod {
    public static final String MOD_ID = "better_whips";

    public BetterWhipsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
    }
}
