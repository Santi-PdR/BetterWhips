package com.betterwhips.network;

import com.betterwhips.BetterWhipsMod;
import com.betterwhips.client.GoldenHeavyWhipClientNetwork;
import com.betterwhips.item.GoldenHeavyWhipCombat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import com.betterwhips.network.WhipNetwork;

public final class GoldenHeavyWhipNetwork {

    private static final int WHIP_POINT_COUNT = 51;
    private static final PrecisionAttackPayload PRECISION_ATTACK = PrecisionAttackPayload.empty();

    private GoldenHeavyWhipNetwork() {}

    public static void register() {
        WhipNetwork.registerToServer(PrecisionAttackPayload.class, PrecisionAttackPayload.STREAM_CODEC, GoldenHeavyWhipNetwork::handlePrecisionAttack);
        WhipNetwork.registerToClient(WhipShockwavePayload.class, WhipShockwavePayload.STREAM_CODEC, GoldenHeavyWhipClientNetwork::handleShockwave);
        WhipNetwork.registerToClient(AttackSpeedStacksPayload.class, AttackSpeedStacksPayload.STREAM_CODEC, GoldenHeavyWhipClientNetwork::handleAttackSpeedStacks);
        WhipNetwork.registerToClient(HitGlowBurstPayload.class, HitGlowBurstPayload.STREAM_CODEC, GoldenHeavyWhipClientNetwork::handleHitGlowBurst);
    }

    public static void sendPrecisionAttack() {
        WhipNetwork.CHANNEL.sendToServer(PRECISION_ATTACK);
    }

    public static void sendPrecisionAttack(Vec3[] points, Vec3[] previous,
                                           double substepSeconds, Vec3 handleAxis,
                                           Vec3 attackDirection) {
        if (points == null || previous == null
                || points.length != WHIP_POINT_COUNT || previous.length != WHIP_POINT_COUNT) {
            sendPrecisionAttack();
            return;
        }
        Vec3 axis = handleAxis == null ? Vec3.ZERO : handleAxis;
        Vec3 direction = attackDirection == null ? Vec3.ZERO : attackDirection;
        WhipNetwork.CHANNEL.sendToServer(new PrecisionAttackPayload(
                true, substepSeconds, axis, direction, points.clone(), previous.clone()));
    }

    public static void sendShockwave(ServerLevel level, Vec3 impact, long seed) {
        WhipShockwavePayload payload = new WhipShockwavePayload(
                impact.x, impact.y, impact.z, seed);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(impact) <= 256.0D * 256.0D) {
                WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
            }
        }
    }
    public static void sendAttackSpeedStacks(ServerPlayer player, int stacks) {
        if (player == null) {
            return;
        }
        int clamped = Math.max(0, Math.min(5, stacks));
        WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new AttackSpeedStacksPayload(clamped));
    }

    public static void sendHitGlowBurst(ServerLevel level, Vec3 position, long seed) {
        if (level == null || position == null) {
            return;
        }
        HitGlowBurstPayload payload = new HitGlowBurstPayload(position.x, position.y, position.z, seed);
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(position) <= 128.0D * 128.0D) {
                WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
            }
        }
    }

    private static void handlePrecisionAttack(PrecisionAttackPayload payload,
                                              WhipNetwork.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            GoldenHeavyWhipCombat.PrecisionPoseSnapshot snapshot = payload.hasPose()
                    ? new GoldenHeavyWhipCombat.PrecisionPoseSnapshot(
                            payload.points(), payload.previous(), payload.substepSeconds(),
                            payload.handleAxis(), payload.attackDirection())
                    : null;
            GoldenHeavyWhipCombat.tryBeginPrecision(player, snapshot);
        }
    }

    public record PrecisionAttackPayload(boolean hasPose, double substepSeconds, Vec3 handleAxis,
                                         Vec3 attackDirection, Vec3[] points, Vec3[] previous)
            {

        public static final WhipNetwork.Codec<PrecisionAttackPayload> STREAM_CODEC =
                WhipNetwork.Codec.of(
                        (buffer, payload) -> {
                            buffer.writeBoolean(payload.hasPose);
                            if (!payload.hasPose) {
                                return;
                            }
                            buffer.writeDouble(payload.substepSeconds);
                            writeVec3(buffer, payload.handleAxis);
                            writeVec3(buffer, payload.attackDirection);
                            for (int i = 0; i < WHIP_POINT_COUNT; ++i) {
                                writeVec3(buffer, payload.points[i]);
                                writeVec3(buffer, payload.previous[i]);
                            }
                        },
                        buffer -> {
                            if (!buffer.readBoolean()) {
                                return empty();
                            }
                            double substepSeconds = buffer.readDouble();
                            Vec3 handleAxis = readVec3(buffer);
                            Vec3 attackDirection = readVec3(buffer);
                            Vec3[] points = new Vec3[WHIP_POINT_COUNT];
                            Vec3[] previous = new Vec3[WHIP_POINT_COUNT];
                            for (int i = 0; i < WHIP_POINT_COUNT; ++i) {
                                points[i] = readVec3(buffer);
                                previous[i] = readVec3(buffer);
                            }
                            return new PrecisionAttackPayload(
                                    true, substepSeconds, handleAxis, attackDirection, points, previous);
                        });

        static PrecisionAttackPayload empty() {
            return new PrecisionAttackPayload(
                    false, 0.0D, Vec3.ZERO, Vec3.ZERO, new Vec3[0], new Vec3[0]);
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

    public record AttackSpeedStacksPayload(int stacks) {

        public static final WhipNetwork.Codec<AttackSpeedStacksPayload> STREAM_CODEC =
                WhipNetwork.Codec.of(
                        (buffer, payload) -> buffer.writeVarInt(payload.stacks),
                        buffer -> new AttackSpeedStacksPayload(buffer.readVarInt()));
    }

    public record HitGlowBurstPayload(double x, double y, double z, long seed)
            {

        public static final WhipNetwork.Codec<HitGlowBurstPayload> STREAM_CODEC =
                WhipNetwork.Codec.of(
                        (buffer, payload) -> {
                            buffer.writeDouble(payload.x);
                            buffer.writeDouble(payload.y);
                            buffer.writeDouble(payload.z);
                            buffer.writeLong(payload.seed);
                        },
                        buffer -> new HitGlowBurstPayload(
                                buffer.readDouble(), buffer.readDouble(),
                                buffer.readDouble(), buffer.readLong()));
    }

    public record WhipShockwavePayload(double x, double y, double z, long seed)
            {

        public static final WhipNetwork.Codec<WhipShockwavePayload> STREAM_CODEC =
                WhipNetwork.Codec.of(
                        (buffer, payload) -> {
                            buffer.writeDouble(payload.x);
                            buffer.writeDouble(payload.y);
                            buffer.writeDouble(payload.z);
                            buffer.writeLong(payload.seed);
                        },
                        buffer -> new WhipShockwavePayload(
                                buffer.readDouble(), buffer.readDouble(),
                                buffer.readDouble(), buffer.readLong()));
    }
}
