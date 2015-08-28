package com.leroy.ronan.utils.cache.beans;

import com.leroy.ronan.utils.cache.CacheResponse;

public class MemoryEntry<T> extends CacheEntry<T> implements CacheResponse<T> {

    private long created;
    private long timeToLive;

    public MemoryEntry(T content, long timeToLive) {
        super(content);
        this.timeToLive = timeToLive;
        this.created = System.currentTimeMillis();
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public long getTimeToLive() {
        return created + timeToLive - System.currentTimeMillis();
    }

    @Override
    public boolean isExpired() {
        return getTimeToLive() < 0;
    }
    
}
