package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
// NOTE: need to provide all entries
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.AnyOf.anyOf;

import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class PropertiesTest {

	private static final Utils utils = Utils.getInstance();
	private final static String propertiesFileName = "application.properties";
	private static String result = null;
	private static Map<String, String> properties = new HashMap<>();
	private static Object[] definedProperties = new Object[] { "chromeDriverPath",
			"firefoxBrowserPath" };

	@SuppressWarnings("static-access")
	@Test
	public void readPropertyTest() {
		result = utils.readProperty("chromeDriverPath");
		// result will be empty:
		// `Utils.readProperty` will scan properties file in application
		// resource path build relative to target/test-classes
		assertThat(result, is(nullValue()));
	}

	@Test
	public void getResourcePathTest() {
		String resourcePath = utils.getResourcePath(propertiesFileName);
		properties = PropertiesParser.getProperties(resourcePath);
		assertThat(properties, notNullValue());
		assertThat(properties.keySet().size(), greaterThan(1));

		assertThat(new HashSet<Object>(properties.keySet()),
				hasItems(definedProperties));
		/*
		 Expected: (a collection containing "missing property") 
		 but: a collection containing "missing property" 
		 was "password", 
		 was "edgeDriverPath", 
		 was "ieDriverPath", 
		 was "firefoxBrowserPath", 
		 was "chromeDriverPath", was "geckoDriverPath", 
		 was "username"
		 */
	}

	@Test
	public void getResourceStreamTest() {
		utils.setDebug(true);
		result = utils.getScriptContent(propertiesFileName);
		assertThat(result, containsString("chromeDriverPath"));
	}

	// @Ignore
	// https://www.baeldung.com/junit-assert-exception
	@Test(expected = NullPointerException.class)
	public void failingResourceStreamTest() {
		utils.setDebug(true);
		result = utils.getScriptContent(
				String.format("src/main/resources/%s", propertiesFileName));
		assertThat(result, is(nullValue()));
	}
}
