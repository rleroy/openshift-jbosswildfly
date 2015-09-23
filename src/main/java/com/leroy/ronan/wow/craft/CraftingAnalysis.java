package com.leroy.ronan.wow.craft;

public class CraftingAnalysis {

	private Long price;
	private Recipe recipe;

	public CraftingAnalysis(Long price, Recipe recipe) {
		super();
		this.price = price;
		this.recipe = recipe;
	}

	public Long getPrice() {
		return price;
	}

	public Recipe getRecipe() {
		return recipe;
	}

}
