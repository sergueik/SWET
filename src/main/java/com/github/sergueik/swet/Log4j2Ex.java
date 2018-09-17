package com.github.sergueik.swet;

// for jvm arguments. Must have "-DAPP_LOG_ROOT=c:/temp"
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Common utilities for separate patterns, files for Logging info, error and debug to console and file logging for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on: https://github.com/hmeclazcke/log4j2-example
public class Log4j2Ex {
	// This example must run with -DAPP_LOG_ROOT=c:/temp
	private static final Logger LOGGER = LogManager
			.getLogger(Log4j2Ex.class.getName());

	public static void main(String[] args) {

		// Prints the jvm arguments. Must have "-DAPP_LOG_ROOT=c:/temp" --> location
		// of the logs.-
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		List<String> jvmArgs = runtimeMXBean.getInputArguments();
		System.out.println("jvm arguments: " + jvmArgs);

		LOGGER.debug("Debug debugging message");
		LOGGER.info("Info information message");
		LOGGER.error("Error Message Logged !!!", new Exception("excepition"));
	}

}
