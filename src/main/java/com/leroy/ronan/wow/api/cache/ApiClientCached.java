package com.leroy.ronan.wow.api.cache;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.wow.api.ApiClient;
import com.leroy.ronan.wow.api.ApiObjectDesc;
import com.leroy.ronan.wow.api.ApiType;
import com.leroy.ronan.wow.beans.WowAuctions;
import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;
import com.leroy.ronan.wow.beans.WowHeadItem;
import com.leroy.ronan.wow.beans.WowItem;

public class ApiClientCached implements ApiClient {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private PersistedCache<WowCharacter> characterCache;
	private PersistedCache<WowGuild> guildCache;
	private PersistedCache<WowAuctions> auctionCache;
	private PersistedCache<WowAuctionsData> auctionDataCache;
	private PersistedCache<WowItem> itemDataCache;
	private PersistedCache<WowHeadItem> wowheadItemCache;
	
	public ApiClientCached(String locale, String apikey, String root){
		characterCache = (new ApiCacheProvider<WowCharacter>(locale, apikey, root)).get("api-characters", s -> new WowCharacter(s));
		guildCache = (new ApiCacheProvider<WowGuild>(locale, apikey, root)).get("api-guilds", s -> new WowGuild(s));
		auctionCache = (new ApiCacheProvider<WowAuctions>(locale, apikey, root)).get("api-auctions", s -> new WowAuctions(s));
		itemDataCache = (new ApiCacheProvider<WowItem>(locale, apikey, root)).get("api-item", s -> new WowItem(s), TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
		
		auctionDataCache = new AuctionDataPersistedCache("api-actionsdata", root);
		
		wowheadItemCache = new WowHeadItemPersistedCache("wowhead-item", root);
	}
	
	@Override
	public WowCharacter getCharacter(String zone, String realm, String name) {
		WowCharacter res = null;
		try{
			res = characterCache.get(ApiObjectDesc.of(zone, ApiType.character, realm, name).toString()).getContent();
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return res;
	}

	@Override
	public WowGuild getGuild(String zone, String realm, String name) {
		return guildCache.get(ApiObjectDesc.of(zone, ApiType.guild, realm, name).toString()).getContent();
	}

	@Override
	public WowAuctionsData getAuctions(String zone, String realm) {
		WowAuctions auction = auctionCache.get(ApiObjectDesc.of(zone, ApiType.auction, "data", realm).toString()).getContent();
		return auctionDataCache.get(auction.getUrl()).getContent();
	}

	@Override
	public WowItem getItem(String zone, Long id) {
		return itemDataCache.get(ApiObjectDesc.of(zone, ApiType.item, null, String.valueOf(id)).toString()).getContent();
	}
	
	@Override
	public WowHeadItem getWowHeadItem(Long id) {
		return wowheadItemCache.get(String.valueOf(id)).getContent();
	}
	
	

}
