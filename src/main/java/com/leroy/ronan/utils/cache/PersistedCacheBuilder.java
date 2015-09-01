package com.leroy.ronan.utils.cache;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.leroy.ronan.utils.cache.simple.AsynchronizedCache;
import com.leroy.ronan.utils.cache.simple.SimpleCache;
import com.leroy.ronan.utils.cache.simple.SynchronizedCache;

public class PersistedCacheBuilder<T> {

	private Function<String, T> load;
    private BiFunction<T, Long, Boolean> isExpired;
    private long timeToLiveAfterError;
    private long timeToLiveAfterSuccess;
    
    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;

    private boolean synchro = false;
    private boolean asynchro = false;
    
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
    
	public void synchro() {
		this.synchro = true;
	}

	public void asynchro() {
		this.asynchro = true;
	}


    public PersistedCache<T> build() {
    	if (asynchro) {
    		return new AsynchronizedCache<T>(load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
    	} else if (synchro){
    		return new SynchronizedCache<T>(load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
    	} else {
    		return new SimpleCache<T>(load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
    	}
    }


}
