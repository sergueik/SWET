package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

// reproduced launch button action
@SuppressWarnings("deprecation")
public class SwetImmutableListCopyOfExcerptionTest {

	private static WebDriver driver;
	private static WebDriverWait wait;
	private static Actions actions;
	private static Alert alert; // unused

	private static String browser = "chrome";
	private static final String baseURL = "about:blank";
	private static String osName = OSUtils.getOsName();
	private Utils utils = Utils.getInstance();

	private static final int flexibleWait = 30;
	private static final int implicitWait = 1;
	private static final long pollingInterval = 500;

	@BeforeClass
	public static void beforeClassMethod() {
		// for exception test the browser selection is hard-coded
		System.err.println("os: " + osName);
		if (osName.equals("windows")) {
			browser = "Chrome";
		} else if (osName.startsWith("mac")) {
			browser = "safari";
		} else {
			browser = "firefox";
		}
		System.err.println("browser: " + browser);
		driver = BrowserDriver.initialize(browser);
	}

	@Before
	public void loadBaseURL() {
		utils.setDriver(driver);
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS)
				.implicitlyWait(implicitWait, TimeUnit.SECONDS)
				.setScriptTimeout(30, TimeUnit.SECONDS);
		utils.setFlexibleWait(flexibleWait);
		wait = new WebDriverWait(driver, flexibleWait);
		wait.pollingEvery(pollingInterval, TimeUnit.MILLISECONDS);
		actions = new Actions(driver);
		utils.setActions(actions);
		driver.get(baseURL);
	}

	// @Ignore
	@Test
	public void testWebPageElementSearch() {
		driver.get("https://www.codeproject.com/");
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

}
