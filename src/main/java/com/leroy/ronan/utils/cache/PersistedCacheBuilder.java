package com.leroy.ronan.utils.cache;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.simple.SimpleCache;

public class PersistedCacheBuilder<T> {

	private Function<String, T> load;
    private BiFunction<T, Long, Boolean> isExpired;
    private long timeToLiveAfterError;
    private long timeToLiveAfterSuccess;
    
    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;

    public PersistedCacheBuilder() {
        super();
    }

	public PersistedCacheBuilder<T> loader(Function<String, T> load) {
        this.load = load;
        return this;
	}

    public PersistedCacheBuilder<T> isExpired(BiFunction<T, Long, Boolean> isExpired){
        this.isExpired = isExpired;
        return this;
    }

	public PersistedCacheBuilder<T> keyToFile(Function<String, File> keyToFile){
        this.keyToFile = keyToFile;
        return this;
    }

    public PersistedCacheBuilder<T> fromFile(Function<File, T> fromFile){
        this.fromFile = fromFile;
        return this;
    }

    public PersistedCacheBuilder<T> toFile(BiConsumer<File, T> toFile){
        this.toFile = toFile;
        return this;
    }
    
    public PersistedCacheBuilder<T> timeToLiveAfterError(long timeToLiveAfterError){
        this.timeToLiveAfterError = timeToLiveAfterError;
        return this;
    }
    
    public PersistedCacheBuilder<T> timeToLiveAfterSuccess(long timeToLiveAfterSuccess){
        this.timeToLiveAfterSuccess = timeToLiveAfterSuccess;
        return this;
    }

    public PersistedCache<T> build() {
        return new SimpleCache<T>(load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
    }
}
