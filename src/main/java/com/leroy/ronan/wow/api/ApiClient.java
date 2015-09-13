package com.leroy.ronan.wow.api;

import com.leroy.ronan.wow.beans.WowAuction;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public interface ApiClient {

	public WowCharacter getCharacter(String zone, String realm, String name);
	public WowGuild getGuild(String zone, String realm, String name);
	public WowAuction getAuctions(String zone, String realm);

}
