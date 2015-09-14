package com.leroy.ronan.wow.api;

import java.util.Comparator;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GuildDataSteps extends WowSteps {

	public GuildDataSteps(World world) {
		super(world);
	}

	@Given("^guild name is \"(.*?)\"$")
	public void guild_name_is(String guild) throws Throwable {
	    getWorld().setGuildName(guild);
	}

	@When("^I get the member list$")
	public void i_get_the_member_list() throws Throwable {
		getWorld().setGuild(getWorld().getClient().getGuild(getWorld().getRegion(), getWorld().getRealm(), getWorld().getGuildName()));
	}

	@Then("^a character with name \"(.*?)\" is in the list$")
	public void a_character_with_name_is_in_the_list(String name) throws Throwable {
		Assert.assertEquals(1, getWorld().getGuild().getMembers().stream().filter(m -> m.getName().equals(name)).count());
		// Download all members
		/*
		getWorld().getGuild().getMembers().stream()
			.map(m -> getWorld().getClient().getCharacter(getWorld().getRegion(), m.getRealm(), m.getName()))
			.filter(c-> c != null)
			.sorted((c1, c2) -> c1.getAverageItemLevelEquipped().compareTo(c2.getAverageItemLevelEquipped()))
			.forEach(c -> System.out.println(c.getName()+"("+c.getAverageItemLevelEquipped()+")"));
			*/
	}

}
