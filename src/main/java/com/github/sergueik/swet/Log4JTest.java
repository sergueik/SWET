package com.github.sergueik.swet;

//The org.apache.log4j.Category  has been deprecated and needs to be replaced by the Logger.
// NOTE: the latter does not appear to allow different ConversionPattern for STDERR and FILE
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
	static final Category logger = Category.getInstance(Log4JTest.class);
	private static Utils utils = Utils.getInstance();

	public static void main(String[] args) {
		new Log4JTest();
		logger.debug("Main(debug) done.");
		logger.info("Main (info) done.");
	}

	public Log4JTest() {
		utils.initializeLogger();
		logger.info("Log4JTest constructor done.");
	}

}
