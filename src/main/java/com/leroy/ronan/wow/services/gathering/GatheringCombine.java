package com.leroy.ronan.wow.services.gathering;

import java.util.ArrayList;
import java.util.List;

public class GatheringCombine extends GatheringMethod {

	private boolean possible;
	private long price;
	private List<MethodStep> steps;
	
	public GatheringCombine(boolean possible, long price) {
		
	}
	
	public GatheringCombine(boolean possible, long price, List<MethodStep> steps) {
		this.possible = possible;
		this.price = price;
		this.steps = steps;
	}
	
	public GatheringCombine combine(GatheringCombine that) {
		this.possible = this.possible && that.possible;
		this.price = this.price + that.price;
		this.steps.addAll(that.steps);
		return this;
	}

	
	public GatheringCombine(GatheringCombine m1, GatheringCombine m2) {
		this.possible = m1.isPossible() && m2.isPossible();
		this.price = m1.getPrice() + m2.getPrice();
		this.steps = new ArrayList<>();
		this.steps.addAll(m1.steps);
		this.steps.addAll(m2.steps);
	}
	
	public GatheringCombine(boolean possible, long reagent, int quantity, long price) {
		this.possible = possible;
		this.price = price;
		this.steps = new ArrayList<>();
	}
	
	@Override
	public boolean isPossible() {
		return possible;
	}

	@Override
	public long getPrice() {
		return price;
	}
	
}
