package com.leroy.ronan.utils.cache;


public interface CacheResponse<T> {

    public T getContent();
    public long getCreated();
    public long getTimeToLive();
    public boolean isExpired();

}
