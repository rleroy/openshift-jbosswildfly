package com.leroy.ronan.utils.cache.simple;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.leroy.ronan.utils.cache.CacheResponse;

public class SynchronizedCache<T> extends SimpleCache<T> {

    public SynchronizedCache(Function<String, T> load, BiFunction<T, Long, Boolean> isExpired, 
    		long timeToLiveAfterError, long timeToLiveAfterSuccess, 
    		Function<String, File> keyToFile, Function<File, T> fromFile, BiConsumer<File, T> toFile) {
		super(load, isExpired, timeToLiveAfterError, timeToLiveAfterSuccess, keyToFile, fromFile, toFile);
	}

	@Override
	public CacheResponse<T> get(String key) {
		CacheResponse<T> res = recall(key);
		if (res == null || res.isExpired()) {
			synchronized ((this.getClass().getName()+".get("+key+")").intern()) {
				res = recall(key);
				if (res == null || res.isExpired()) {
					res = super.get(key);
				}
			}
		}
		return res;
	}

}
