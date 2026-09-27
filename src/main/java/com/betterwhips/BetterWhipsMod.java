package com.betterwhips;

import com.betterwhips.client.BetterWhipsClient;

import com.betterwhips.command.WhipCommands;
import com.betterwhips.item.AmethystWhipCombat;
import com.betterwhips.item.ChainWhipCombat;
import com.betterwhips.item.DestructionWhipCombat;
import com.betterwhips.item.DiamondBladeWhipCombat;
import com.betterwhips.item.EmeraldWhipCombat;
import com.betterwhips.item.GoldenHeavyWhipCombat;
import com.betterwhips.item.LeatherWhipCombat;
import com.betterwhips.item.LightningWhipCombat;
import com.betterwhips.item.LightningWhipTimeStop;
import com.betterwhips.item.RoyalSlimeWhipCombat;
import com.betterwhips.item.TrainerWhipCombat;
import com.betterwhips.item.SeaRippleWhipCombat;
import com.betterwhips.item.SeaRippleWhipProtection;
import com.betterwhips.item.WhipFriendlySupport;
import com.betterwhips.item.WindwhispererWhipCombat;
import com.betterwhips.network.AmethystWhipNetwork;
import com.betterwhips.network.ChainWhipNetwork;
import com.betterwhips.network.DestructionWhipNetwork;
import com.betterwhips.network.DiamondBladeWhipNetwork;
import com.betterwhips.network.EmeraldWhipNetwork;
import com.betterwhips.network.GoldenHeavyWhipNetwork;
import com.betterwhips.network.LeatherWhipNetwork;
import com.betterwhips.network.LightningWhipNetwork;
import com.betterwhips.network.RoyalSlimeWhipNetwork;
import com.betterwhips.network.TrainerWhipNetwork;
import com.betterwhips.network.SeaRippleWhipNetwork;
import com.betterwhips.network.WindwhispererWhipNetwork;
import com.betterwhips.registry.ModCreativeTabs;
import com.betterwhips.registry.ModEffects;
import com.betterwhips.registry.ModItems;
import com.betterwhips.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

@Mod(value="better_whips")
public final class BetterWhipsMod {
    public static final String MOD_ID = "better_whips";

    public BetterWhipsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> BetterWhipsClient::init);
        ModItems.ITEMS.register(modBus);
        ModEffects.MOB_EFFECTS.register(modBus);
        ModSounds.SOUND_EVENTS.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        RoyalSlimeWhipNetwork.register();
        LeatherWhipNetwork.register();
        LightningWhipNetwork.register();
        ChainWhipNetwork.register();
        GoldenHeavyWhipNetwork.register();
        DiamondBladeWhipNetwork.register();
        EmeraldWhipNetwork.register();
        AmethystWhipNetwork.register();
        TrainerWhipNetwork.register();
        SeaRippleWhipNetwork.register();
        DestructionWhipNetwork.register();
        WindwhispererWhipNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(RoyalSlimeWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(LeatherWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(LightningWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(LightningWhipTimeStop::onEntityTickPre);
        MinecraftForge.EVENT_BUS.addListener(ChainWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(GoldenHeavyWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(DiamondBladeWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(EmeraldWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(AmethystWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(TrainerWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(SeaRippleWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(SeaRippleWhipCombat::onAttackEntity);
        MinecraftForge.EVENT_BUS.addListener(SeaRippleWhipCombat::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(SeaRippleWhipProtection::onIncomingDamage);
        MinecraftForge.EVENT_BUS.addListener(SeaRippleWhipProtection::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(SeaRippleWhipProtection::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(DestructionWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(WindwhispererWhipCombat::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(WhipFriendlySupport::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(WhipCommands::register);
    }
}
