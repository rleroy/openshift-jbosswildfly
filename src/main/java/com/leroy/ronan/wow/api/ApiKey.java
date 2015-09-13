package com.leroy.ronan.wow.api;

public class ApiKey {

	private ApiType type;
	private String realm;
	private String name;

	public static ApiKey of(String key){
		return new ApiKey(key);
	}
	
	public static ApiKey of(ApiType type, String realm, String name){
		return new ApiKey(type, realm, name);
	}
	
	private ApiKey(ApiType type, String realm, String name) {
		super();
		this.type = type;
		this.realm = realm;
		this.name = name;
	}
	
	private ApiKey(String key){
		super();
		String[] parts = key.split("/");
		this.type = ApiType.valueOf(parts[0]);
		this.realm = parts[1];
		this.name = parts[2];
	}
	
	public ApiType getType() {
		return type;
	}
	public String getRealm() {
		return realm;
	}
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return this.getType()+"/"+this.getRealm()+"/"+this.getName();
	}
}
