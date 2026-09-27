
package com.betterwhips.network;

import com.betterwhips.client.DiamondBladeWhipClientNetwork;
import com.betterwhips.item.DiamondBladeWhipCombat;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import com.betterwhips.network.WhipNetwork;




public final class DiamondBladeWhipNetwork {
    private static final int WHIP_POINT_COUNT = 25;
    private static final PrecisionAttackPayload PRECISION_ATTACK = PrecisionAttackPayload.empty();

    private DiamondBladeWhipNetwork() {
    }

    public static void register() {
        WhipNetwork.registerToServer(PrecisionAttackPayload.class, PrecisionAttackPayload.STREAM_CODEC, DiamondBladeWhipNetwork::handlePrecisionAttack);
        WhipNetwork.registerToClient(WhipShockwavePayload.class, WhipShockwavePayload.STREAM_CODEC, DiamondBladeWhipClientNetwork::handleShockwave);
        WhipNetwork.registerToClient(AttackSpeedStacksPayload.class, AttackSpeedStacksPayload.STREAM_CODEC, DiamondBladeWhipClientNetwork::handleAttackSpeedStacks);
        WhipNetwork.registerToClient(HitGlowBurstPayload.class, HitGlowBurstPayload.STREAM_CODEC, DiamondBladeWhipClientNetwork::handleHitGlowBurst);
    }

    public static void sendPrecisionAttack() {
        WhipNetwork.CHANNEL.sendToServer(PRECISION_ATTACK);
    }

    public static void sendPrecisionAttack(Vec3[] points, Vec3[] previous, double substepSeconds, Vec3 handleAxis, Vec3 attackDirection) {
        if (points == null || previous == null || points.length != 25 || previous.length != 25) {
            DiamondBladeWhipNetwork.sendPrecisionAttack();
            return;
        }
        Vec3 axis = handleAxis == null ? Vec3.ZERO : handleAxis;
        Vec3 direction = attackDirection == null ? Vec3.ZERO : attackDirection;
        WhipNetwork.CHANNEL.sendToServer(new PrecisionAttackPayload(true, substepSeconds, axis, direction, (Vec3[])points.clone(), (Vec3[])previous.clone()));
    }

    public static void sendShockwave(ServerLevel level, Vec3 impact, long seed) {
        WhipShockwavePayload payload = new WhipShockwavePayload(impact.x, impact.y, impact.z, seed);
        for (ServerPlayer player : level.players()) {
            if (!(player.distanceToSqr(impact) <= 65536.0)) continue;
            WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }
    }

    public static void sendAttackSpeedStacks(ServerPlayer player, int stacks) {
        if (player == null) {
            return;
        }
        int clamped = Math.max(0, Math.min(5, stacks));
        WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new AttackSpeedStacksPayload(clamped));
    }

    public static void sendHitGlowBurst(ServerLevel level, Vec3 position, Vec3 slashDirection, boolean magicMirror, long seed) {
        if (level == null || position == null) {
            return;
        }
        Vec3 direction = slashDirection == null ? Vec3.ZERO : slashDirection;
        HitGlowBurstPayload payload = new HitGlowBurstPayload(position.x, position.y, position.z, direction.x, direction.y, direction.z, magicMirror, seed);
        for (ServerPlayer player : level.players()) {
            if (!(player.distanceToSqr(position) <= 16384.0)) continue;
            WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }
    }

    private static void handlePrecisionAttack(PrecisionAttackPayload payload, WhipNetwork.Context context) {
        Player player = context.player();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            DiamondBladeWhipCombat.PrecisionPoseSnapshot snapshot = payload.hasPose() ? new DiamondBladeWhipCombat.PrecisionPoseSnapshot(payload.points(), payload.previous(), payload.substepSeconds(), payload.handleAxis(), payload.attackDirection()) : null;
            DiamondBladeWhipCombat.tryBeginPrecision(player2, snapshot);
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
            DiamondBladeWhipNetwork.writeVec3(buffer, payload.handleAxis);
            DiamondBladeWhipNetwork.writeVec3(buffer, payload.attackDirection);
            for (int i = 0; i < 25; ++i) {
                DiamondBladeWhipNetwork.writeVec3(buffer, payload.points[i]);
                DiamondBladeWhipNetwork.writeVec3(buffer, payload.previous[i]);
            }
        }, buffer -> {
            if (!buffer.readBoolean()) {
                return PrecisionAttackPayload.empty();
            }
            double substepSeconds = buffer.readDouble();
            Vec3 handleAxis = DiamondBladeWhipNetwork.readVec3(buffer);
            Vec3 attackDirection = DiamondBladeWhipNetwork.readVec3(buffer);
            Vec3[] points = new Vec3[25];
            Vec3[] previous = new Vec3[25];
            for (int i = 0; i < 25; ++i) {
                points[i] = DiamondBladeWhipNetwork.readVec3(buffer);
                previous[i] = DiamondBladeWhipNetwork.readVec3(buffer);
            }
            return new PrecisionAttackPayload(true, substepSeconds, handleAxis, attackDirection, points, previous);
        });

        static PrecisionAttackPayload empty() {
            return new PrecisionAttackPayload(false, 0.0, Vec3.ZERO, Vec3.ZERO, new Vec3[0], new Vec3[0]);
        }

        
    }

    public record WhipShockwavePayload(double x, double y, double z, long seed) {

public static final WhipNetwork.Codec<WhipShockwavePayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeDouble(payload.x);
            buffer.writeDouble(payload.y);
            buffer.writeDouble(payload.z);
            buffer.writeLong(payload.seed);
        }, buffer -> new WhipShockwavePayload(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readLong()));

        
    }

    public record AttackSpeedStacksPayload(int stacks) {

public static final WhipNetwork.Codec<AttackSpeedStacksPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> buffer.writeVarInt(payload.stacks), buffer -> new AttackSpeedStacksPayload(buffer.readVarInt()));

        
    }

    public record HitGlowBurstPayload(double x, double y, double z, double dx, double dy, double dz, boolean magicMirror, long seed) {

public static final WhipNetwork.Codec<HitGlowBurstPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeDouble(payload.x);
            buffer.writeDouble(payload.y);
            buffer.writeDouble(payload.z);
            buffer.writeDouble(payload.dx);
            buffer.writeDouble(payload.dy);
            buffer.writeDouble(payload.dz);
            buffer.writeBoolean(payload.magicMirror);
            buffer.writeLong(payload.seed);
        }, buffer -> new HitGlowBurstPayload(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean(), buffer.readLong()));

        
    }
}

