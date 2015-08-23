package com.leroy.ronan.wow.api.service;

import java.util.HashMap;
import java.util.Map;

import com.leroy.ronan.wow.api.ApiResponse;
import com.leroy.ronan.wow.api.ApiType;

public class ApiClientMemService extends ApiClientService{

    private Map<String, String> cache;

    public ApiClientMemService(){
    	super();
    	this.cache = new HashMap<>();
    }
    
	@Override
	public boolean isAvailable(String zone, ApiType type, String realm, String name) {
        String key = buildkey(zone, type, realm, name);
        return cache.containsKey(key);
	}

	@Override
	protected ApiResponse getDataInternal(String zone, ApiType type, String realm, String name) {
        ApiResponse res = null;
        String key = buildkey(zone, type, realm, name);
        if (cache.containsKey(key)){
            String json = cache.get(key);
            res = new ApiResponse(json);
        }
        return res;
	}

	@Override
	public void putData(String zone, ApiType type, String realm, String name, String json) {
        String key = buildkey(zone, type, realm, name);
        cache.put(key, json);
	}
	
	private String buildkey(String zone, ApiType type, String realm, String name) {
        return zone+"/"+type.name() + "/" + realm + "/" + name;
	}


}
