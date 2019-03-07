package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
// import static org.hamcrest.CoreMatchers.*;
// NOTE: a need to switch to hamcrest-all.jar and Matchers 
// just for resolving method 'containsInAnyOrder'
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.github.sergueik.swet.TestConfigurationParser;

public class TestConfigurationParserTest {
	/**
	 * Testing of the Selenium WebDriver Elementor Tool (SWET) Configuration table helper
	 *  @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
	 */

	private static boolean skipHeaders = true;
	private static String defaultConfig = "test.configuration";
	private static String configuPath = String.format("%s/src/main/resources/%s",
			System.getProperty("user.dir"), defaultConfig);
	private static List<Object> result = new ArrayList<>();
	private static Object[] expected = new Object[] { "A1", "A2", "A3", "B1",
			"B2", "B3", "C1", "C2", "C3", "D1", "D2", "D3" /* , "E1" */ };

	@BeforeClass
	public static void beforeSuiteMethod() throws Exception {
		result = flatten(TestConfigurationParser.getConfiguration(configuPath));
	}

	// @Ignore
	@Test
	public void junitBuiltIArrayTest() {
		assertArrayEquals(expected, result.toArray());
	}

	// @Ignore
	@Test
	public void orderInsensitiveTest() {
		// NOTE: would fail
		Set<Object> dataSet = new HashSet<Object>(Arrays.asList(expected));
		assertTrue(new HashSet<Object>(result).containsAll(dataSet));
	}

	// @Ignore
	@Test
	public void orderSensitiveTest() {
		// order-sensitive
		assertThat(result.toArray(), is(expected));
	}

	@Test
	public void iterateAnyOfTest() {
		for (Object resultItem : result) {
			// only works with small sets
			// will be failing with
			// Expected: (is "A1" or is "A2" or is "A3" or is "B1" or is "B2" or is
			// "B3")
			// but: was "C1"
			assertThat(resultItem, anyOf(is(expected[0]), is(expected[1]),
					is(expected[2]), is(expected[3]), is(expected[4]), is(expected[5])));
		}
	}

	// https://www.javaworld.com/article/2074689/core-java/hamcrest-containing-matchers.html
	// @Ignore
	@Test
	public void hasItemsTest() {
		assertThat(new HashSet<Object>(result), hasItems(expected));
	}

	// @Ignore
	// https://www.javaworld.com/article/2074689/core-java/hamcrest-containing-matchers.html
	@Test
	public void containsInAnyOrderTest() {
		assertThat(new HashSet<Object>(result), containsInAnyOrder(expected));
	}

	// https://stackoverflow.com/questions/40186270/java-flatten-an-array-using-recursion
	private static List<Object> flatten(Object input) {
		List<Object> result = new ArrayList<Object>();
		if (input.getClass().isArray()) {
			for (int pos = 0; pos < Array.getLength(input); pos++) {
				result.addAll(flatten(Array.get(input, pos)));
			}
		} else if (input instanceof List) {
			for (Object element : (List<?>) input) {
				result.addAll(flatten(element));
			}
		} else {
			result.add(input);
		}
		return result;
	}
}
