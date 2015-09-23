package com.leroy.ronan.wow.services;

import com.leroy.ronan.wow.beans.WowAuctionsDataAuction;
import com.leroy.ronan.wow.beans.WowItem;

public class PriceFormater {

	public static String formatPrice(long pc){
		long remaining = pc;
		long po = remaining / 10000;
		remaining -= po * 10000;
		long pa = remaining / 100;
		remaining -= pa * 100;
		return po+"po"+pa+"pa"+remaining+"pc";
	}

	public static String getDisplayString(WowAuctionsDataAuction auction, WowItem item) {
		String res = "";
		res += item.getName();
		res += " ("+item.getId()+")";
		res += " "+auction.getQuantity();
		res += " - "+formatPrice(auction.getBuyout());
		res += " - "+formatPrice(auction.getBuyout()/auction.getQuantity());
		return res;
	}

}
