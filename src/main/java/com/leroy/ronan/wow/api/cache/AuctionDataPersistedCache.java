package com.leroy.ronan.wow.api.cache;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.PersistedCacheType;
import com.leroy.ronan.utils.cache.simple.AbstractPersistedCache;
import com.leroy.ronan.wow.beans.WowAuctionsData;

public class AuctionDataPersistedCache extends AbstractPersistedCache<WowAuctionsData> {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	private String root;
	
	public AuctionDataPersistedCache(String name, String root) {
		super(PersistedCacheType.ASYNCHRONIZED,
			  name,
			  TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES),
			  TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS),
			  TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES),
			  TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
		this.root = root;
	}

	protected WowAuctionsData load(String key) {
		WowAuctionsData res = null;
		try {
			URI uri = new URI(key);
			res = new WowAuctionsData(ApiUtils.loadUri(uri));
		} catch (URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		return res;
	}
	
	protected File keyToFile(String key) {
		String[] parts = key.split("/");
		String zone = parts[2].split("\\.")[0];
		String name = parts[4];
		return new File(this.root+"/"+zone+"/auction/data/"+name+".json");
	}
	
	protected WowAuctionsData fromFile(File f){
    	String json = null;
    	try {
			json = new String(Files.readAllBytes(f.toPath()), "utf-8");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    	return new WowAuctionsData(json);
    }

	protected void toFile(File f, WowAuctionsData data){
		f.getParentFile().mkdirs();
		if (f.exists()){
			f.delete();
		}
		if (data != null){
			try {
				f.createNewFile();
				Files.write(f.toPath(), data.getJson().getBytes("utf-8"));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
    }

}
