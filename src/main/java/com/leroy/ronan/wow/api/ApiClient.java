package com.leroy.ronan.wow.api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.leroy.ronan.wow.api.service.ApiClientFileService;
import com.leroy.ronan.wow.api.service.ApiClientMemService;
import com.leroy.ronan.wow.api.service.ApiClientService;
import com.leroy.ronan.wow.api.service.ApiClientWebService;
import com.leroy.ronan.wow.beans.WowAuction;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public class ApiClient {

	private String zone;
	private ApiClientService web;
	private ApiClientService file;
	private ApiClientService mem;
	
	public ApiClient(String zone) {
		this.zone = zone;
		
        this.web = new ApiClientWebService("en_GB", "8vkxyhwqkb787e47utga6r5djuw2unqt");
        this.file = new ApiClientFileService("apifiles", LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), Duration.ofHours(24));
        this.mem = new ApiClientMemService();
	}
	
	public WowCharacter getCharacter(String realm, String name) {
        ApiResponse data = this.getData(ApiType.character, realm, name);
        return new WowCharacter(data.getJSON());
	}
	
	public WowGuild getGuild(String realm, String name) {
		ApiResponse data = this.getData(ApiType.guild, realm, name);
        return new WowGuild(data.getJSON());
	}
	
	public WowAuction getAuctions(String realm) {
		ApiResponse data = this.getData(ApiType.auction, "data", realm);
		
		WowAuction auction = new WowAuction(data.getJSON());
		this.getData(ApiType.auctiontype, realm, auction.getUrl());
		
		
		
		return auction;
	}

	private ApiResponse getData(ApiType type, String realm, String name) {
		ApiResponse res = mem.getData(zone, type, realm, name);
        if (res == null){
            res = file.getData(zone, type, realm, name);
            if (res == null){
                res = web.getData(zone, type, realm, name);
                file.putData(zone, type, realm, name, res.getJSON());
            }
            mem.putData(zone, type, realm, name, res.getJSON());
        }
        return res;
	}
	
	public int getRemoteCallCount() {
		return this.web.getCallCount();
	}
	
	public boolean isCharacterPersisted(String realm, String name) {
		return this.file.isAvailable(this.zone, ApiType.character, realm, name);
	}
	
	public void setCharacterData(String realm, String name, String data) {
        file.putData(zone, ApiType.character, realm, name, data);
	}

}
