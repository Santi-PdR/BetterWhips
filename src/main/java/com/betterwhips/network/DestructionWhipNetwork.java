
package com.betterwhips.network;

import com.betterwhips.client.DestructionWhipClientNetwork;
import com.betterwhips.item.DestructionWhipCombat;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import com.betterwhips.network.WhipNetwork;




public final class DestructionWhipNetwork {
    private static final int WHIP_POINT_COUNT = 25;
    private static final PrecisionAttackPayload PRECISION_ATTACK = PrecisionAttackPayload.empty();

    private DestructionWhipNetwork() {
    }

    public static void register() {
        WhipNetwork.registerToServer(PrecisionAttackPayload.class, PrecisionAttackPayload.STREAM_CODEC, DestructionWhipNetwork::handlePrecisionAttack);
        WhipNetwork.registerToClient(WhipShockwavePayload.class, WhipShockwavePayload.STREAM_CODEC, DestructionWhipClientNetwork::handleShockwave);
        WhipNetwork.registerToClient(AttackSpeedStacksPayload.class, AttackSpeedStacksPayload.STREAM_CODEC, DestructionWhipClientNetwork::handleAttackSpeedStacks);
        WhipNetwork.registerToClient(HitGlowBurstPayload.class, HitGlowBurstPayload.STREAM_CODEC, DestructionWhipClientNetwork::handleHitGlowBurst);
        WhipNetwork.registerToClient(BodyLaserPayload.class, BodyLaserPayload.STREAM_CODEC, DestructionWhipClientNetwork::handleBodyLaser);
    }

    public static void sendPrecisionAttack() {
        DestructionWhipNetwork.sendPrecisionAttack(0L, -1, Vec3.ZERO, Vec3.ZERO);
    }

    public static void sendPrecisionAttack(long motionSeed) {
        DestructionWhipNetwork.sendPrecisionAttack(motionSeed, -1, Vec3.ZERO, Vec3.ZERO);
    }

    public static void sendPrecisionAttack(long motionSeed, int targetEntityId, Vec3 targetEyeOffset, Vec3 endEyeOffset) {
        WhipNetwork.CHANNEL.sendToServer(PrecisionAttackPayload.empty(motionSeed, targetEntityId, targetEyeOffset, endEyeOffset));
    }

