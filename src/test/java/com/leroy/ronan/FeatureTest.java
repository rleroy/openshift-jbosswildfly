package com.leroy.ronan;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(format={"pretty"},
                 monochrome=true,
                 strict=true
                 //,tags = {"@tag"}
                )
public class FeatureTest {

}
