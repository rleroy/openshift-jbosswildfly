package com.leroy.ronan.utils.cache.beans;

import com.leroy.ronan.utils.cache.CacheResponse;

public class MemoryEntry<T> extends CacheEntry<T> implements CacheResponse<T>, ExpirableEntry{

    private long created;
    private long timeToLive;

    public MemoryEntry(T content, long timeToLive) {
        super(content);
        this.timeToLive = timeToLive;
        this.created = System.currentTimeMillis();
    }

    @Override
    public boolean isExpired() {
        boolean res = false;
        if (System.currentTimeMillis() - created > timeToLive){
        	res = true;
        }
        return res;
    }
}
