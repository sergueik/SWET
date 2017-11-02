package org.swet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Common utilities for YAML configuration file  for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class YamlHelper {

	private static DumperOptions options = new DumperOptions();
	private static Yaml yaml = null;
	private static Calendar calendar;
	private static String yamlFile = null;

	public static void main(String[] args) throws IOException {

		yamlFile = (args.length == 0)
				? String.format("%s/%s", System.getProperty("user.dir"), "sample.yaml")
				: args[0];

		Configuration config = loadConfiguration(yamlFile);
		printConfiguration(config);

		Map<String, String> selectorFromSWD = new HashMap<>();
		selectorFromSWD.put("ElementXPath", "xpath");
		selectorFromSWD.put("ElementCssSelector", "cssSelector");
		selectorFromSWD.put("ElementText", "text");
		selectorFromSWD.put("ElementId", "id");
		// TODO:
		selectorFromSWD.put("ElementLinkText", "linkText");
		selectorFromSWD.put("ElementTagName", "tagName");
		Map<String, Map<String, String>> internalConfiguration = new HashMap<>();

		Map<String, String> keywordTable = new HashMap<>();
		keywordTable.put("CLICK", "clickButton");
		keywordTable.put("CLICK_BUTTON", "clickButton");
		keywordTable.put("CLICK_CHECKBOX", "clickCheckBox");
		keywordTable.put("CLICK_LINK", "clickLink");
		keywordTable.put("CLICK_RADIO", "clickRadioButton");
		keywordTable.put("CLOSE_BROWSER", "closeBrowser");
		keywordTable.put("CREATE_BROWSER", "openBrowser");
		keywordTable.put("ELEMENT_PRESENT", "elementPresent");
		keywordTable.put("GET_ATTR", "getElementAttribute");
		keywordTable.put("GET_TEXT", "getElementText");
		keywordTable.put("GOTO_URL", "navigateTo");
		keywordTable.put("SELECT_OPTION", "selectDropDown");
		keywordTable.put("SET_TEXT", "enterText");
		keywordTable.put("SEND_KEYS", "enterText");
		keywordTable.put("SWITCH_FRAME", "switchFrame");
		keywordTable.put("VERIFY_ATTR", "verifyAttribute");
		keywordTable.put("VERIFY_TEXT", "verifyText");
		keywordTable.put("CLEAR_TEXT", "clearText");
		keywordTable.put("WAIT", "wait");

		internalConfiguration.put("SWDSelector", selectorFromSWD);
		internalConfiguration.put("Keywords", keywordTable);

		printConfiguration(internalConfiguration);
		internalConfiguration = loadData(String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), "internalConfiguration.yaml"));
		selectorFromSWD = internalConfiguration.get("SWDSelector");
		keywordTable = internalConfiguration.get("Keywords");
	}

	// TODO: generic method
	public static Map<String, Map<String, String>> loadData(String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
		}
		Map<String, Map<String, String>> data = new HashMap<>();
		try (InputStream in = Files.newInputStream(Paths.get(fileName))) {
			data = yaml.loadAs(in, data.getClass());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static Configuration loadConfiguration(String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
		}
		Configuration config = null;
		try (InputStream in = Files.newInputStream(Paths.get(fileName))) {
			config = yaml.loadAs(in, Configuration.class);
			// TODO: better method naming
			YamlHelper.saveConfiguration(config);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return config;
	}

	public static void printConfiguration(Object config) {
		saveConfiguration(config);
	}

	public static void saveConfiguration(Object config) {
		saveConfiguration(config, null);
	}

	@SuppressWarnings("deprecation")
	public static void saveConfiguration(Object config, String fileName) {
		if (yaml == null) {
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			yaml = new Yaml(options);
		}
		/*
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date = new Date();
		try {
			config.updated = format.parse(String.format("%4d-%2d-%2d", date.getYear(),
					date.getMonth(), date.getDay()));
		} catch (java.text.ParseException e) {
			config.updated = date;
		}
		*/
		calendar = new GregorianCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,
						Locale.US)).toPattern().replaceAll("\\byy\\b", "yyyy")
								.replaceAll("\\bM\\b", "MM").replaceAll("\\bd\\b", "dd"));
		System.err.println("Testing date format: " + dateFormat.toPattern());

		if (config instanceof Configuration) {
			try {
				((Configuration) config).setUpdated(
						dateFormat.parse(dateFormat.format(calendar.getTime())));
			} catch (java.text.ParseException e) {
				System.err.println("Ignoring date parse exception: " + e.toString());
				((Configuration) config).setUpdated(new Date());
			}
		}
		if (fileName != null) {
			try {
				Writer out = new OutputStreamWriter(new FileOutputStream(fileName),
						"UTF8");
				yaml.dump(config, out);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println(yaml.dump(config));
		}
	}
}