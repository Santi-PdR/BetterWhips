package com.betterwhips.network;
import com.betterwhips.BetterWhipsMod;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
/** Forge 1.20.1 channel for whip actions and synchronized effects. */
public final class WhipNetwork {
 private static final String PROTOCOL="1";
 public static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(new ResourceLocation(BetterWhipsMod.MOD_ID,"main"),()->PROTOCOL,PROTOCOL::equals,PROTOCOL::equals);
 private static int nextMessageId; private WhipNetwork(){}
 public static <T> void registerToServer(Class<T> type,Codec<T> codec,BiConsumer<T,Context> handler){CHANNEL.registerMessage(nextMessageId++,type,codec::encode,codec::decode,(m,c)->handle(m,c,handler),Optional.of(NetworkDirection.PLAY_TO_SERVER));}
 public static <T> void registerToClient(Class<T> type,Codec<T> codec,BiConsumer<T,Context> handler){CHANNEL.registerMessage(nextMessageId++,type,codec::encode,codec::decode,(m,c)->handle(m,c,handler),Optional.of(NetworkDirection.PLAY_TO_CLIENT));}
 private static <T> void handle(T message,Supplier<NetworkEvent.Context> supplier,BiConsumer<T,Context> handler){NetworkEvent.Context c=supplier.get();c.enqueueWork(()->handler.accept(message,new Context(c)));c.setPacketHandled(true);}
 public static final class Context {private final NetworkEvent.Context context;private Context(NetworkEvent.Context c){context=c;}public Player player(){return context.getSender();}public void enqueueWork(Runnable r){context.enqueueWork(r);}}
 public static final class Codec<T>{private final BiConsumer<FriendlyByteBuf,T> encoder;private final Function<FriendlyByteBuf,T> decoder;private Codec(BiConsumer<FriendlyByteBuf,T> e,Function<FriendlyByteBuf,T>d){encoder=e;decoder=d;}public static <T> Codec<T> of(BiConsumer<FriendlyByteBuf,T>e,Function<FriendlyByteBuf,T>d){return new Codec<>(e,d);}private void encode(T m,FriendlyByteBuf b){encoder.accept(b,m);}private T decode(FriendlyByteBuf b){return decoder.apply(b);}}
}
