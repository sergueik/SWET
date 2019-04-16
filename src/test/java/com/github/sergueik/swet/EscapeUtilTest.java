package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import com.github.sergueik.swet.Utils;

@SuppressWarnings("deprecation")
public class EscapeUtilTest {

	private static String osName = OSUtils.getOsName();
	private Utils utils = Utils.getInstance();

	@BeforeClass
	public static void beforeClassMethod() {
	}

	@Before
	public void loadBaseURL() {
		utils.setDebug(true);
	}

	// @Ignore
	@Test
	public void testBasicXMLEscape() {
		String data = "<?xml version=\"1.0\"?><build><node attribute='value'></build>";
		String escapedData = utils.escape(data);
		assertFalse(escapedData.isEmpty());
		assertFalse(escapedData.matches("&(?:<|>|\"|');"));
		// NOTE: negative lookahead
		assertFalse(escapedData.matches("&(?!lt|gt|amp|quot|apos|#x);"));
		System.err.println("Validated : " + escapedData);
	}

	@Test
	public void testCodePointEscape() {
		// Create 2 char primitives
		char ch1 = '\ud800';
		char ch2 = '\udc00';
		String data = "<data>" + Character.toString(ch1) + Character.toString(ch2)
				+ "</data>";

		// assign code point value of surrogate pair ch1, ch2 to cp
		int codePoint = Character.toCodePoint(ch1, ch2);
		// Print code point value of surrogate pair ch1, ch2
		System.err.println(
				String.format("Supplementary code point value is 0x%08X", codePoint));
		String escapedData = utils.escape(data);
		// NOTE: pad the same way, usual format is the "0x%08X"
		assertTrue(escapedData.matches(
				String.format("^.*(?:%s).*$", String.format("&#x%05X", codePoint))));
		System.err.println("Validated : " + escapedData);
	}

}
