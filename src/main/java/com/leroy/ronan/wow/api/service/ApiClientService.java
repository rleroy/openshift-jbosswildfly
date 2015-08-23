package com.leroy.ronan.wow.api.service;

import com.leroy.ronan.wow.api.ApiResponse;
import com.leroy.ronan.wow.api.ApiType;

public abstract class ApiClientService {

	private int callCount;
	
	public ApiClientService(){
		this.callCount = 0;
	}
	
	public int getCallCount(){
		return this.callCount;
	}
	
	public abstract boolean isAvailable(String zone, ApiType type, String realm, String name);

	public final ApiResponse getData(String zone, ApiType type, String realm, String name){
		this.callCount++;
		return this.getDataInternal(zone, type, realm, name);
	}
	
	protected abstract ApiResponse getDataInternal(String zone, ApiType type, String realm, String name);

	public abstract void putData(String zone, ApiType type, String realm, String name, String json);

}
