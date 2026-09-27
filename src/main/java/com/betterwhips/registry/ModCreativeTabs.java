package com.betterwhips.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "better_whips");

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.better_whips.main"))
                    .icon(() -> new ItemStack(ModItems.ROYAL_SLIME_WHIP.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ROYAL_SLIME_WHIP.get());
                        output.accept(ModItems.LEATHER_WHIP.get());
                        output.accept(ModItems.LIGHTNING_WHIP.get());
                        output.accept(ModItems.CHAIN_WHIP.get());
                        output.accept(ModItems.GOLDEN_HEAVY_WHIP.get());
                        output.accept(ModItems.DIAMOND_BLADE_WHIP.get());
                        output.accept(ModItems.EMERALD_WHIP.get());
                        output.accept(ModItems.AMETHYST_WHIP.get());
                        output.accept(ModItems.TRAINER_WHIP.get());
                        output.accept(ModItems.UNTAMED_SEA_WHIP.get());
                        output.accept(ModItems.DESTRUCTION_WHIP.get());
                        output.accept(ModItems.WINDWHISPERER_WHIP.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}
