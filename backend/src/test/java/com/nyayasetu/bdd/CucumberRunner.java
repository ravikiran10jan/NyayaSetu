package com.nyayasetu.bdd;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.nyayasetu.bdd.steps")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, summary")
public class CucumberRunner {}
