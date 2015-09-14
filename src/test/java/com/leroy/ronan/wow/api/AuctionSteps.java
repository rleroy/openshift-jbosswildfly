package com.leroy.ronan.wow.api;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;
import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowAuctionsDataAuction;
import com.leroy.ronan.wow.beans.WowItem;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AuctionSteps extends WowSteps {

	private WowAuctionsData auctions;
	
	public AuctionSteps(World world) {
		super(world);
	}
	
	@When("^I get the auctions$")
	public void i_get_the_auctions() throws Throwable {
		auctions = getWorld().getClient().getAuctions(getWorld().getRegion(), getWorld().getRealm());
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
		// Print all items.
		/*
		this.auctions.getAuctions().stream()
				.parallel()
				.map(a -> getWorld().getClient().getItem(getWorld().getRegion(), a.getItem()))
				.forEach(i -> System.out.println(i.getName()));
				*/
	}


}
