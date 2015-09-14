package com.leroy.ronan.wow.beans;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WowItem extends WowJson{
	
	private Long id;
	private String name;

	public WowItem(String json) {
		super(json);
		
        JSONObject obj = (JSONObject)JSONValue.parse(json);
        id = (Long)obj.get("id");
        name = (String)obj.get("name");
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
