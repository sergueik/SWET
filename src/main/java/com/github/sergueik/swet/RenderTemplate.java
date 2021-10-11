package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019,2021 Serguei Kouzmine
 */

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

/**
 * Generate source code from TestData hash of ElementData recorded page interaction and a 
 * caller-provided twig template for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class RenderTemplate {

	private static String defaultTemplateFileName = "templates/core_selenium_java.twig";
	private static String defaultTemplateName = "Core Selenium Java";
	private String templateName = defaultTemplateName;

	private String templateAbsolutePath = "";
	private JtwigTemplate template = null;
	private static boolean debug = false;

	public static String getDefaultTemplateFileName() {
		return defaultTemplateFileName;
	}

	public void setTemplateName(String data) {
		utils.initializeLogger();
		logger.info("Initialized logger.");
		if (data != null) {
			this.templateName = data;
		}
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateAbsolutePath(String data) {
		this.templateAbsolutePath = data;
	}

	public static void setDebug(boolean data) {
		RenderTemplate.debug = data;
	}

	public String getTemplateAbsolutePath() {
		return templateAbsolutePath;
	}

	private static Map<String, String> createSampleElementData() {
		Map<String, String> elementData = new HashMap<>();
		elementData.put("ElementId", "id");
		elementData.put("ElementCodeName",
				"name of the element, supplied during recoring");
		elementData.put("ElementText", "text of the element, when available");
		elementData.put("ElementXPath", "/html//img[1]");
		elementData.put("ElementVariable", "elementVariable");
		elementData.put("ElementCssSelector", "div#gbw > a.highlight");
		elementData.put("ElementSelectedBy", "ElementCssSelector");
		if (debug) {
			System.err.println("Sample Element data: " + elementData.toString());
		}
		return elementData;
	}

	private static Map<String, Map<String, String>> createSampleTestData() {
		final Map<String, Map<String, String>> testData = new HashMap<>();
		Map<String, String> elementData = createSampleElementData();
		testData.put("1", elementData);
		if (debug) {
			// NOTE: http://mmahmoodict.wikidot.com/apache-commons-lang3-builder-example
			// for strongly-typed Element class
			System.err.println("Sample Test data: " + testData.toString());
		}
		return testData;
	}

	private static final Logger logger = LoggerFactory.getLogger(RenderTemplate.class);
	
	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);

	private static Utils utils = Utils.getInstance();

	public String renderTest() {
		Map<String, Map<String, String>> testData = new HashMap<>();
		testData = createSampleTestData();
		return renderTest(testData);
	}

	public String renderTest(Map<String, Map<String, String>> testData) {
		Iterator<String> testDataKeys = testData.keySet().iterator();
		String stepId;
		List<String> scripts = new ArrayList<>();
		while (testDataKeys.hasNext()) {
			stepId = testDataKeys.next();
			System.err.println("Rendering step " + stepId);
			Map<String, String> elementData = testData.get(stepId);
			scripts.add(renderElement(elementData));
		}
		StringBuilder result = new StringBuilder();
		for (String line : scripts) {
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}

	private String renderElement(Map<String, String> data) {
		// Cache template contents
		if (template == null) {
			if (this.templateAbsolutePath != "") {
				System.err.println(
						"Load tempate by absolute path: " + this.templateAbsolutePath);
				template = JtwigTemplate.fileTemplate(this.templateAbsolutePath);
			} else {
				System.err
						.println("Load tempate by  resource path: " + this.templateName);
				template = JtwigTemplate.classpathTemplate(this.templateName);
			}
		}
		JtwigModel model = JtwigModel.newModel();
		if (debug) {
			System.err.println("The template: " + template.toString());
		}

		for (String key : data.keySet()) {
			if (debug) {
				System.err
						.println(String.format("\"%s\" = \"%s\"", key, data.get(key)));
			}
			model.with(key, data.get(key).replace("\"", "\\\""));
		}
		String output = template.render(model);
		if (debug) {
			System.err.println("renderElement : " + output);
		}
		return output;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		RenderTemplate template = new RenderTemplate();
		RenderTemplate.setDebug(true);
		// TODO: tests for by name template discoveries
		if (args.length > 0) {
			template.setTemplateAbsolutePath(
					String.format("%s/%s", System.getProperty("user.dir"), args[0]));
		} else {
			template.setTemplateAbsolutePath(String.format("%s/src/main/resources/%s",
					System.getProperty("user.dir"),
					RenderTemplate.getDefaultTemplateFileName()));
		}
		String payload = template.renderTest();
		System.err.println("Rendered sample: " + payload);
	}

}
