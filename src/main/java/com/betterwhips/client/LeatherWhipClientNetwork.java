package com.betterwhips.client;

import com.betterwhips.network.LeatherWhipNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import com.betterwhips.network.WhipNetwork;

public final class LeatherWhipClientNetwork {
    private LeatherWhipClientNetwork() {}

    public static void handleAttackSpeedStacks(LeatherWhipNetwork.AttackSpeedStacksPayload payload,
                                               WhipNetwork.Context context) {

    }

    public static void handleHitGlowBurst(LeatherWhipNetwork.HitGlowBurstPayload payload,
                                          WhipNetwork.Context context) {
        context.enqueueWork(() -> LeatherWhipHitEffects.spawnBurst(
                payload.x(), payload.y(), payload.z(), payload.seed()));
    }

    public static void handleShockwave(LeatherWhipNetwork.WhipShockwavePayload payload,
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
