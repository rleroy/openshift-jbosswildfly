package com.leroy.ronan.wow.services.gathering;

public class GatheringBuy extends GatheringMethod {

	private long id;
	private int quantity;
	private long price;
	
	public GatheringBuy(long id, int quantity, long price) {
		super();
		this.id = id;
		this.quantity = quantity;
		this.price = price;
	}

	@Override
	public boolean isPossible() {
		return true;
	}

	@Override
	public long getPrice() {
		return price;
	}
	
	public long getId() {
		return id;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
}
