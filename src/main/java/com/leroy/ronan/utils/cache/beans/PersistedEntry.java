package com.leroy.ronan.utils.cache.beans;

import java.util.function.BiFunction;

public class PersistedEntry<T> extends CacheEntry<T> {

    private long lastModified;
    private BiFunction<T, Long, Boolean> isExpired;
    
    public PersistedEntry(T content, long lastModified, BiFunction<T, Long, Boolean> isExpired) {
        super(content);
        this.lastModified = lastModified;
        this.isExpired = isExpired;
    }

    public boolean isExpired() {
        return isExpired.apply(getContent(), lastModified);
    }

    public long getLastModified() {
        return lastModified;
    }

}
