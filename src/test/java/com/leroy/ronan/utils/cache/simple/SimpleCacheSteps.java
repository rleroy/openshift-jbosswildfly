package com.leroy.ronan.utils.cache.simple;

import java.io.File;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;

import com.leroy.ronan.utils.cache.CacheBuilder;
import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.utils.cache.TestCacheUtils;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SimpleCacheSteps {

    private static final String KEY   = TestCacheUtils.KEY;
    private static final String FRESH = TestCacheUtils.FRESH;
    private static final String OLD   = TestCacheUtils.OLD;

    private MutableInt callCount;
    private String memData;
    private String fsData;

    private CacheBuilder<String> builder;
    private SimpleCache<String> service;

	@Given("^a simple cache service$")
	public void a_cache_service() throws Throwable {
        builder = new CacheBuilder<String>();
	}

	@Given("^data is ok$")
	public void data_is_ok() throws Throwable {
		callCount = new MutableInt(0);
		builder = builder.loader(key -> {
									callCount.add(1);
									return FRESH;
								});
	}

	@Given("^data is ko$")
	public void data_is_ko() throws Throwable {
        callCount = new MutableInt(0);
        builder = builder.loader(key -> {
                                    callCount.add(1);
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
        TestCacheUtils.write(new File("cache-tmp/test/key.tmp"), fsData);

        service = builder
				.keyToFile(TestCacheUtils::keyToFile)
		        .fromFile(TestCacheUtils::read)
		        .toFile(TestCacheUtils::write)
		        .isExpired(TestCacheUtils::isExpired)
				.build();
        service = builder.build();
        if (memData != null){
            service.learn(KEY, memData, 100);
            if (OLD.equals(memData)){
            	Thread.sleep(100);
            }
        }
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

	@Then("^the number of call to the loader should be (\\d+)$")
	public void the_number_of_call_to_the_loader_should_be(int call) throws Throwable {
		Assert.assertEquals(call, callCount.intValue());
	}
}
