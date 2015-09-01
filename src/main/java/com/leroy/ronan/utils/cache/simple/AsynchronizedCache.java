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

public class AsynchronizedCache<T> extends SynchronizedCache<T>{

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

	public AsynchronizedCache(Function<String, T> load, BiFunction<T, Long, Boolean> isExpired,
			long timeToLiveAfterError, long timeToLiveAfterSuccess, Function<String, File> keyToFile,
			Function<File, T> fromFile, BiConsumer<File, T> toFile) {
		super(load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
	}

	@Override
	public CacheResponse<T> get(String key) {
		CacheResponse<T> res = null;
		CompletableFuture<CacheResponse<T>> future = CompletableFuture.supplyAsync(() -> super.get(key));
		try {
			res = future.get(10, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			res = future.getNow(super.recall(key));
			if (res == null){
				this.learn(key, null, 10);
				res = super.recall(key);
			}
		}
		System.out.println(res.getContent() + "/" + res.getTimeToLive());
		return res;
	}
	
}
