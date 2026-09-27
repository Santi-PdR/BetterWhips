package com.betterwhips.client;

import com.betterwhips.network.RoyalSlimeWhipNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import com.betterwhips.network.WhipNetwork;

public final class RoyalSlimeWhipClientNetwork {
    private RoyalSlimeWhipClientNetwork() {}

    public static void handleAttackSpeedStacks(RoyalSlimeWhipNetwork.AttackSpeedStacksPayload payload,
                                               WhipNetwork.Context context) {
        context.enqueueWork(() -> RoyalSlimeWhipHudOverlay.setStacks(payload.stacks()));
    }

    public static void handleHitGlowBurst(RoyalSlimeWhipNetwork.HitGlowBurstPayload payload,
                                          WhipNetwork.Context context) {
        context.enqueueWork(() -> RoyalSlimeWhipHitEffects.spawnBurst(
                payload.x(), payload.y(), payload.z(), payload.seed()));
    }

    public static void handleShockwave(RoyalSlimeWhipNetwork.WhipShockwavePayload payload,
                                       WhipNetwork.Context context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                WhipShockwaveEffects.spawnWhipShockwave(
                        level, payload.x(), payload.y(), payload.z(), payload.seed());
            }
        });
    }

}
