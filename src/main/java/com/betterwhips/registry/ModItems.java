package com.betterwhips.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "better_whips");

    public static final RegistryObject<Item> ROYAL_SLIME_WHIP = register("royal_slime_whip", true);
    public static final RegistryObject<Item> LEATHER_WHIP = register("leather_whip", false);
    public static final RegistryObject<Item> LIGHTNING_WHIP = register("lightning_whip", false);
    public static final RegistryObject<Item> CHAIN_WHIP = register("chain_whip", false);
    public static final RegistryObject<Item> GOLDEN_HEAVY_WHIP = register("golden_heavy_whip", false);
    public static final RegistryObject<Item> DIAMOND_BLADE_WHIP = register("diamond_blade_whip", false);
    public static final RegistryObject<Item> EMERALD_WHIP = register("emerald_whip", false);
    public static final RegistryObject<Item> AMETHYST_WHIP = register("amethyst_whip", false);
    public static final RegistryObject<Item> TRAINER_WHIP = register("trainer_whip", false);
    public static final RegistryObject<Item> UNTAMED_SEA_WHIP = register("untamed_sea_whip", false);
    public static final RegistryObject<Item> DESTRUCTION_WHIP = register("destruction_whip", true);
    public static final RegistryObject<Item> WINDWHISPERER_WHIP = register("windwhisperer_whip", false);

    private static RegistryObject<Item> register(String name, boolean fireResistant) {
        return ITEMS.register(name, () -> {
            Item.Properties properties = new Item.Properties().stacksTo(1);
            if (fireResistant) properties = properties.fireResistant();
            return new Item(properties);
        });
    }

    private ModItems() {}
}
