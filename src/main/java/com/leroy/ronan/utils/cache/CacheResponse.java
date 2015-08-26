package com.leroy.ronan.utils.cache;

public class CacheResponse<T> {

    private T content;
    
    public CacheResponse(T content){
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public boolean isExpired() {
        return false;
    }
}
