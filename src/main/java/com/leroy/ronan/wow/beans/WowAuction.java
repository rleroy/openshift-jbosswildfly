package com.leroy.ronan.wow.beans;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WowAuction {

	private String url;
	private Long lastModified;
	
	public WowAuction(String json) {
        super();
        
        JSONObject obj = (JSONObject)JSONValue.parse(json);
        JSONArray files = (JSONArray)obj.get("files");
        JSONObject file = (JSONObject)files.get(0);
        url = (String)file.get("url");
        lastModified = (Long)file.get("lastModified");
	}

	public String getUrl() {
		return url;
	}

	public Long getLastModified() {
		return lastModified;
	}

}
