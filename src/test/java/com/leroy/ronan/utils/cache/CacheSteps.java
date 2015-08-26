package com.leroy.ronan.utils.cache;

import java.io.File;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CacheSteps {

    private static final String KEY = "key";
    private static final String FRESH = "FRESH";

    private MutableInt callCount ;
    
    private CacheBuilder<String> builder;
    private Cache<String> service;
	private CacheResponse<String> memData;
	private CacheResponse<String> fsData;

	private CacheResponse<String> response;
	
	@Given("^a cache service$")
	public void a_cache_service() throws Throwable {
        builder = new CacheBuilder<String>();
	}

	@Given("^loading is fast$")
	public void loading_is_fast() throws Throwable {
        builder = builder.loading(CacheSpeed.FAST);
	}

	@Given("^reading is fast$")
	public void reading_is_fast() throws Throwable {
        builder = builder.reading(CacheSpeed.FAST);
	}

	@Given("^writing is fast$")
	public void writing_is_fast() throws Throwable {
        builder = builder.writing(CacheSpeed.FAST);
	}

	@Given("^data is ok$")
	public void data_is_ok() throws Throwable {
		callCount = new MutableInt(0);
		builder = builder.loader(key -> {
									callCount.add(1);
									return FRESH;
								});
	}

	@Given("^file system data is fresh$")
	public void file_system_data_is_fresh() throws Throwable {
		fsData = new CacheResponse<>(FRESH, 0, (s, l) -> false);
	}

	@Given("^memory data is fresh$")
	public void memory_data_is_fresh() throws Throwable {
        memData = new CacheResponse<>(FRESH, 0, (s, l) -> false);
	}
	
	@Given("^memory data is empty$")
	public void memory_data_is_empty() throws Throwable {
        memData = null;
	}



	@When("^I access the data$")
	public void i_access_the_data() throws Throwable {
		callCount = new MutableInt(0);
		service = builder
				.keyToFile(s -> new File("cache-tmp/test/key.tmp"))
		        .fromFile(FileUtils::read)
		        .toFile(FileUtils::write)
		        .isExpired((s, l) -> false)
				.build();
		
        service = builder.build();
        
        service.setInMemory(KEY, memData);
        service.setInPersistence(KEY, fsData);
        
        response = service.get(KEY);
	}

	@Then("^I should get the response fresh$")
	public void i_should_get_the_response_fresh() throws Throwable {
        Assert.assertEquals(FRESH, response.getContent());
        Assert.assertEquals(false, response.isExpired());
	}

	@Then("^the memory should be fresh$")
	public void the_memory_should_be_fresh() throws Throwable {
        CacheResponse<String> mem = service.getInMemory(KEY);
        Assert.assertEquals(FRESH, mem.getContent());
        Assert.assertEquals(false, mem.isExpired());
	}

	@Then("^the file system should be fresh$")
	public void the_file_system_should_be_fresh() throws Throwable {
        CacheResponse<String> persist = service.getInPersistence(KEY);
        Assert.assertEquals(FRESH, persist.getContent());
        Assert.assertEquals(false, persist.isExpired());
	}

	@Then("^the number of call to the loader should be (\\d+)$")
	public void the_number_of_call_to_the_loader_should_be(int call) throws Throwable {
		Assert.assertEquals(call, callCount.intValue());
	}

	
	/*
    
    private String data;
    private CacheResponse<String> fsData;
    private CacheResponse<String> memData;
    
    private CacheBuilder<String> builder;
    private Cache<String> service;
    private CacheResponse<String> response;
    
    @Given("^a cache service$")
    public void a_cache_service() throws Throwable {
        builder = new CacheBuilder<String>();
    }

    @Given("^loading is fast$")
    public void loading_is_fast() throws Throwable {
        builder = builder.loading(CacheSpeed.FAST);
    }

    @Given("^building is fast$")
    public void building_is_fast() throws Throwable {
        builder = builder.reading(CacheSpeed.FAST);
    }

    @Given("^data is ok$")
    public void data_is_ok() throws Throwable {
        data = DATA;
    }

    @Given("^file system data is good$")
    public void file_system_data_is_good() throws Throwable {
        fsData = new CacheResponse<>(DATA);
    }

    @Given("^memory data is good$")
    public void memory_data_is_good() throws Throwable {
        memData = new CacheResponse<>(DATA);
    }
    
    @Given("^memory data is null$")
    public void memory_data_is_null() throws Throwable {
        memData = new CacheResponse<>(null);
    }

    @Given("^memory data is expi$")
    public void memory_data_is_expi() throws Throwable {
        CacheResponse<String> mock = Mockito.mock(CacheResponse.class);
        Mockito.when(mock.getContent()).thenReturn(DATA);
        Mockito.when(mock.isExpired()).thenReturn(true);
        memData = mock;
    }

    @When("^I access de data$")
    public void i_access_de_data() throws Throwable {
        builder.keyToFile(s -> new File("cache-tmp/test/key.tmp"));
        builder.fromFile(FileUtils::read);
        builder.toFile(FileUtils::write);
        
        service = builder.build();
        service.setInMemory(KEY, memData);
        service.setInPersistence(KEY, fsData);
        response = service.get(KEY);
    }

    @Then("^I should get the response good$")
    public void i_should_get_the_response_good() throws Throwable {
        Assert.assertEquals(DATA, response.getContent());
        Assert.assertEquals(false, response.isExpired());
    }

    @Then("^the memory should be good$")
    public void the_memory_should_be_good() throws Throwable {
        CacheResponse<String> mem = service.getInMemory(KEY);
        Assert.assertEquals(DATA, mem.getContent());
        Assert.assertEquals(false, mem.isExpired());
    }

    @Then("^the file system should be good$")
    public void the_file_system_should_be_good() throws Throwable {
        CacheResponse<String> persist = service.getInPersistence(KEY);
        Assert.assertEquals(DATA, persist.getContent());
        Assert.assertEquals(false, persist.isExpired());
    }
    */
}
