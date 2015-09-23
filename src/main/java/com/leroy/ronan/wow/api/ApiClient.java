package com.leroy.ronan.wow.api;

import com.leroy.ronan.wow.beans.WowAuctionsData;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;
import com.leroy.ronan.wow.beans.WowHeadItem;
import com.leroy.ronan.wow.beans.WowItem;

public interface ApiClient {

	public WowCharacter getCharacter(String zone, String realm, String name);
	public WowGuild getGuild(String zone, String realm, String name);
	public WowAuctionsData getAuctions(String zone, String realm);
	public WowItem getItem(String zone, Long id);
	public WowHeadItem getWowHeadItem(Long id);
	
}
