package com.leroy.ronan.wow.craft;

public class CraftingAnalysis {

	public static final CraftingAnalysis EMPTY = new CraftingAnalysis(null, null);
	
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

    public CraftingAnalysis multiply(int coeff) {
        return new CraftingAnalysis(this.buyprice*coeff, recipe);
    }

    public CraftingAnalysis add(CraftingAnalysis that) {
        return new CraftingAnalysis(this.buyprice+that.buyprice, recipe);
    }

    public CraftingAnalysis divide(int coeff) {
        return new CraftingAnalysis(this.buyprice/coeff, recipe);
    }

    public CraftingAnalysis best(CraftingAnalysis that) {
        CraftingAnalysis res;
        if (that.buyprice == null){
            res = this;
        } else if (this.buyprice == null){
            res = that;
        } else if (this.buyprice < that.buyprice) {
            res = this;
        } else {
            res = that;
        }
        return res;
    }
    
}
