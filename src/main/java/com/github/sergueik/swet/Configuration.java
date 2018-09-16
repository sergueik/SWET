package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Test configuration serializer class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

final class Configuration {
	public Date created;
	public Date updated;
	public String version;
	public String seleniumVersion;
	public BrowserConfiguration browserConfiguration;
	public List<String> browsers;
	public Map<String, Map<String, String>> elements;
	public Map<String, String> plugins;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date data) {
		this.created = data;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date data) {
		this.updated = data;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String data) {
		this.version = data;
	}

	public String getSeleniumVersion() {
		return seleniumVersion;
	}

	public void setSeleniumVersion(String data) {
		this.seleniumVersion = data;
	}

	public BrowserConfiguration getBrowserConfiguration() {
		return browserConfiguration;
	}

	public void setBrowserConfiguration(BrowserConfiguration data) {
		this.browserConfiguration = data;
	}

	public List<String> getBrowsers() {
		return browsers;
	}

	public void setBrowsers(List<String> data) {
		this.browsers = data;
	}

	public Map<String, String> getPlugins() {
		return plugins;
	}

	public void setPlugins(Map<String, String> data) {
		this.plugins = data;
	}

	public Map<String, Map<String, String>> getElements() {
		return elements;
	}

	public void setElements(Map<String, Map<String, String>> data) {
		this.elements = data;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(format("Version: %s\n", version))
				.append(format("Created: %s\n", created))
				.append(format("Selenium version: %s\n", seleniumVersion))
				.append(format("Supported browsers: %s\n", browsers))
				.append(format("Using: %s\n", browserConfiguration))
				.append(format("Plugins: %s\n", plugins))
				.append(format("Elements: %s\n", elements)).toString();
	}
}
