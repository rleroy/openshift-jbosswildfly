package com.leroy.ronan.utils.cache;

import java.util.function.BiFunction;

public class CacheResponse<T> {

    private T content;
    private long loaded;
    private BiFunction<T, Long, Boolean> isExpired;
    
    public CacheResponse(T content, long loaded, BiFunction<T, Long, Boolean> isExpired){
        this.content = content;
        this.loaded = loaded;
        this.isExpired = isExpired;
    }

    public T getContent() {
        return content;
    }

    public boolean isExpired() {
    	return isExpired.apply(content, loaded);
    }
}
