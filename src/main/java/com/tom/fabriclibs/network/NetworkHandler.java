package com.tom.fabriclibs.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import com.tom.fabriclibs.DistExecutor;

import io.netty.buffer.Unpooled;

public class NetworkHandler {
	private static final Identifier DATA_S2C = new Identifier("fabriclibs", "data_packet_s2c");
	private static final Identifier DATA_C2S = new Identifier("fabriclibs", "data_packet_c2s");

	public static void init() {
		DistExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			ClientSidePacketRegistry.INSTANCE.register(DATA_S2C, (ctx, buf) -> {
				CompoundTag tag = buf.readCompoundTag();
				ctx.getTaskQueue().submit(() -> {
					if(MinecraftClient.getInstance().currentScreen instanceof IDataReceiver) {
						((IDataReceiver)MinecraftClient.getInstance().currentScreen).receive(tag);
					}
				});
			});
		});
		ServerSidePacketRegistry.INSTANCE.register(DATA_C2S, (ctx, buf) -> {
			CompoundTag tag = buf.readCompoundTag();
			ctx.getTaskQueue().submit(() -> {
				ServerPlayerEntity sender = (ServerPlayerEntity) ctx.getPlayer();
				if(sender.currentScreenHandler instanceof IDataReceiver) {
					((IDataReceiver)sender.currentScreenHandler).receive(tag);
				}
			});
		});
	}


	public static void sendToServer(CompoundTag tag) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCompoundTag(tag);
		ClientSidePacketRegistry.INSTANCE.sendToServer(DATA_C2S, buf);
	}

	public static void sendTo(PlayerEntity player, CompoundTag tag) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCompoundTag(tag);
		ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DATA_S2C, buf);
	}
}
