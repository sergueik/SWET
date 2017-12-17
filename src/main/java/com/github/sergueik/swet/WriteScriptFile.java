package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.sergueik.swet.Utils;
import com.google.gson.Gson;

import java.io.FileReader;

/**
 * origin:  https://github.com/watarus-nt/SeleniumGenerator
 */
public class WriteScriptFile {

	private String packageName;
	private List<String> importList = new ArrayList<>();
	private String classPart;
	private List<String> objectDeclarations = new ArrayList<>();
	private List<String> setUpPart = new ArrayList<>();
	private List<String> testPart = new ArrayList<>();
	private List<String> testStepsPart = new ArrayList<>();
	private List<String> tearDownPart = new ArrayList<>();
	private static Utils utils = Utils.getInstance();
	private AI_Parser parser;

	public WriteScriptFile(String jsonData) throws FileNotFoundException {
		parser = new AI_Parser(jsonData);

	}

	public void generateTestScripts(String path) {
		TestCaseDTO[] testCaseList = parser.getTestCaseDTO();
		for (int i = 0; i < testCaseList.length; i++) {
			writeTestScriptFile(testCaseList[i], path);
		}
	}

	public void writeTestScriptFile(TestCaseDTO testCaseDTO, String path) {
		List<String> content = createTestScriptContext(testCaseDTO);

		String testScriptFolderPath = path + File.separator
				+ parser.getTestSuiteName();
		File testDirectory = new File(testScriptFolderPath);
		if (!testDirectory.exists()) {
			if (testDirectory.mkdir()) {
			} else {
				System.out
						.println("Failed to create directory: " + testScriptFolderPath);
			}
		}

		String testScriptPath = testScriptFolderPath + File.separator
				+ testCaseDTO.getName() + "Test.java";
		System.err.println("Writing script to file: " + testScriptPath);
		Utils.writeToFile(content, testScriptPath, true);

	}

	private List<String> createTestScriptContext(TestCaseDTO testCaseDTO) {
		List<String> context = new ArrayList<>();
		context.add(initialPackageName());
		context.addAll(initialImportList());
		context.add(initialClassPart(testCaseDTO.getName()));
		context.addAll(initialObjectDeclarations());
		context.addAll(initialSetUpPart());
		context.addAll(initialTestPart(testCaseDTO));
		context.addAll(initialTearDownPart());
		context.add("}");

		clearOldContent();
		return context;
	}

	private void clearOldContent() {
		getImportList().clear();
		setClassPart("");
		getObjectDeclarations().clear();
		getSetUpPart().clear();
		getTestPart().clear();
		getTestStepsPart().clear();
		getTearDownPart().clear();
	}

	private String initialPackageName() {
		setPackageName("package " + parser.getTestSuiteName() + ";\n");
		return getPackageName();
	}

	private List<String> initialImportList() {
		this.importList.add(utils
				.getScriptContent(String.format("templates/%s", "importList.twig")));
		this.importList.add("");
		return getImportList();
	}

	private String initialClassPart(String testName) {
		setClassPart("public class " + testName + "Test {\n");
		return getClassPart();
	}

	private List<String> initialObjectDeclarations() {
		this.objectDeclarations.add(utils
				.getScriptContent(String.format("templates/%s", "initObjects.twig")));
		this.objectDeclarations.add("");
		return getObjectDeclarations();
	}

	private List<String> initialSetUpPart() {
		this.setUpPart.add(utils
				.getScriptContent(String.format("templates/%s", "beforeTest.twig")));
		this.setUpPart.add("");
		return getSetUpPart();
	}

	private List<String> initialTestPart(TestCaseDTO testCaseDTO) {
		this.testPart.add("\t@Test");
		this.testPart.add("\tpublic void "
				+ testCaseDTO.getName().toLowerCase().trim() + "() throws Exception {");
		this.testPart.addAll(initialTestStepPart(testCaseDTO));
		this.testPart.add("\t}");
		this.testPart.add("");
		return getTestPart();
	}

	private List<String> initialTestStepPart(TestCaseDTO testCaseDTO) {
		// add later after have a mapping between displayed action names
		// and implement action names
		// this.testStepsPart.add("\n");
		List<String> steps = new ArrayList<>();

		for (TestStepDTO stepDTO : testCaseDTO.getStep()) {
			steps.addAll(initialTestStep(stepDTO));
		}

		setTestStepsPart(steps);
		return getTestStepsPart();
	}

	private List<String> initialTestStep(TestStepDTO testStepDTO) {
		List<String> testStep = new ArrayList<>();
		testStep.add("\t\t// Step name: " + testStepDTO.getName());
		testStep.add(buildActionStep(testStepDTO));
		return testStep;
	}

