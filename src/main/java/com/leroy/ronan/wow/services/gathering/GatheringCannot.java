package com.leroy.ronan.wow.services.gathering;

public class GatheringCannot extends GatheringMethod {

	@Override
	public boolean isPossible() {
		return false;
	}

	@Override
	public long getPrice() {
		return 0;
	}

}
