package com.leroy.ronan.utils.cache.simple;

import java.io.File;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;
import com.leroy.ronan.utils.cache.PersistedCacheType;
import com.leroy.ronan.utils.cache.beans.MemoryEntry;

public abstract class AbstractPersistedCache<T> implements PersistedCache<T>{

	private PersistedCache<T> cache;
	
	public AbstractPersistedCache(PersistedCacheType type, String name,
			long timeToLiveAfterError, long timeToLiveAfterSuccess,
			long timeToWaitResponse, long timeToLiveIfNoResponse) {
		PersistedCacheBuilder<T> builder = new PersistedCacheBuilder<>();
		switch (type){
		case ASYNCHRONIZED:
			builder = builder.asynchro();
			break;
		case SYNCHRONIZED:
			builder.synchro();
			break;
		case SIMPLE:
		default:
			break;
		}
		cache = builder.name(name)
			.timeToLiveAfterError(timeToLiveAfterError)
			.timeToLiveAfterSuccess(timeToLiveAfterSuccess)
			.timeToWaitResponse(timeToWaitResponse)
			.timeToLiveIfNoResponse(timeToLiveIfNoResponse)
			.loader(this::load)
			.keyToFile(this::keyToFile)
			.fromFile(this::fromFile)
			.toFile(this::toFile)
			.build();
	}
	
	protected abstract T load(String key);
	protected abstract File keyToFile(String key);
	protected abstract T fromFile(File key);
	protected abstract void toFile(File key, T data);

	@Override
	public CacheResponse<T> get(String key) {
		return cache.get(key);
	}

	@Override
	public MemoryEntry<T> recall(String key) {
		return cache.recall(key);
	}

	@Override
	public MemoryEntry<T> learn(String key, T value, long timeToLive) {
		return cache.learn(key, value, timeToLive);
	}
}	
