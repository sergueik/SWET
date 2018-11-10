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

final class SideRecording {
	private String id;
	private String name;
	private String url;
	private List<SideSuite> suites;
	private List<SideTest> tests;
	private List<String> urls;

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<SideSuite> getSuites() {
		return suites;
	}

	public void setSuites(List<SideSuite> suites) {
		this.suites = suites;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public void setTests(List<SideTest> tests) {
		this.tests = tests;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String data) {
		this.id = data;
	}

	public List<SideTest> getTests() {
		return tests;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(format("\"id\": \"%s\"\n", id))
				.append(format("\"name\": \"%s\"\n", name))
				.append(format("\"urls\": \"%s\"\n", urls.toString()))
				.append(format("\"tests\": %s\n", tests.toString()))
				.append(format("\"suites\": %s\n", suites.toString())).toString();
	}
}
