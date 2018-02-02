package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.github.sergueik.jprotractor.KeywordLibrary;
import com.github.sergueik.swet.YamlHelper;

/**
 * Test for Selenium WebDriver Elementor Tool (SWET) YAML configuration file reader for 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class YamlHelperTest {

	private static Map<String, String> keywordTable = new HashMap<>();
	private static Set<String> supportedKeywords = new HashSet<>();
	private static String yamlFile = null;
	private static String internalConfiguration = String.format(
			"%s/src/main/resources/%s", System.getProperty("user.dir"),
			"internalConfiguration.yaml");
	private static List<String> keywordList = new ArrayList<>();
	private static Object[] keywordArray = new Object[] {}; // empty
	private static List<String> supportedKeywordList = new ArrayList<>();

	private static List<Object> result = new ArrayList<>();

	private static boolean debug = false;

	private static String[] helpTopics = { "Testsuite Program creation",
			"Keyword-driven Framework flow creation",
			"Saving and restoring sessions" };
	private static Map<String, String> help;

	@BeforeClass
	public static void loadFromYaml() throws IOException {
		yamlFile = String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), "help.yaml");
		help = YamlHelper.loadHelp(yamlFile);
		Map<String, Map<String, String>> data = YamlHelper
				.loadData(internalConfiguration);

		// selectorFromSWD = internalConfiguration.get("SWDSelectors");
		keywordTable = data.get("Keywords");
		supportedKeywords = KeywordLibrary.getKeywords();
	}

	@BeforeClass
	public static void convertSetsToArrays() {

		for (String k : keywordTable.keySet()) {
			keywordList.add(k);
		}
		Collections.sort(keywordList);
		System.err.format("Loaded %d keys from YAML:\n",
				keywordTable.keySet().size());
		if (debug) {
			System.err.println(keywordList);
		}
		// e.g. CLOSE_BROSWER will not be in the keywordTable

		for (String k : supportedKeywords) {
			supportedKeywordList.add(k);
		}
		Collections.sort(supportedKeywordList);
		System.err.format("Loaded %d keys from JAR:\n", supportedKeywords.size());
		if (debug) {
			System.err.println(supportedKeywordList);
			/*
			for (String k : supportedKeywordList) {
				System.err.println("[" + k + "]");
			}
			*/
		}
		keywordArray = new Object[keywordList.size()];
		keywordList.toArray(keywordArray);
		if (debug) {
			System.err.println("Keyword array:");
			for (int cnt = 0; cnt != keywordArray.length; cnt++) {
				System.err.println("[" + keywordArray[cnt] + "]");
			}
		}
	}

	// NOTE: `containsAll` takes sets and does not show the outliers
	@Test
	public void supportedKeywordsContainsKeywordTableTest() {

		assertTrue(supportedKeywords.containsAll(keywordTable.keySet()));
		assertFalse(keywordTable.keySet().containsAll(supportedKeywords));
	}

	// get prepared to sets disappear from public visibility
	@Test
	public void supportedKeywordsContainsKeywordTableWithArraysTest() {
		assertTrue(new HashSet<Object>(supportedKeywordList)
				.containsAll(new HashSet<Object>(keywordList)));
		assertFalse(new HashSet<Object>(keywordList)
				.containsAll(new HashSet<Object>(supportedKeywordList)));
	}

	// NOTE: `hasItems` prints information about mismatches in a funny way
	@Test
	public void supportedKeywordsHasItemsKeywordTableTest() {
		assertThat(new HashSet<Object>(supportedKeywordList),
				hasItems(keywordArray));
		// NOTE: Type sensitive:
		// the following would fail:
		// assertThat(new HashSet<Object>(supportedKeywordList),
		// hasItems(keywordList));
		/*
				keywordArray[0] = "XXXX";
				assertThat(new HashSet<Object>(supportedKeywordList),
						hasItems(keywordArray));
						*/
	}

	// `containsInAnyOrder` shows the mismatches, but appear to fail when
	// first arg has elements absent from the second arg
	@Test
	public void loadKeywordTableTest() {

		assertThat(new HashSet<Object>(keywordList),
				containsInAnyOrder(keywordArray));
		/*
		keywordList.add("XXXX");
		assertThat(new HashSet<Object>(keywordList),
				containsInAnyOrder(keywordArray));
		// Expected: iterable over [
		// "CLEAR_TEXT", "CLICK", "CLICK_BUTTON", "CLICK_CHECKBOX",
		// "CLICK_LINK", "CLICK_RADIO", "CLOSE_BROWSER", "CREATE_BROWSER",
		// "ELEMENT_PRESENT", "GET_ATTR", "GET_TEXT", "GOTO_URL", "SELECT_OPTION",
		// "SEND_KEYS", "SET_TEXT", "SWITCH_FRAME", "VERIFY_ATTR",
		// "VERIFY_TEXT", "WAIT"] in any order
		// but: Not matched: "XXXX"
		assertThat(new HashSet<Object>(supportedKeywordList),
				containsInAnyOrder(keywordArray));
		*/
	}

	@Test
	public void regexMatchKeywordTableTest() {
		Pattern pattern = Pattern
				.compile("(?:" + StringUtils.join(keywordList, "|") + ")");
		System.err.println("Pattern:\n" + pattern.toString());
		String input = StringUtils.join(supportedKeywordList.toArray(), "|");
		System.err.println("Input:\n" + input);
		Matcher matcher = pattern.matcher(input);
		assertTrue(matcher.find());
	}

	@Test
	public void regexNotMatchKeywordTableTest() {
		Pattern pattern = Pattern
				.compile("^(?!" + StringUtils.join(keywordList, "|") + ").*$");
		System.err.println("Pattern:\n" + pattern.toString());
		/*
		 NOTE: expressions like below will not help
		  		Pattern pattern = Pattern
				.compile("^.*\\|(?!" + StringUtils.join(keywordList, "|") + ")\\|.*$");
		
		 */
		/*
		String input = StringUtils.join(supportedKeywordList.toArray(), "|");
		System.err.println("Input:\n" + input);
		Matcher matcher = pattern.matcher(input);
		assertFalse(matcher.find());
		*/
		boolean result = false;
		for (String item : supportedKeywordList) {
			if (debug) {
				System.err.println("Input:\n" + item);
			}
			Matcher matcher = pattern.matcher(item);
			result |= matcher.find();
			if (debug) {
				if (result) {
					System.err.println("Found outlier: " + item);
				}
			}
		}
		assertTrue(result);
	}

	@Ignore
	@Test
	public void exactMatchTest() throws IOException {

		/*		assertThat(help.keySet().toArray(),
						is(new Object[] { "Template-based Testsuite creation",
								"Keyword-driven Framework suite creation",
								"Saving and restoring" }));
		*/
		assertThat(help.keySet().toArray(), is((Object[]) helpTopics));

	}

	@Test
	public void regexMatchTest() throws IOException {

		Pattern pattern = Pattern
				.compile("(" + StringUtils.join(helpTopics, "|") + ")");
		String result = StringUtils.join(help.keySet().toArray(), "|");
		Matcher matcher = pattern.matcher(result);
		assertTrue(matcher.find());
		pattern = Pattern
				.compile("^(?!" + StringUtils.join(helpTopics, "|") + ").*$");
		matcher = pattern.matcher(result);
		assertFalse(matcher.find());
	}

	@Test
	public void containsTest() throws IOException {

		List<Object> helpTopicArray = new ArrayList<>();
		helpTopicArray.addAll(help.keySet());
		assertTrue(CollectionUtils.containsAny(helpTopicArray,
				new ArrayList<Object>(Arrays.asList(helpTopics))));
	}

}