package com.leroy.ronan.utils.cache;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CacheBuilder<T> {

    private CacheSpeed loading;
    private CacheSpeed reading;
    private Function<String, CacheResponse<T>> loader;
/*
    private Function<String, CacheResponse<T>> reader;
    private Function<String, CacheResponse<T>> getter;
  */  
    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;
    
    public CacheBuilder() {
        super();
    }

    public CacheBuilder<T> loading(CacheSpeed loading) {
        this.loading = loading;
        return this;
    }

    public CacheBuilder<T> reading(CacheSpeed reading) {
        this.reading = reading;
        return this;
    }

    public CacheBuilder<T> loadWith(Function<String, CacheResponse<T>> loader) {
        this.loader = loader;
        return this;
    }
    
    public CacheBuilder<T> keyToFile(Function<String, File> keyToFile){
        this.keyToFile = keyToFile;
        return this;
    }

    public CacheBuilder<T> fromFile(Function<File, T> fromFile){
        this.fromFile = fromFile;
        return this;
    }

    public CacheBuilder<T> toFile(BiConsumer<File, T> toFile){
        this.toFile = toFile;
        return this;
    }

    public Cache<T> build() {
        return new Cache<T>(keyToFile, fromFile, toFile);
    }
    
    
}
