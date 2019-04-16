package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Common configuration / properties file parsers for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class PropertiesParser {

	/*
	
		private static String propertiesFileName = "test.properties";
		Map<String, String> propertiesMap = PropertiesParser
						.getProperties(String.format("%s/src/main/resources/%s",
								System.getProperty("user.dir"), propertiesFileName));
		String username = propertiesMap.get("username");
		String password =  propertiesMap.get("password");
		StringBuilder loggingSb = new StringBuilder();
		Formatter formatter = new Formatter(loggingSb, Locale.US)
	*/

	public static Map<String, String> getProperties(final String fileName) {
		Properties p = new Properties();
		Map<String, String> propertiesMap = new HashMap<>();
		// System.err.println(String.format("Reading properties file: '%s'",
		// fileName));
		try {
			p.load(new FileInputStream(fileName));
			@SuppressWarnings("unchecked")
			Enumeration<String> e = (Enumeration<String>) p.propertyNames();
			for (; e.hasMoreElements();) {
				String key = e.nextElement();
				String val = p.get(key).toString();
				System.out.println(String.format("Reading: '%s' = '%s'", key, val));
				propertiesMap.put(key, Utils.resolveEnvVars(val));
			}

		} catch (FileNotFoundException e) {
			System.err.println(
					String.format("Properties file was not found: '%s'", fileName));
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(
					String.format("Properties file is not readable: '%s'", fileName));
			e.printStackTrace();
		}
		return (propertiesMap);
	}
}