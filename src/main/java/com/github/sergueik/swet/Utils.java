package com.github.sergueik.swet;

/**
 * Copyright 2014 - 2019,2021 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
//for sorting elements by valueColumn, returns Array List of indexColumn
//probably a regular class is sufficient for java 8 projects
//http://backport-jsr166.sourceforge.net/doc/api/edu/emory/mathcs/backport/java/util/Collections.html
//import edu.emory.mathcs.backport.java.util.Collections;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Common utilities class for Selenium WebDriver Elementor Tool (SWET)
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class Utils {

	private static Utils instance = new Utils();

	private static Keys keyCTRL;
	private final String defaultKey = "ElementCodeName";
	private static final String getSWDCommand = "return document.swdpr_command === undefined ? '' : document.swdpr_command;";

	private static String osName = OSUtils.getOsName();
	private boolean debug = false;

	public void setDebug(boolean value) {
		debug = value;
	}

	private WebDriver driver;
	private WebDriverWait wait;
	private Actions actions;
	private int flexibleWait = 5;

	private Utils() {
	}

	public static Utils getInstance() {
		return instance;
	}

	private String defaultScript;

	public String getDefaultScript() {
		return defaultScript;
	}

	public void setDefaultScript(String value) {
		defaultScript = value;
	}

	public void setFlexibleWait(int flexibleWait) {
		this.flexibleWait = flexibleWait;
		this.wait = new WebDriverWait(this.driver, flexibleWait);
	}

	public void setDriver(WebDriver value) {
		driver = value;
	}

	public void setActions(Actions value) {
		actions = value;
	}

	// NOTE: put inside "WEB-INF/classes" for web hosted app
	public String getScriptContent(String resourceFileName) {
		try {
			if (debug) {
				System.err.println("Script contents: " + getResourceURI(resourceFileName));
			}
			final InputStream stream = getResourceStream(resourceFileName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// NOTE: getResourceURI may not work with standalone or web hosted
	// application
	public String getResourceURI(String resourceFileName) {
		try {
			if (debug) {
				System.err.println("Getting resource URI for: " + resourceFileName);
			}

			URI uri = this.getClass().getClassLoader().getResource(resourceFileName).toURI();
			if (debug) {
				System.err.println("Resource URI: " + uri.toString());
			}
			return uri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getResourceStream(String resourceFilePath) {
		return this.getClass().getClassLoader().getResourceAsStream(resourceFilePath);
	}

	public String getResourcePath(String resourceFileName) {
		final String resourcePath = String.format("%s/src/main/resources/%s", System.getProperty("user.dir"),
				resourceFileName);
		if (debug) {
			System.err.println("Project based resource path: " + resourcePath);
		}
		return resourcePath;
	}

	public String writeDataJSON(Map<String, String> data, String defaultPayload) {
		String payload = defaultPayload;
		JSONObject json = new JSONObject();
		try {
			for (String key : data.keySet()) {
				json.put(key, data.get(key));
			}
			StringWriter wr = new StringWriter();
			json.write(wr);
			payload = wr.toString();
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e);
		}
		return payload;
	}

	public String readSideData(Optional<Map<String, Object>> parameters) {
		return readSideData(null, parameters);
	}

	public String readSideData(String payload, Optional<Map<String, Object>> parameters) {

		return readSideData(payload, parameters, "(?:id|name|url|tests)");
	}

	// Deserialize the hashmap from the JSON
	// see also
	// https://stackoverflow.com/questions/3763937/gson-and-deserializing-an-array-of-objects-with-arrays-in-it
	// https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
	public String readSideData(String payload, Optional<Map<String, Object>> parameters, String acceptedKeys) {
		if (debug) {
			System.err.println("Accepted keys: " + acceptedKeys);
		}

		Map<String, Object> collector = (parameters.isPresent()) ? parameters.get() : new HashMap<>();

		String data = (payload == null)
				? "{\"id\":\"837d3acd-285e-478a-8d46-817df0a5b4d9\",\"name\":\"Google<br>\",\"url\":\"https://www.google.com \t\",\"tests\":[{\"id\":\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\",\"name\":\"Google\",\"commands\":[{\"id\":\"160c2276-d9b3-4523-bdf3-b914111ca407\",\"comment\":\"\",\"command\":\"open\",\"target\":\"/images\",\"value\":\"\"}]}],\"suites\":[{\"id\":\"05e89807-cb33-4ca6-8ca4-10e1cdf127c3\",\"name\":\"Default Suite\",\"tests\":[\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\"]}],\"urls\":[\"https://www.google.co.in\",\"https://www.google.co.in\"]}"
				: payload;
		if (debug) {
			System.err.println("Processing payload: " + data.replaceAll(",", ",\n"));
		}
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {

				String propertyKey = propIterator.next();
				if (!propertyKey.matches(acceptedKeys /* "(?:id|name|url|tests)" */)) {
					System.err.println("Ignoring key: " + propertyKey);
					continue;
				}
				if (debug) {
					System.err.println("Processing key: " + propertyKey);
				}
				Boolean found = false;
				try {
					String propertyVal = (String) elementObj.getString(propertyKey);
					// logger.info(propertyKey + ": " + propertyVal);
					if (debug) {
						System.err.println("Loaded string: " + propertyKey + ": " + propertyVal);
					}
					collector.put(propertyKey, propertyVal);
					found = true;
				} catch (JSONException e) {
					System.err.println("Exception (ignored, continue): " + e.toString());
				}
				if (found) {
					continue;
				}
				try {
					org.json.JSONArray propertyArrayVal = elementObj.getJSONArray(propertyKey);
					int length = propertyArrayVal.length();
					if (debug) {
						System.err.println("Can process array of size: " + length);
					}
					StringBuffer innerData = new StringBuffer();
					;
					for (int index = 0; index < length; index++) {
						JSONObject rowObject = propertyArrayVal.getJSONObject(index);
						if (debug) {
							System.err.println("Can process object: " + rowObject.toString());
						}
						// "comment,id,value,command,target"
						readSideData(rowObject.toString(), Optional.<Map<String, Object>>empty(),
								"(?:comment|id|value|command|target)");

						Iterator<String> rowObjectIterator = rowObject.keys();

						while (rowObjectIterator.hasNext()) {
							String rowObjectKey = rowObjectIterator.next();
							innerData.append(String.format("%s,", rowObjectKey));
							if (debug) {
								System.err.println("Processing Row key: " + rowObjectKey);
							}
						}
					}
					collector.put(propertyKey, innerData.toString());
					found = true;
				} catch (JSONException e) {
					System.err.println("Exception (ignored, continue): " + e.toString());
				}
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored, aborting): " + e.toString());
			return null;
		}
		return (String) collector.get("id");
	}

	public String readData(Optional<Map<String, String>> parameters) {
		return readData(null, parameters);
	}

	// Deserialize the hashmap from the JSON
	// see also
	// https://stackoverflow.com/questions/3763937/gson-and-deserializing-an-array-of-objects-with-arrays-in-it
	// https://futurestud.io/tutorials/gson-mapping-of-arrays-and-lists-of-objects
	public String readData(String payload, Optional<Map<String, String>> parameters) {

		Map<String, String> collector = (parameters.isPresent()) ? parameters.get() : new HashMap<>();

		String data = (payload == null)
				? "{ \"Url\": \"http://www.google.com\", \"ElementCodeName\": \"Name of the element\", \"CommandId\": \"d5be4ea9-c51f-4e61-aefc-e5c83ba00be8\", \"ElementCssSelector\": \"html div.home-logo_custom > img\", \"ElementId\": \"\", \"ElementXPath\": \"/html//img[1]\" }"
				: payload;
		try {
			JSONObject elementObj = new JSONObject(data);
			@SuppressWarnings("unchecked")
			Iterator<String> propIterator = elementObj.keys();
			while (propIterator.hasNext()) {
				String propertyKey = propIterator.next();
				String propertyVal = elementObj.getString(propertyKey);
				// logger.info(propertyKey + ": " + propertyVal);
				if (debug) {
					System.err.println("readData: " + propertyKey + ": " + propertyVal);
				}
				collector.put(propertyKey, propertyVal);
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.toString());
			return null;
		}
		return collector.get(defaultKey);
	}

	public void flushVisualSearchResult() {
		executeScript("document.swdpr_command = undefined;");
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	public String readVisualSearchResult(String payload) {
		return readVisualSearchResult(payload, Optional.<Map<String, String>>empty());
	}

	public String readVisualSearchResult(final String payload, Optional<Map<String, String>> parameters) {
		Boolean collectResults = parameters.isPresent();
		Map<String, String> collector = (collectResults) ? parameters.get() : new HashMap<>();
		String result = readData(payload, Optional.of(collector));
		assertTrue(collector.containsKey("ElementId"));
		// NOTE: elementCodeName will not be set if
		// user clicked the SWD Table Close Button
		// ElementId is always set
		// TODO: read the 'ElementSelectedBy'
		return result;
	}

	public void highlight(WebElement element, long highlight_interval) {
		try {
			new WebDriverWait(driver, flexibleWait).until(ExpectedConditions.visibilityOf(element));
			executeScript("arguments[0].style.border='3px solid yellow'", element);
			Thread.sleep(highlight_interval);
			executeScript("arguments[0].style.border=''", element);
		} catch (InterruptedException e) {
			System.err.println("Exception (ignored): " + e.toString());
		}
	}

	public void completeVisualSearch(String elementCodeName) {
		WebElement swdAddElementButton = null;
		try {
			WebElement swdControl = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
			assertThat(swdControl, notNullValue());
			WebElement swdCodeID = wait
					.until(ExpectedConditions.visibilityOf(swdControl.findElement(By.id("SwdPR_PopUp_CodeIDText"))));
			assertThat(swdCodeID, notNullValue());
			// Act
			swdCodeID.sendKeys(elementCodeName);
			swdAddElementButton = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver _driver) {
					System.err.println("Waiting for the element to become available...");
					Iterator<WebElement> _elements = _driver
							.findElements(By.cssSelector("div#SwdPR_PopUp > form > input[type='button']")).iterator();
					WebElement result = null;
					Pattern pattern = Pattern.compile(Pattern.quote("Add element"), Pattern.CASE_INSENSITIVE);
					while (_elements.hasNext()) {
						WebElement _element = _elements.next();
						Matcher matcher = pattern.matcher(_element.getAttribute("value"));
						if (matcher.find()) {
							result = _element;
							break;
						}
					}
					return result;
				}
			});
		} catch (Exception e) {
			ExceptionDialogEx.getInstance().render(e);
			if (driver != null) {
				try {
					// NOTE: BrowserDriver static method
					BrowserDriver.close();
				} catch (Exception ex) {
					System.err.println("Exception (ignored): " + ex.toString());
				}
			}
		}

		assertThat(swdAddElementButton, notNullValue());
		highlight(swdAddElementButton);
		flash(swdAddElementButton);
		// Act
		swdAddElementButton.click();
	}

	public void completeVisualSearchSaved(String elementCodeName) {
		/*
		 * WebElement swdControl = wait.until(new Function<WebDriver, WebElement>() {
		 * 
		 * @Override public WebElement apply(WebDriver d) { WebElement e =
		 * d.findElement(By.id("SWDTable")); return e.isDisplayed() ? e : null; } });
		 */
		WebElement swdControl = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
		assertThat(swdControl, notNullValue());
		/*
		 * WebElement swdCodeID = wait.until(new Function<WebDriver, WebElement>() {
		 * 
		 * @Override public WebElement apply(WebDriver d) { WebElement e =
		 * d.findElement(By.id("SwdPR_PopUp_CodeIDText")); return e.isDisplayed() ? e :
		 * null; } });
		 */
		WebElement swdCodeID = wait
				.until(ExpectedConditions.visibilityOf(swdControl.findElement(By.id("SwdPR_PopUp_CodeIDText"))));
		assertThat(swdCodeID, notNullValue());
		swdCodeID.sendKeys(elementCodeName);
		/*
		 * WebElement swdAddElementButton = wait .until(new Function<WebDriver,
		 * WebElement>() {
		 * 
		 * @Override public WebElement apply(WebDriver d) { WebElement e =
		 * d.findElement(By.cssSelector(
		 * "div#SwdPR_PopUp > input[type='button'][value='Add element']"));
		 * System.err.println( "in apply iterator (1): Text = " +
		 * e.getAttribute("value")); return e.isDisplayed() ? e : null; } });
		 */
		/*
		 * WebElement swdAddElementButton = wait
		 * .until(ExpectedConditions.visibilityOf(swdControl.findElement(
		 * By.xpath("//input[@type='button'][@value='Add element']"))));
		 * assertThat(swdAddElementButton, notNullValue());
		 */
		WebElement swdAddElementButton = null;
		try {
			swdAddElementButton = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver _driver) {
					System.err.println("Waiting for the element to become available...");
					Iterator<WebElement> _elements = _driver
							.findElements(By.cssSelector("div#SwdPR_PopUp > form > input[type='button']")).iterator();
					WebElement result = null;
					Pattern pattern = Pattern.compile(Pattern.quote("Add element"), Pattern.CASE_INSENSITIVE);
					while (_elements.hasNext()) {
						WebElement _element = _elements.next();
						Matcher matcher = pattern.matcher(_element.getAttribute("value"));
						if (matcher.find()) {
							result = _element;
							break;
						}
					}
					return result;
				}
			});
		} catch (Exception e) {
			// TODO: dialog
			// ExceptionDialogEx.getInstance().render(e);
			System.err.println("Exception: " + e.toString());
		}

		assertThat(swdAddElementButton, notNullValue());
		highlight(swdAddElementButton);
		flash(swdAddElementButton);
		// Act
		swdAddElementButton.click();
	}

	public void closeVisualSearch() {
		WebElement swdControl = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
		assertThat(swdControl, notNullValue());

		WebElement swdCloseButton = wait
				.until(ExpectedConditions.visibilityOf(swdControl.findElement(By.id("SwdPR_PopUp_CloseButton"))));
		assertThat(swdCloseButton, notNullValue());
		highlight(swdCloseButton);
		swdCloseButton.click();
	}

	// http://stackoverflow.com/questions/34176392/fluentwait-throwing-the-method-unti-in-the-type-waitwebdriver-is-not-applicab
	private void closeVisualSearchSaved() {

		WebElement swdControl = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
		assertThat(swdControl, notNullValue());
		/*
		 * WebElement swdCloseButton = null; try { swdCloseButton = wait.until(new
		 * Function<WebDriver, WebElement>() {
		 * 
		 * @Override public WebElement apply(WebDriver d) { Iterator<WebElement> i = d
		 * .findElements(By.id("SwdPR_PopUp_CloseButton")).iterator(); WebElement result
		 * = null; // "(?:" + "Navigate Back" + ")" Pattern pattern =
		 * Pattern.compile(Pattern.quote("X"), Pattern.CASE_INSENSITIVE); while
		 * (i.hasNext()) { WebElement e = (WebElement) i.next(); String t = e.getText();
		 * // System.err.println("in apply iterator (2): Text = " + t); Matcher matcher
		 * = pattern.matcher(t); if (matcher.find()) { result = e; break; } } return
		 * result; } }); assertThat(swdCloseButton, notNullValue());
		 * utils.highlight(swdCloseButton); swdCloseButton.click();
		 * 
		 * } catch (Exception e) { // TODO: dialog System.err.println("Exception: " +
		 * e.toString()); }
		 */

		WebElement swdCloseButton = wait
				.until(ExpectedConditions.visibilityOf(swdControl.findElement(By.id("SwdPR_PopUp_CloseButton"))));
		assertThat(swdCloseButton, notNullValue());
		highlight(swdCloseButton);
		swdCloseButton.click();
	}

	public String getElementText(WebElement element) {
		// http://stackoverflow.com/questions/6743912/get-the-pure-text-without-html-element-by-javascript
		String script = "var element = arguments[0];var text = element.innerText || element.textContent || ''; return text;";
		return (String) executeScript(script, element);
	}

	public void inspectElement(WebElement element) {
		keyCTRL = osName.startsWith("mac") ? Keys.COMMAND : Keys.CONTROL;

		if (osName.startsWith("mac")) {
			actions.keyDown(keyCTRL).build().perform();
			actions.moveToElement(element).contextClick().build().perform();
			actions.keyUp(keyCTRL).build().perform();
		} else {
			actions.moveToElement(element).build().perform();
			actions.keyDown(keyCTRL).contextClick().keyUp(keyCTRL).build().perform();
		}
		// Assert
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	public void highlight(WebElement element) {
		highlight(element, 100);
	}

	public static boolean createFolder(String path) throws Exception {
		File dir = new File(path.trim());
		if (!dir.exists()) {
			return dir.mkdir();
		} else
			return false;
	}

	/*
	 * public static String getDate() { try { DateFormat dateFormat = new
	 * SimpleDateFormat("yyyy-MM-dd"); Date date = new Date(); return
	 * dateFormat.format(date); } catch (Exception e) { } return ""; }
	 * 
	 * public static String getTime() { try { DateFormat dateFormat = new
	 * SimpleDateFormat("HHmmss"); Date date = new Date(); return
	 * dateFormat.format(date); } catch (Exception e) { } return ""; }
	 */

	public static void writeToFile(List<String> content, String filename, Boolean overwriteFlag) {
		File file = new File(filename);
		if (overwriteFlag) {

			try {
				file.createNewFile();
				FileWriter fw = null;
				try {
					fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					for (String line : content) {
						bw.write(line);
						bw.newLine();
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Write content to " + filename + " succesfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> readFileLineByLine(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		List<String> res = new ArrayList<>();

		String line = null;
		while ((line = br.readLine()) != null) {
			res.add(line);
		}
		br.close();
		return res;
	}

	// based on:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
	/*
	 * public static String getPropertyEnv(String name, String defaultValue) {
	 * String value = System.getProperty(name); if (value == null) { value =
	 * System.getenv(name); if (value == null) { value = defaultValue; } } return
	 * value; }
	 */

	public String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (debug) {
			System.err.println("Getting propety or environment: " + name);
		}
		// compatible with
		// org.apache.commons.configuration.PropertiesConfiguration.interpolatedConfiguration
		// https://commons.apache.org/proper/commons-configuration/userguide_v1.10/howto_utilities.html
		if (value == null) {

			Pattern p = Pattern.compile("^(\\w+:)(\\w+)$");
			Matcher m = p.matcher(name);
			if (m.find()) {
				String propertyName = m.replaceFirst("$2");
				if (debug) {
					System.err.println("Interpolating " + propertyName);
				}
				value = System.getProperty(propertyName);
			}
			if (value == null) {
				if (debug) {
					System.err.println("Trying environment " + name);
				}
				value = System.getenv(name);
				if (value == null) {
					if (debug) {
						System.err.println("Nothing found for " + name);
					}
					value = defaultValue;
				}
			}
		}
		return value;
	}

	public static String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}
		Pattern p = Pattern.compile("\\$(?:\\{(\\w+)\\}|(\\w+))");
		Matcher m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb, null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// based on:
	// https://github.com/abhishek8908/selenium-drivers-download-plugin/blob/master/src/main/java/com/github/abhishek8908/util/DriverUtil.java
	public static String readProperty(String propertyName, String propertyFile, String defaultValue) {
		String resourcePath = "";
		try {
			resourcePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			System.err.println(String.format("The running application resource path: \"%s\"", resourcePath));
		} catch (NullPointerException e) {
			System.err.println("Exception (ignored): " + e.toString());
			/*
			 * if (debug) { e.printStackTrace(); }
			 */
		}
		Configuration config = null;
		try {
			config = new PropertiesConfiguration(resourcePath + propertyFile);

			Configuration extConfig = ((PropertiesConfiguration) config).interpolatedConfiguration();
			final String value = extConfig.getProperty(propertyName).toString();
			return (value == null) ? defaultValue : value;
		} catch (ConfigurationException e) {
			return null;
		}
	}

	public static String readProperty(String propertyName) {
		System.err.println("x");
		return readProperty(propertyName, "application.properties", null);
	}

	public static String readProperty(String propertyName, String defaultValue) {
		final String value = readProperty(propertyName, "application.properties", defaultValue);
		return value == null ? defaultValue : value;
	}

	// TODO: array
	public void injectScripts(Optional<String> script) {
		ArrayList<String> scripts = (defaultScript == null) ? new ArrayList<>()
				: new ArrayList<>(Arrays.asList(getScriptContent(defaultScript)));
		if (script.isPresent()) {
			scripts.add(script.get());
		}
		for (String s : scripts) {
			System.err.println(String.format("Executing: %s\u2026", s.substring(0, 30)));
			if (s != null)
				executeScript(s);
		}
	}

	public void injectElementSearch(Optional<String> script) {
		List<String> scripts = new ArrayList<>(Arrays.asList(getScriptContent(defaultScript)));
		if (script.isPresent()) {
			scripts.add(script.get());
		}
		for (String s : scripts) {
			if (s != null)
				System.err.println(String.format("Adding the script: %s\u2026", s.substring(0, 100)));
			executeScript(s);
		}
	}

	public Object executeScript(String script, Object... arguments) {
		if (driver != null && (driver instanceof JavascriptExecutor)) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class.cast(driver);
			// IE: org.openqa.selenium.NoSuchWindowException
			// Chrome: Exception in thread "main"
			// org.openqa.selenium.WebDriverException: disconnected: not connected to
			// DevTools
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed: driver it not defined properly");
		}
	}

	public String readManifestVersion() {
		// https://stackoverflow.com/questions/1429172/how-do-i-list-the-files-inside-a-jar-file
		CodeSource src = this.getClass().getProtectionDomain().getCodeSource();
		String result = null;
		String manifestTag = "Implementation-Version: (\\d+\\.\\d+(?:\\.\\d+)(?:\\-SNAPSHOT)*)";
		try {
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry ze = null;
			while ((ze = zip.getNextEntry()) != null) {
				String manifestResourcePath = ze.getName();
				if (manifestResourcePath.endsWith("MANIFEST.MF")) {
					InputStream inputStream = getResourceStream(manifestResourcePath);
					String manifestSource = IOUtils.toString(inputStream, "UTF8");

					Pattern pattern = Pattern.compile(manifestTag, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(Pattern.quote(manifestSource));
					if (matcher.find()) {
						result = matcher.group(1);
						System.err.println("Discovered version: " + result + " in manifest : " + manifestResourcePath);
					}
					IOUtils.closeQuietly(inputStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String id() {
		return UUID.randomUUID().toString();
	}

	public String getPayload() {
		// java.uil.Random
		// java.security.SecureRandom
		return executeScript(getSWDCommand).toString();
	}

	// sorting elements by valueColumn, returns Array List of indexColumn
	public List<String> sortSteps(Map<String, Map<String, String>> testData, String indexColumn, String valueColumn) {

		List<String> sortedSteps = new ArrayList<>();
		Map<String, Integer> elementSteps = testData.values().stream()
				.collect(Collectors.toMap(o -> o.get(indexColumn), o -> Integer.parseInt(o.get(valueColumn))));
		/*
		 * elementSteps = testData.keySet().stream().collect(Collectors.toMap(o -> o, o
		 * -> Integer.parseInt(testData.get(o).get(valueColumn))));
		 */
		List<Entry<String, Integer>> stepNumbers = new ArrayList<>();
		stepNumbers.addAll(elementSteps.entrySet());
		Collections.sort(stepNumbers, new Comparator<Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> obj_left, Entry<String, Integer> obj_right) {
				return obj_left.getValue().compareTo(obj_right.getValue());

			}
		});
		return stepNumbers.stream().map(e -> e.getKey()).collect(Collectors.toList());
	}

	public void sleep(Integer seconds) {
		long secondsLong = (long) seconds;
		try {
			Thread.sleep(secondsLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void flash(WebElement element) {
		String bgcolor = element.getCssValue("backgroundColor");
		for (int i = 0; i < 3; i++) {
			changeColor("rgb(0,200,0)", element);
			changeColor(bgcolor, element);
		}
	}

	public void changeColor(String color, WebElement element) {
		executeScript("arguments[0].style.backgroundColor = '" + color + "'", element);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
	}

	// sorting example from
	// http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	// currently not used
	public <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public void initializeLogger(String propertiesFilePath) {
		Properties logProperties = new Properties();
		try {
			// Can not load XML configuration?
			// Log Properties: [[</appender>, </root>, <root>, </log4j, <log4j,
			// <layout, <!--,
			// <?xml, log4j, debug, value, <level, The, <appender, </layout>,
			// <!DOCTYPE, <appender-ref, -->, <param]]
			// Confirmed in test, that the default log4j properties is being loaded
			if (debug) {
				System.err.println("Log Properties file path: " + propertiesFilePath);
			}
			logProperties.load(new FileInputStream(propertiesFilePath));
			if (debug) {
				System.err.println("Log Properties: " + Arrays.asList(logProperties.keySet()));
			}
			PropertyConfigurator.configure(logProperties);
		} catch (IOException e) {
			throw new RuntimeException("Fail to load: " + propertiesFilePath);
		}
	}

	public void initializeLogger() {
		String defaultLog4JPropertiesFilePath = String.format("%s/%s/%s", System.getProperty("user.dir"),
				"src/main/resources", "log4j.xml");
		initializeLogger(defaultLog4JPropertiesFilePath);
	}

	private StringBuilder stringBuilder = new StringBuilder(512 * 1024);

	public String escape(String value) {
		stringBuilder.setLength(0);
		int offset = 0;
		while (offset < value.length()) {
			int codePoint = value.codePointAt(offset);
			escape(codePoint);
			offset += Character.charCount(codePoint);
		}
		return stringBuilder.toString();
	}

	// origin:
	// https://github.com/dhatim/fastexcel/blob/master/fastexcel-writer/src/main/java/org/dhatim/fastexcel/Writer.java
	// Formats the char with relevant XML escaping. Invalid characters in XML 1.0
	// are
	private void escape(int charCode) {
		// only render supported range
		// https://en.wikipedia.org/wiki/Valid_characters_in_XML
		if (!(charCode == 0x9 || charCode == 0xa || charCode == 0xD || (charCode >= 0x20 && charCode <= 0xd7ff)
				|| (charCode >= 0xe000 && charCode <= 0xfffd) || (charCode >= 0x10000 && charCode <= 0x10ffff))) {
			return;
		}
		switch (charCode) {
		case '<':
			stringBuilder.append("&lt;");
			break;
		case '>':
			stringBuilder.append("&gt;");
			break;
		case '&':
			stringBuilder.append("&amp;");
			break;
		case '\'':
			stringBuilder.append("&apos;");
			break;
		case '"':
			stringBuilder.append("&quot;");
			break;
		default:
			if (charCode > 0x7e || charCode < 0x20) {
				stringBuilder.append("&#x").append(Integer.toHexString(charCode)).append(';');
			} else {
				stringBuilder.append((char) charCode);
			}
			break;
		}
	}

}
