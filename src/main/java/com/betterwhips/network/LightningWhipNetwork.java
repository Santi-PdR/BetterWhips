
package com.betterwhips.network;

import com.betterwhips.client.LightningWhipClientNetwork;
import com.betterwhips.item.LightningWhipCombat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import com.betterwhips.network.WhipNetwork;




public final class LightningWhipNetwork {
    private static final int WHIP_POINT_COUNT = 57;
    private static final PrecisionAttackPayload PRECISION_ATTACK = PrecisionAttackPayload.empty();

    private LightningWhipNetwork() {
    }

    public static void register() {
        WhipNetwork.registerToServer(PrecisionAttackPayload.class, PrecisionAttackPayload.STREAM_CODEC, LightningWhipNetwork::handlePrecisionAttack);
        WhipNetwork.registerToClient(WhipShockwavePayload.class, WhipShockwavePayload.STREAM_CODEC, LightningWhipClientNetwork::handleShockwave);
        WhipNetwork.registerToClient(AttackSpeedStacksPayload.class, AttackSpeedStacksPayload.STREAM_CODEC, LightningWhipClientNetwork::handleAttackSpeedStacks);
        WhipNetwork.registerToClient(HitGlowBurstPayload.class, HitGlowBurstPayload.STREAM_CODEC, LightningWhipClientNetwork::handleHitGlowBurst);
        WhipNetwork.registerToClient(ChainHitPayload.class, ChainHitPayload.STREAM_CODEC, LightningWhipClientNetwork::handleChainHit);
        WhipNetwork.registerToClient(DirectWrapPayload.class, DirectWrapPayload.STREAM_CODEC, LightningWhipClientNetwork::handleDirectWrap);
    }

    public static void sendPrecisionAttack() {
        WhipNetwork.CHANNEL.sendToServer(PRECISION_ATTACK);
    }

    public static void sendPrecisionAttack(Vec3[] points, Vec3[] previous, double substepSeconds, Vec3 handleAxis, Vec3 attackDirection) {
        if (points == null || previous == null || points.length != 57 || previous.length != 57) {
            LightningWhipNetwork.sendPrecisionAttack();
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

    public static void sendHitGlowBurst(ServerLevel level, Vec3 position, long seed) {
        if (level == null || position == null) {
            return;
        }
        HitGlowBurstPayload payload = new HitGlowBurstPayload(position.x, position.y, position.z, seed);
        for (ServerPlayer player : level.players()) {
            if (!(player.distanceToSqr(position) <= 16384.0)) continue;
            WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), payload);
        }
    }

