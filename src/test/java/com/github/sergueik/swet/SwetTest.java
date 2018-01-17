package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.sergueik.swet.Utils;
import com.github.sergueik.swet.ExceptionDialogEx;

@SuppressWarnings("deprecation")
public class SwetTest {

	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Actions actions;

	private static Alert alert; // unused

	private static Keys keyCTRL;
	private static int flexibleWait = 30;
	private static int implicitWait = 1;
	private static long pollingInterval = 500;
	private static String browser = "chrome";
	private static String baseURL = "about:blank";
	private static final String getSWDCommand = "return document.swdpr_command === undefined ? '' : document.swdpr_command;";
	private static Map<String, String> data = new HashMap<>();
	private static String osName = OSUtils.getOsName();
	private Utils utils = Utils.getInstance();

	private static boolean pause_after_script_injection = false;
	private static int timeout_after_script_injection = 1000;
	private static boolean pause_after_test = false;
	private static int timeout_after_test = 1000;

	// Converting legacy SWD "Element selected By" keys to
	// selectorTable keys
	private static Map<String, String> mapSWD2CoreSelenium = new HashMap<>();
	static {
		mapSWD2CoreSelenium.put("ElementXPath", "xpath");
		mapSWD2CoreSelenium.put("ElementCssSelector", "cssSelector");
		mapSWD2CoreSelenium.put("ElementText", "text");
		mapSWD2CoreSelenium.put("ElementId", "id");
		// TODO:
		// mapSWD2CoreSelenium.put("ElementLinkText", "linkText");
		mapSWD2CoreSelenium.put("ElementTagName", "tagName");
	}

	@BeforeClass
	public static void beforeSuiteMethod() throws Exception {

		// browser selection is hard-coded

		System.err.println("os: " + osName);
		if (osName.startsWith("windows")) {

			driver = BrowserDriver.initialize(browser);
		} else if (osName.startsWith("Mac")) {
			driver = BrowserDriver.initialize("safari");
		} else {
			driver = BrowserDriver.initialize("firefox");
		}
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		actions = new Actions(driver);
		keyCTRL = osName.startsWith("Mac") ? Keys.COMMAND : Keys.CONTROL;
	}

	@AfterClass
	public static void afterSuiteMethod() {
		if (driver != null) {
			try {
				BrowserDriver.close();
			} catch (Exception e) {
				// System.err.println("Ignored exception (after suite): " +
				// e.toString());
			}
		}
	}

	@Before
	public void loadBaseURL() {
		driver.get(baseURL);
		utils.setDriver(driver);
	}

	@After
	public void resetBrowser() {
		driver.get("about:blank");
	}

