package com.leroy.ronan.wow.api.cache;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.wow.api.ApiClient;
import com.leroy.ronan.wow.api.ApiObjectDesc;
import com.leroy.ronan.wow.api.ApiType;
import com.leroy.ronan.wow.beans.WowAuction;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public class ApiClientCached implements ApiClient {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private PersistedCache<WowCharacter> characterCache;
	private PersistedCache<WowGuild> guildCache;
	private PersistedCache<WowAuction> auctionCache;
	
	public ApiClientCached(String locale, String apikey, String root){
		characterCache = (new ApiCacheProvider<WowCharacter>(locale, apikey, root)).get("api-characters", s -> new WowCharacter(s));
		guildCache = (new ApiCacheProvider<WowGuild>(locale, apikey, root)).get("api-guilds", s -> new WowGuild(s));
		auctionCache = (new ApiCacheProvider<WowAuction>(locale, apikey, root)).get("api-auctions", s -> new WowAuction(s));
		
	}
	
	@Override
	public WowCharacter getCharacter(String zone, String realm, String name) {
		return characterCache.get(ApiObjectDesc.of(zone, ApiType.character, realm, name).toString()).getContent();
	}

	@Override
	public WowGuild getGuild(String zone, String realm, String name) {
		return guildCache.get(ApiObjectDesc.of(zone, ApiType.guild, realm, name).toString()).getContent();
	}

	@Override
	public WowAuction getAuctions(String zone, String realm) {
		return auctionCache.get(ApiObjectDesc.of(zone, ApiType.auction, "data", realm).toString()).getContent();
	}
}
