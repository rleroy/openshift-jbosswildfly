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
import com.leroy.ronan.wow.api.ApiType;
import com.leroy.ronan.wow.beans.WowAuction;
import com.leroy.ronan.wow.beans.WowCharacter;
import com.leroy.ronan.wow.beans.WowGuild;

public class ApiClientCached implements ApiClient {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	private String locale;
	private String apiKey;
	private String root;
	
	private PersistedCache<WowCharacter> characterCache;
	private PersistedCache<WowGuild> guildCache;
	
	public ApiClientCached(String locale, String apiKey, String root){
		this.locale = locale;
		this.apiKey = apiKey;
		this.root = root;
        
        PersistedCacheBuilder<WowCharacter> builderCharacters = new PersistedCacheBuilder<>();
        characterCache = builderCharacters
                .asynchro()
                .name("api-characters")
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .loader(k -> new WowCharacter(this.load(k)))
                .keyToFile(this::keyToFile)
                .fromFile(f -> new WowCharacter(this.fromFile(f)))
                .toFile((f, c) -> this.toFile(f, c.getJson()))
                .build()
                ;

        PersistedCacheBuilder<WowGuild> builderGuilds = new PersistedCacheBuilder<>();
        guildCache = builderGuilds
                .asynchro()
                .name("api-guilds")
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .loader(k -> new WowGuild(this.load(k)))
                .keyToFile(this::keyToFile)
                .fromFile(f -> new WowGuild(this.fromFile(f)))
                .toFile((f, c) -> this.toFile(f, c.getJson()))
                .build()
                ;

	}
	
	@Override
	public WowCharacter getCharacter(String zone, String realm, String name) {
		return characterCache.get(ApiKey.of(zone, ApiType.character, realm, name).toString()).getContent();
	}

	@Override
	public WowGuild getGuild(String zone, String realm, String name) {
		return guildCache.get(ApiKey.of(zone, ApiType.guild, realm, name).toString()).getContent();
	}

	@Override
	public WowAuction getAuctions(String zone, String realm) {
		return null;
	}
	
	private String load(String key){
    	ApiKey apikey = ApiKey.of(key);
        List<String> optionsList = new ArrayList<>();
        optionsList.add("locale="+locale);
        optionsList.add("apikey="+apiKey);
        if (apikey.getType().getFields() != null && apikey.getType().getFields().size() > 0){
            optionsList.addAll(apikey.getType().getFields().stream().map(s -> "fields=" + s).collect(Collectors.toList()));
        }
        String options = String.join("&", optionsList); 
        String data = null;
		try {
			URI uri = new URI("https", apikey.getZone()+".api.battle.net", "/wow/"+apikey.getType()+"/"+apikey.getRealm()+"/"+apikey.getName(), options, null);
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
    	ApiKey apikey = ApiKey.of(key);
    	return new File(this.root+"/"+apikey.getZone()+"/"+apikey.getType()+"/"+apikey.getRealm()+"/"+apikey.getName()+".json");
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
