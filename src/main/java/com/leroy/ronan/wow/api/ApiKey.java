package com.leroy.ronan.wow.api;

public class ApiKey {

	private String zone;
	private ApiType type;
	private String realm;
	private String name;

	public static ApiKey of(String key){
		return new ApiKey(key);
	}
	
	public static ApiKey of(String zone, ApiType type, String realm, String name){
		return new ApiKey(zone, type, realm, name);
	}
	
	private ApiKey(String zone, ApiType type, String realm, String name) {
		super();
		this.zone = zone;
		this.type = type;
		this.realm = realm;
		this.name = name;
	}
	
	private ApiKey(String key){
		super();
		String[] parts = key.split("/");
		this.zone = parts[0];
		this.type = ApiType.valueOf(parts[1]);
		this.realm = parts[2];
		this.name = parts[3];
	}
	
	public String getZone() {
		return zone;
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

	@Override
	public String toString() {
		return this.zone+"/"+this.type+"/"+this.realm+"/"+this.name;
	}
}
