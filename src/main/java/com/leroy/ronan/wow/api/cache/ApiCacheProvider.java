package com.leroy.ronan.wow.api.cache;

import java.io.File;
import java.net.URI;
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
		return get(name, fromString, TimeUnit.MILLISECONDS.convert(6, TimeUnit.HOURS));
	}

	public PersistedCache<T> get(String name, Function<String, T> fromString, long ttl) {
		return builder
                .asynchro()
                .name(name)
                .timeToLiveAfterError(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
                .timeToLiveAfterSuccess(ttl)
                .timeToWaitResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .timeToLiveIfNoResponse(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES))
                .loader(k -> fromString.apply(this.load(k)))
                .keyToFile(this::keyToFile)
                .fromFile(f -> fromString.apply(ApiUtils.fromFile(f)))
                .toFile(ApiUtils::toFile)
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
			String path = "/wow/"+desc.getType();
			if (desc.getRealm() != null) {
				path += "/"+desc.getRealm();
			}
			path += "/"+desc.getName();
			URI uri = new URI("https", desc.getZone()+".api.battle.net", path, options, null);
			data = ApiUtils.loadUri(uri);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        return data;
	}

    private File keyToFile(String key){
    	ApiObjectDesc desc = ApiObjectDesc.of(key);
    	return new File(this.root+"/"+desc.toString()+".json");
    }
}
