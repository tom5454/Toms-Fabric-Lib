package com.tom.fabriclibs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import com.tom.fabriclibs.events.WandererTradesEvent;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class VillagerTradingManager {
	private static final Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> VANILLA_TRADES = new HashMap<>();
	private static final Int2ObjectMap<TradeOffers.Factory[]> WANDERER_TRADES = new Int2ObjectOpenHashMap<>();


	static  {
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.entrySet().forEach(e -> {

			Int2ObjectOpenHashMap<TradeOffers.Factory[]> int2ObjectOpenHashMap = new Int2ObjectOpenHashMap<>();
			e.getValue().int2ObjectEntrySet().forEach(v -> int2ObjectOpenHashMap.put(v.getIntKey(), v.getValue()));
			VANILLA_TRADES.put(e.getKey(), int2ObjectOpenHashMap);
		});
		TradeOffers.WANDERING_TRADER_TRADES.int2ObjectEntrySet().forEach(e ->
		WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf(e.getValue(), e.getValue().length))
				);
	}

	public static void postWandererEvent() {
		List<TradeOffers.Factory> generic = new ArrayList<>();
		List<TradeOffers.Factory> rare = new ArrayList<>();
		Arrays.stream(WANDERER_TRADES.get(1)).forEach(generic::add);
		Arrays.stream(WANDERER_TRADES.get(2)).forEach(rare::add);
		Events.EVENT_BUS.post(new WandererTradesEvent(generic, rare));
		TradeOffers.WANDERING_TRADER_TRADES.put(1, generic.toArray(new TradeOffers.Factory[0]));
		TradeOffers.WANDERING_TRADER_TRADES.put(2, rare.toArray(new TradeOffers.Factory[0]));
	}
}
