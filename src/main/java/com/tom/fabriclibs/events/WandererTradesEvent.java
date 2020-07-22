package com.tom.fabriclibs.events;

import java.util.List;

import net.minecraft.village.TradeOffers;

import com.tom.fabriclibs.event.Event;

public class WandererTradesEvent extends Event {

	protected List<TradeOffers.Factory> generic;
	protected List<TradeOffers.Factory> rare;

	public WandererTradesEvent(List<TradeOffers.Factory> generic, List<TradeOffers.Factory> rare)
	{
		this.generic = generic;
		this.rare = rare;
	}

	public List<TradeOffers.Factory> getGenericTrades()
	{
		return generic;
	}

	public List<TradeOffers.Factory> getRareTrades()
	{
		return rare;
	}
}
