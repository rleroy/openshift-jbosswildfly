package com.leroy.ronan.wow.services;

import com.leroy.ronan.wow.beans.WowAuctionsDataAuction;
import com.leroy.ronan.wow.beans.WowItem;

public class AuctionUtils {

	public static boolean isFelblight(WowAuctionsDataAuction auction, WowItem item){
		boolean res = false;
		if ("Felblight".equalsIgnoreCase(item.getName())){
			res = true;
		}
		return res;
	}
	
	public static String getDisplayString(WowAuctionsDataAuction auction, WowItem item) {
		String res = "";
		res += item.getName();
		res += " ("+item.getId()+")";
		res += " "+auction.getQuantity();
		res += " - "+getPrice(auction.getBuyout());
		res += " - "+getPrice(auction.getBuyout()/auction.getQuantity());
		return res;
	}
	
	private static String getPrice(long pc){
		long remaining = pc;
		long po = remaining / 10000;
		remaining -= po * 10000;
		long pa = remaining / 100;
		remaining -= pa * 100;
		return po+"po"+pa+"pa"+remaining+"pc";
	}
	
}
