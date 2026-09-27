
package com.betterwhips.network;

import com.betterwhips.item.WindwhispererWhipCombat;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import com.betterwhips.network.WhipNetwork;




public final class WindwhispererWhipNetwork {
    private static final int WHIP_POINT_COUNT = 51;
    private static final PrecisionAttackPayload PRECISION_ATTACK = PrecisionAttackPayload.empty();

    private WindwhispererWhipNetwork() {
    }

    public static void register() {
        WhipNetwork.registerToServer(PrecisionAttackPayload.class, PrecisionAttackPayload.STREAM_CODEC, WindwhispererWhipNetwork::handlePrecisionAttack);
    }

    public static void sendPrecisionAttack() {
        WhipNetwork.CHANNEL.sendToServer(PRECISION_ATTACK);
    }

    public static void sendPrecisionAttack(Vec3[] points, Vec3[] previous, double substepSeconds, Vec3 handleAxis, Vec3 attackDirection) {
        if (points == null || previous == null || points.length != 51 || previous.length != 51) {
            WindwhispererWhipNetwork.sendPrecisionAttack();
            return;
        }
        WhipNetwork.CHANNEL.sendToServer(new PrecisionAttackPayload(true, substepSeconds, handleAxis == null ? Vec3.ZERO : handleAxis, attackDirection == null ? Vec3.ZERO : attackDirection, (Vec3[])points.clone(), (Vec3[])previous.clone()));
    }

    public static void sendShockwave(ServerLevel level, Vec3 impact, long seed) {
    }

    public static void sendHitGlowBurst(ServerLevel level, Vec3 position, Vec3 slashDirection, boolean magicMirror, long seed) {
    }

    private static void handlePrecisionAttack(PrecisionAttackPayload payload, WhipNetwork.Context context) {
        Player player = context.player();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            WindwhispererWhipCombat.PrecisionPoseSnapshot snapshot = payload.hasPose() ? new WindwhispererWhipCombat.PrecisionPoseSnapshot(payload.points(), payload.previous(), payload.substepSeconds(), payload.handleAxis(), payload.attackDirection()) : null;
            WindwhispererWhipCombat.tryBeginPrecision(player2, snapshot);
        }
    }

    private static void writeVec3(FriendlyByteBuf buffer, Vec3 value) {
        Vec3 safe = value == null ? Vec3.ZERO : value;
        buffer.writeDouble(safe.x);
        buffer.writeDouble(safe.y);
        buffer.writeDouble(safe.z);
    }

    private static Vec3 readVec3(FriendlyByteBuf buffer) {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public record PrecisionAttackPayload(boolean hasPose, double substepSeconds, Vec3 handleAxis, Vec3 attackDirection, Vec3[] points, Vec3[] previous) {

public static final WhipNetwork.Codec<PrecisionAttackPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeBoolean(payload.hasPose);
            if (!payload.hasPose) {
                return;
            }
            buffer.writeDouble(payload.substepSeconds);
            WindwhispererWhipNetwork.writeVec3(buffer, payload.handleAxis);
            WindwhispererWhipNetwork.writeVec3(buffer, payload.attackDirection);
            for (int i = 0; i < 51; ++i) {
                WindwhispererWhipNetwork.writeVec3(buffer, payload.points[i]);
                WindwhispererWhipNetwork.writeVec3(buffer, payload.previous[i]);
            }
        }, buffer -> {
            if (!buffer.readBoolean()) {
                return PrecisionAttackPayload.empty();
            }
            double dt = buffer.readDouble();
            Vec3 axis = WindwhispererWhipNetwork.readVec3(buffer);
            Vec3 direction = WindwhispererWhipNetwork.readVec3(buffer);
            Vec3[] points = new Vec3[51];
            Vec3[] previous = new Vec3[51];
            for (int i = 0; i < 51; ++i) {
                points[i] = WindwhispererWhipNetwork.readVec3(buffer);
                previous[i] = WindwhispererWhipNetwork.readVec3(buffer);
            }
            return new PrecisionAttackPayload(true, dt, axis, direction, points, previous);
        });

        static PrecisionAttackPayload empty() {
            return new PrecisionAttackPayload(false, 0.0, Vec3.ZERO, Vec3.ZERO, new Vec3[0], new Vec3[0]);
        }

        
    }
}

