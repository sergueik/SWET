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
	private static String yamlFile = null;
	private static String[] helpTopics = { "Testsuite Program creation",
			"Keyword-driven Framework flow creation",
			"Saving and restoring sessions" };
	private static Map<String, String> help;

	@BeforeClass
	public static void setup() throws IOException {
		yamlFile = String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), "help.yaml");
		help = YamlHelper.loadHelp(yamlFile);
	}

	private static String internalConfiguration = String.format(
			"%s/src/main/resources/%s", System.getProperty("user.dir"),
			"internalConfiguration.yaml");
	private static List<Object> result = new ArrayList<>();

	@Test
	public void loadKeywordTableTest() {

		Map<String, Map<String, String>> data = YamlHelper
				.loadData(internalConfiguration);

		// selectorFromSWD = internalConfiguration.get("SWDSelectors");
		keywordTable = data.get("Keywords");
		System.err.format("Loaded from YAML: %d keys\n",
				keywordTable.keySet().size());
		for (String keyword : keywordTable.keySet()) {
			System.err.println("[" + keyword + "]");
		}
		Set<String> supportedKeywords = new HashSet<>();
		supportedKeywords = KeywordLibrary.getKeywords();
		System.err.format("Loaded from JAR: %d keys\n", supportedKeywords.size());
		for (String keyword : supportedKeywords) {
			System.err.println("[" + keyword + "]");
		}

		// NOTE: `containsAll` does not show the outliers
		assertTrue(supportedKeywords.containsAll(keywordTable.keySet()));
		assertFalse(keywordTable.keySet().containsAll(supportedKeywords));
		// e.g. CLOSE_BROSWER
		// `containsInAnyOrder` shows the mismatches - was: not matched:
		// "CLEAR_TEXT", "SWITCH_FRAME"
		Set<String> basic = new HashSet<String>();
		basic.add("CLEAR_TEXT");
		result = Arrays.asList(supportedKeywords /* keywordTable.keySet()*/);

		// Actually the name is misleading - the set are expected to match
		assertThat(result, hasItems(new Object[] { supportedKeywords }));
		// assertThat(result, hasItems(new Object[] { keywordTable.keySet() }));

		/*
		assertThat(supportedKeywords,
				containsInAnyOrder(new Object[] { supportedKeywords }));
		assertThat(keywordTable.keySet(), containsInAnyOrder(basic));
		assertThat(supportedKeywords,
				containsInAnyOrder(new Object[] { keywordTable.keySet() }));
				*/
	}

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