    public static void sendPrecisionAttack(Vec3[] points, Vec3[] previous, double substepSeconds, Vec3 handleAxis, Vec3 attackDirection, long motionSeed, int targetEntityId, Vec3 targetEyeOffset, Vec3 endEyeOffset) {
        if (points == null || previous == null || points.length != 25 || previous.length != 25) {
            DestructionWhipNetwork.sendPrecisionAttack(motionSeed, targetEntityId, targetEyeOffset, endEyeOffset);
            return;
        }
        Vec3 axis = handleAxis == null ? Vec3.ZERO : handleAxis;
        Vec3 direction = attackDirection == null ? Vec3.ZERO : attackDirection;
        Vec3 targetOffset = targetEyeOffset == null ? Vec3.ZERO : targetEyeOffset;
        Vec3 endOffset = endEyeOffset == null ? Vec3.ZERO : endEyeOffset;
        WhipNetwork.CHANNEL.sendToServer(new PrecisionAttackPayload(true, motionSeed, targetEntityId, targetOffset, endOffset, substepSeconds, axis, direction, (Vec3[])points.clone(), (Vec3[])previous.clone()));
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

    public static void sendBodyLaser(ServerLevel level, ServerPlayer owner, int sourceSegment, LivingEntity target, Vec3 fallbackFrom, Vec3 fallbackTo, float fullWidth, long seed) {
        if (level == null || owner == null || target == null || fallbackFrom == null || fallbackTo == null) {
            return;
        }
        float width = Math.max(0.001f, fullWidth);
        BodyLaserPayload payload = new BodyLaserPayload(owner.getId(), sourceSegment, target.getId(), fallbackFrom.x, fallbackFrom.y, fallbackFrom.z, fallbackTo.x, fallbackTo.y, fallbackTo.z, width, seed);
        Vec3 center = fallbackFrom.lerp(fallbackTo, 0.5);
        for (ServerPlayer player : level.players()) {
            if (!(player.distanceToSqr(center) <= 16384.0)) continue;
            WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }
    }

    private static void handlePrecisionAttack(PrecisionAttackPayload payload, WhipNetwork.Context context) {
        Player player = context.player();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            DestructionWhipCombat.PrecisionPoseSnapshot snapshot = payload.hasPose() ? new DestructionWhipCombat.PrecisionPoseSnapshot(payload.points(), payload.previous(), payload.substepSeconds(), payload.handleAxis(), payload.attackDirection()) : null;
            DestructionWhipCombat.tryBeginPrecision(player2, snapshot, payload.motionSeed(), payload.targetEntityId(), payload.targetEyeOffset(), payload.endEyeOffset());
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

    public record PrecisionAttackPayload(boolean hasPose, long motionSeed, int targetEntityId, Vec3 targetEyeOffset, Vec3 endEyeOffset, double substepSeconds, Vec3 handleAxis, Vec3 attackDirection, Vec3[] points, Vec3[] previous) {

public static final WhipNetwork.Codec<PrecisionAttackPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeBoolean(payload.hasPose);
            buffer.writeLong(payload.motionSeed);
            buffer.writeInt(payload.targetEntityId);
            DestructionWhipNetwork.writeVec3(buffer, payload.targetEyeOffset);
            DestructionWhipNetwork.writeVec3(buffer, payload.endEyeOffset);
            if (!payload.hasPose) {
                return;
            }
            buffer.writeDouble(payload.substepSeconds);
            DestructionWhipNetwork.writeVec3(buffer, payload.handleAxis);
            DestructionWhipNetwork.writeVec3(buffer, payload.attackDirection);
            for (int i = 0; i < 25; ++i) {
                DestructionWhipNetwork.writeVec3(buffer, payload.points[i]);
                DestructionWhipNetwork.writeVec3(buffer, payload.previous[i]);
            }
        }, buffer -> {
            boolean hasPose = buffer.readBoolean();
            long motionSeed = buffer.readLong();
            int targetEntityId = buffer.readInt();
            Vec3 targetEyeOffset = DestructionWhipNetwork.readVec3(buffer);
            Vec3 endEyeOffset = DestructionWhipNetwork.readVec3(buffer);
            if (!hasPose) {
                return PrecisionAttackPayload.empty(motionSeed, targetEntityId, targetEyeOffset, endEyeOffset);
            }
            double substepSeconds = buffer.readDouble();
            Vec3 handleAxis = DestructionWhipNetwork.readVec3(buffer);
            Vec3 attackDirection = DestructionWhipNetwork.readVec3(buffer);
            Vec3[] points = new Vec3[25];
            Vec3[] previous = new Vec3[25];
            for (int i = 0; i < 25; ++i) {
                points[i] = DestructionWhipNetwork.readVec3(buffer);
                previous[i] = DestructionWhipNetwork.readVec3(buffer);
            }
            return new PrecisionAttackPayload(true, motionSeed, targetEntityId, targetEyeOffset, endEyeOffset, substepSeconds, handleAxis, attackDirection, points, previous);
        });

        static PrecisionAttackPayload empty() {
            return PrecisionAttackPayload.empty(0L, -1, Vec3.ZERO, Vec3.ZERO);
        }

        static PrecisionAttackPayload empty(long motionSeed) {
            return PrecisionAttackPayload.empty(motionSeed, -1, Vec3.ZERO, Vec3.ZERO);
        }

        static PrecisionAttackPayload empty(long motionSeed, int targetEntityId, Vec3 targetEyeOffset, Vec3 endEyeOffset) {
            return new PrecisionAttackPayload(false, motionSeed, targetEntityId, targetEyeOffset == null ? Vec3.ZERO : targetEyeOffset, endEyeOffset == null ? Vec3.ZERO : endEyeOffset, 0.0, Vec3.ZERO, Vec3.ZERO, new Vec3[0], new Vec3[0]);
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

    public record BodyLaserPayload(int ownerEntityId, int sourceSegment, int targetEntityId, double fallbackFromX, double fallbackFromY, double fallbackFromZ, double fallbackToX, double fallbackToY, double fallbackToZ, float fullWidth, long seed) {

public static final WhipNetwork.Codec<BodyLaserPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeVarInt(payload.ownerEntityId);
            buffer.writeVarInt(payload.sourceSegment);
            buffer.writeVarInt(payload.targetEntityId);
            buffer.writeDouble(payload.fallbackFromX);
            buffer.writeDouble(payload.fallbackFromY);
            buffer.writeDouble(payload.fallbackFromZ);
            buffer.writeDouble(payload.fallbackToX);
            buffer.writeDouble(payload.fallbackToY);
            buffer.writeDouble(payload.fallbackToZ);
            buffer.writeFloat(payload.fullWidth);
            buffer.writeLong(payload.seed);
        }, buffer -> new BodyLaserPayload(buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat(), buffer.readLong()));

        
    }
}

