package org.swet;

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
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * origin:  https://raw.githubusercontent.com/watarus-nt/SeleniumGenerator
 */
public class WriteScriptFile {
	private String packageName;
	private List<String> importList = new ArrayList<String>();
	private String classPart;
	private List<String> objectDeclarations = new ArrayList<String>();
	private List<String> setUpPart = new ArrayList<String>();
	private List<String> testPart = new ArrayList<String>();
	private List<String> testStepsPart = new ArrayList<String>();
	private List<String> tearDownPart = new ArrayList<String>();
	private AI_Parser parser;

	public WriteScriptFile() throws FileNotFoundException {
		parser = new AI_Parser();

	}

	public static void main(String[] args) throws FileNotFoundException {
		WriteScriptFile writeScriptFile = new WriteScriptFile();

		writeScriptFile.generateTestScripts();
	}

	public void generateTestScripts() {
		TestCaseDTO[] testCaseList = parser.getTestCaseDTO();
		for (int i = 0; i < testCaseList.length; i++) {
			writeTestScriptFile(testCaseList[i]);
		}
	}

	public void generateTestScripts(String path) {
		TestCaseDTO[] testCaseList = parser.getTestCaseDTO();
		for (int i = 0; i < testCaseList.length; i++) {
			writeTestScriptFile(testCaseList[i], path);
		}
	}

	public void writeTestScriptFile(TestCaseDTO testCaseDTO) {
		List<String> content = createTestScriptContext(testCaseDTO);
		String testScriptPath = "src/test/java/" + parser.getTestSuiteName() + "/"
				+ testCaseDTO.getName() + "Test.java";
		Utility.writeToFile(content, testScriptPath, true);
	}

	public void writeTestScriptFile(TestCaseDTO testCaseDTO, String path) {
		List<String> content = createTestScriptContext(testCaseDTO);
		// String testScriptPath = path + "src/test/java/" +
		// parser.getTestSuiteName() + "/" + testCaseDTO.getName() + "Test.java";
		String testScriptPath = path + File.separator + "src" + File.separator
				+ "test" + File.separator + "java" + File.separator
				+ parser.getTestSuiteName() + File.separator + testCaseDTO.getName()
				+ "Test.java";

		Utility.writeToFile(content, testScriptPath, true);
	}

	private List<String> createTestScriptContext(TestCaseDTO testCaseDTO) {
		List<String> context = new ArrayList<String>();
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
		this.importList.add("import libs.WebDriverUtils.SeleniumKeywords;");
		this.importList.add("import libs.WebDriverUtils.WebDriverManager;");
		this.importList.add("import org.openqa.selenium.WebDriver;");
		this.importList.add("import org.testng.annotations.AfterTest;");
		this.importList.add("import org.testng.annotations.BeforeTest;");
		this.importList.add("import org.testng.annotations.Parameters;");
		this.importList.add("import org.testng.annotations.Test;");
		this.importList.add("import utils.Logs.Log;");
		this.importList.add("import utils.Utility;");
		this.importList.add("");
		return getImportList();
	}

	private String initialClassPart(String testName) {
		setClassPart("public class " + testName + "Test {\n");
		return getClassPart();
	}

	private List<String> initialObjectDeclarations() {
		this.objectDeclarations.add("\tLog log;");
		this.objectDeclarations.add("\tprivate WebDriver driver;");
		this.objectDeclarations.add("\tprivate SeleniumKeywords seleniumKeywords;");
		this.objectDeclarations.add("");
		return getObjectDeclarations();
	}

	private List<String> initialSetUpPart() {
		this.setUpPart.add("\t@Parameters({\"browser\", \"URL\"})");
		this.setUpPart.add("\t@BeforeTest");
		this.setUpPart.add(
				"\tpublic void setUp(String browser, String URL) throws Exception {");
		this.setUpPart
				.add("\t\tlog = Utility.createLog(this.getClass().getSimpleName());");
		this.setUpPart
				.add("\t\tdriver = WebDriverManager.createWebDriver(browser, log);");
		this.setUpPart
				.add("\t\tseleniumKeywords = new SeleniumKeywords(driver, log);");
		this.setUpPart.add("\t\tseleniumKeywords.open_Url(URL);");

		this.setUpPart.add("\t}");
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
		List<String> steps = new ArrayList<String>();

		for (TestStepDTO stepDTO : testCaseDTO.getStep()) {
			steps.addAll(initialTestStep(stepDTO));
		}

		setTestStepsPart(steps);
		return getTestStepsPart();
	}

	private List<String> initialTestStep(TestStepDTO testStepDTO) {
		List<String> testStep = new ArrayList<String>();
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
		actionString += ", \"" + Utility.handleEscapeCharacters(locatorValue)
				+ "\"";

		if (testStepDTO.getThirdPara() != null) {
			actionString += ", \"" + testStepDTO.getThirdPara() + "\");";
		} else {
			actionString += ");";
		}
		return actionString;
	}