	@Ignore
	@Test
	public void testWebPageElementSearch() {
		driver.get("https://www.codeproject.com/");
		WebElement element = wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector("img[src *= 'post_an_article.png']"))));
		assertThat(element, notNullValue());
		utils.injectScripts(Optional.<String> empty());
		// pause_after_script_injection
		if (pause_after_script_injection) {
			utils.sleep(timeout_after_script_injection);
		}
		utils.highlight(element);
		// Act
		inspectElement(element);
		completeVisualSearch("element name");

		// Assert
		String payload = (String) utils.executeScript(getSWDCommand);
		assertFalse(payload.isEmpty());
		System.err.println("Result:\n" + readVisualSearchResult(payload));
		Map<String, String> payloadDetails = new HashMap<>();
		utils.readData(payload, Optional.of(payloadDetails));
		verifyNeededKeys(payloadDetails);
		// verifyKey(payloadDetails, "ElementSelectedBy");
		verifySelectors(payloadDetails);
		if (pause_after_test) {
			utils.sleep(timeout_after_test);
		}
	}

	private void verifyKey(Map<String, String> result, String key, String Value) {

		assertTrue(result.containsKey((Object) key));

		// TODO: a better assert
		assertThat(result, Matchers.<String> hasKey(key));
	}

	private void verifyNeededKeys(Map<String, String> result) {

		// TODO: a better assert

		Object[] objSWDkeys = mapSWD2CoreSelenium.keySet().toArray();
		String[] neededKeys = new String[objSWDkeys.length + 1];

		for (int cnt = 0; cnt != objSWDkeys.length; cnt++) {
			// "ElementLinkText" is not returned yet
			neededKeys[cnt] = objSWDkeys[cnt].toString();
		}
		neededKeys[neededKeys.length - 1] = "ElementSelectedBy";
		assertThat(result.keySet(), hasItems(neededKeys));
	}

	// TODO: automatically discover new methods using reflection ?
	private void verifySelectors(Map<String, String> result) {
		List<String> methodKeys = new ArrayList<>(
				Arrays.asList("ElementCssSelector", "ElementXPath", "ElementId"));
		for (String methodKey : methodKeys) {
			if (result.get(methodKey) == null
					|| result.get(methodKey).length() == 0) {
				continue;
			}
			System.err.println(String.format("Testing %s with \"%s\"", methodKey,
					result.get(methodKey)));
			String methodName = mapSWD2CoreSelenium.get(methodKey);
			try {
				Method method = By.class.getMethod(methodName, String.class);
				WebElement element = driver
						.findElement((By) method.invoke(null, result.get(methodKey)));
				assertThat(element, notNullValue());
				utils.highlight(element);
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

		}

		WebElement element = driver
				.findElement(By.cssSelector(result.get("ElementCssSelector")));
		assertThat(element, notNullValue());
		utils.highlight(element);
		element = driver.findElement(By.xpath(result.get("ElementXPath")));
		assertThat(element, notNullValue());
		utils.highlight(element);
	}

	@Ignore
	@SuppressWarnings("deprecation")
	@Test
	public void testNoIds() {
		driver.get("http://www.yahoo.com/");
		WebElement element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.id("uh-logo"))));
		assertThat(element, notNullValue());
		utils.highlight(element);
		element = wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				// TODO: no such element: Unable to locate element: {"method":"css
				// selector","selector":"h1[id *='yui_'] > a[ href =
				// 'https://www.yahoo.com/' ]"}(..)
				// By.cssSelector("h1[id *='yui_'] > a[ href = 'https://www.yahoo.com/'
				// ]")
				By.cssSelector("h1 > a[ href = 'https://www.yahoo.com/' ]"))));
		assertThat(element, notNullValue());

		utils.highlight(element);
		// System.err.println("Parent (1):\n" +
		// element.findElement(By.xpath("..")).getAttribute("outerHTML")) ;
		utils.injectScripts(Optional.<String> empty());
		// pause_after_script_injection
		if (pause_after_script_injection) {
			utils.sleep(timeout_after_script_injection);
		}
		utils.highlight(element);
		// Act
		inspectElement(element);
		completeVisualSearch("Yahoo Logo");

		// Assert
		String payload = (String) utils.executeScript(getSWDCommand);
		assertFalse(payload.isEmpty());
		String result = readVisualSearchResult(payload);
		System.err.println("Result:\n" + result);
		Map<String, String> details = new HashMap<>();
		utils.readData(payload, Optional.of(details));
		Map<String, String> expected = new HashMap<>();
		expected.put("ElementId", "uh-logo");
		expected.put("ElementXPath",
				"id(\"yui_3_18_0_4_1508530499248_1058\")/a[ @href = \"https://www.yahoo.com/\" ]");
		expected.put("ElementCssSelector",
				"h1#yui_3_18_0_4_1508531183201_1027 > a[ href = \"https://www.yahoo.com/\" ]");

		for (String selector : expected.keySet()) {
			String expectedValue = expected.get(selector);
			String actualValue = details.get(selector);
			// NOTE: this assert method is deprecated
			Assert
					.assertEquals(
							String.format("Expected:\n%s\nActual:\n%s\n", expectedValue,
									actualValue),
							replaceID(expectedValue), replaceID(actualValue));
			assertThat(replaceID(expectedValue), equalTo(replaceID(actualValue)));
		}
		if (pause_after_test) {
			utils.sleep(timeout_after_test);
		}
	}

	// @Ignore
	@Test
	public void testStatic() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		// Unsupported URL protocol:
		// file:///Users/sergueik/dev/selenium_java/swd_recorder/target/test-classes/ElementSearch.html
		WebElement element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		utils.highlight(element);
		// Act
		inspectElement(element);
		// Assert
		utils.sleep(1000);
		completeVisualSearch("this element name");

		// Assert
		String payload = (String) utils.executeScript(getSWDCommand);
		assertFalse(payload.isEmpty());
		Map<String, String> elementData = new HashMap<>();
		String elementName = readVisualSearchResult(payload,
				Optional.of(elementData));
		Configuration config = new Configuration();
		BrowserConfiguration browserConfiguration = new BrowserConfiguration();
		browserConfiguration.name = "chrome";
		browserConfiguration.version = "54.0";
		browserConfiguration.driverVersion = "2.27";
		browserConfiguration.driverPath = "c:/java/selenium/chromedriver.exe";
		browserConfiguration.platform = getOsName();
		config.created = new Date();
		config.browserConfiguration = browserConfiguration;
		config.updated = new Date();
		Map<String, Map<String, String>> testData = new HashMap<>();
		String commandId = elementData.get("CommandId");
		testData.put(commandId, elementData);
		config.elements = testData;

		YamlHelper.printConfiguration(config);
	}

	@Test
	public void testChangeElementSelectedBy() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		WebElement element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		utils.highlight(element);
		// Act
		inspectElement(element);
		// Assert
		utils.sleep(1000);
		WebElement radio = wait.until(ExpectedConditions
				.visibilityOf(driver.findElement(By.xpath("//*[@id='ElementXPath']"))));
		radio.click();
		completeVisualSearch("this element name");
		// Assert
		String payload = (String) utils.executeScript(getSWDCommand);
		assertFalse(payload.isEmpty());
		Map<String, String> payloadDetails = new HashMap<>();
		utils.readData(payload, Optional.of(payloadDetails));
		verifyNeededKeys(payloadDetails);
		// verifyKey(payloadDetails, "ElementSelectedBy");
	}

	public static String getOsName() {

		if (osName == null) {
			osName = System.getProperty("os.name");
			if (osName.startsWith("Windows")) {
				osName = "windows";
			}
		}
		return osName;
	}

	private String replaceID(String selectorValue) {
		return selectorValue.replaceAll("id\\(.+\\)", "id(<ID>)")
				.replaceAll("#(?:\\S+)(\\s)", "#<ID>$1");
	}

	String readVisualSearchResult(String payload) {
		return readVisualSearchResult(payload,
				Optional.<Map<String, String>> empty());
	}

	private String readVisualSearchResult(final String payload,
			Optional<Map<String, String>> parameters) {
		// System.err.println("Processing payload: " + payload);
		Boolean collectResults = parameters.isPresent();
		Map<String, String> collector = (collectResults) ? parameters.get()
				: new HashMap<>();
		String result = utils.readData(payload, Optional.of(collector));
		assertTrue(collector.containsKey("ElementId"));
		// NOTE: elementCodeName will not be set if
		// user clicked the SWD Table Close Button
		// ElementId is always set
		return result;
	}

	private void completeVisualSearch(String elementCodeName) {
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
		utils.highlight(swdAddElementButton);
		utils.flash(swdAddElementButton);
		// Act
		swdAddElementButton.click();
	}

	// http://stackoverflow.com/questions/34176392/fluentwait-throwing-the-method-unti-in-the-type-waitwebdriver-is-not-applicab
	private void closeVisualSearch() {

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
		utils.highlight(swdCloseButton);
		swdCloseButton.click();
	}

	private String getElementText(WebElement element) {
		// http://stackoverflow.com/questions/6743912/get-the-pure-text-without-html-element-by-javascript
		String script = "var element = arguments[0];var text = element.innerText || element.textContent || ''; return text;";
		return (String) utils.executeScript(script, element);
	}

	private void inspectElement(WebElement element) {

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
}
