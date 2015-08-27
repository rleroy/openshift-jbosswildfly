package com.leroy.ronan.utils.cache.beans;


public abstract class CacheEntry<T> {

    private T content;

    public CacheEntry(T content) {
        super();
        this.content = content;
    }
    
    public T getContent() {
        return content;
    }
    
}