	private List<String> initialTearDownPart() {
		this.tearDownPart.add("\t@AfterTest");
		this.tearDownPart.add("\tpublic void tearDown() {");
		this.tearDownPart.add("\t\tdriver.quit();");
		this.tearDownPart.add("\t}");
		this.tearDownPart.add("");
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

	private static class AI_Parser {

		private TestParamsDTO testParamsDTO;
		private TestSuiteDTO testSuiteDTO;
		private TestCaseDTO[] testCaseDTO;
		private TestStepDTO[] testStepDTO;
		private LocateElementDTO locateElementDTO;

		public AI_Parser() throws FileNotFoundException {
			Gson gson = new Gson();
			try {
				this.testParamsDTO = gson.fromJson(new FileReader("sampleAItest.json"),
						TestParamsDTO.class);
				setTestSuiteDTO(testParamsDTO.getTestsuite());
				setTestCaseDTO(getTestSuiteDTO().getTestcase());

			} catch (FileNotFoundException e) {
				System.out
						.println("[FAILED] - Can't find input sampleAItest.json file!");
				throw e;
			}
		}

		public AI_Parser(String inputJsonPath) throws FileNotFoundException {
			Gson gson = new Gson();
			try {
				this.testParamsDTO = gson.fromJson(new FileReader(inputJsonPath),
						TestParamsDTO.class);
				setTestSuiteDTO(testParamsDTO.getTestsuite());
				setTestCaseDTO(getTestSuiteDTO().getTestcase());

			} catch (FileNotFoundException e) {
				System.out
						.println("[FAILED] - Can't find input " + inputJsonPath + "file!");
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

		/*
		public static void main(String[] args) throws FileNotFoundException {
		    AI_Parser p = new AI_Parser();
		    p.printTestSuiteDetails();
		}
		*/

		public void printTestSuiteDetails() throws FileNotFoundException {
			Gson gson = new Gson();
			TestParamsDTO params = gson.fromJson(new FileReader("sampleAItest.json"),
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

		public void example() throws FileNotFoundException {
			Gson gson = new Gson();

			// 1. JSON to Java object, read it from a file.
			// Staff staff = gson.fromJson(new FileReader("D:\\file.json"),
			// Staff.class);
			// 2. JSON to Java object, read it from a Json String.
			// String jsonInString = "{'name' : 'mkyong'}";
			// Staff staff = gson.fromJson(jsonInString, Staff.class);

			// JSON to JsonElement, convert to String later.
			// JsonElement json = gson.fromJson(new FileReader("D:\\file.json"),
			// JsonElement.class);
			// String result = gson.toJson(json);

			// TestSuiteJson suite = gson.fromJson(new
			// FileReader("sampleAItest.json"), TestSuiteJson.class);
			/*TestParams[] params = gson.fromJson(new FileReader("sampleAItest.json"), TestParams[].class);
			System.out.println(params[0].getBrowser());
			System.out.println(params[0].getURL());
			System.out.println(params[0].getTestsuite().getName());
			System.out.println(params[0].getTestsuite().getTestcase()[0].getName());
			System.out.println(params[0].getTestsuite().getTestcase()[0].getStep()[0].getName());
			System.out.println(params[0].getTestsuite().getTestcase()[0].getStep()[0].getAction());
			System.out.println(params[0].getTestsuite().getTestcase()[0].getStep()[0].getLocateElement().getBy());
			System.out.println(params[0].getTestsuite().getTestcase()[0].getStep()[0].getLocateElement().getValue());
			System.out.println(params[0].getTestsuite().getTestcase()[0].getStep()[0].getThirdPara());*/

			TestParamsDTO params = gson.fromJson(new FileReader("sampleAItest.json"),
					TestParamsDTO.class);
			System.out.println(params.getBrowser());
			System.out.println(params.getURL());
			System.out.println(params.getTestsuite().getName());
			System.out.println(
					"Number of testcases: " + params.getTestsuite().getTestcase().length);
			System.out.println(params.getTestsuite().getTestcase()[0].getName());
			System.out.println("Number of test steps of testcase 1: "
					+ params.getTestsuite().getTestcase()[0].getStep().length);
			System.out.println(
					params.getTestsuite().getTestcase()[0].getStep()[0].getName());
			System.out.println(
					params.getTestsuite().getTestcase()[0].getStep()[0].getAction());
			System.out.println(params.getTestsuite().getTestcase()[0].getStep()[0]
					.getLocateElement().getBy());
			System.out.println(params.getTestsuite().getTestcase()[0].getStep()[0]
					.getLocateElement().getValue());
			System.out.println(
					params.getTestsuite().getTestcase()[0].getStep()[0].getThirdPara());
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

	private static class TestCaseDTO {
		private String name;
		private TestStepDTO[] step;

		@Override
		public String toString() {
			return "TestCase name: " + name + "\n" + step;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TestStepDTO[] getStep() {
			return step;
		}

		public void setStep(TestStepDTO[] step) {
			this.step = step;
		}
	}

	private static class TestSuiteDTO {
		private String name;

		private TestCaseDTO[] testcase;

		@Override
		public String toString() {
			return "TestSuite name: " + name + "\n" + testcase;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TestCaseDTO[] getTestcase() {
			return testcase;
		}

		public void setTestcase(TestCaseDTO[] testcase) {
			this.testcase = testcase;
		}

	}

	private static class TestParamsDTO {
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

		public void setBrowser(String browser) {
			this.browser = browser;
		}

		public String getURL() {
			return URL;
		}

		public void setURL(String URL) {
			this.URL = URL;
		}

		public TestSuiteDTO getTestsuite() {
			return testsuite;
		}

		public void setTestsuite(TestSuiteDTO testsuite) {
			this.testsuite = testsuite;
		}
	}

	private static class TestStepDTO {
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

		public void setName(String name) {
			this.name = name;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public LocateElementDTO getLocateElement() {
			return locateElement;
		}

		public void setLocateElement(LocateElementDTO locateElement) {
			this.locateElement = locateElement;
		}

		public String getThirdPara() {
			return thirdPara;
		}

		public void setThirdPara(String thirdPara) {
			this.thirdPara = thirdPara;
		}

		private static class LocateElementDTO {
			private String by;
			private String value;

			@Override
			public String toString() {
				return "By: " + by + "\n" + "Value: " + value;
			}

			public String getBy() {
				return by;
			}

			public void setBy(String by) {
				this.by = by;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}
		}
	}

	private static class ActionsMapping {
		public static Map<String, String> actions = new HashMap<>();

		static {
			try {
				initialMapping();
				// System.out.println("Load mapping succesfully!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			// actions.put("Go to URL".toLowerCase(), "getLink");
			// actions.put("Click".toLowerCase(), "click");
			// actions.put("Write Text".toLowerCase(), "type_Text");
			// actions.put("Select Option".toLowerCase(), "selectOption");
			// actions.put(LoadConfigFile.getPropertyByName())
		}

		public static Boolean checkActionExists(String inputAction) {
			return actions.containsKey(inputAction);
		}

		public static String getAction(String inputAction) {
			if (checkActionExists(inputAction)) {
				return actions.get(inputAction);
			} else {
				return null;
			}
		}

		public static void initialMapping() throws IOException {
			List<String> content = Utility.readFileLineByLine("config.properties");
			for (String line : content) {
				if (line.contains("selenium.")) {
					String keyword = line.split("=")[0].split("\\.")[1];
					String inputAction = line.split("=")[1].toLowerCase();
					actions.put(inputAction.trim().replaceAll("\\s+", ""), keyword);
				}
			}
		}

	}

  private static class LocateElementDTO {
    private String by;
    private String value;

    @Override
    public String toString() {
        return "By: " + by + "\n"
                + "Value: " + value;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
	private static class Utility {

		public static boolean createFolder(String path) throws Exception {
			File theDir = new File(path.trim());

			// if the directory does not exist, create it
			if (!theDir.exists()) {
				return theDir.mkdir();

			} else
				return false;
		}

		/**
		 * Kill Process without log
		 *
		 * @param processName
		 * @throws Exception
		 * @author pminhhung
		 */
		public static void killProcess(String processName) throws Exception {
			try {

				if (System.getProperty("os.name").startsWith("Windows")) {
					Runtime.getRuntime().exec("taskkill /F /IM " + processName.trim());
				} else if (System.getProperty("os.name").startsWith("Mac")) {
					Runtime.getRuntime().exec("killall " + processName.trim());
				}

			} catch (Exception ex) {
				throw ex;
			}
		}

		/**
		 * Get date.
		 *
		 * @return Current date
		 * @throws Exception
		 */
		public static String getDate() throws Exception {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			return dateFormat.format(date);
		}

		/**
		 * Get time.
		 *
		 * @return Current time
		 * @throws Exception
		 */
		public static String getTime() throws Exception {
			DateFormat dateFormat = new SimpleDateFormat("HHmmss");
			Date date = new Date();
			return dateFormat.format(date);
		}

		public static void writeToFile(List<String> content, String filename,
				Boolean overwriteFlag) {
			File file = new File(filename);
			// if file doesnt exists, then create it
			if (overwriteFlag) {
				try {
					file.createNewFile();
					FileWriter fw = null;
					try {
						fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						for (String line : content) {
							bw.write(line);
							bw.newLine();
						}
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Write content to " + filename + " succesfully!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public static String handleEscapeCharacters(String input) {
			return StringEscapeUtils.escapeJava(input);
		}

		public static List<String> readFileLineByLine(String filename)
				throws IOException {
			FileInputStream fis = new FileInputStream(filename);
			// Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			List<String> res = new ArrayList<String>();
			// List<String> re = new ArrayList<String>();

			String line = null;
			while ((line = br.readLine()) != null) {
				res.add(line);
			}
			br.close();
			return res;
		}

	}
}
