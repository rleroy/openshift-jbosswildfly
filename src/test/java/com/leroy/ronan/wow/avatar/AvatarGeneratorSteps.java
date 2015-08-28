package com.leroy.ronan.wow.avatar;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;
import com.leroy.ronan.wow.avatar.AvatarGenerator;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AvatarGeneratorSteps extends WowSteps{
	
	private List<String> characters;
	private CacheResponse<BufferedImage> img;
	
	public AvatarGeneratorSteps(World world) {
		super(world);
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
		AvatarGenerator generator = new AvatarGenerator(System.getenv("OPENSHIFT_DATA_DIR"));
		this.img = generator.get(getWorld().getRegion(), getWorld().getRealm(), characters.toArray(new String[0]));
	}

	@Then("^an avatar is available$")
	public void an_avatar_is_available() throws Throwable {
		Assert.assertNotNull(this.img.getContent());
	}

}
