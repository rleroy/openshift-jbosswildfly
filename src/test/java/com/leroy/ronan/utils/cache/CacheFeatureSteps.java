package com.leroy.ronan.utils.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.PersistedCache;
import com.leroy.ronan.utils.cache.PersistedCacheBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CacheFeatureSteps {

    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    private static final String KEY   = TestCacheUtils.KEY;
    private static final String FRESH = TestCacheUtils.FRESH;
    private static final String OLD   = TestCacheUtils.OLD;

    private static final long loadingTime = 1000;
    private static final long timeToLiveSuccess = 60000;
    private static final long timeToLiveError = 1000;
    private static final long timeToWaitResponse = 100;
    private static final long timeToLiveIfNoResponse = 100;
    
    private MutableInt loaderCallCount;
    private MutableInt writerCallCount;
    private MutableInt readerCallCount;
    
    private String memData;
    private String fsData;

    private PersistedCacheBuilder<String> builder;
    private PersistedCache<String> service;
    
    private List<Set<CacheResponse<String>>> responsesByBatch = new ArrayList<>();

	@Given("^a simple cache service$")
	public void a_cache_service() throws Throwable {
        builder = new PersistedCacheBuilder<String>();
	}
	
	@Given("^a synchronized cache service$")
	public void a_synchronized_cache_service() throws Throwable {
        builder = new PersistedCacheBuilder<String>();
        builder.synchro();
	}

	@Given("^an asynchronized cache service$")
	public void an_asynchronized_cache_service() throws Throwable {
        builder = new PersistedCacheBuilder<String>();
        builder.asynchro();
        builder.timeToWaitResponse(timeToWaitResponse);
        builder.timeToLiveIfNoResponse(timeToLiveIfNoResponse);
	}

	@Given("^data is ok$")
	public void data_is_ok() throws Throwable {
		builder = builder.loader(key -> {
									loaderCallCount.add(1);
									return FRESH;
								});
	}

	@Given("^data is ok but slow$")
	public void data_is_ok_but_slow() throws Throwable {
		builder = builder.loader(key -> {
									loaderCallCount.add(1);
									try {
										Thread.sleep(loadingTime);
									} catch (Exception e) {
										log.error(e.getMessage());
									}
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

	private void buildService() throws Throwable {
		loaderCallCount = new MutableInt(0);
		readerCallCount = new MutableInt(0);
		writerCallCount = new MutableInt(0);
		
        service = builder
                .timeToLiveAfterError(timeToLiveError)
                .timeToLiveAfterSuccess(timeToLiveSuccess)
				.keyToFile(TestCacheUtils::keyToFile)
		        .fromFile(t -> {
		        	readerCallCount.increment();
	        		return TestCacheUtils.read(t);
		        })
		        .toFile((f, s) -> {
		        	writerCallCount.increment();
		        	TestCacheUtils.write(f, s);
		        })
				.build();
        
      	TestCacheUtils.write(new File("cache-tmp/test/key.tmp"), fsData);
        if (OLD.equals(fsData)){
        	File f = new File("cache-tmp/test/key.tmp");
        	f.setLastModified(0);
        }
        
        if (OLD.equals(memData)){
        	service.learn(KEY, memData, 0);
        } else if (FRESH.equals(memData)){
        	service.learn(KEY, memData, timeToLiveSuccess);
        }
        
        Thread.sleep(50);
	}
	
	@When("^I access the data$")
	public void i_access_the_data() throws Throwable {
		if (service == null){
			buildService();
		}
	}
	
	@When("^I access (\\d+) time the data at once$")
	public void i_access_time_the_data_at_once(int nb) throws Throwable {
		if (service == null){
			buildService();
		}
		Set<CacheResponse<String>> responses = new HashSet<>();
		IntStream.range(0, nb).parallel().forEach(i -> responses.add(service.get(KEY)));
		responsesByBatch.add(responses);
	}

	@When("^I wait for the end of the loading$")
	public void i_wait_for_the_end_of_the_loading() throws Throwable {
		Thread.sleep(loadingTime*2);
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
	
	@Then("^first batch of responses should have been null with a low ttl$")
	public void first_batch_of_answers_should_have_been_null_with_a_low_ttl() throws Throwable {
		responsesByBatch.get(0).stream().forEach(response -> {
			Assert.assertEquals(null, response.getContent());
			Assert.assertTrue(response.getTimeToLive() <= timeToLiveIfNoResponse);
		});
	}
	
	@Then("^first batch of responses should have been expired with a low ttl$")
	public void first_batch_of_responses_should_have_been_expired_with_a_low_ttl() throws Throwable {
		responsesByBatch.get(0).stream().forEach(response -> {
			Assert.assertEquals(OLD, response.getContent());
			Assert.assertTrue(response.getTimeToLive() <= timeToLiveIfNoResponse);
		});
	}

	@Then("^second batch of responses should have been fresh with a high ttl$")
	public void second_batch_of_answers_should_have_been_fresh_with_a_high_ttl() throws Throwable {
		responsesByBatch.get(1).stream().forEach(response -> {
			Assert.assertEquals(FRESH, response.getContent());
			Assert.assertTrue(response.getTimeToLive() > timeToLiveIfNoResponse);
		});
	}

}
