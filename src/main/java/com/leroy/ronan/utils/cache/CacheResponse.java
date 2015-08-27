package com.leroy.ronan.utils.cache;


public interface CacheResponse<T> {

    public T getContent();
    public boolean isExpired();

}
