package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import com.github.sergueik.swet.WriteScriptFile;
import com.github.sergueik.swet.Utils;
import com.github.sergueik.swet.OSUtils;

public class WriteScriptFileTest {

	private static String scriptsPath;
	private static Gson gson;
	private static String resourcePath;
	private static Utils utils = Utils.getInstance();

	@BeforeClass
	public static void Setup() {
		resourcePath = utils.getResourcePath("sampleTest.json");
		gson = new Gson();
		scriptsPath = System.getProperty("user.dir") + File.separator + "scripts";
		File scriptsDirectory = new File(scriptsPath);
		if (!scriptsDirectory.exists()) {
			if (scriptsDirectory.mkdir()) {
			} else {
				System.out.println("Failed to create directory: " + scriptsPath);
			}
		}
	}

	// cleanup generated test sources
	// TODO: error detection
	@AfterClass
	public static void Cleanup() {
		/*
		try {
			FileUtils.deleteDirectory(new File(scriptsPath));
		} catch (IOException e) {
		}
		*/
	}

	@Test
	public void writeTestScript() throws FileNotFoundException {
		// Warning: places generated sources into the working girectory
		WriteScriptFile writeScriptFile = new WriteScriptFile(resourcePath);
		writeScriptFile.generateTestScripts(scriptsPath);
	}

	@Test
	public void dumplJSon() throws FileNotFoundException {
		WriteScriptFile.AI_Parser parser = new WriteScriptFile.AI_Parser(
				resourcePath);
		parser.printTestSuiteDetails();
	}

	@Test
	public void dumpRawJSon() throws FileNotFoundException {

		WriteScriptFile.TestParamsDTO params = gson.fromJson(
				new FileReader(resourcePath), WriteScriptFile.TestParamsDTO.class);

		System.out.println(params.getBrowser());
		System.out.println(params.getURL());
		System.out.println(params.getTestsuite().getName());
		System.out.println(
				"Number of testcases: " + params.getTestsuite().getTestcase().length);
		System.out.println(params.getTestsuite().getTestcase()[0].getName());
		System.out.println("Number of test steps of testcase 1: "
				+ params.getTestsuite().getTestcase()[0].getStep().length);
		System.out.println(String.format("Step 1 name: \"%s\"",
				params.getTestsuite().getTestcase()[0].getStep()[0].getName()));
		System.out.println(String.format("Step 1 action: \"%s\"",
				params.getTestsuite().getTestcase()[0].getStep()[0].getAction()));
		System.out
				.println(String.format("Step 1 locateElement selector type : \"%s\"",
						params.getTestsuite().getTestcase()[0].getStep()[0]
								.getLocateElement().getBy()));
		System.out
				.println(String.format("Step 1 locateElement selector value: \"%s\"",
						params.getTestsuite().getTestcase()[0].getStep()[0]
								.getLocateElement().getValue()));
		System.out.println(String.format("Step 1 extra param: \"%s\"",
				params.getTestsuite().getTestcase()[0].getStep()[0].getThirdPara()));
	}
}
