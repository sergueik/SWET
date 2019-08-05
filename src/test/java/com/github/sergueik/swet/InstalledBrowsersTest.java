package com.github.sergueik.swet;

import static java.lang.System.err;
import static java.lang.System.out;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
// [WARNING] No processor claimed any of these annotations: org.junit.AfterClass,org.junit.BeforeClass,org.junit.Ignore,org.junit.After,org.junit.Test,org.junit.Before

import org.junit.Test;

import com.github.sergueik.swet.Utils;
import com.github.sergueik.swet.OSUtils;

public class InstalledBrowsersTest {

	private static Map<String, String> browserNames = new HashMap<>();
	static {
		browserNames.put("chrome.exe", "Google Chrome");
		browserNames.put("iexplore.exe", "Internet Explorer");
		browserNames.put("firefox.exe", "Mozilla Firefox");
	}
	private static Utils utils = Utils.getInstance();
	private static String result = null;
	private static final boolean debug = true;

	@Test
	public void test() {
		utils.setDebug(debug);
		List<String> browsers = OSUtils.getInstalledBrowsers();
		assertTrue(browsers.size() > 0);
		out.println("Your browsers: " + browsers);

		for (String browserName : browserNames.keySet()) {
			if (browsers.contains(browserName)) {
				assertTrue(OSUtils.isInstalled(browserName));
				assertTrue(OSUtils.getMajorVersion(browserName) > 0);
				out.println(String.format("%s version: %s",
						browserNames.get(browserName), OSUtils.getVersion(browserName)));
			} else {
				assertFalse(OSUtils.isInstalled(browserName));
				assertTrue(OSUtils.getMajorVersion(browserName) == 0);
			}
		}
	}
}