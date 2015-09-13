package com.leroy.ronan.wow.api.cache;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;


public class AuctionDataPersistedCacheTest {

	@Test
	public void keyToFileTest(){
		AuctionDataPersistedCache cache = new AuctionDataPersistedCache("test", "apifiles");
		File f = cache.keyToFile("http://eu.battle.net/auction-data/2258948a184dc11ddc78b107afc423bd/auctions.json");
		File expected = new File("apifiles/eu/auction/data/2258948a184dc11ddc78b107afc423bd.json");
		Assert.assertEquals(expected.toPath(), f.toPath());
	}
	
}
