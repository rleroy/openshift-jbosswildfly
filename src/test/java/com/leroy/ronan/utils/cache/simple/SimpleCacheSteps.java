package com.leroy.ronan.utils.cache.simple;

import java.io.File;
import java.util.stream.IntStream;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;
import com.leroy.ronan.utils.cache.TestCacheUtils;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SimpleCacheSteps {

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private static final String KEY   = TestCacheUtils.KEY;
    private static final String FRESH = TestCacheUtils.FRESH;
    private static final String OLD   = TestCacheUtils.OLD;

    private MutableInt loaderCallCount;
    private MutableInt writerCallCount;
    private MutableInt readerCallCount;
    
    private String memData;
    private String fsData;

    private PersistedCacheBuilder<String> builder;
    private PersistedCache<String> service;

	@Given("^a simple cache service$")
	public void a_cache_service() throws Throwable {
        builder = new PersistedCacheBuilder<String>();
	}
	
	@Given("^a synchronized cache service$")
	public void a_synchronized_cache_service() throws Throwable {
        builder = new PersistedCacheBuilder<String>();
        builder.synchro();
	}


	@Given("^data is ok$")
	public void data_is_ok() throws Throwable {
		builder = builder.loader(key -> {
									loaderCallCount.add(1);
									return FRESH;
								});
	}

	@Given("^data is ko$")
	public void data_is_ko() throws Throwable {
        builder = builder.loader(key -> {
                                    loaderCallCount.add(1);
                                    return null;
                                });
	}
	
	@Given("^memory data is fresh$")
	public void memory_data_is_fresh() throws Throwable {
        memData = FRESH;
	}
	
    @Given("^memory data is expired$")
    public void memory_data_is_expired() throws Throwable {
        memData = OLD;
    }

	@Given("^memory data is empty$")
	public void memory_data_is_empty() throws Throwable {
        memData = null;
	}
	
    @Given("^file system data is fresh$")
    public void file_system_data_is_fresh() throws Throwable {
        fsData = FRESH;
    }
    
    @Given("^file system data is expired$")
    public void file_system_data_is_expired() throws Throwable {
        fsData = OLD;
    }

    @Given("^file system data is empty$")
    public void file_system_data_is_empty() throws Throwable {
        fsData = null;
    }

	@When("^I access the data$")
	public void i_access_the_data() throws Throwable {
		loaderCallCount = new MutableInt(0);
		readerCallCount = new MutableInt(0);
		writerCallCount = new MutableInt(0);
		
		long ttlSuccess = 1000;
		long ttlError = 1000;
		
        service = builder
                .timeToLiveAfterError(ttlError)
                .timeToLiveAfterSuccess(ttlSuccess)
				.keyToFile(TestCacheUtils::keyToFile)
		        .fromFile(t -> {
		        	readerCallCount.increment();
	        		return TestCacheUtils.read(t);
		        })
		        .toFile((f, s) -> {
		        	writerCallCount.increment();
		        	TestCacheUtils.write(f, s);
		        })
		        //.isExpired(TestCacheUtils::isExpired)
				.build();
        service = builder.build();
        
      	TestCacheUtils.write(new File("cache-tmp/test/key.tmp"), fsData);
        if (OLD.equals(memData)){
        	service.learn(KEY, memData, ttlSuccess);
        }
    	Thread.sleep(ttlSuccess);
        if (FRESH.equals(fsData)){
        	TestCacheUtils.write(new File("cache-tmp/test/key.tmp"), fsData);
        }
        if (FRESH.equals(memData)){
        	service.learn(KEY, memData, ttlSuccess);
        }
	}
	
	@When("^I access (\\d+) time the data at once$")
	public void i_access_time_the_data_at_once(int nb) throws Throwable {
		i_access_the_data();
		IntStream.range(0, nb).parallel().forEach(i -> service.get(KEY));
	}


	@Then("^I should get the response fresh$")
	public void i_should_get_the_response_fresh() throws Throwable {
	    CacheResponse<String> response= service.get(KEY);
	    Assert.assertEquals(FRESH, response.getContent());
	}
	
	@Then("^I should get the response empty$")
	public void i_should_get_the_response_empty() throws Throwable {
        CacheResponse<String> response = service.get(KEY);
        Assert.assertEquals(null, response.getContent());
	}

	@Then("^I should get the response expired$")
	public void i_should_get_the_response_expired() throws Throwable {
        CacheResponse<String> response= service.get(KEY);
        Assert.assertEquals(OLD, response.getContent());
	}

	@Then("^the memory should be fresh$")
	public void the_memory_should_be_fresh() throws Throwable {
        CacheResponse<String> mem = service.recall(KEY);
        Assert.assertEquals(FRESH, mem.getContent());
	}

	@Then("^the memory should be empty$")
	public void the_memory_should_be_empty() throws Throwable {
	    CacheResponse<String> mem = service.recall(KEY);
        Assert.assertEquals(null, mem.getContent());
	}

    @Then("^the memory should be expired$")
    public void the_memory_should_be_expired() throws Throwable {
        CacheResponse<String> mem = service.recall(KEY);
        Assert.assertEquals(OLD, mem.getContent());
    }

	@Then("^the file system should be fresh$")
	public void the_file_system_should_be_fresh() throws Throwable {
        Assert.assertEquals(FRESH, TestCacheUtils.read(TestCacheUtils.keyToFile(KEY)));
	}
	
	@Then("^the file system should be empty$")
	public void the_file_system_should_be_empty() throws Throwable {
	    Assert.assertFalse(TestCacheUtils.keyToFile(KEY).exists());
	}

	@Then("^the file system should be expired$")
	public void the_file_system_should_be_expired() throws Throwable {
        Assert.assertEquals(OLD, TestCacheUtils.read(TestCacheUtils.keyToFile(KEY)));
	}

	@Then("^data should have been read once$")
	public void data_should_have_been_read_once() throws Throwable {
		Assert.assertEquals(1, readerCallCount.intValue());
	}

	@Then("^data should have been loaded once$")
	public void data_should_have_been_loaded_once() throws Throwable {
		Assert.assertEquals(1, loaderCallCount.intValue());
	}

	@Then("^data should have been written once$")
	public void data_should_have_been_written_once() throws Throwable {
		Assert.assertEquals(1, writerCallCount.intValue());
	}
	
	@Then("^the number of call to the loader should be (\\d+)$")
	public void the_number_of_call_to_the_loader_should_be(int call) throws Throwable {
		Assert.assertEquals(call, loaderCallCount.intValue());
	}
}
