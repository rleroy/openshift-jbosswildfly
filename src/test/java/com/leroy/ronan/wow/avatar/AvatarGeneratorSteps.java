package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.leroy.ronan.wow.avatar.AvatarGenerator;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AvatarGeneratorSteps {
	
	private String region;
	private String realm;
	private List<String> characters;
	private BufferedImage img;
	
	private AvatarGenerator generator;

	@Given("^region is \"(.*?)\"$")
	public void region_is(String region) throws Throwable {
		this.region = region;
	}

	@Given("^realm name is \"(.*?)\"$")
	public void realm_name_is(String realm) throws Throwable {
		this.realm = realm;
	}

	@Given("^I want character name \"(.*?)\" in image$")
	public void i_want_character_name_in_image(String character) throws Throwable {
		if (this.characters == null){
			this.characters = new ArrayList<>();
		}
		this.characters.add(character);
	}
	
	@When("^I get the avatar$")
	public void i_get_the_avatar() throws Throwable {
		AvatarGenerator generator = new AvatarGenerator();
		this.img = generator.buildImage(region, realm, characters.toArray(new String[0]));
	}

	@Then("^an avatar is available$")
	public void an_avatar_is_available() throws Throwable {
		Assert.assertNotNull(this.img);
	}

}
