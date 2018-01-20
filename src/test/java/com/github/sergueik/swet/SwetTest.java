package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@SuppressWarnings("deprecation")
public class SwetTest {

	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Actions actions;

	private static Alert alert; // unused

	private static final String browser = "chrome";
	private static final String baseURL = "about:blank";
	private static String osName = OSUtils.getOsName();
	private Utils utils = Utils.getInstance();

	private static boolean pause_after_script_injection = false;
	private static final int timeout_after_script_injection = 1000;
	private static boolean pause_after_test = false;
	private static final int timeout_after_test = 1000;
	private static final int flexibleWait = 30;
	private static final int implicitWait = 1;
	private static final long pollingInterval = 500;

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
		} else if (osName.startsWith("mac")) {
			driver = BrowserDriver.initialize("safari");
		} else {
			driver = BrowserDriver.initialize("firefox");
		}
		driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		actions = new Actions(driver);
	}

	@Before
	public void loadBaseURL() {
		driver.get(baseURL);
		utils.setDriver(driver);
		utils.setFlexibleWait(flexibleWait);
		utils.setActions(actions);
	}

	// @Ignore
	@Test
	public void testWebPageElementSearch() {
		driver.get("https://www.codeproject.com/");
		WebElement target = wait.until(ExpectedConditions.visibilityOf(driver
				.findElement(By.cssSelector("img[src *= 'post_an_article.png']"))));
		assertThat(target, notNullValue());
		utils.injectScripts(Optional.<String> empty());
		// pause_after_script_injection
		if (pause_after_script_injection) {
			utils.sleep(timeout_after_script_injection);
		}
		utils.highlight(target);
		// Act
		utils.inspectElement(target);
		utils.completeVisualSearch("element name");

		// Assert
		String payload = utils.getPayload();
		assertFalse(payload.isEmpty());
		System.err.println("Result:\n" + utils.readVisualSearchResult(payload));
		Map<String, String> details = new HashMap<>();
		utils.readData(payload, Optional.of(details));
		verifyNeededKeys(details);
		// verifyEntry(details, "ElementSelectedBy", nil);
		verifySelectors(details);
		if (pause_after_test) {
			utils.sleep(timeout_after_test);
		}
	}

	// @Ignore
	@Test
	public void testStaticPage() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		// Unsupported URL protocol:
		// file:///Users/sergueik/dev/selenium_java/swd_recorder/target/test-classes/ElementSearch.html
		WebElement target = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		utils.highlight(target);
		// Act
		utils.inspectElement(target);
		// Assert
		utils.sleep(1000);
		utils.completeVisualSearch("this element name");

		// Assert
		String payload = utils.getPayload();
		assertFalse(payload.isEmpty());
		Map<String, String> elementData = new HashMap<>();
		String elementName = utils.readVisualSearchResult(payload,
				Optional.of(elementData));
		Configuration config = new Configuration();
		BrowserConfiguration browserConfiguration = new BrowserConfiguration();
		browserConfiguration.name = "chrome";
		browserConfiguration.version = "54.0";
		browserConfiguration.driverVersion = "2.27";
		browserConfiguration.driverPath = "c:/java/selenium/chromedriver.exe";
		browserConfiguration.platform = OSUtils.getOsName();
		config.created = new Date();
		config.browserConfiguration = browserConfiguration;
		config.updated = new Date();
		Map<String, Map<String, String>> testData = new HashMap<>();
		String commandId = elementData.get("CommandId");
		testData.put(commandId, elementData);
		config.elements = testData;

		YamlHelper.printConfiguration(config);
	}

	// @Ignore
	@Test
	public void testOnOffSearch() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		// Unsupported URL protocol:
		// file:///Users/sergueik/dev/selenium_java/swd_recorder/target/test-classes/ElementSearch.html
		WebElement target = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		// Act
		for (int cnt = 0; cnt != 3; cnt++) {
			utils.highlight(target);
			utils.inspectElement(target);
			// Assert
			utils.sleep(1000);
			utils.closeVisualSearch();
		}
		utils.inspectElement(target);
		utils.completeVisualSearch("this element name");

		// TODO: refactor
		String payload = utils.getPayload();
		assertFalse(payload.isEmpty());
		System.err.println("Result:\n" + utils.readVisualSearchResult(payload));
		Map<String, String> details = new HashMap<>();
		utils.readData(payload, Optional.of(details));
		verifyNeededKeys(details);

	}

	// @Ignore
	@Test
	public void testIncompleteSubmission() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		// Unsupported URL protocol:
		// file:///Users/sergueik/dev/selenium_java/swd_recorder/target/test-classes/ElementSearch.html
		WebElement target = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		utils.highlight(target);
		// Act
		utils.inspectElement(target);
		utils.completeVisualSearch("");

		utils.completeVisualSearch("this element name");

		// TODO: refactor
		String payload = utils.getPayload();
		assertFalse(payload.isEmpty());
		System.err.println("Result:\n" + utils.readVisualSearchResult(payload));
		Map<String, String> details = new HashMap<>();
		utils.readData(payload, Optional.of(details));
		verifyNeededKeys(details);

	}

	// @Ignore
	@Test
	public void testExcludeDisabledElementSelectedByChoices() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		WebElement target = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		utils.highlight(target);
		// Act
		utils.inspectElement(target);
		// Assert
		List<String> labels = driver
				.findElements(By.cssSelector("form#SWDForm label[for]")).stream()
				.map(e -> e.getAttribute("for")).collect(Collectors.toList());
		for (String label : labels) {
			// NOTE: org.openqa.selenium.InvalidSelectorException
			Optional<WebElement> radioElement = driver.findElements(By.xpath(String
					.format("//*[@id='%s'][not( @disabled) or not(@disabled='disabled')]",
							label)))
					.stream().findFirst();
			if (radioElement.isPresent()) {
				utils.sleep(1000);
				radioElement.get().click();
			}
		}
	}

	// @Ignore
	@Test
	public void testChangeElementSelectedBy() {
		driver.get(utils.getResourceURI("ElementSearch.html"));
		utils.injectScripts(Optional.<String> empty());
		WebElement target = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		utils.highlight(target);
		// Act
		utils.inspectElement(target);
		// Assert
		List<String> labels = driver
				.findElements(By.cssSelector("form#SWDForm label[for]")).stream()
				.map(e -> e.getAttribute("for")).collect(Collectors.toList());

		String lastLabel = null;
		Collections.sort(labels, String.CASE_INSENSITIVE_ORDER);
		for (String label : labels) {
			utils.sleep(100);
			WebElement radioElement = wait.until(ExpectedConditions.visibilityOf(
					driver.findElement(By.xpath(String.format("//*[@id='%s']", label)))));
			assertThat(radioElement, notNullValue());
			radioElement.click();
			lastLabel = label;
		}
		utils.completeVisualSearch("changing strategy attribute");
		// Assert
		String payload = utils.getPayload();
		assertFalse(payload.isEmpty());
		Map<String, String> details = new HashMap<>();
		utils.readData(payload, Optional.of(details));
		verifyNeededKeys(details);
		verifyEntry(details, "ElementSelectedBy", lastLabel);
	}

	// @Ignore
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
		utils.inspectElement(element);
		utils.completeVisualSearch("Yahoo Logo");

		// Assert
		String payload = utils.getPayload();
		assertFalse(payload.isEmpty());
		String result = utils.readVisualSearchResult(payload);
		System.err.println("Result:\n" + result);
		Map<String, String> details = new HashMap<>();
		utils.readData(payload, Optional.of(details));
		Map<String, String> expected = new HashMap<>();
		expected.put("ElementId", "uh-logo");
		expected.put("ElementXPath",
				"id(\"yui_3_18_0_4_1508530499248_1058\")/a[ @href = \"https://www.yahoo.com/\" ]");
		expected.put("ElementCssSelector",
				"h1#yui_3_18_0_4_1516464233498_948 > a[ href = \"https://www.yahoo.com/\" ]");

		for (String selector : expected.keySet()) {
			String expectedValue = expected.get(selector);
			String actualValue = details.get(selector);
			// NOTE: this assert method is deprecated
			assertThat(actualValue, is(equalTo(expectedValue)));
			assertThat(replaceID(expectedValue), equalTo(replaceID(actualValue)));
		}
		if (pause_after_test) {
			utils.sleep(timeout_after_test);
		}
	}

	@After
	public void resetBrowser() {
		driver.get("about:blank");
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

	// Utils

	// see also:
	// https://github.com/dimitrisli/JUnitShowcase/blob/master/src/test/java/com/dimitrisli/junitshowcase/hamcrest/HamcrestMatchersTesting.java
	private void verifyEntry(Map<String, String> result, String key,
			String value) {

		// TODO: a straight assert
		assertThat(result, Matchers.<String> hasKey(key));
		assertTrue(result.containsKey((Object) key));
		assertThat(result, hasEntry(key, value));
	}

	private String replaceID(String value) {
		return value.replaceAll("id\\(.+\\)", "id(<ID>)")
				.replaceAll("#(?:\\S+)(\\s)", "#<ID>$1");
	}

	// TODO: a better assert
	private void verifyNeededKeys(Map<String, String> result) {

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

}
