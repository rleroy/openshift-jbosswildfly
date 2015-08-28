package com.leroy.ronan.utils.cache;

import com.leroy.ronan.utils.cache.beans.MemoryEntry;

public interface PersistedCache<T> {

    public CacheResponse<T> get(String key);
    public MemoryEntry<T> recall(String key);
    public MemoryEntry<T> learn(String key, T value, long timeToLive);
    
}
