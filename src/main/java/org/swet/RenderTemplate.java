package org.swet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

/**
 * Generate source code from testData and twig template for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class RenderTemplate {

	private String templateName = "templates/example2.twig";
	private String templateAbsolutePath = "";
	private JtwigTemplate template = null;

	public void setTemplateName(String data) {
		this.templateName = data;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateAbsolutePath(String data) {
		this.templateAbsolutePath = data;
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
		return elementData;
	}

	private static Map<String, Map<String, String>> createSampleTestData() {
		final Map<String, Map<String, String>> testData = new HashMap<>();
		Map<String, String> elementData = createSampleElementData();
		testData.put("1", elementData);
		return testData;
	}

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
			System.err.println("Step " + stepId);
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
		for (String key : data.keySet()) {
			// System.err.println(String.format("\"%s\" = \"%s\"", key,
			// data.get(key)));
			model.with(key, data.get(key).replace("\"", "\\\""));
		}
		String output = template.render(model);
		// System.err.println("renderElement : " + output);
		return output;
	}
}
