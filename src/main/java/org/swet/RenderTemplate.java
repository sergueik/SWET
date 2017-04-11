package org.swet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import org.swet.Utils;

/**
 * Generate source code from testData and twig template for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class RenderTemplate {

	private static final HashMap<String, String> elementData = createSampleElementData();
	private static HashMap<String, HashMap<String, String>> testData;
	private String templateName = "templates/example2.twig";
	private String templateAbsolutePath = "";

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

	private static HashMap<String, String> createSampleElementData() {
		HashMap<String, String> elementData = new HashMap<String, String>();
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

	private static HashMap<String, HashMap<String, String>> createSampleTestData() {
		final HashMap<String, HashMap<String, String>> testData = new HashMap<String, HashMap<String, String>>();
		testData.put("1", elementData);
		return testData;
	}

	public String renderTest() {
		testData = createSampleTestData();
		return renderTest(testData);
	}

	public String renderTest(HashMap<String, HashMap<String, String>> testData) {
		Iterator<String> testDataKeys = testData.keySet().iterator();
		String stepId;
		List<String> scripts = new ArrayList<String>();
		while (testDataKeys.hasNext()) {
			stepId = testDataKeys.next();
			HashMap<String, String> elementData = testData.get(stepId);
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
		JtwigTemplate template = null;
		if (this.templateAbsolutePath != "") {
			template = JtwigTemplate.fileTemplate(this.templateAbsolutePath);
		} else {
			template = JtwigTemplate.classpathTemplate(this.templateName);
		}
		JtwigModel model = JtwigModel.newModel();
		for (String key : data.keySet()) {
			model.with(key, data.get(key).replace("\"", "\\\""));
		}
		String output = template.render(model);
		return output;
	}

	// Scan the template directory and build the hash of template name / path
	// options.
	public void listFilesForFolder(final File dir, String note,
			HashMap<String, String> templates) {
		FileReader fileReader = null;
		String contents = null;
		if (dir.listFiles().length == 0) {
			return;
		}
		for (final File fileEntry : dir.listFiles()) {
			contents = null;
			if (fileEntry.getName().endsWith(".twig")) {
				if (fileEntry.isFile()) {
					try {
						fileReader = new FileReader(fileEntry);
						char[] template = new char[(int) fileEntry.length()];
						fileReader.read(template);
						contents = new String(template);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (fileReader != null) {
							try {
								fileReader.close();
							} catch (IOException e) {
							}
						}
					}
				}
				if (contents != null) {
					// find comment in the template
					Matcher matcherTwigComment = Pattern
							.compile("\\{#(?:\\r?\\n)?(.*)(?:\\r?\\n)?#\\}",
									Pattern.MULTILINE)
							.matcher(contents);
					if (matcherTwigComment.find()) {
						String comment = matcherTwigComment.group(1);
						String templateName = null;
						// find template name in the comment
						Matcher matcherTemplate = Pattern
								.compile("template: (.*)$", Pattern.MULTILINE).matcher(comment);
						if (matcherTemplate.find()) {
							String templateAbsolutePath = fileEntry.getAbsolutePath();
							templateName = matcherTemplate.group(1);
							String templateLabel = String.format("%s (%s)", templateName,
									(note == null) ? "unknown" : note);
							System.out.println(String.format("Make option for \"%s\": \"%s\"",
									templateAbsolutePath, templateLabel));
							if (templates.containsKey(templateLabel)) {
								templates.replace(templateLabel, templateAbsolutePath);
							} else {
								templates.put(templateLabel, templateAbsolutePath);
							}
							System.out.println(String.format("Data for option \"%s\": \"%s\"",
									templateLabel, templates.get(templateLabel)));
						} else {
							System.out
									.println(String.format("no tag: %s", fileEntry.getName()));
						}
					} else {
						System.out
								.println(String.format("no tag: %s", fileEntry.getName()));
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO:
		// C:\developer\sergueik\selenium_java\swd_recorder\src\main\resources\templates\example3.twig
		String templatePath = "C:\\developer\\sergueik\\selenium_java\\swd_recorder\\src\\main\\resources\\templates\\example3.twig";
		RenderTemplate renderTemplate = new RenderTemplate();
		System.err
				.println(String.format("Reading template from %s ...", templatePath));
		renderTemplate.setTemplateAbsolutePath(
				templatePath.replace("\\\\", "\\").replace("\\", "/"));
		String output = renderTemplate.renderTest();
		System.err.println("Rendered: " + output);
	}
}
