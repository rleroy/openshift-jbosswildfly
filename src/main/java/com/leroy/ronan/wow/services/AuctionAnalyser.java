package com.leroy.ronan.wow.services;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowAuctionsDataAuction;
import com.leroy.ronan.wow.beans.WowHeadItem;
import com.leroy.ronan.wow.beans.WowHeadReagent;
import com.leroy.ronan.wow.beans.WowHeadSpell;
import com.leroy.ronan.wow.craft.CraftingAnalysis;
import com.leroy.ronan.wow.services.gathering.GatheringBuy;
import com.leroy.ronan.wow.services.gathering.GatheringCannot;
import com.leroy.ronan.wow.services.gathering.GatheringMethod;

public class AuctionAnalyser {

	private WowAuctionsData auctions;
	private Function<Long, WowHeadItem> wowhead;
	
	public AuctionAnalyser(WowAuctionsData auctions, Function<Long, WowHeadItem> wowhead) {
		this.auctions = auctions;
		this.wowhead = wowhead;
	}
	
	public long getSellPrice(long id) {
		long minprice = this.auctions.getAuctions().stream().parallel()
			.filter(a -> a.getItem().equals(id))
			.map(a -> a.getBuyout()/a.getQuantity())
			.min(Long::compare)
			.orElse(0l);
		return minprice*99/100;
	}

	public long getBuyPrice(long id, int quantity) {
		List<WowAuctionsDataAuction> list = this.auctions.getAuctions().stream().parallel()
			.filter(a -> a.getItem().equals(id))
			.collect(Collectors.toList());
		list.sort((a1, a2) -> a1.getBuyout().compareTo(a2.getBuyout()));
		int count = 0;
		long res = 0;
		for (WowAuctionsDataAuction cur : list) {
			count += cur.getQuantity();
			res += cur.getBuyout();
			if (count >=  quantity) {
				break;
			}
		}
		if (count < quantity) {
			throw new IllegalArgumentException("Cannot buy more than "+count+" items.");
		}
		return res;
	}
	
	public CraftingAnalysis getCraftingAnalysis(long id) throws NotCraftableException {
		WowHeadItem item = wowhead.apply(id);
		CraftingAnalysis res;
		if (item.getCreatedBy().size() == 0 ){
			res = new CraftingAnalysis(0l, null);
		} else {
			for (WowHeadSpell s : item.getCreatedBy()) {
				for (WowHeadReagent r : s.getReagents()) {
					WowHeadItem curItem = wowhead.apply(r.getId());
				}
			}
			// TODO 
			res = new CraftingAnalysis(0l, null);
		}
		return res;
	}
	
	public GatheringMethod howToGetAtAuctionHouse(long id, int quantity) {
		List<WowAuctionsDataAuction> list = this.auctions.getAuctions().stream().parallel()
				.filter(a -> a.getItem().equals(id))
				.collect(Collectors.toList());
		list.sort((a1, a2) -> a1.getBuyout().compareTo(a2.getBuyout()));
		int count = 0;
		long price = 0;
		for (WowAuctionsDataAuction cur : list) {
			count += cur.getQuantity();
			price += cur.getBuyout();
			if (count >=  quantity) {
				break;
			}
		}
		
		GatheringMethod res;
		if (count < quantity) {
			res = new GatheringCannot();
		} else {
			res = new GatheringBuy(id, quantity, price);
		}
		return res;
	}
	


	
/*
	public GatheringMethod howToGet(long reagent, int quantity) {
		GatheringMethod buy = howToGetAtAuctionHouse(reagent, quantity);

		GatheringMethod craft = wowhead.apply(reagent).getCreatedBy()
			.stream()
			.map(s -> howToGetReagents(s))
			.min((m1, m2) -> Long.compare(m1.getPrice(), m2.getPrice()))
			.orElse(null)
			;
		GatheringMethod res = null;
		if (!buy.isPossible()) {
			res = craft;
		} else if (!craft.isPossible()) {
			res = buy;
		} else if (buy.getPrice() < craft.getPrice()) {
			res = buy;
		} else {
			res = craft;
		}
		return res;
	}
/*
	public GatheringMethod howToGetReagents(WowHeadSpell spell) {
		spell.getReagents().stream().parallel()
			.map(r -> howToGet(r.getId(), r.getCount()))
			.reduce((m1, m2) -> new GatheringCombine(m1, m2))
			.orElse(new GatheringCannot());
			
		return null;
	}
	*/

	/*
	public long getCraftPrice(long id) throws NotCraftableException{
		WowHeadItem item = wowhead.apply(id);
		if (item.getCreatedBy().size() > 0) {
			WowHeadSpell cheaper = item.getCreatedBy().stream()
				.min((s1, s2) -> Long.compare(this.getCraftPrice(s1), this.getCraftPrice(s1)))
				.get();
		}else{
			throw new NotCraftableException(id + " ("+item.getName()+") is not craftable.");
		}
	}
	*/

}
