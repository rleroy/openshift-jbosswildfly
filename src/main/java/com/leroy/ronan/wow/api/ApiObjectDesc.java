package com.leroy.ronan.wow.api;

public class ApiObjectDesc {

	private String zone;
	private ApiType type;
	private String realm;
	private String name;

	public static ApiObjectDesc of(String desc){
		return new ApiObjectDesc(desc);
	}
	
	public static ApiObjectDesc of(String zone, ApiType type, String realm, String name){
		return new ApiObjectDesc(zone, type, realm, name);
	}
	
	private ApiObjectDesc(String zone, ApiType type, String realm, String name) {
		super();
		this.zone = zone;
		this.type = type;
		this.realm = realm;
		this.name = name;
	}
	
	private ApiObjectDesc(String desc){
		super();
		String[] parts = desc.split("/");
		this.zone = parts[0];
		this.type = ApiType.valueOf(parts[1]);
		if (parts.length <= 3){
			this.realm = null;
			this.name = parts[2];
		} else { 
			this.realm = parts[2];
			this.name = parts[3];
		}
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
		String res = this.zone+"/"+this.type+"/";
		if (this.realm != null) {
			res += this.realm+"/";
		}
		res += this.name;
		return res;
	}
}