    public static void sendDirectWrap(ServerLevel level, LivingEntity target, long seed) {
        if (level == null || target == null) {
            return;
        }
        DirectWrapPayload payload = new DirectWrapPayload(seed, ArcTarget.of(target));
        Vec3 center = target.getBoundingBox().getCenter();
        for (ServerPlayer viewer : level.players()) {
            if (!(viewer.distanceToSqr(center) <= 16384.0)) continue;
            WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), payload);
        }
    }

    public static void sendArcRoute(ServerLevel level, List<ArcTarget> route, long seed) {
        if (level == null || route == null || route.isEmpty()) {
            return;
        }
        ChainHitPayload payload = new ChainHitPayload(seed, route);
        block0: for (ServerPlayer viewer : level.players()) {
            for (ArcTarget endpoint : payload.targets()) {
                if (viewer.distanceToSqr(endpoint.center()) > 16384.0) continue;
                WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), payload);
                continue block0;
            }
        }
    }

    private static void handlePrecisionAttack(PrecisionAttackPayload payload, WhipNetwork.Context context) {
        Player player = context.player();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            LightningWhipCombat.PrecisionPoseSnapshot snapshot = payload.hasPose() ? new LightningWhipCombat.PrecisionPoseSnapshot(payload.points(), payload.previous(), payload.substepSeconds(), payload.handleAxis(), payload.attackDirection()) : null;
            LightningWhipCombat.tryBeginPrecision(player2, snapshot);
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
            LightningWhipNetwork.writeVec3(buffer, payload.handleAxis);
            LightningWhipNetwork.writeVec3(buffer, payload.attackDirection);
            for (int i = 0; i < 57; ++i) {
                LightningWhipNetwork.writeVec3(buffer, payload.points[i]);
                LightningWhipNetwork.writeVec3(buffer, payload.previous[i]);
            }
        }, buffer -> {
            if (!buffer.readBoolean()) {
                return PrecisionAttackPayload.empty();
            }
            double substepSeconds = buffer.readDouble();
            Vec3 handleAxis = LightningWhipNetwork.readVec3(buffer);
            Vec3 attackDirection = LightningWhipNetwork.readVec3(buffer);
            Vec3[] points = new Vec3[57];
            Vec3[] previous = new Vec3[57];
            for (int i = 0; i < 57; ++i) {
                points[i] = LightningWhipNetwork.readVec3(buffer);
                previous[i] = LightningWhipNetwork.readVec3(buffer);
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

    public record HitGlowBurstPayload(double x, double y, double z, long seed) {

public static final WhipNetwork.Codec<HitGlowBurstPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeDouble(payload.x);
            buffer.writeDouble(payload.y);
            buffer.writeDouble(payload.z);
            buffer.writeLong(payload.seed);
        }, buffer -> new HitGlowBurstPayload(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readLong()));

        
    }

    public record ChainHitPayload(long seed, List<ArcTarget> targets) {

public static final WhipNetwork.Codec<ChainHitPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeLong(payload.seed);
            buffer.writeVarInt(payload.targets.size());
            for (ArcTarget target : payload.targets) {
                buffer.writeVarInt(target.entityId());
                buffer.writeUUID(target.uuid());
                LightningWhipNetwork.writeVec3(buffer, target.center());
                buffer.writeFloat(target.radiusX());
                buffer.writeFloat(target.halfHeight());
                buffer.writeFloat(target.radiusZ());
            }
        }, buffer -> {
            long seed = buffer.readLong();
            int count = buffer.readVarInt();
            if (count != 7) {
                throw new IllegalArgumentException("Invalid electric route endpoint count: " + count);
            }
            ArrayList<ArcTarget> targets = new ArrayList<ArcTarget>(count);
            for (int i = 0; i < count; ++i) {
                targets.add(new ArcTarget(buffer.readVarInt(), buffer.readUUID(), LightningWhipNetwork.readVec3(buffer), buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));
            }
            return new ChainHitPayload(seed, targets);
        });

        public ChainHitPayload {
            targets = List.copyOf(targets);
            if (targets.size() != 7) {
                throw new IllegalArgumentException("Electric route must contain direct source plus six hops");
            }
        }

        
    }

    public record DirectWrapPayload(long seed, ArcTarget target) {

public static final WhipNetwork.Codec<DirectWrapPayload> STREAM_CODEC = WhipNetwork.Codec.of((buffer, payload) -> {
            buffer.writeLong(payload.seed);
            ArcTarget target = payload.target;
            buffer.writeVarInt(target.entityId());
            buffer.writeUUID(target.uuid());
            LightningWhipNetwork.writeVec3(buffer, target.center());
            buffer.writeFloat(target.radiusX());
            buffer.writeFloat(target.halfHeight());
            buffer.writeFloat(target.radiusZ());
        }, buffer -> new DirectWrapPayload(buffer.readLong(), new ArcTarget(buffer.readVarInt(), buffer.readUUID(), LightningWhipNetwork.readVec3(buffer), buffer.readFloat(), buffer.readFloat(), buffer.readFloat())));

        
    }

    public record ArcTarget(int entityId, UUID uuid, Vec3 center, float radiusX, float halfHeight, float radiusZ) {
        public ArcTarget {
            if (uuid == null || center == null || !Double.isFinite(center.x + center.y + center.z) || !Float.isFinite(radiusX + halfHeight + radiusZ) || radiusX <= 0.0f || halfHeight <= 0.0f || radiusZ <= 0.0f) {
                throw new IllegalArgumentException("Invalid electric arc endpoint");
            }
        }

        public static ArcTarget of(LivingEntity entity) {
            AABB box = entity.getBoundingBox();
            return new ArcTarget(entity.getId(), entity.getUUID(), box.getCenter(), Math.max(0.01f, (float)box.getXsize() * 0.5f), Math.max(0.01f, (float)box.getYsize() * 0.5f), Math.max(0.01f, (float)box.getZsize() * 0.5f));
        }
    }
}

