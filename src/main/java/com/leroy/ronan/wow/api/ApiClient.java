package com.leroy.ronan.wow.api;

import java.awt.image.BufferedImage;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public interface ApiClient {

	public WowCharacter getCharacter(String zone, String realm, String name);
	public WowGuild getGuild(String zone, String realm, String name);
	public WowAuctionsData getAuctions(String zone, String realm);
	
}
