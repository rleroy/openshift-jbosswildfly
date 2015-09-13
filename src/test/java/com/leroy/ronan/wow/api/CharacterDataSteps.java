package com.leroy.ronan.wow.api;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;
import com.leroy.ronan.wow.beans.WowCharacter;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CharacterDataSteps extends WowSteps {
	
	public CharacterDataSteps(World world) {
		super(world);
	}
	
	@Given("^character name is \"(.*?)\"$")
	public void character_name_is(String name) throws Throwable {
		getWorld().setCharacterName(name);
	}
	
	@When("^I get the character data$")
	public void i_get_the_character_data() throws Throwable {
		getWorld().setCharacter(getWorld().getClient().getCharacter(getWorld().getRegion(), getWorld().getRealm(), getWorld().getCharacterName()));
	}

	@Then("^I am able to know the ilvl of this character$")
	public void i_am_able_to_know_the_ilvl_of_this_character() throws Throwable {
		Assert.assertNotNull(getWorld().getCharacter().getAverageItemLevel());
	}

	@Then("^I am able to know the achievementPoints of this character$")
	public void i_am_able_to_know_the_achievementPoints_of_this_character() throws Throwable {
		Assert.assertNotNull(getWorld().getCharacter().getAchievementPoints());
	}
	
	@Then("^I am able to know this character is a reroll of \"(.*?)\" in realm \"(.*?)\"$")
	public void i_am_able_to_know_this_character_is_a_reroll_of(String otherName, String otherRealm) throws Throwable {
		WowCharacter otherCharacter = getWorld().getClient().getCharacter(getWorld().getRegion(), otherRealm, otherName);
		Assert.assertEquals(getWorld().getCharacter().getAchievementPoints(), otherCharacter.getAchievementPoints());
	}

}
