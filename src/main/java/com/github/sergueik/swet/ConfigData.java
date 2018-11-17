package com.github.sergueik.swet;

/**
 * Copyright 2018 Serguei Kouzmine
 */

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

final class ConfigData {

	private String browser;
	private String baseURL;
	private String templateName;
	private String templateDirectory;
	private String templatePath;
	private String id;

	private List<String> tests;

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String data) {
		this.browser = data;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String data) {
		this.baseURL = data;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String data) {
		this.templateName = data;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String data) {
		this.templatePath = data;
	}

	public void setTemplateDirectory(String data) {
		this.templateDirectory = data;
	}

	public String getTemplateDirectory() {
		return templateDirectory;
	}

	public String getId() {
		return id;
	}

	public void setId(String data) {
		this.id = data;
	}

	public void setTests(List<String> data) {
		this.tests = data;
	}

	public List<String> getTests() {
		return tests;
	}

	// https://stackoverflow.com/questions/11038553/serialize-java-object-with-gson
	// https://dzone.com/articles/automate-the-planet-10
	@Override
	public String toString() {
		return new StringBuilder().append(format("\"id\": \"%s\"\n", id))
				.append(format("\"browser\": \"%s\"\n", browser))
				.append(format("\"Template Name\": \"%s\"\n", templateName))
				.append(format("\"Template Directory\": \"%s\"\n", templateDirectory))
				.append(format("\"Template Path\": \"%s\"\n", templatePath))
				.append(format("\"tests\": %s\n", tests.toString())).toString();
	}
}
