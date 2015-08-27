package com.leroy.ronan.utils.cache;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.leroy.ronan.utils.cache.simple.SimpleCache;

public class CacheBuilder<T> {

	private Function<String, T> load;
    private BiFunction<T, Long, Boolean> isExpired;

    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;

    public CacheBuilder() {
        super();
    }

	public CacheBuilder<T> loader(Function<String, T> load) {
        this.load = load;
        return this;
	}

    public CacheBuilder<T> isExpired(BiFunction<T, Long, Boolean> isExpired){
        this.isExpired = isExpired;
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

    public SimpleCache<T> build() {
        return new SimpleCache<T>(load, isExpired, keyToFile, fromFile, toFile);
    }

}
