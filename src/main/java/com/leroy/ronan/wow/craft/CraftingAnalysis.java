package com.leroy.ronan.wow.craft;

public class CraftingAnalysis {

	private Long buyprice;
	private Recipe recipe;

	public CraftingAnalysis(Long price, Recipe recipe) {
		super();
		this.buyprice = price;
		this.recipe = recipe;
	}

	public Long getBuyprice() {
		return buyprice;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public Long getCraftPrice() {
		Long res = null;
		if (this.recipe != null) {
			res = this.recipe.getPrice();
		}
		return res;
	}

}
