package com.leroy.ronan.wow.api;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;
import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowAuctionsDataAuction;
import com.leroy.ronan.wow.beans.WowHeadItem;
import com.leroy.ronan.wow.beans.WowItem;
import com.leroy.ronan.wow.craft.CraftingReagents;
import com.leroy.ronan.wow.services.AuctionAnalyser;
import com.leroy.ronan.wow.services.NotCraftableException;
import com.leroy.ronan.wow.services.PriceFormater;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AuctionSteps extends WowSteps {

	private long id;
	private String name;
	
	private WowAuctionsData auctions;
	private WowItem item;
	private WowHeadItem wowheadItem;
	
	public AuctionSteps(World world) {
		super(world);
	}

	@Given("^item is (\\d+) / \"(.*?)\"$")
	public void item_is(long id, String name) throws Throwable {
		this.id = id;
		this.name = name;
	}

	@When("^I get the auctions$")
	public void i_get_the_auctions() throws Throwable {
		auctions = getWorld().getClient().getAuctions(getWorld().getRegion(), getWorld().getRealm());
	}

	@When("^I get the reagents of this item$")
	public void i_get_the_reagents_of_this_item() throws Throwable {
		item = getWorld().getClient().getItem(getWorld().getRegion(), this.id);
		wowheadItem = getWorld().getClient().getWowHeadItem(this.id);
	}

	@When("^I get the crafting price$")
	public void i_get_the_crafting_price() throws Throwable {
		AuctionAnalyser analyser = new AuctionAnalyser(auctions, id -> getWorld().getClient().getWowHeadItem(id));
		analyser.getCraftingAnalysis(this.id);
	}

	@Then("^I should get the list of auctions$")
	public void i_should_get_the_list_of_auctions() throws Throwable {
		Assert.assertNotNull(auctions);
	}

	@Then("^I can analyse the auctions of \"(.*?)\"$")
	public void i_can_analyse_the_auctions_of(String character) throws Throwable {
		List<WowAuctionsDataAuction> myAuctions = this.auctions.getAuctions().stream()
			.parallel()
			.filter(a -> a.getOwner().equals(character))
			.collect(Collectors.toList());
		
		for (WowAuctionsDataAuction cur : myAuctions) {
			WowItem item = getWorld().getClient().getItem(getWorld().getRegion(), cur.getItem());
			System.out.println(item.getName());
		}
	}

	@Then("^I can analyse its cost$")
	public void i_can_analyse_its_cost() throws Throwable {
		AuctionAnalyser analyser = new AuctionAnalyser(auctions, id -> getWorld().getClient().getWowHeadItem(id));
		wowheadItem.getCreatedBy().stream()
			.forEach(spell -> System.out.println("Spell:"+spell.getName()));
		
		System.out.println("sellprice   :"+PriceFormater.formatPrice(analyser.getSellPrice(CraftingReagents.felblight.getId())));
		System.out.println("buyprice   1:"+PriceFormater.formatPrice(analyser.getBuyPrice(CraftingReagents.felblight.getId(),   1)));
		System.out.println("buyprice  10:"+PriceFormater.formatPrice(analyser.getBuyPrice(CraftingReagents.felblight.getId(),  10)));
		System.out.println("buyprice  50:"+PriceFormater.formatPrice(analyser.getBuyPrice(CraftingReagents.felblight.getId(),  50)));
		System.out.println("buyprice 100:"+PriceFormater.formatPrice(analyser.getBuyPrice(CraftingReagents.felblight.getId(), 100)));
		System.out.println("buyprice 500:"+PriceFormater.formatPrice(analyser.getBuyPrice(CraftingReagents.felblight.getId(), 500)));
	}
	
}
