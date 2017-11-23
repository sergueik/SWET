package org.swet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.swet.OSUtils;

public class BrowserDiscoveryTest {

	private static String osName = OSUtils.getOsName();
	private static Map<String, String> browserNames = new HashMap<>();
	static {
		browserNames.put("chrome.exe", "Google Chrome");
		browserNames.put("edge.exe", "Edge");
		browserNames.put("iexplore.exe", "Internet Explorer");
		browserNames.put("firefox.exe", "Mozilla Firefox");
		browserNames.put("safari", "Safari");
	}

	@Test
	public void testInstalledBrowserInformaation() {
		List<String> browsers = OSUtils.getInstalledBrowsers();
		assertTrue(browsers.size() > 0);
		// System.err.println("Installed browsers: " + browsers);

		for (String browserName : browserNames.keySet()) {
			if (browsers.contains(browserName)) {
				assertTrue(OSUtils.isInstalled(browserName));
				assertTrue(OSUtils.getMajorVersion(browserName) > 0);
				System.err.println(String.format("%s version: %s",
						browserNames.get(browserName), OSUtils.getVersion(browserName)));
			} else {
				assertFalse(OSUtils.isInstalled(browserName));
				assertTrue(OSUtils.getMajorVersion(browserName) == 0);
			}
		}
	}
}
