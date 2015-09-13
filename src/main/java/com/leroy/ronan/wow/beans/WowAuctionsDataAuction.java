package com.leroy.ronan.wow.beans;

public class WowAuctionsDataAuction {
	
    private Long auc;
    private Long item;
    private String owner;
    private String ownerRealm;
    private Long bid;
    private Long buyout;
    private Long quantity;
    
	public WowAuctionsDataAuction(Long auc, Long item, String owner, String ownerRealm, Long bid, Long buyout, Long quantity) {
		super();
		this.auc = auc;
		this.item = item;
		this.owner = owner;
		this.ownerRealm = ownerRealm;
		this.bid = bid;
		this.buyout = buyout;
		this.quantity = quantity;
	}

	public Long getAuc() {
		return auc;
	}

	public Long getItem() {
		return item;
	}

	public String getOwner() {
		return owner;
	}

	public String getOwnerRealm() {
		return ownerRealm;
	}

	public Long getBid() {
		return bid;
	}

	public Long getBuyout() {
		return buyout;
	}

	public Long getQuantity() {
		return quantity;
	}
}
