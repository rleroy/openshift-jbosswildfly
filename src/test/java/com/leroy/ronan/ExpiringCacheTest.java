package com.leroy.ronan;

import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import com.google.code.joliratools.cache.ExpiringCache;

public class ExpiringCacheTest {

	@Test
	public void test() throws InterruptedException{
		Map<Integer, String> map = new ExpiringCache<Integer, String>(50);
		IntStream.range(0, 1000).forEach(i -> map.put(Integer.valueOf(i), Integer.toHexString(i)));
	    Assert.assertEquals("ff", map.get(Integer.valueOf(255)));
	    Thread.sleep(10);
	    Assert.assertEquals("ff", map.get(Integer.valueOf(255)));
	    Thread.sleep(100);
	    Assert.assertNull(map.get(Integer.valueOf(255)));
	}

}
