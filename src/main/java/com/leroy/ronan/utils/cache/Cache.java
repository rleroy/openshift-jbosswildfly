package com.leroy.ronan.utils.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Cache<T> {

    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;
    
    private Map<String, SoftReference<CacheResponse<T>>> memory;
    
    public Cache(Function<String, File> keyToFile, Function<File, T> fromFile, BiConsumer<File, T> toFile) {
        super();
        this.keyToFile = keyToFile;
        this.fromFile = fromFile;
        this.toFile = toFile;
        this.memory = new HashMap<>();
    }

    public CacheResponse<T> get(String key) {
        CacheResponse<T> result;
        
        CacheResponse<T> memorized = getInMemory(key);
        if (memorized == null){
            result = null;
        } else if (memorized.isExpired()){
            result = null;
        } else {
            result = memorized;
        }
        return result;
    }

    protected CacheResponse<T> getInMemory(String key) {
        CacheResponse<T> response = null;
        if (memory.containsKey(key)){
            response = memory.get(key).get();
        }
        return response;
    }
    
    protected void setInMemory(String key, CacheResponse<T> value) {
        memory.put(key, new SoftReference<CacheResponse<T>>(value));
    }

    protected CacheResponse<T> getInPersistence(String key) {
        File f = keyToFile.apply(key);
        T content = null;
        if (f.exists()){
            content = fromFile.apply(f);
        }
        return new CacheResponse<T>(content);
    }

    public void setInPersistence(String key, CacheResponse<T> value) {
        File f = keyToFile.apply(key);
        f.getParentFile().mkdirs();
        if (f.exists()){
            f.delete();
        }
        if (value.getContent() != null){
            toFile.accept(f, value.getContent());
        }
    }

}
