package com.github.sergueik.swet;

// The org.apache.log4j.Category  has been deprecated 
// and should be replaced by the Logger
import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

// based on: https://alvinalexander.com/blog/post/java/sample-how-format-log4j-logging-logfile-output
public class Log4JTest {
	// our log4j category reference
	
	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);

	@SuppressWarnings("deprecation")
	static final Category log = Category.getInstance(Log4JTest.class);
	static final String LOG_PROPERTIES_FILE = "src/main/resources/log4J.properties";

	public static void main(String[] args) {
		new Log4JTest();
		log.info("Main done.");
	}

	public Log4JTest() {
		initializeLogger();
		log.info("Log4JTest constructor done.");
	}

	private void initializeLogger() {
		Properties logProperties = new Properties();

		try {
			logProperties.load(new FileInputStream(LOG_PROPERTIES_FILE));
			PropertyConfigurator.configure(logProperties);
			log.info("Initialize Logger.");
		} catch (IOException e) {
			throw new RuntimeException(
					"Fail to load: " + LOG_PROPERTIES_FILE);
		}
	}
}
