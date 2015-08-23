package com.leroy.ronan.wow;

import cucumber.api.java.en.Given;

public class RealmSteps extends WowSteps {

	public RealmSteps(World world) {
		super(world);
	}
	
	@Given("^region is \"(.*?)\"$")
	public void region_is(String region) throws Throwable {
		getWorld().setRegion(region);
	}

	@Given("^realm name is \"(.*?)\"$")
	public void realm_name_is(String realm) throws Throwable {
		getWorld().setRealm(realm);
	}

}
