package org.swet;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import java.util.Date;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Ignore;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.swet.BrowserDriver;
import org.swet.Utils;

public class SwetTest {

	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Actions actions;
	private static Alert alert;
	private static Keys keyCTRL;
	private static int flexibleWait = 5;
	private static int implicitWait = 1;
	private static long pollingInterval = 500;
	private static String baseURL = "about:blank";
	private static final String getCommand = "return document.swdpr_command === undefined ? '' : document.swdpr_command;";
	private static HashMap<String, String> data = new HashMap<String, String>();
	private static String osName = OSUtils.getOsName();

	@BeforeClass
	public static void beforeSuiteMethod() throws Exception {

		if (osName.toLowerCase().startsWith("windows")) {
			driver = BrowserDriver.initialize("chrome");
			/*
			// IE 10 works, IE 11 does not			
			driver = new InternetExplorerDriver(capabilities);
			*/
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
		try {
			BrowserDriver.close();
		} catch (Exception e) {
			System.err.println("Ignored exception: " + e.toString());
		}
	}

	@Before
	public void loadBaseURL() {
		driver.get(baseURL);
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
		injectKeyMaster(Optional.<String> empty());
		highlight(element);
		// Act

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
		completeVisualSearch("element name");

		// Assert
		String payload = (String) executeScript(getCommand);
		assertFalse(payload.isEmpty());
		String result = readVisualSearchResult(payload);
	}

	// @Ignore
	@Test
	public void testStatic() {
		driver.get(new Utils().getPageContent("ElementSearch.html"));
		injectKeyMaster(Optional.<String> empty());
		// Unsupported URL protocol:
		// file:///Users/sergueik/dev/selenium_java/swd_recorder/target/test-classes/ElementSearch.html
		WebElement element = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.tagName("h1"))));
		highlight(element);
		// Act
		actions.moveToElement(element).keyDown(keyCTRL).contextClick()
				.keyUp(keyCTRL).build().perform();
		// Assert
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		completeVisualSearch("this element name");

		// Assert
		String payload = (String) executeScript(getCommand);
		assertFalse(payload.isEmpty());
		HashMap<String, String> elementData = new HashMap<String, String>(); // empty
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
		HashMap<String, HashMap<String, String>> testData = new HashMap<String, HashMap<String, String>>();
		String commandId = elementData.get("CommandId");
		testData.put(commandId, elementData);
		config.elements = testData;

		YamlHelper.printConfiguration(config);
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

	String readVisualSearchResult(String payload) {
		return readVisualSearchResult(payload,
				Optional.<HashMap<String, String>> empty());
	}

	private String readVisualSearchResult(final String payload,
			Optional<HashMap<String, String>> parameters) {
		System.err.println("Processing payload: " + payload);
		Boolean collectResults = parameters.isPresent();
		HashMap<String, String> collector = (collectResults) ? parameters.get()
				: new HashMap<String, String>();
		String result = new Utils().readData(payload, Optional.of(collector));
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
							.findElements(
									By.cssSelector("div#SwdPR_PopUp > input[type='button']"))
							.iterator();
					WebElement result = null;
					Pattern pattern = Pattern.compile(Pattern.quote("Add element"),
							Pattern.CASE_INSENSITIVE);
					while (_elements.hasNext()) {
						WebElement _element = (WebElement) _elements.next();
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
			// (new ExceptionDialogEx(Display.getCurrent(), shell, e)).execute();
			System.err.println("Exception: " + e.toString());
		}

		assertThat(swdAddElementButton, notNullValue());
		highlight(swdAddElementButton);
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
			highlight(swdCloseButton);
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

	private Object executeScript(String script, Object... arguments) {
		if (driver instanceof JavascriptExecutor) {
			JavascriptExecutor javascriptExecutor = JavascriptExecutor.class
					.cast(driver);
			return javascriptExecutor.executeScript(script, arguments);
		} else {
			throw new RuntimeException("Script execution failed.");
		}
	}

	private void injectKeyMaster(Optional<String> script) {
		ArrayList<String> scripts = new ArrayList<String>(
				Arrays.asList(new Utils().getScriptContent("ElementSearch.js")));
		if (script.isPresent()) {
			scripts.add(script.get());
		}
		for (String s : scripts) {
			if (s != null)
				executeScript(s);
		}
	}

	private void highlight(WebElement element) {
		highlight(element, 100);
	}

	// http://stackoverflow.com/questions/11010569/highlight-a-dom-element-on-mouse-over-like-inspect-does
	private void highlight(WebElement element, long highlight_interval) {
		if (wait == null) {
			wait = new WebDriverWait(driver, flexibleWait);
		}
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			executeScript("arguments[0].style.border='3nnpx solid yellow'", element);
			Thread.sleep(highlight_interval);
			executeScript("arguments[0].style.border=''", element);
		} catch (InterruptedException e) {
			System.err.println("Ignored: " + e.toString());
		}
	}

	private String getElementText(WebElement element) {
		// http://stackoverflow.com/questions/6743912/get-the-pure-text-without-html-element-by-javascript
		String script = "var element = arguments[0];var text = element.innerText || element.textContent || ''; return text;";
		return (String) executeScript(script, element);
	}
}
