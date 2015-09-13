package com.leroy.ronan.wow.beans;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WowAuctionsData extends WowJson{

	private Set<WowAuctionsDataAuction> auctions;
	
	public WowAuctionsData(String json) {
		super(json);
		
        JSONObject object = (JSONObject)JSONValue.parse(json);
        JSONArray auctionsArray = (JSONArray)object.get("auctions");
        int size = auctionsArray.size();

        auctions = IntStream.range(0, size)
        		.boxed()
        		.map(i -> (JSONObject)auctionsArray.get(i))
        		.map(obj -> new WowAuctionsDataAuction(
						(Long)obj.get("auc"),
						(Long)obj.get("item"),
						(String)obj.get("owner"),
						(String)obj.get("ownerRealm"),
						(Long)obj.get("bid"),
						(Long)obj.get("buyout"),
						(Long)obj.get("quantity")
        							)
        			)
        		.collect(Collectors.toSet());
	}

	public Set<WowAuctionsDataAuction> getAuctions() {
		return auctions;
	}
}
