package com.leroy.ronan.wow.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.junit.Assert;
import org.mockito.Mockito;

import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowAuctionsDataAuction;
import com.leroy.ronan.wow.beans.WowHeadItem;
import com.leroy.ronan.wow.beans.WowHeadSpell;
import com.leroy.ronan.wow.beans.WowItem;
import com.leroy.ronan.wow.craft.CraftingAnalysis;
import com.leroy.ronan.wow.services.AuctionAnalyser;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AuctionAnalyserSteps {

	private Set<WowAuctionsDataAuction> auctions = new HashSet<>();
	private WowItem item;
	private Function<Long, WowHeadItem> wowhead;
	
	private long price;
	private Exception e;
	private CraftingAnalysis analysis;

	@Given("^there is nothing to sell$")
	public void there_is_nothing_to_sell() throws Throwable {
		auctions.clear();
	}
	
	@Given("^there is an offer for (\\d+) of this item at (\\d+)$")
	public void there_is_an_offer_for_of_this_item_at(long quantity, long price) throws Throwable {
		WowAuctionsDataAuction auction = Mockito.mock(WowAuctionsDataAuction.class);
		Mockito.when(auction.getBuyout()).thenReturn(price);
		Mockito.when(auction.getQuantity()).thenReturn(quantity);
		auctions.add(auction);
	}

	@Given("^item is not craftable$")
	public void item_is_not_craftable() throws Throwable {
		item = Mockito.mock(WowItem.class);
		Mockito.when(item.getId()).thenReturn(0l);
		wowhead = i -> {
			WowHeadItem item = Mockito.mock(WowHeadItem.class);
			Mockito.when(item.getId()).thenReturn(0l);
			return item;
		};
	}
	
	@Given("^item is craftable$")
	public void item_is_craftable() throws Throwable {
		item = Mockito.mock(WowItem.class);

		Set<WowHeadSpell> spells = new HashSet<>();
		WowHeadSpell spell = Mockito.mock(WowHeadSpell.class);
		spells.add(spell);
		
		Mockito.when(item.getId()).thenReturn(0l);
		wowhead = i -> {
			WowHeadItem item = Mockito.mock(WowHeadItem.class);
			Mockito.when(item.getId()).thenReturn(0l);
			Mockito.when(item.getCreatedBy()).thenReturn(spells);
			return item;
		};
	}

	@When("^I want to sell this item$")
	public void i_want_to_sell_this_item() throws Throwable {
		AuctionAnalyser analyser = buildAnalyser();
		price = analyser.getSellPrice(item.getId());
	}

	@When("^I want to buy this item$")
	public void i_want_to_buy_this_item() throws Throwable {
		i_want_to_buy_of_this_item(1);
	}
	
	@When("^I want to buy (\\d+) of this item$")
	public void i_want_to_buy_of_this_item(int qty) throws Throwable {
		try {
			AuctionAnalyser analyser = buildAnalyser();
			price = analyser.getBuyPrice(item.getId(), qty);
		}catch(Exception e){
			this.e = e;
		}
	}
	
	@When("^I want to analyse the price of this item$")
	public void i_want_to_analyse_the_price_of_this_item() throws Throwable {
		AuctionAnalyser analyser = buildAnalyser();
		analysis = analyser.getCraftingAnalysis(item.getId());
	}

	private AuctionAnalyser buildAnalyser() {
		WowAuctionsData auctionsData = Mockito.mock(WowAuctionsData.class);
		Mockito.when(auctionsData.getAuctions()).thenReturn(auctions);
		return new AuctionAnalyser(auctionsData, wowhead);
	}

	@Then("^it should be impossible$")
	public void it_should_be_impossible() throws Throwable {
		Assert.assertNotNull(this.e);
		Assert.assertEquals(IllegalArgumentException.class, this.e.getClass());
	}

	@Then("^price should be (\\d+)$")
	public void selling_price_should_be(long price) throws Throwable {
		Assert.assertEquals(price, this.price);
	}

	@Then("^analysis should return no crafting price$")
	public void analysis_should_return_no_crafting_price() throws Throwable {
		Assert.assertNull(analysis.getPrice());
	}

	@Then("^analysis should return no crafting recipe$")
	public void analysis_should_return_no_crafting_recipe() throws Throwable {
		Assert.assertNull(analysis.getRecipe());
	}

	@Then("^analysis should return a crafting recipe$")
	public void analysis_should_return_a_crafting_recipe() throws Throwable {
		Assert.assertNotNull(analysis.getRecipe());
	}

}
