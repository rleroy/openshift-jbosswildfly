package com.leroy.ronan.utils.cache.simple;

import java.io.File;
import java.lang.ref.SoftReference;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.beans.LoadedEntry;
import com.leroy.ronan.utils.cache.beans.MemoryEntry;
import com.leroy.ronan.utils.cache.beans.PersistedEntry;

public class SimpleCache<T> implements PersistedCache<T> {

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private String name;
    
	private Function<String, T> load;
	private BiFunction<T, Long, Boolean> isExpired;
    private long timeToLiveAfterError;
    private long timeToLiveAfterSuccess;

    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;
    
	private Map<String, SoftReference<MemoryEntry<T>>> memory;
    
    public SimpleCache(String name,
    			Function<String, T> load, 
    			BiFunction<T, Long, Boolean> isExpired, 
    			long timeToLiveAfterError, 
    			long timeToLiveAfterSuccess,
    			Function<String, File> keyToFile, 
    			Function<File, T> fromFile,
    			BiConsumer<File, T> toFile) {
        super();
        this.name = name;
        this.load = load;
        if (isExpired != null){
            this.isExpired = isExpired;
        } else {
        	this.isExpired = this::isExpiredDefault;
        }
        this.timeToLiveAfterError = timeToLiveAfterError;
        this.timeToLiveAfterSuccess = timeToLiveAfterSuccess;
        
        this.keyToFile = keyToFile;
        this.fromFile = fromFile;
        this.toFile = toFile;
        
        this.memory = new HashMap<>();
    }

    public CacheResponse<T> get(String key) {
    	log.debug(name + ": get("+key+")");
        CacheResponse<T> response;
        MemoryEntry<T> memorized = recall(key);
        if (memorized == null) {
            PersistedEntry<T> persisted = read(key);
            if (persisted == null) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    response = learn(key,  null, timeToLiveAfterError);
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), timeToLiveAfterSuccess);
                }
            } else if (persisted.isExpired()) {
                response = learn(key, persisted.getContent(), timeToLiveAfterError);
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() != null){
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), timeToLiveAfterSuccess);
                }
            } else {
                response = learn(key, persisted.getContent(), persisted.getLastModified() + timeToLiveAfterSuccess - System.currentTimeMillis());
            }
        } else if (memorized.isExpired()) {
            PersistedEntry<T> persisted = read(key);
            if (persisted == null) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    write(key, memorized.getContent());
                    response = learn(key,  memorized.getContent(), timeToLiveAfterError);
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), timeToLiveAfterSuccess);
                }
            } else if (persisted.isExpired()) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    response = learn(key, memorized.getContent(), timeToLiveAfterError);
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), timeToLiveAfterSuccess);
                }
            } else {
                response = learn(key, persisted.getContent(), persisted.getLastModified() + timeToLiveAfterSuccess - System.currentTimeMillis());
            }
        } else {
            response = memorized;
        }
    	log.debug(name + ": get("+key+") <- " +response);
    	return response;
    }
    
    public MemoryEntry<T> recall(String key) {
        MemoryEntry<T> res = Optional.ofNullable(memory.get(key)).map(SoftReference::get).orElse(null);
        return res;
    }
    
    public MemoryEntry<T> learn(String key, T value, long timeToLive) {
        MemoryEntry<T> entry = new MemoryEntry<T>(value, timeToLive);
        memory.put(key, new SoftReference<MemoryEntry<T>>(entry));
        return entry;
    }
    
    private PersistedEntry<T> read(String key) {
    	log.debug(name + ": read("+key+")");
    	PersistedEntry<T> res = null;
        File f = keyToFile.apply(key);
        if (f.exists()) {
            T value = fromFile.apply(f);
            res = new PersistedEntry<T>(value, f.lastModified(), isExpired);
        }
    	log.debug(name + ": read("+key+") <- " +res);
        return res;
    }
    
    private void write(String key, T value) {
        File f = keyToFile.apply(key);
        f.getParentFile().mkdirs();
        if (f.exists()){
            f.delete();
        }
        if (value != null){
            toFile.accept(f, value);
        }
    }
    
    private LoadedEntry<T> load(String key) {
    	log.debug(name + ": load("+key+")");
    	T value = this.load.apply(key);
    	log.debug(name + ": load("+key+") <- " + value);
        return new LoadedEntry<T>(value);
    }

    private boolean isExpiredDefault(T t, long lastmodified){
    	boolean res = false;
    	LocalDateTime expirationDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastmodified + timeToLiveAfterSuccess), ZoneId.systemDefault());
    	LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    	if (now.isAfter(expirationDate)){
    		res = true;
    	}
    	return res;
    }

}
