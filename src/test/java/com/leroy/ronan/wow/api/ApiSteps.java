package com.leroy.ronan.wow.api;

import org.junit.Assert;

import com.leroy.ronan.wow.World;
import com.leroy.ronan.wow.WowSteps;

import cucumber.api.java.en.Then;

public class ApiSteps extends WowSteps {

	public ApiSteps(World world) {
		super(world);
	}

	@Then("^the API was only called once$")
	public void the_API_was_only_called_once() throws Throwable {
		Assert.assertEquals(1, getWorld().getClient().getRemoteCallCount());
	}

}
