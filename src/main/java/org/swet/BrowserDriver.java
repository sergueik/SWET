package org.swet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

/**
 * Browser Driver wrapper class for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class BrowserDriver {

	public static WebDriver driver;
	private static String location = "";
	private static String chromeDriverPath = "c:/java/selenium/chromedriver.exe";
	private static String iEDriverPath = "c:/java/selenium/IEDriverServer.exe";
	private static String geckoDriverPath = "c:/java/selenium/geckodriver.exe";

	public static WebDriver initialize(String browser) {

		DesiredCapabilities capabilities = null;
		browser = browser.toLowerCase();
		if (browser.equals("firefox")) {
			capabilities = capabilitiesFirefox();
		} else if (browser.equals("phantomjs")) {
			capabilities = capabilitiesPhantomJS();
		} else if (browser.equals("chrome")) {
			capabilities = capabilitiesChrome();
		} else if (browser.equals("iexplore")) {
			capabilities = capabilitiesInternetExplorer();
		} else if (browser.equals("android")) {
			capabilities = capabilitiesAndroid();
		} else if (browser.equals("safari")) {
			capabilities = capabilitiesSafari();
		} else if (browser.equals("iphone")) {
			capabilities = capabilitiesiPhone();
		} else if (browser.equals("ipad")) {
			capabilities = capabilitiesiPad();
		}

		if (location.toLowerCase().contains("http:")) {
			try {
				// log.info("Running on Selenium Grid: " + location);
				driver = new RemoteWebDriver(new URL(location), capabilities);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		} else if (browser.equals("firefox")) {
			driver = new FirefoxDriver(capabilities);
		} else if (browser.equals("phantomjs")) {
			driver = new PhantomJSDriver(capabilities);
		} else if (browser.equals("safari")) {
			SafariOptions options = new SafariOptions();
			driver = new SafariDriver(options);
		} else if (browser.equals("chrome")) {
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("iexplore")) {
			File file = new File(iEDriverPath);
			System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
			driver = new InternetExplorerDriver(capabilities);
		} else if (browser.equals("android")) {
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("iphone")) {
			driver = new ChromeDriver(capabilities);
		} else if (browser.equals("ipad")) {
			driver = new ChromeDriver(capabilities);
		}
		// ngDriver = new NgWebDriver(driver);
		return driver;
	}

	private static DesiredCapabilities capabilitiesSafari() {
		DesiredCapabilities capabilities = DesiredCapabilities.safari();
		SafariOptions options = new SafariOptions();
		options.setUseCleanSession(true);
		capabilities.setCapability(SafariOptions.CAPABILITY, options);
		return capabilities;
	}

	private static DesiredCapabilities capabilitiesPhantomJS() {

		DesiredCapabilities capabilities = new DesiredCapabilities("phantomjs", "",
				Platform.ANY);
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
				new String[] { "--web-security=false", "--ssl-protocol=any",
						"--ignore-ssl-errors=true", "--local-to-remote-url-access=true",
						"--webdriver-loglevel=INFO" });
		return capabilities;
	}

	private static DesiredCapabilities capabilitiesAndroid() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", "Google Nexus 5");

		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		return capabilities;
	}

	private static DesiredCapabilities capabilitiesiPhone() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", "Apple iPhone 6");

		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		return capabilities;
	}

	private static DesiredCapabilities capabilitiesiPad() {
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();

		Map<String, String> mobileEmulation = new HashMap<String, String>();
		mobileEmulation.put("deviceName", "Apple iPad");

		Map<String, Object> chromeOptions = new HashMap<String, Object>();
		chromeOptions.put("mobileEmulation", mobileEmulation);
		capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

		return capabilities;
	}

	private static DesiredCapabilities capabilitiesFirefox() {
		System.setProperty("webdriver.gecko.driver", geckoDriverPath);
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		// Exception:
		// capabilities.setCapability("marionette", false);
		capabilities.setCapability("firefox_binary",
				new File("C:/Program Files (x86)/Mozilla Firefox/firefox.exe")
						.getAbsolutePath());
		FirefoxProfile profile = new FirefoxProfile();
		profile.setEnableNativeEvents(true);
		profile.setAcceptUntrustedCertificates(true);
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		capabilities.setCapability("elementScrollBehavior", 1);
		capabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
		return capabilities;
	}

	// https://sites.google.com/a/chromium.org/chromedriver/capabilities
	// http://peter.sh/experiments/chromium-command-line-switches/
	// https://developer.chrome.com/extensions/contentSettings
	// http://www.programcreek.com/java-api-examples/index.php?api=org.openqa.selenium.chrome.ChromeOptions
	private static DesiredCapabilities capabilitiesChrome() {

		System.setProperty("webdriver.chrome.driver",
				(new File(chromeDriverPath)).getAbsolutePath());
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		ChromeOptions options = new ChromeOptions();

		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		String downloadFilepath = System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "target"
				+ System.getProperty("file.separator");
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("enableNetwork", "true");
		options.setExperimentalOption("prefs", chromePrefs);
		options.addArguments("allow-running-insecure-content");
		options.addArguments("allow-insecure-localhost");
		options.addArguments("enable-local-file-accesses");
		options.addArguments("disable-notifications");
		// options.addArguments("start-maximized");
		options.addArguments("browser.download.folderList=2");
		options.addArguments(
				"--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");
		options.addArguments("browser.download.dir=" + downloadFilepath);
		// options.addArguments("user-data-dir=/path/to/your/custom/profile");
		capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		return capabilities;
	}

	private static DesiredCapabilities capabilitiesInternetExplorer() {
		DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();

		capabilities.setCapability(
				InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
				true);
		capabilities.setCapability("ignoreZoomSetting", true);
		capabilities.setCapability("ignoreProtectedModeSettings", true);
		capabilities.setCapability("requireWindowFocus", true);
		capabilities.setBrowserName(
				DesiredCapabilities.internetExplorer().getBrowserName());
		return capabilities;
	}

	public static void close() {
		driver.close();
		if (driver != null) {
			driver.quit();
		}
	}
}