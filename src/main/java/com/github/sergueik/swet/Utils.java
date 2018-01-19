package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Map.Entry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

/**
 * Common utilities class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class Utils {

	private static Utils instance = new Utils();

	private static Keys keyCTRL;
	private static final String getSWDCommand = "return document.swdpr_command === undefined ? '' : document.swdpr_command;";

	private static String osName = OSUtils.getOsName();
	private WebDriver driver;
	private WebDriverWait wait;
	private Actions actions;
	private int flexibleWait = 5;

	public void setFlexibleWait(int flexibleWait) {
		this.flexibleWait = flexibleWait;
		this.wait = new WebDriverWait(this.driver, flexibleWait);
	}

	public void setDriver(WebDriver value) {
		this.driver = value;
	}

	public void setActions(Actions value) {
		this.actions = value;
	}

	private Utils() {
	}

	public static Utils getInstance() {
		return instance;
	}

	private static String defaultScript = "ElementSearch.js";

	public String getScriptContent(String resourceFileName) {
		try {
			/* System.err.println("Script contents: " + getResourceURI(resourceFileName));		*/
			final InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream(resourceFileName);
			final byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			return new String(bytes, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// NOTE: getResourceURI does not work well with standalone app.
	public String getResourceURI(String resourceFileName) {
		try {
			URI uri = this.getClass().getClassLoader().getResource(resourceFileName)
					.toURI();
			// System.err.println("Resource URI: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getResourceStream(String resourceFilePath) {
		return this.getClass().getClassLoader()
				.getResourceAsStream(resourceFilePath);
	}

	public String getResourcePath(String resourceFileName) {
		return String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), resourceFileName);
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

	public String readData(Optional<Map<String, String>> parameters) {
		return readData(null, parameters);
	}

	public String readData(String payload,
			Optional<Map<String, String>> parameters) {

		Map<String, String> collector = (parameters.isPresent()) ? parameters.get()
				: new HashMap<>();

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
				System.err.println("readData: " + propertyKey + ": " + propertyVal);
				collector.put(propertyKey, propertyVal);
			}
		} catch (JSONException e) {
			System.err.println("Exception (ignored): " + e.toString());
			return null;
		}
		return collector.get("ElementCodeName");
	}

	public void flushVisualSearchResult() {
		executeScript("document.swdpr_command = undefined;");
	}

	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	public String readVisualSearchResult(String payload) {
		return readVisualSearchResult(payload,
				Optional.<Map<String, String>> empty());
	}

	public String readVisualSearchResult(final String payload,
			Optional<Map<String, String>> parameters) {
		Boolean collectResults = parameters.isPresent();
		Map<String, String> collector = (collectResults) ? parameters.get()
				: new HashMap<>();
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
			new WebDriverWait(driver, flexibleWait)
					.until(ExpectedConditions.visibilityOf(element));
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
			WebElement swdControl = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.id("SWDTable"))));
			assertThat(swdControl, notNullValue());
			WebElement swdCodeID = wait.until(ExpectedConditions.visibilityOf(
					swdControl.findElement(By.id("SwdPR_PopUp_CodeIDText"))));
			assertThat(swdCodeID, notNullValue());
			// Act
			swdCodeID.sendKeys(elementCodeName);
			swdAddElementButton = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver _driver) {
					System.err.println("Waiting for the element to become available...");
					Iterator<WebElement> _elements = _driver
							.findElements(By
									.cssSelector("div#SwdPR_PopUp > form > input[type='button']"))
							.iterator();
					WebElement result = null;
					Pattern pattern = Pattern.compile(Pattern.quote("Add element"),
							Pattern.CASE_INSENSITIVE);
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
		WebElement swdControl = wait.until(new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver d) {
				WebElement e = d.findElement(By.id("SWDTable"));
				return e.isDisplayed() ? e : null;
			}
		});
		*/
		WebElement swdControl = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
		assertThat(swdControl, notNullValue());
		/*
		WebElement swdCodeID = wait.until(new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver d) {
				WebElement e = d.findElement(By.id("SwdPR_PopUp_CodeIDText"));
				return e.isDisplayed() ? e : null;
			}
		});
		*/
		WebElement swdCodeID = wait.until(ExpectedConditions
				.visibilityOf(swdControl.findElement(By.id("SwdPR_PopUp_CodeIDText"))));
		assertThat(swdCodeID, notNullValue());
		swdCodeID.sendKeys(elementCodeName);
		/*
		WebElement swdAddElementButton = wait
		.until(new Function<WebDriver, WebElement>() {
			@Override
			public WebElement apply(WebDriver d) {
				WebElement e = d.findElement(By.cssSelector(
						"div#SwdPR_PopUp > input[type='button'][value='Add element']"));
				System.err.println(
						"in apply iterator (1): Text = " + e.getAttribute("value"));
				return e.isDisplayed() ? e : null;
			}
		});
		*/
		/*
		WebElement swdAddElementButton = wait
				.until(ExpectedConditions.visibilityOf(swdControl.findElement(
						By.xpath("//input[@type='button'][@value='Add element']"))));
		assertThat(swdAddElementButton, notNullValue());
		*/
		WebElement swdAddElementButton = null;
		try {
			swdAddElementButton = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver _driver) {
					System.err.println("Waiting for the element to become available...");
					Iterator<WebElement> _elements = _driver
							.findElements(By
									.cssSelector("div#SwdPR_PopUp > form > input[type='button']"))
							.iterator();
					WebElement result = null;
					Pattern pattern = Pattern.compile(Pattern.quote("Add element"),
							Pattern.CASE_INSENSITIVE);
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
		WebElement swdControl = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
		assertThat(swdControl, notNullValue());

		WebElement swdCloseButton = wait.until(ExpectedConditions.visibilityOf(
				swdControl.findElement(By.id("SwdPR_PopUp_CloseButton"))));
		assertThat(swdCloseButton, notNullValue());
		highlight(swdCloseButton);
		swdCloseButton.click();
	}

	// http://stackoverflow.com/questions/34176392/fluentwait-throwing-the-method-unti-in-the-type-waitwebdriver-is-not-applicab
	private void closeVisualSearchSaved() {

		WebElement swdControl = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.id("SWDTable"))));
		assertThat(swdControl, notNullValue());
		/*
		WebElement swdCloseButton = null;
		try {
			swdCloseButton = wait.until(new Function<WebDriver, WebElement>() {
				@Override
				public WebElement apply(WebDriver d) {
					Iterator<WebElement> i = d
							.findElements(By.id("SwdPR_PopUp_CloseButton")).iterator();
					WebElement result = null;
					// "(?:" + "Navigate Back" + ")"
					Pattern pattern = Pattern.compile(Pattern.quote("X"),
							Pattern.CASE_INSENSITIVE);
					while (i.hasNext()) {
						WebElement e = (WebElement) i.next();
						String t = e.getText();
						// System.err.println("in apply iterator (2): Text = " + t);
						Matcher matcher = pattern.matcher(t);
						if (matcher.find()) {
							result = e;
							break;
						}
					}
					return result;
				}
			});
			assertThat(swdCloseButton, notNullValue());
			utils.highlight(swdCloseButton);
			swdCloseButton.click();
		
		} catch (Exception e) {
			// TODO: dialog
			System.err.println("Exception: " + e.toString());
		}
		*/

		WebElement swdCloseButton = wait.until(ExpectedConditions.visibilityOf(
				swdControl.findElement(By.id("SwdPR_PopUp_CloseButton"))));
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
		keyCTRL = osName.startsWith("Mac") ? Keys.COMMAND : Keys.CONTROL;

		if (osName.startsWith("Mac")) {
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
	public static String getDate() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			return dateFormat.format(date);
		} catch (Exception e) {
		}
		return "";
	}
	
	public static String getTime() {
		try {
			DateFormat dateFormat = new SimpleDateFormat("HHmmss");
			Date date = new Date();
			return dateFormat.format(date);
		} catch (Exception e) {
		}
		return "";
	}
	*/

	public static void writeToFile(List<String> content, String filename,
			Boolean overwriteFlag) {
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

	public static List<String> readFileLineByLine(String filename)
			throws IOException {
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

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(name);
			if (value == null) {
				value = defaultValue;
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
			m.appendReplacement(sb,
					null == envVarValue ? "" : envVarValue.replace("\\", "\\\\"));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	// TODO: array
	public void injectScripts(Optional<String> script) {
		ArrayList<String> scripts = (defaultScript == null) ? new ArrayList<>()
				: new ArrayList<>(Arrays.asList(getScriptContent(defaultScript)));
		if (script.isPresent()) {
			scripts.add(script.get());
		}
		for (String s : scripts) {
			System.err
					.println(String.format("Executing: %s\u2026", s.substring(0, 30)));
			if (s != null)
				executeScript(s);
		}
	}

	public void injectElementSearch(Optional<String> script) {
		List<String> scripts = new ArrayList<>(
				Arrays.asList(getScriptContent(defaultScript)));
		if (script.isPresent()) {
			scripts.add(script.get());
		}
		for (String s : scripts) {
			if (s != null)
				System.err.println(
						String.format("Adding the script: %s\u2026", s.substring(0, 100)));
			executeScript(s);
		}
	}

	public Object executeScript(String script, Object... arguments) {
		if (driver != null && (driver instanceof JavascriptExecutor)) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			// IE: org.openqa.selenium.NoSuchWindowException
			// Chrome: Exception in thread "main"
			// org.openqa.selenium.WebDriverException: disconnected: not connected to
			// DevTools
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException(
					"Script execution failed: driver it not defined properly");
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

					Pattern pattern = Pattern.compile(manifestTag,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(Pattern.quote(manifestSource));
					if (matcher.find()) {
						result = matcher.group(1);
						System.err.println("Discovered version: " + result
								+ " in manifest : " + manifestResourcePath);
					}
					IOUtils.closeQuietly(inputStream);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getPayload() {
		return executeScript(getSWDCommand).toString();
	}

	// sorting elements by valueColumn, returns Array List of indexColumn
	public List<String> sortSteps(Map<String, Map<String, String>> testData,
			String indexColumn, String valueColumn) {

		List<String> sortedSteps = new ArrayList<>();
		Map<String, Integer> elementSteps = testData.values().stream()
				.collect(Collectors.toMap(o -> o.get(indexColumn),
						o -> Integer.parseInt(o.get(valueColumn))));
		/*
		elementSteps = testData.keySet().stream().collect(Collectors.toMap(o -> o,
				o -> Integer.parseInt(testData.get(o).get(valueColumn))));
		*/
		List<Entry<String, Integer>> stepNumbers = new ArrayList<>();
		stepNumbers.addAll(elementSteps.entrySet());
		Collections.sort(stepNumbers, new Comparator<Entry<String, Integer>>() {

			public int compare(Entry<String, Integer> obj_left,
					Entry<String, Integer> obj_right) {
				return obj_left.getValue().compareTo(obj_right.getValue());

			}
		});
		return stepNumbers.stream().map(e -> e.getKey())
				.collect(Collectors.toList());
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
		executeScript("arguments[0].style.backgroundColor = '" + color + "'",
				element);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
	}

	// sorting example from
	// http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	// currently not used
	public <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
			Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new));
	}

	public void initializeLogger() {
		Properties logProperties = new Properties();
		String log4J_properties = String.format("%s/%s/%s",
				System.getProperty("user.dir"), "src/main/resources", "log4j.xml");
		try {
			logProperties.load(new FileInputStream(log4J_properties));
			PropertyConfigurator.configure(logProperties);
		} catch (IOException e) {
			throw new RuntimeException("Fail to load: " + log4J_properties);
		}
	}
}
