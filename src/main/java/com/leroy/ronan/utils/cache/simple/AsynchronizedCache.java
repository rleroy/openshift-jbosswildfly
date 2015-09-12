package com.leroy.ronan.utils.cache.simple;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.log4j.Logger;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.beans.MemoryEntry;

public class AsynchronizedCache<T> extends SynchronizedCache<T>{

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private long timeToWaitResponse;
    private long timeToLiveIfNoResponse;
    
	public AsynchronizedCache(String name, Function<String, T> load, BiFunction<T, Long, Boolean> isExpired,
			long timeToLiveAfterError, long timeToLiveAfterSuccess, long timeToWaitResponse, long timeToLiveIfNoResponse,
			Function<String, File> keyToFile, Function<File, T> fromFile, BiConsumer<File, T> toFile) {
		super(name, load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
		this.timeToWaitResponse = timeToWaitResponse;
		this.timeToLiveIfNoResponse = timeToLiveIfNoResponse;
	}

	@Override
	public CacheResponse<T> get(String key) {
		CacheResponse<T> res = null;
		CompletableFuture<CacheResponse<T>> future = CompletableFuture.supplyAsync(() -> super.get(key));
		try {
			res = future.get(timeToWaitResponse, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			res = future.getNow(super.recall(key));
			if (res == null){
				this.learn(key, null, timeToLiveIfNoResponse);
				res = super.recall(key);
			}
		}
		return res;
	}

	@Override
	public MemoryEntry<T> learn(String key, T value, long timeToLive) {
		MemoryEntry<T> newEntry = new MemoryEntry<T>(value, timeToLive);
		MemoryEntry<T> prevEntry = this.recall(key);
		if (prevEntry == null || prevEntry.getTimeToLive() < newEntry.getTimeToLive()) {
			synchronized ((this.getClass().getName()+".learn("+key+")").intern()) {
				prevEntry = this.recall(key);
				if (prevEntry == null || prevEntry.getTimeToLive() < newEntry.getTimeToLive()) {
					prevEntry = super.learn(key, value, newEntry.getTimeToLive());
				}
			}
		}
		return prevEntry;
	}
	
	
}
