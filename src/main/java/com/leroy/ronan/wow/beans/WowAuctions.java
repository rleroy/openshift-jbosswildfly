package com.leroy.ronan.wow.beans;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WowAuctions {

	public WowAuctions(String json) {
        super();
        
        JSONObject obj = (JSONObject)JSONValue.parse(json);
        JSONArray files = (JSONArray)obj.get("files");
        JSONObject file = (JSONObject)files.get(0);
        String url = (String)file.get("url");
        Long lastModified = (Long)file.get("lastModified");
        
	}

}
