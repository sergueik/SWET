package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.github.sergueik.swet.SideRecording;
import com.github.sergueik.swet.SideTest;
import com.github.sergueik.swet.SideCommand;
import com.github.sergueik.swet.SideSuite;

/**
 * Side Suite serializer class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

final class SideSuite {

	private String name;
	private String id;
	private List<String> testNames;

	public String getName() {
		return name;
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

	public void setTestNames(List<String> data) {
		this.testNames = data;
	}

	public List<String> getTestNames() {
		return testNames;
	}

	// https://github.com/SeleniumHQ/selenium-ide/issues/222
	// Can Selenium IDE TNG resurrect support to export script to html format?
	// https://dzone.com/articles/automate-the-planet-10
	@Override
	public String toString() {
		return new StringBuilder().append(format("\"id\": \"%s\"\n", id))
				.append(format("\"name\": \"%s\"\n", name))
				.append(format("\"testNames\": %s\n", testNames.toString())).toString();
	}
}
