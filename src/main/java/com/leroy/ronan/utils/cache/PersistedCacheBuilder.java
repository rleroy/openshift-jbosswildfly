package com.leroy.ronan.utils.cache;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.leroy.ronan.utils.cache.simple.AsynchronizedCache;
import com.leroy.ronan.utils.cache.simple.SimpleCache;
import com.leroy.ronan.utils.cache.simple.SynchronizedCache;

public class PersistedCacheBuilder<T> {

	private String name;
	private Function<String, T> load;
    private BiFunction<T, Long, Boolean> isExpired;
    private long timeToLiveAfterError;
    private long timeToLiveAfterSuccess;
    private long timeToWaitResponse;
    private long timeToLiveIfNoResponse;
    
    private Function<String, File> keyToFile;
    private Function<File, T> fromFile;
    private BiConsumer<File, T> toFile;

    private PersistedCacheType type = PersistedCacheType.SIMPLE;
    
    public PersistedCacheBuilder() {
        super();
    }

	public PersistedCacheBuilder<T> name(String name) {
        this.name = name;
        return this;
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
    
    public PersistedCacheBuilder<T> timeToWaitResponse(long timeToWaitResponse){
        this.timeToWaitResponse = timeToWaitResponse;
        return this;
    }
    
    public PersistedCacheBuilder<T> timeToLiveIfNoResponse(long timeToLiveIfNoResponse){
        this.timeToLiveIfNoResponse = timeToLiveIfNoResponse;
        return this;
    }
    
	public PersistedCacheBuilder<T> synchro() {
		this.type = PersistedCacheType.SYNCHRONIZED;
		return this;
	}

	public PersistedCacheBuilder<T> asynchro() {
		this.type = PersistedCacheType.ASYNCHRONIZED;
		return this;
	}

	public PersistedCacheBuilder<T> simple() {
		this.type = PersistedCacheType.SIMPLE;
		return this;
	}
	
    public PersistedCache<T> build() {
    	PersistedCache<T> res;
		switch (type){
		case ASYNCHRONIZED:
			res = new AsynchronizedCache<T>(name, load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, timeToWaitResponse, timeToLiveIfNoResponse, keyToFile, fromFile, toFile);
			break;
		case SYNCHRONIZED:
			res = new SynchronizedCache<T>(name, load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
			break;
		case SIMPLE:
		default:
			res = new SimpleCache<T>(name, load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
			break;
		}
		return res;
    }


}
