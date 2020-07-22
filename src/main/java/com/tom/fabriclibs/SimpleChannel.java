package com.tom.fabriclibs;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import io.netty.buffer.Unpooled;

public class SimpleChannel {
	private final Identifier identifier;
	private Map<Class<?>, BiConsumer<?, PacketByteBuf>> encoders = new HashMap<>();
	private Map<Class<?>, Identifier> ids = new HashMap<>();

	public SimpleChannel(Identifier identifier) {
		this.identifier = identifier;
	}

	//ClientSidePacketRegistry
	//ServerSidePacketRegistry
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Identifier identifier;
		public Builder named(Identifier identifier) {
			this.identifier = identifier;
			return this;
		}

		public SimpleChannel build() {
			return new SimpleChannel(identifier);
		}
	}

	public <MSG> void registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, PacketByteBuf> encoder, Function<PacketByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<PacketContext>> messageConsumer){
		Identifier id = new Identifier(identifier.getNamespace(), identifier.getPath() + "/" + index);
		DistExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			ClientSidePacketRegistry.INSTANCE.register(id, (c, b) -> {
				MSG msg = decoder.apply(b);
				messageConsumer.accept(msg, () -> c);
			});
		});
		ServerSidePacketRegistry.INSTANCE.register(id, (c, b) -> {
			MSG msg = decoder.apply(b);
			messageConsumer.accept(msg, () -> c);
		});
		encoders.put(messageType, encoder);
		ids.put(messageType, id);
	}

	@SuppressWarnings("unchecked")
	public <MSG> Packet<?> packet(MSG msg, boolean client) {
		Class<MSG> clazz = (Class<MSG>) msg.getClass();
		BiConsumer<MSG, PacketByteBuf> enc = (BiConsumer<MSG, PacketByteBuf>) encoders.get(clazz);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		enc.accept(msg, buf);
		Identifier id = ids.get(clazz);
		if(client)return ClientSidePacketRegistry.INSTANCE.toPacket(id, buf);
		else return ServerSidePacketRegistry.INSTANCE.toPacket(id, buf);
	}

}
