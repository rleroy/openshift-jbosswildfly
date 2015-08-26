package com.leroy.ronan.utils.cache;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CacheBuilder<T> {

	private CacheSpeed loading;
	private CacheSpeed reading;
	private CacheSpeed writing;

	private Function<String, T> load;

    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;
	private BiFunction<T, Long, Boolean> isExpired;

    
    public CacheBuilder() {
        super();
    }

	public CacheBuilder<T> loading(CacheSpeed speed) {
		this.loading = speed;
        return this;
	}

	public CacheBuilder<T> reading(CacheSpeed speed) {
		this.reading = speed;
        return this;
	}

	public CacheBuilder<T> writing(CacheSpeed speed) {
		this.writing = speed;
        return this;
	}

	public CacheBuilder<T> loader(Function<String, T> load) {
        this.load = load;
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

    public CacheBuilder<T> isExpired(BiFunction<T, Long, Boolean> isExpired){
        this.isExpired = isExpired;
        return this;
    }

    public Cache<T> build() {
        return new Cache<T>(loading, reading, writing, load, keyToFile, fromFile, toFile, isExpired);
    }

}
