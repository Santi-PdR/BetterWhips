package com.betterwhips.registry;

import com.betterwhips.item.AmethystWhipItem;
import com.betterwhips.item.ChainWhipItem;
import com.betterwhips.item.DestructionWhipItem;
import com.betterwhips.item.DiamondBladeWhipItem;
import com.betterwhips.item.EmeraldWhipItem;
import com.betterwhips.item.GoldenHeavyWhipItem;
import com.betterwhips.item.LeatherWhipItem;
import com.betterwhips.item.LightningWhipItem;
import com.betterwhips.item.RoyalSlimeWhipItem;
import com.betterwhips.item.SeaRippleWhipItem;
import com.betterwhips.item.TrainerWhipItem;
import com.betterwhips.item.WindwhispererWhipItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "better_whips");
    public static final RegistryObject<RoyalSlimeWhipItem> ROYAL_SLIME_WHIP = ITEMS.register("royal_slime_whip", () -> new RoyalSlimeWhipItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final RegistryObject<LeatherWhipItem> LEATHER_WHIP = ITEMS.register("leather_whip", () -> new LeatherWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LightningWhipItem> LIGHTNING_WHIP = ITEMS.register("lightning_whip", () -> new LightningWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<ChainWhipItem> CHAIN_WHIP = ITEMS.register("chain_whip", () -> new ChainWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<GoldenHeavyWhipItem> GOLDEN_HEAVY_WHIP = ITEMS.register("golden_heavy_whip", () -> new GoldenHeavyWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DiamondBladeWhipItem> DIAMOND_BLADE_WHIP = ITEMS.register("diamond_blade_whip", () -> new DiamondBladeWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<EmeraldWhipItem> EMERALD_WHIP = ITEMS.register("emerald_whip", () -> new EmeraldWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<AmethystWhipItem> AMETHYST_WHIP = ITEMS.register("amethyst_whip", () -> new AmethystWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<TrainerWhipItem> TRAINER_WHIP = ITEMS.register("trainer_whip", () -> new TrainerWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<SeaRippleWhipItem> UNTAMED_SEA_WHIP = ITEMS.register("untamed_sea_whip", () -> new SeaRippleWhipItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<DestructionWhipItem> DESTRUCTION_WHIP = ITEMS.register("destruction_whip", () -> new DestructionWhipItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final RegistryObject<WindwhispererWhipItem> WINDWHISPERER_WHIP = ITEMS.register("windwhisperer_whip", () -> new WindwhispererWhipItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }
}
