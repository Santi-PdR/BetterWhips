package com.betterwhips.client;

import com.betterwhips.network.DiamondBladeWhipNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import com.betterwhips.network.WhipNetwork;

public final class DiamondBladeWhipClientNetwork {
    private DiamondBladeWhipClientNetwork() {}

    public static void handleAttackSpeedStacks(DiamondBladeWhipNetwork.AttackSpeedStacksPayload payload,
                                               WhipNetwork.Context context) {

    }

    public static void handleHitGlowBurst(DiamondBladeWhipNetwork.HitGlowBurstPayload payload,
                                          WhipNetwork.Context context) {
        context.enqueueWork(() -> DiamondBladeWhipHitEffects.spawnBurst(
                payload.x(), payload.y(), payload.z(),
                payload.dx(), payload.dy(), payload.dz(), payload.magicMirror(), payload.seed()));
    }

    public static void handleShockwave(DiamondBladeWhipNetwork.WhipShockwavePayload payload,
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
