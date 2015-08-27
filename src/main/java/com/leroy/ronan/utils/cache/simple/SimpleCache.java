package com.leroy.ronan.utils.cache.simple;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.beans.LoadedEntry;
import com.leroy.ronan.utils.cache.beans.MemoryEntry;
import com.leroy.ronan.utils.cache.beans.PersistedEntry;

public class SimpleCache<T> {

	private Function<String, T> load;
	private BiFunction<T, Long, Boolean> isExpired;

    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;
    
	private Map<String, SoftReference<MemoryEntry<T>>> memory;
    
    public SimpleCache(Function<String, T> load, BiFunction<T, Long, Boolean> isExpired, 
    			 Function<String, File> keyToFile, Function<File, T> fromFile, BiConsumer<File, T> toFile) {
        super();
        this.load = load;
        this.isExpired = isExpired;
        
        this.keyToFile = keyToFile;
        this.fromFile = fromFile;
        this.toFile = toFile;
        
        this.memory = new HashMap<>();
    }

    public CacheResponse<T> get(String key) {
        CacheResponse<T> response;
        
        MemoryEntry<T> memorized = recall(key);
        if (memorized == null) {
            PersistedEntry<T> persisted = read(key);
            if (persisted == null) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    response = learn(key,  null, 0l);
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), System.currentTimeMillis());
                }
            } else if (persisted.isExpired()) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    response = learn(key, persisted.getContent(), persisted.getLastModified());
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), System.currentTimeMillis());
                }
            } else {
                response = learn(key, persisted.getContent(), persisted.getLastModified());
            }
        } else if (memorized.isExpired()) {
            PersistedEntry<T> persisted = read(key);
            if (persisted == null) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    write(key, memorized.getContent());
                    response = learn(key,  memorized.getContent(), 0l);
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), System.currentTimeMillis());
                }
            } else if (persisted.isExpired()) {
                LoadedEntry<T> loaded = load(key);
                if (loaded.getContent() == null){
                    response = learn(key, memorized.getContent(), 0l);
                } else {
                    write(key, loaded.getContent());
                    response = learn(key, loaded.getContent(), System.currentTimeMillis());
                }
            } else {
                response = learn(key, persisted.getContent(), persisted.getLastModified());
            }
        } else {
            response = memorized;
        }
        
        return response;
    }
    
    protected MemoryEntry<T> recall(String key){
        return Optional.ofNullable(memory.get(key)).map(SoftReference::get).orElse(null);
    }
    
    protected MemoryEntry<T> learn(String key, T value, long lastModified){
        MemoryEntry<T> entry = new MemoryEntry<T>(value, lastModified, isExpired);
        memory.put(key, new SoftReference<MemoryEntry<T>>(entry));
        return entry;
    }
    
    private PersistedEntry<T> read(String key){
        PersistedEntry<T> res = null;
        File f = keyToFile.apply(key);
        if (f.exists()) {
            T value = fromFile.apply(f);
            res = new PersistedEntry<T>(value, f.lastModified(), isExpired);
        }
        return res;
    }
    
    private void write(String key, T value){
        File f = keyToFile.apply(key);
        f.getParentFile().mkdirs();
        if (f.exists()){
            f.delete();
        }
        if (value != null){
            toFile.accept(f, value);
        }
    }
    
    private LoadedEntry<T> load(String key){
        T value = this.load.apply(key);
        return new LoadedEntry<T>(value);
    }

}