	private String buildActionStep(TestStepDTO testStepDTO) {
		String actionString = "";
		actionString = "\t\tseleniumKeywords."
				+ ActionsMapping.getAction(testStepDTO.getAction().toLowerCase());
		actionString += "(\"" + testStepDTO.getLocateElement().getBy() + "\"";
		String locatorValue = testStepDTO.getLocateElement().getValue();
		actionString += ", \"" + StringEscapeUtils.escapeJava(locatorValue) + "\"";

		if (testStepDTO.getThirdPara() != null) {
			actionString += ", \"" + testStepDTO.getThirdPara() + "\");";
		} else {
			actionString += ");";
		}
		return actionString;
	}

	private List<String> initialTearDownPart() {
		this.tearDownPart.add(utils
				.getScriptContent(String.format("templates/%s", "aftertest.twig")));
		return getTearDownPart();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<String> getImportList() {
		return importList;
	}

	public void setImportList(List<String> importList) {
		this.importList = importList;
	}

	public String getClassPart() {
		return classPart;
	}

	public void setClassPart(String classPart) {
		this.classPart = classPart;
	}

	public List<String> getObjectDeclarations() {
		return objectDeclarations;
	}

	public void setObjectDeclarations(List<String> objectDeclarations) {
		this.objectDeclarations = objectDeclarations;
	}

	public List<String> getSetUpPart() {
		return setUpPart;
	}

	public void setSetUpPart(List<String> setUpPart) {
		this.setUpPart = setUpPart;
	}

	public List<String> getTestPart() {
		return testPart;
	}

	public void setTestPart(List<String> testPart) {
		this.testPart = testPart;
	}

	public List<String> getTearDownPart() {
		return tearDownPart;
	}

	public void setTearDownPart(List<String> tearDownPart) {
		this.tearDownPart = tearDownPart;
	}

	public List<String> getTestStepsPart() {
		return testStepsPart;
	}

	public void setTestStepsPart(List<String> testStepsPart) {
		this.testStepsPart = testStepsPart;
	}

	public static class AI_Parser {

		private String jsonData;
		private TestParamsDTO testParamsDTO;
		private TestSuiteDTO testSuiteDTO;
		private TestCaseDTO[] testCaseDTO;
		private TestStepDTO[] testStepDTO;
		private LocateElementDTO locateElementDTO;

		public AI_Parser(String jsonData) throws FileNotFoundException {
			this.jsonData = jsonData;
			Gson gson = new Gson();
			try {
				this.testParamsDTO = gson.fromJson(new FileReader(jsonData),
						TestParamsDTO.class);
				setTestSuiteDTO(testParamsDTO.getTestsuite());
				setTestCaseDTO(getTestSuiteDTO().getTestcase());

			} catch (FileNotFoundException e) {
				System.out.println("[FAILED] - Can't find input file: " + jsonData);
				throw e;
			}
		}

		public String getTestSuiteName() {
			return testParamsDTO.getTestsuite().getName();
		}

		public String getBrowser() {
			return testParamsDTO.getBrowser();
		}

		public String getURL() {
			return testParamsDTO.getURL();
		}

		public int getNumberOfTestCases() {
			return getTestCaseDTO().length;
		}

		public String getTestCaseName(TestCaseDTO testCaseDTO) {
			return testCaseDTO.getName();
		}

		public int getNumberOfTestStepInTestCase(TestCaseDTO testCaseDTO) {
			return testCaseDTO.getStep().length;
		}

		public void printTestSuiteDetails() throws FileNotFoundException {
			Gson gson = new Gson();
			TestParamsDTO params = gson.fromJson(new FileReader(jsonData),
					TestParamsDTO.class);
			System.out.println("Broswer: " + params.getBrowser());
			System.out.println("URL: " + params.getURL());
			System.out.println("Testsuite name: " + params.getTestsuite().getName());
			int numberOfTestCases = params.getTestsuite().getTestcase().length;
			for (int i = 0; i < numberOfTestCases; i++) {
				TestCaseDTO testCaseDTO = params.getTestsuite().getTestcase()[i];
				System.out
						.println("Testcase " + (i + 1) + ": " + testCaseDTO.getName());
				int numberOfTestSteps = testCaseDTO.getStep().length;
				for (int j = 0; j < numberOfTestSteps; j++) {
					TestStepDTO testStepDTO = testCaseDTO.getStep()[j];
					System.out.println(
							"\tStep name " + (j + 1) + " : " + testStepDTO.getName());
					int k = 0;
					System.out
							.println("\t\t" + (k + 1) + ". Name: " + testStepDTO.getName());
					System.out.println(
							"\t\t" + (k + 2) + ". Action: " + testStepDTO.getAction());
					System.out.println("\t\t" + (k + 3) + ". Locate Element By: "
							+ testStepDTO.getLocateElement().getBy());
					// System.out.println("\t\t" + (k + 4) + ". Locate Element Value: " +
					// handleEscapeCharacters(testStepDTO.getLocateElement().getValue()));
					System.out.println("\t\t" + (k + 4) + ". Locate Element Value: "
							+ testStepDTO.getLocateElement().getValue());
					if (testStepDTO.getThirdPara() != null) {
						System.out.println("\t\t" + (k + 5) + ". Text To Write: "
								+ testStepDTO.getThirdPara());
					}
				}
			}
		}

		public TestParamsDTO getTestParamsDTO() {
			return testParamsDTO;
		}

		public void setTestParamsDTO(TestParamsDTO testParamsDTO) {
			this.testParamsDTO = testParamsDTO;
		}

		public TestSuiteDTO getTestSuiteDTO() {
			return testSuiteDTO;
		}

		public void setTestSuiteDTO(TestSuiteDTO testSuiteDTO) {
			this.testSuiteDTO = testSuiteDTO;
		}

		public TestCaseDTO[] getTestCaseDTO() {
			return testCaseDTO;
		}

		public void setTestCaseDTO(TestCaseDTO[] testCaseDTO) {
			this.testCaseDTO = testCaseDTO;
		}

		public TestStepDTO[] getTestStepDTO() {
			return testStepDTO;
		}

		public void setTestStepDTO(TestStepDTO[] testStepDTO) {
			this.testStepDTO = testStepDTO;
		}

		public LocateElementDTO getLocateElementDTO() {
			return locateElementDTO;
		}

		public void setLocateElementDTO(LocateElementDTO locateElementDTO) {
			this.locateElementDTO = locateElementDTO;
		}
	}

	public static class TestCaseDTO {

		private String name;
		private TestStepDTO[] step;

		@Override
		public String toString() {
			return "TestCase name: " + name + "\n" + step;
		}

		public String getName() {
			return name;
		}

		public void setName(String value) {
			this.name = value;
		}

		public TestStepDTO[] getStep() {
			return step;
		}

		public void setStep(TestStepDTO[] value) {
			this.step = value;
		}
	}

	public static class TestSuiteDTO {

		private String name;
		private TestCaseDTO[] testcase;

		@Override
		public String toString() {
			return "TestSuite name: " + name + "\n" + testcase;
		}

		public String getName() {
			return name;
		}

		public void setName(String value) {
			this.name = value;
		}

		public TestCaseDTO[] getTestcase() {
			return testcase;
		}

		public void setTestcase(TestCaseDTO[] value) {
			this.testcase = value;
		}
	}

	public static class TestParamsDTO {

		private String browser;
		private String URL;
		private TestSuiteDTO testsuite;

		@Override
		public String toString() {
			return browser + "\n" + URL + "\n" + testsuite;
		}

		public String getBrowser() {
			return browser;
		}

		public void setBrowser(String value) {
			this.browser = value;
		}

		public String getURL() {
			return URL;
		}

		public void setURL(String value) {
			this.URL = value;
		}

		public TestSuiteDTO getTestsuite() {
			return testsuite;
		}

		public void setTestsuite(TestSuiteDTO value) {
			this.testsuite = value;
		}
	}

	public static class TestStepDTO {

		private String name;
		private String action;
		private LocateElementDTO locateElement;
		private String thirdPara;

		@Override
		public String toString() {
			return "TestStep name: " + name + "\n" + "Action name: " + action + "\n"
					+ locateElement + "\n" + thirdPara;
		}

		public String getName() {
			return name;
		}

		public void setName(String value) {
			this.name = value;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String value) {
			this.action = value;
		}

		public LocateElementDTO getLocateElement() {
			return locateElement;
		}

		public void setLocateElement(LocateElementDTO value) {
			this.locateElement = value;
		}

		public String getThirdPara() {
			return thirdPara;
		}

		public void setThirdPara(String value) {
			this.thirdPara = value;
		}
	}

	public static class LocateElementDTO {
		private String by;
		private String value;

		@Override
		public String toString() {
			return "By: " + by + "\n" + "Value: " + value;
		}

		public String getBy() {
			return by;
		}

		public void setBy(String value) {
			this.by = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class ActionsMapping {
		public static Map<String, String> actions = new HashMap<>();

		static {
			try {
				initialMapping();
				// System.out.println("Load mapping succesfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static Boolean checkActionExists(String inputAction) {
			return actions.containsKey(inputAction);
		}

		public static String getAction(String value) {
			return checkActionExists(value) ? actions.get(value) : null;
		}

		public static void initialMapping() throws IOException {
			List<String> content = Utils
					.readFileLineByLine(utils.getResourcePath("config.properties"));
			for (String line : content) {
				if (line.contains("selenium.")) {
					String keyword = line.split("=")[0].split("\\.")[1];
					String inputAction = line.split("=")[1].toLowerCase();
					actions.put(inputAction.trim().replaceAll("\\s+", ""), keyword);
				}
			}
		}
	}
}
