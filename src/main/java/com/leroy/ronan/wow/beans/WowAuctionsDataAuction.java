package com.leroy.ronan.wow.beans;

public class WowAuctionsDataAuction {
	
    private long auc;
    private long item;
    private String owner;
    private String ownerRealm;
    private long bid;
    private long buyout;
    private long quantity;
    
	public WowAuctionsDataAuction(long auc, long item, String owner, String ownerRealm, long bid, long buyout,
			long quantity) {
		super();
		this.auc = auc;
		this.item = item;
		this.owner = owner;
		this.ownerRealm = ownerRealm;
		this.bid = bid;
		this.buyout = buyout;
		this.quantity = quantity;
	}

	public long getAuc() {
		return auc;
	}

	public long getItem() {
		return item;
	}

	public String getOwner() {
		return owner;
	}

	public String getOwnerRealm() {
		return ownerRealm;
	}

	public long getBid() {
		return bid;
	}

	public long getBuyout() {
		return buyout;
	}

	public long getQuantity() {
		return quantity;
	}
}
