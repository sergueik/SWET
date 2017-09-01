package org.swet;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import org.swet.WriteScriptFile;
import org.swet.Utils;
import org.swet.OSUtils;

import com.google.gson.Gson;

public class WriteScriptFileTest {

	private static String workingDirectory;
	private static Gson gson;
	private static String resourcePath;

	@BeforeClass
	public static void Setup() {
		resourcePath = (new org.swet.Utils()).getResourcePath("sampleTest.json");
		gson = new Gson();
		workingDirectory = System.getProperty("user.dir");
	}

	// TODO: cleanup generated test sources:
	// those will likely fail to compile under the project pom.xml
	@AfterClass
	public static void Cleanup() {
    
    
  }

	@Test
	public void writeTestScript() throws FileNotFoundException {
		// Warning: places generated sources into the working girectory
		WriteScriptFile writeScriptFile = new WriteScriptFile(resourcePath);
		writeScriptFile.generateTestScripts(workingDirectory);
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
