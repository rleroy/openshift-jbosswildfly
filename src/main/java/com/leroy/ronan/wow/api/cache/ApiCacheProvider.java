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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;
import com.leroy.ronan.wow.api.ApiObjectDesc;
import com.leroy.ronan.wow.beans.WowJson;

public class ApiCacheProvider<T extends WowJson> {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	private String locale;
	private String apikey;
	private String root;

	private PersistedCacheBuilder<T> builder;
	
	public ApiCacheProvider(String locale, String apikey, String root){
		super();
		this.locale = locale;
		this.apikey = apikey;
		this.root = root;
		this.builder = new PersistedCacheBuilder<>();
	}

	public PersistedCache<T> get(String name, Function<String, T> fromString) {
		return builder
                .asynchro()
                .name(name)
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS))
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .loader(k -> fromString.apply(this.load(k)))
                .keyToFile(this::keyToFile)
                .fromFile(f -> fromString.apply(this.fromFile(f)))
                .toFile(this::toFile)
                .build();
	}

	private String load(String key){
    	ApiObjectDesc desc = ApiObjectDesc.of(key);
        List<String> optionsList = new ArrayList<>();
        optionsList.add("locale="+locale);
        optionsList.add("apikey="+apikey);
        if (desc.getType().getFields() != null && desc.getType().getFields().size() > 0){
            optionsList.addAll(desc.getType().getFields().stream().map(s -> "fields=" + s).collect(Collectors.toList()));
        }
        String options = String.join("&", optionsList); 
        String data = null;
		try {
			URI uri = new URI("https", desc.getZone()+".api.battle.net", "/wow/"+desc.getType()+"/"+desc.getRealm()+"/"+desc.getName(), options, null);
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
    	ApiObjectDesc desc = ApiObjectDesc.of(key);
    	return new File(this.root+"/"+desc.getZone()+"/"+desc.getType()+"/"+desc.getRealm()+"/"+desc.getName()+".json");
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

    private void toFile(File f, T t){
		f.getParentFile().mkdirs();
		if (f.exists()){
			f.delete();
		}
		if (t != null){
			try {
				f.createNewFile();
				Files.write(f.toPath(), t.getJson().getBytes("utf-8"));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
    }
}
