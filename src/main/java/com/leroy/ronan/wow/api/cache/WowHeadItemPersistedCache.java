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
import com.leroy.ronan.wow.beans.WowHeadItem;

public class WowHeadItemPersistedCache extends AbstractPersistedCache<WowHeadItem>{

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	private String root;

	public WowHeadItemPersistedCache(String name, String root) {
		super(PersistedCacheType.ASYNCHRONIZED, 
				name, 
				TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES),
				TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS),
				TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES),
				TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
		this.root = root;
	}

	@Override
	protected WowHeadItem load(String key) {
		WowHeadItem res = null;
		try {
			URI uri = new URI("http://www.wowhead.com/item="+key+"&xml");
			res = new WowHeadItem(ApiUtils.loadUri(uri));
		} catch (URISyntaxException e) {
			log.error(e.getMessage(), e);
		}
		return res;
	}

	@Override
	protected File keyToFile(String key) {
    	return new File(this.root+"/wowhead/item/"+key+".xml");
	}

	protected WowHeadItem fromFile(File f){
    	String xml = null;
    	try {
			xml = new String(Files.readAllBytes(f.toPath()), "utf-8");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    	return new WowHeadItem(xml);
    }

	protected void toFile(File f, WowHeadItem data){
		f.getParentFile().mkdirs();
		if (f.exists()){
			f.delete();
		}
		if (data != null){
			try {
				f.createNewFile();
				Files.write(f.toPath(), data.xml().getBytes("utf-8"));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
    }
}
