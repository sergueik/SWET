package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.sergueik.swet.OSUtils;
import com.google.common.collect.Lists;

public class BrowserDiscoveryTest {

	private static String osName = OSUtils.getOsName();
	private static Map<String, String> browserNames = new HashMap<>();
	static {
		switch (osName) {
		case "windows":
			browserNames.put("chrome.exe", "Google Chrome");
			browserNames.put("edge.exe", "Edge");
			browserNames.put("iexplore.exe", "Internet Explorer");
			browserNames.put("firefox.exe", "Mozilla Firefox");
			browserNames.put("safari", "Safari");
			break;
		case "linux":
			browserNames.put("google-chrome", "Google Chrome");
			browserNames.put("firefox", "Mozilla Firefox");
			// browserNames.put("chrome", "Google Chrome (wrong proces name)");
			break;
		case "mac os x":
			browserNames.put("Google Chrome", "Google Chrome");
			browserNames.put("Mozilla Firefox", "Mozilla Firefox");
			browserNames.put("Safari", "Safari");
			break;
		}
	}

	@Test
	public void testInstalledBrowserInformaation() {
		if (osName.equals("windows")) {
			List<String> browsers = OSUtils.getInstalledBrowsers();
			assertTrue(browsers.size() > 0);
			// System.err.println("Installed browsers: " + browsers);

			for (String browserName : browserNames.keySet()) {
				if (browsers.contains(browserName)) {
					assertTrue(OSUtils.isInstalled(browserName));
					assertTrue(OSUtils.getMajorVersion(browserName) > 0);
					System.err.println(String.format("%s version: %s", browserNames.get(browserName),
							OSUtils.getVersion(browserName)));
				} else {
					assertFalse(OSUtils.isInstalled(browserName));
					assertTrue(OSUtils.getMajorVersion(browserName) == 0);
				}
			}
		}
		if (osName.equals("mac os x")) {
			List<String> browsers = new ArrayList<String>(browserNames.keySet());
			for (String browserName : browsers) {
					if ( !OSUtils.findAppInPath(browserName) ) {
						System.err.println(
								String.format("Broser not installed: %s", browserName));
					} else {
						System.err.println("Found browser: " + browserNames.get(browserName));

					}
			}
		}
		if (osName.equals("linux")) {
			List<String> browsers = new ArrayList<String>(browserNames.keySet());
			for (String browserName : browsers) {
				String command = "/usr/bin/which " + browserName;
				Runtime runtime = Runtime.getRuntime();
				Process process;
				try {
					process = runtime.exec(command);
					int exitCode = process.waitFor();
					if (exitCode != 0) {
						System.err.println(
								String.format("Process exit code: %d for browser process %s", exitCode, browserName));
					} else {
						System.err.println("Found browser: " + browserNames.get(browserName));

					}
				} catch (IOException | InterruptedException e) {
					System.err.println("Exception (ignnord): " + e.getMessage());
				}
			}
		}
	}
}
