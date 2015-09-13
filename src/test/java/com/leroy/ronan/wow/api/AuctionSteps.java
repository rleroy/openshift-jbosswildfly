package com.leroy.ronan.wow.api;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;
import com.leroy.ronan.wow.beans.WowAuction;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AuctionSteps extends WowSteps {

	private WowAuction auctions;
	
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


}
