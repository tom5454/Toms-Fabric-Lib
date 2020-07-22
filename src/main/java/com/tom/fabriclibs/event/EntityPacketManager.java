package com.tom.fabriclibs.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;

import com.tom.fabriclibs.DistExecutor;

import io.netty.buffer.Unpooled;

public class EntityPacketManager {
	private static final Identifier id = new Identifier("fabriclibs", "spawn_entity");
	private static final Map<EntityType<?>, Function<EntitySpawnS2CPacket, Entity>> entityFactories = new HashMap<>();
	public static void init() {
		DistExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			ClientSidePacketRegistry.INSTANCE.register(id, (c, b) -> {
				EntitySpawnS2CPacket pckt = new EntitySpawnS2CPacket();
				try {
					pckt.read(b);
				} catch (IOException e) {
				}
				c.getTaskQueue().execute(() -> {
					Function<EntitySpawnS2CPacket, Entity> factory = entityFactories.get(pckt.getEntityTypeId());
					if(factory != null) {
						double d = pckt.getX();
						double e = pckt.getY();
						double f = pckt.getZ();
						Entity ent = factory.apply(pckt);
						if (ent != null) {
							int i = pckt.getId();
							ent.updateTrackedPosition(d, e, f);
							ent.pitch = (pckt.getPitch() * 360) / 256.0F;
							ent.yaw = (pckt.getYaw() * 360) / 256.0F;
							ent.setEntityId(i);
							ent.setUuid(pckt.getUuid());
							MinecraftClient.getInstance().world.addEntity(i, ent);
						}
					}
				});
			});
		});
	}

	public static void registerEntityFactory(EntityType<?> type, Function<EntitySpawnS2CPacket, Entity> factory) {
		entityFactories.put(type, factory);
	}

	public static Packet<?> toPacket(Entity ent, Packet<?> pckt) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		try {
			pckt.write(buf);
		} catch (IOException e) {
		}
		return ServerSidePacketRegistry.INSTANCE.toPacket(id, buf);
	}
}
