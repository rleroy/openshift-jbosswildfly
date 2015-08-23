package com.leroy.ronan.wow.api;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class FileSystemCacheSteps extends WowSteps {
	
	public FileSystemCacheSteps(World world) {
		super(world);
	}

	@Given("^character data does not exist in the file system$")
	public void character_data_does_not_exist_in_the_file_system() throws Throwable {
		getWorld().getClient().setCharacterData(getWorld().getRealm(), getWorld().getCharacterName(), null);
	}
	
	@Given("^character data exists in the file system$")
	public void character_data_exists_in_the_file_system() throws Throwable {
		String data = new String(Files.readAllBytes(Paths.get(getClass().getResource("features/sample.json").toURI())));
		getWorld().getClient().setCharacterData(getWorld().getRealm(), getWorld().getCharacterName(), data);
	}
	
	@Then("^data for the character has been saved in the file system$")
	public void data_for_the_character_has_been_saved_in_the_file_system() throws Throwable {
		Assert.assertTrue(getWorld().getClient().isCharacterPersisted(getWorld().getRealm(), getWorld().getCharacterName()));
	}

	@Then("^the API was never called$")
	public void the_API_was_never_called() throws Throwable {
		Assert.assertEquals(0, getWorld().getClient().getRemoteCallCount());
	}
	
}
