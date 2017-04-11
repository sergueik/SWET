package org.swet;

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Test configuration serializer class for Selenium Webdriver Elementor Tool
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

final class Configuration {
	public Date created;
	public Date updated;
	public String version;
	public String seleniumVersion;
	public BrowserConfiguration browserConfiguration;
	public List<String> browsers;
	public HashMap<String, HashMap<String, String>> elements;
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

	public HashMap<String, HashMap<String, String>> getElements() {
		return elements;
	}

	public void setElements(HashMap<String, HashMap<String, String>> data) {
		this.elements = data;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(format("Version: %s\n", version))
				.append(format("created: %s\n", created))
				.append(format("Selenium version: %s\n", seleniumVersion))
				.append(format("Supported browsers: %s\n", browsers))
				.append(format("Using: %s\n", browserConfiguration))
				.append(format("Plugins: %s\n", plugins))
				.append(format("elements: %s\n", elements)).toString();
	}
}
