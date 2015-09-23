package com.leroy.ronan.wow.craft;

public enum CraftingReagents {
	
	felblight(127759),
	;

	long id;

	private CraftingReagents(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}
