package com.leroy.ronan.wow;

import com.leroy.ronan.wow.api.ApiClient;
import com.leroy.ronan.wow.api.cache.ApiClientCached;
import com.leroy.ronan.wow.api.service.ApiClientImpl;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public class World {

	private String region;
	private String realm;
	private String guildName;
	private String characterName;

	private ApiClient client;
	
	private WowGuild guild;
	private WowCharacter character;
	
	public void setRegion(String region) {
		this.region = region;
		this.client = new ApiClientCached(region, "en_GB", "8vkxyhwqkb787e47utga6r5djuw2unqt", "apifiles");
		//this.client = new ApiClientImpl(region);
	}
	public String getRegion() {
		return region;
	}
	public ApiClient getClient() {
		return client;
	}

	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	public String getGuildName() {
		return guildName;
	}
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}
	
	public String getCharacterName() {
		return characterName;
	}
	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}
	
	public WowGuild getGuild() {
		return guild;
	}
	public void setGuild(WowGuild guild) {
		this.guild = guild;
	}
	
	public WowCharacter getCharacter() {
		return character;
	}
	public void setCharacter(WowCharacter character) {
		this.character = character;
	}

	
}
