package com.leroy.ronan.wow.api.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;
import com.leroy.ronan.wow.api.ApiClient;
import com.leroy.ronan.wow.api.ApiKey;
import com.leroy.ronan.wow.api.ApiResponse;
import com.leroy.ronan.wow.api.ApiType;
import com.leroy.ronan.wow.beans.WowAuction;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public class ApiClientCached implements ApiClient {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private String zone;
	private String locale;
	private String apiKey;
	private String root;
	private PersistedCache<String> cacheAPI;
	
	public ApiClientCached(String zone, String locale, String apiKey, String root){
		this.zone = zone;
		this.locale = locale;
		this.apiKey = apiKey;
		this.root = root;
        PersistedCacheBuilder<String> builder = new PersistedCacheBuilder<>();
        cacheAPI = builder
                .asynchro()
                .name("api")
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .loader(this::load)
                .keyToFile(this::keyToFile)
                .fromFile(this::fromFile)
                .toFile(this::toFile)
                .build()
                ;
	}
	
	@Override
	public WowCharacter getCharacter(String realm, String name) {
        String data = cacheAPI.get(ApiKey.of(ApiType.character, realm, name).getPath()).getContent();
        return new WowCharacter(data);
	}

	@Override
	public WowGuild getGuild(String realm, String name) {
        String data = cacheAPI.get(ApiKey.of(ApiType.guild, realm, name).getPath()).getContent();
        return new WowGuild(data);
	}

	@Override
	public WowAuction getAuctions(String realm) {
		return null;
	}

	private String load(String key){
		return this.load(this.zone, ApiKey.of(key)); 
	}
	
	private String load(String zone, ApiKey key){
        List<String> optionsList = new ArrayList<>();
        optionsList.add("locale="+locale);
        optionsList.add("apikey="+apiKey);
        if (key.getType().getFields() != null && key.getType().getFields().size() > 0){
            optionsList.addAll(key.getType().getFields().stream().map(s -> "fields=" + s).collect(Collectors.toList()));
        }
        String options = String.join("&", optionsList); 
        String data = null;
		try {
			URI uri = new URI("https", zone+".api.battle.net", "/wow/"+key.getPath(), options, null);
			URL url = uri.toURL();
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			data = "";
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
			    data += inputLine;
			}
			br.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        return data;
	}
	
    private File keyToFile(String key){
    	return new File(this.root+"/"+this.zone+"/"+ApiKey.of(key).getPath()+".json");
    }

    private String fromFile(File f){
    	String res = null;
    	try {
			res = new String(Files.readAllBytes(f.toPath()), "utf-8");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    	return res;
    }

    private void toFile(File f, String data){
		f.getParentFile().mkdirs();
		if (f.exists()){
			f.delete();
		}
		if (data != null){
			try {
				f.createNewFile();
				Files.write(f.toPath(), data.getBytes("utf-8"));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
    }

}
