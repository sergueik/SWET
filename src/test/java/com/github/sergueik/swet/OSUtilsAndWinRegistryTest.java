package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.sergueik.swet.Utils;
import com.github.sergueik.swet.OSUtils;

@SuppressWarnings("deprecation")
public class OSUtilsAndWinRegistryTest {

	private static Utils utils = Utils.getInstance();
	private static String result = null;
	private static final boolean debug = true;

	@BeforeClass
	public static void beforeSuiteMethod() throws Exception {

	}

	@Before
	public void before() {
	}

	@Test
	public void registryReadBrowsersTest() {
		List<String> browsers = OSUtils.getInstalledBrowsers();
		assertThat(browsers, notNullValue());
		assertThat(browsers.size(), greaterThan(2));
		if (debug) {
			System.err.println("Installed browsers: " + browsers.toString());
		}
	}

	@Test
	public void registryReadZoomTest() {
		int zoom = OSUtils.getZoom();
		assertThat(zoom, notNullValue());
		assertThat(zoom, anyOf(equalTo(100000), equalTo(80000), equalTo(-1)));
		// NOTE: possibly after a cold reboot:
		// Expected: <100000>
		// but: was <-1>
		// TODO:
		// "ResetZoomOnStartup2"
		// assertThat(zoom, anyOf(equalTo(1), equalTo(-1)));

		if (debug) {
			System.err.println("Zoom: " + zoom);
		}
	}

	// based on:
	// https://answers.microsoft.com/en-us/ie/forum/all/ie-11-how-do-i-get-a-default-zoom-to-stick/19510f5a-c339-45d1-b74e-edc885ef5517
	@Test
	public void registryReadAdvancedOptionsZoomTest() {
		int zoom = OSUtils.getAdvancedOptionsZoom();
		assertThat(zoom, notNullValue());
		assertThat(zoom, equalTo(1));
		if (debug) {
			System.err.println("Accesibility Zoom: " + zoom);
		}
	}

	@Ignore
	@Test
	public void registryWinRegistryReadAdvancedOptionsTest1() {
		String value = null;
		try {
			value = OSUtils.WinRegistry.readString(
					OSUtils.WinRegistry.HKEY_LOCAL_MACHINE,
					"SOFTWARE\\Microsoft\\Internet Explorer\\AdvancedOptions\\ACCESSIBILITY\\ZOOMLEVEL",
					"CheckedValue");
		} catch (IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			System.err.println("Exceptiion (ignored): " + e.toString());
		}
		// assertThat(value, notNullValue());
		// java.lang.NumberFormatException: null
		assertThat(Integer.parseInt(value), equalTo(1));
		if (debug) {
			System.err.println("Accesibility Zoom CheckedValue: " + value);
		}

	}

	@Test
	public void registryWinRegistryReadAdvancedOptionsTest2() {
		String value = null;
		try {
			value = OSUtils.WinRegistry.readString(
					OSUtils.WinRegistry.HKEY_LOCAL_MACHINE,
					"SOFTWARE\\Microsoft\\Internet Explorer\\AdvancedOptions\\ACCESSIBILITY\\ZOOMLEVEL",
					"ValueName");
		} catch (IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			System.err.println("Exceptiion (ignored): " + e.toString());
		}
		assertThat(value, notNullValue());
		// assertThat(Integer.parseInt(value), equalTo(1));
		if (debug) {
			System.err.println("Accesibility Zoom ValueName: " + value);
		}

	}

}
