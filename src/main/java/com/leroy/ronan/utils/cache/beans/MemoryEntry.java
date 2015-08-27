package com.leroy.ronan.utils.cache.beans;

import java.util.function.BiFunction;

import com.leroy.ronan.utils.cache.CacheResponse;

public class MemoryEntry<T> extends CacheEntry<T> implements CacheResponse<T>, ExpirableEntry{

    private long lastModified;
    private long created;
    private BiFunction<T, Long, Boolean> isExpired;

    public MemoryEntry(T content, long lastModified, BiFunction<T, Long, Boolean> isExpired) {
        super(content);
        this.lastModified = lastModified;
        this.isExpired = isExpired;
        this.created = System.currentTimeMillis();
    }

    @Override
    public boolean isExpired() {
        boolean res = false;
        if (System.currentTimeMillis() - created > 100){
            res = isExpired.apply(getContent(), lastModified);
        }
        return res;
    }
}
