package com.github.sergueik.swet;

// for jvm arguments. Must have "-DAPP_LOG_ROOT=c:/temp"
// can also assert if the -Dfile.encoding=UTF-8 argument is passed into surefile plugin properly
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Common utilities for separate patterns, files for Logging info, error and debug to console and file logging for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on: https://github.com/hmeclazcke/log4j2-example
public class Log4j2Test {
	// This example must run with -DAPP_LOG_ROOT=c:/temp
	private static final Logger LOGGER = LogManager
			.getLogger(Log4j2Test.class.getName());

	@Test
	public void sampleLog4Jtest() {

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
