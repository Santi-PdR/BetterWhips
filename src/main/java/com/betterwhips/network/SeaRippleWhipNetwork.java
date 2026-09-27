
package com.betterwhips.network;

import com.betterwhips.client.SeaRippleWhipClientState;
import com.betterwhips.client.SeaRippleWhipEffects;
import com.betterwhips.item.SeaRippleWhipCombat;
import com.betterwhips.physics.SeaRippleWhipMotion;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import com.betterwhips.network.WhipNetwork;



public final class SeaRippleWhipNetwork {
    public static final int ATTACK = 0;
    public static final int BLADE = 1;

    private SeaRippleWhipNetwork() {
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)"better_whips", (String)path);
    }

    public static void register() {
        WhipNetwork.registerToServer(Request.class, Request.CODEC, (payload, context) -> context.enqueueWork(() -> {
            Player patt0$temp = context.player();
            if (patt0$temp instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)patt0$temp;
                SeaRippleWhipCombat.request(player, payload.action());
            }
        }));
        WhipNetwork.registerToClient(Snapshot.class, Snapshot.CODEC, (payload, context) -> context.enqueueWork(() -> SeaRippleWhipClientState.receive(payload)));
        WhipNetwork.registerToClient(Impact.class, Impact.CODEC, (payload, context) -> context.enqueueWork(() -> SeaRippleWhipEffects.receive(payload)));
    }

    public static void request(int action) {
        WhipNetwork.CHANNEL.sendToServer(new Request(action));
    }

    public static void nearby(ServerLevel level, Vec3 position, Object packet) {
        for (ServerPlayer player : level.players()) {
            if (!(player.distanceToSqr(position) <= 9216.0)) continue;
            WhipNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    private static void vec(FriendlyByteBuf b, Vec3 p) {
        b.writeDouble(p.x);
        b.writeDouble(p.y);
        b.writeDouble(p.z);
    }

    private static Vec3 vec(FriendlyByteBuf b) {
        Vec3 p = new Vec3(b.readDouble(), b.readDouble(), b.readDouble());
        if (!SeaRippleWhipMotion.finite(p)) {
            throw new IllegalArgumentException("Non-finite sea VFX vector");
        }
        return p;
    }

    private static void stroke(FriendlyByteBuf b, SeaRippleWhipMotion.Stroke s) {
        b.writeByte(s.kind());
        b.writeLong(s.startTick());
        b.writeVarInt(s.duration());
        SeaRippleWhipNetwork.vec(b, s.origin());
        SeaRippleWhipNetwork.vec(b, s.aim());
        b.writeByte(s.handSign());
        b.writeBoolean(s.empowered());
    }

    private static SeaRippleWhipMotion.Stroke stroke(FriendlyByteBuf b) {
        return new SeaRippleWhipMotion.Stroke(b.readUnsignedByte(), b.readLong(), b.readVarInt(), SeaRippleWhipNetwork.vec(b), SeaRippleWhipNetwork.vec(b), b.readByte(), b.readBoolean());
    }

    public record Request(int action) {

public static final WhipNetwork.Codec<Request> CODEC = WhipNetwork.Codec.of((b, p) -> b.writeByte(p.action), b -> new Request(b.readUnsignedByte()));

        public Request {
            if (action < 0 || action > 1) {
                throw new IllegalArgumentException("Invalid sea action");
            }
        }

        
    }

    public record Snapshot(int entityId, UUID uuid, ResourceLocation dimension, long serverTick, SeaRippleWhipMotion.Stroke stroke, long nextAttack, long bladeReady, long empoweredUntil) {

public static final WhipNetwork.Codec<Snapshot> CODEC = WhipNetwork.Codec.of((b, p) -> {
            b.writeVarInt(p.entityId);
            b.writeUUID(p.uuid);
            b.writeResourceLocation(p.dimension);
            b.writeLong(p.serverTick);
            SeaRippleWhipNetwork.stroke(b, p.stroke);
            b.writeLong(p.nextAttack);
            b.writeLong(p.bladeReady);
            b.writeLong(p.empoweredUntil);
        }, b -> new Snapshot(b.readVarInt(), b.readUUID(), b.readResourceLocation(), b.readLong(), SeaRippleWhipNetwork.stroke(b), b.readLong(), b.readLong(), b.readLong()));

        public Snapshot {
            if (uuid == null || dimension == null || stroke == null) {
                throw new IllegalArgumentException("Invalid sea state");
            }
        }

        
    }

    public record Impact(ResourceLocation dimension, Vec3 position, Vec3 direction, int entityId, UUID uuid, boolean empowered, float size, long seed) {

public static final WhipNetwork.Codec<Impact> CODEC = WhipNetwork.Codec.of((b, p) -> {
            b.writeResourceLocation(p.dimension);
            SeaRippleWhipNetwork.vec(b, p.position);
            SeaRippleWhipNetwork.vec(b, p.direction);
            b.writeVarInt(p.entityId);
            b.writeUUID(p.uuid);
            b.writeBoolean(p.empowered);
            b.writeFloat(p.size);
            b.writeLong(p.seed);
        }, b -> new Impact(b.readResourceLocation(), SeaRippleWhipNetwork.vec(b), SeaRippleWhipNetwork.vec(b), b.readVarInt(), b.readUUID(), b.readBoolean(), b.readFloat(), b.readLong()));

        public Impact {
            if (dimension == null || !SeaRippleWhipMotion.finite(position) || !SeaRippleWhipMotion.finite(direction) || uuid == null || !Float.isFinite(size) || size < 0.0f || size > 16.0f) {
                throw new IllegalArgumentException("Invalid sea impact");
            }
        }

        
    }
}

