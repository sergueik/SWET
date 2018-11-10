package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.notNullValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.sergueik.swet.Utils;

import com.github.sergueik.swet.SideRecording;
import com.github.sergueik.swet.SideTest;
import com.github.sergueik.swet.SideCommand;
import com.github.sergueik.swet.SideSuite;

// https://www.baeldung.com/gson-deserialization-guide
@SuppressWarnings("deprecation")
public class UtilsTest {

	private static Utils utils = Utils.getInstance();
	private static Boolean updated = false;
	private static String result = null;
	private static Map<String, String> elementData = new HashMap<>();
	private static Object[] expected = new Object[] { "ElementCssSelector",
			"ElementCodeName", "CommandId", "ElementId", "ElementXPath", "Url" };

	@BeforeClass
	public static void beforeSuiteMethod() throws Exception {

	}

	@Before
	public void loadBaseData() {
		utils.readData(Optional.of(elementData));
		System.err.println("Loaded: " + elementData.keySet());
	}

	@Ignore
	@Test
	public void dataKeysTest() {
		List<Object> subkeys = Arrays.asList(expected);
		// subkeys.remove("Url");
		Set<Object> dataSet = new HashSet<Object>(subkeys);
		assertTrue(new HashSet<Object>(elementData.keySet()).containsAll(dataSet));
	}

	@Ignore
	@Test
	public void readDataTest() {
		String payload = "{\"cluster\":{\"datacenter\":\"moscow\",\"node\":\"consul\",\"par\":[\"a\",\"b\",\"c\"],\"par2\":{\"key1\":\"val1\",\"key2\":\"val2\"},\"par3\":{\"key3\":[{\"key4\":\"val4\",\"key5\":\"val5\"},{\"key6\":\"val6\",\"key7\":\"val7\"}]}}}";
		// Exception (ignored): org.json.JSONException: JSONObject["cluster"] not a
		// string.
		utils.readData(payload, Optional.of(elementData));
		System.err.println("Loaded: " + elementData.keySet());

	}

	@Ignore
	// TODO: sort?
	@Test
	public void altDataKeysTest() {
		assertArrayEquals(expected, elementData.keySet().toArray());
	}

	@Ignore
	@Test
	public void writeDataJSONTest() {
		// assertArrayEquals(expected, elementData.keySet().toArray());
		// expected.length = expected.length - 1;
		result = utils.writeDataJSON(elementData, "{}");
		System.err.println("Result: " + result);
	}

	// example recording from
	// https://examples.javacodegeeks.com/enterprise-java/selenium/selenium-ide-commands-example/
	@Ignore
	@Test
	public void loadSideDataTest() {
		utils.setDebug(true);
		System.err.println("loadSideDataTest");
		String sideExamplepPayload = "{\"id\":\"837d3acd-285e-478a-8d46-817df0a5b4d9\",\"name\":\"Google<br>\",\"url\":\"https://www.google.com \t\",\"tests\":[{\"id\":\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\",\"name\":\"Google\",\"commands\":[{\"id\":\"160c2276-d9b3-4523-bdf3-b914111ca407\",\"comment\":\"\",\"command\":\"open\",\"target\":\"/images\",\"value\":\"\"},{\"id\":\"856ac533-41f0-4091-813d-6f865cf72985\",\"comment\":\"\",\"command\":\"open\",\"target\":\"/\",\"value\":\"\"},{\"id\":\"9c8e6eaf-3b39-435f-93f8-6716f159721d\",\"comment\":\"\",\"command\":\"windowMaximize\",\"target\":\"\",\"value\":\"\"},{\"id\":\"4a0595ff-61c3-41fc-94fa-b338e5c65faf\",\"comment\":\"\",\"command\":\"highlight\",\"target\":\"id=lst-ib\",\"value\":\"\"},{\"id\":\"602f45cf-a2f4-4491-b51c-a1036cebe879\",\"comment\":\"\",\"command\":\"pause\",\"target\":\"\",\"value\":\"200\"},{\"id\":\"22d356ea-f33a-49b7-ab07-53ecba35cf65\",\"comment\":\"\",\"command\":\"click\",\"target\":\"//input[@type='text']\",\"value\":\"\"},{\"id\":\"37f57010-4677-4bbf-8a1d-e0f19faadbb2\",\"comment\":\"\",\"command\":\"store\",\"target\":\"selenium ide\",\"value\":\"text\"},{\"id\":\"47b767f5-a7d4-4332-bc4f-5e1e986cf732\",\"comment\":\"\",\"command\":\"type\",\"target\":\"id=lst-ib\",\"value\":\"${text}\"},{\"id\":\"f2b9ac0a-7cce-4c0b-bb29-277eeea8bf12\",\"comment\":\"\",\"command\":\"verifyElementPresent\",\"target\":\"//input[@value='Google Search']\",\"value\":\"\"},{\"id\":\"5bfa6d57-3039-4a5a-82c1-e56dabb3cae5\",\"comment\":\"\",\"command\":\"verifyText\",\"target\":\"id=SIvCob\",\"value\":\"Google offered in: ...\"},{\"id\":\"673164e5-5802-4763-9dbe-f6afef86bef8\",\"comment\":\"\",\"command\":\"sendKeys\",\"target\":\"id=lst-ib\",\"value\":\"${KEY_ENTER}\"},{\"id\":\"82eb4147-efe2-4965-b71a-3132e3f7651b\",\"comment\":\"\",\"command\":\"refresh\",\"target\":\"\",\"value\":\"\"},{\"id\":\"b269914c-4a15-40c3-b467-8c36c01d331b\",\"comment\":\"\",\"command\":\"clickAt\",\"target\":\"id=logo\",\"value\":\"\"},{\"id\":\"f129939b-8f3b-4220-92a7-c4ecb4e9aff8\",\"comment\":\"\",\"command\":\"verifyTitle\",\"target\":\"Google\",\"value\":\"\"},{\"id\":\"1a503afd-367b-4f28-a8b1-01d489da5d81\",\"comment\":\"\",\"command\":\"storeTitle\",\"target\":\"\",\"value\":\"var1\"},{\"id\":\"768c0d16-b6bf-48e1-a424-b3248ebd088f\",\"comment\":\"\",\"command\":\"echo\",\"target\":\"${var1}\",\"value\":\"\"}]}],\"suites\":[{\"id\":\"05e89807-cb33-4ca6-8ca4-10e1cdf127c3\",\"name\":\"Default Suite\",\"tests\":[\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\"]}],\"urls\":[\"https://www.google.co.in\",\"https://www.google.co.in\"]}";
		// load and forget
		utils.readSideData(sideExamplepPayload,
				Optional.<Map<String, Object>> empty());
		Map<String, Object> result = new HashMap<String, Object>();
		utils.readSideData(sideExamplepPayload, Optional.of(result));
		System.err
				.println("Returned (keys): " + result.keySet() + " " + result.values());

	}

	@Test
	public void loadSideDataCommandsTest() {
		utils.setDebug(true);
		System.err.println("loadSideDataTest");
		String sideExamplepPayload = "{\"name\":\"Google\",\"id\":\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\",\"commands\":[{\"comment\":\"\",\"id\":\"160c2276-d9b3-4523-bdf3-b914111ca407\",\"value\":\"\",\"command\":\"open\",\"target\":\"/images\"},{\"comment\":\"\",\"id\":\"856ac533-41f0-4091-813d-6f865cf72985\",\"value\":\"\",\"command\":\"open\",\"target\":\"/\"},{\"comment\":\"\",\"id\":\"9c8e6eaf-3b39-435f-93f8-6716f159721d\",\"value\":\"\",\"command\":\"windowMaximize\",\"target\":\"\"},{\"comment\":\"\",\"id\":\"4a0595ff-61c3-41fc-94fa-b338e5c65faf\",\"value\":\"\",\"command\":\"highlight\",\"target\":\"id=lst-ib\"},{\"comment\":\"\",\"id\":\"602f45cf-a2f4-4491-b51c-a1036cebe879\",\"value\":\"200\",\"command\":\"pause\",\"target\":\"\"},{\"comment\":\"\",\"id\":\"22d356ea-f33a-49b7-ab07-53ecba35cf65\",\"value\":\"\",\"command\":\"click\",\"target\":\"//input[@type='text']\"},{\"comment\":\"\",\"id\":\"37f57010-4677-4bbf-8a1d-e0f19faadbb2\",\"value\":\"text\",\"command\":\"store\",\"target\":\"selenium ide\"},{\"comment\":\"\",\"id\":\"47b767f5-a7d4-4332-bc4f-5e1e986cf732\",\"value\":\"${text}\",\"command\":\"type\",\"target\":\"id=lst-ib\"},{\"comment\":\"\",\"id\":\"f2b9ac0a-7cce-4c0b-bb29-277eeea8bf12\",\"value\":\"\",\"command\":\"verifyElementPresent\",\"target\":\"//input[@value='Google Search']\"},{\"comment\":\"\",\"id\":\"5bfa6d57-3039-4a5a-82c1-e56dabb3cae5\",\"value\":\"Google offered in: ...\",\"command\":\"verifyText\",\"target\":\"id=SIvCob\"},{\"comment\":\"\",\"id\":\"673164e5-5802-4763-9dbe-f6afef86bef8\",\"value\":\"${KEY_ENTER}\",\"command\":\"sendKeys\",\"target\":\"id=lst-ib\"},{\"comment\":\"\",\"id\":\"82eb4147-efe2-4965-b71a-3132e3f7651b\",\"value\":\"\",\"command\":\"refresh\",\"target\":\"\"},{\"comment\":\"\",\"id\":\"b269914c-4a15-40c3-b467-8c36c01d331b\",\"value\":\"\",\"command\":\"clickAt\",\"target\":\"id=logo\"},{\"comment\":\"\",\"id\":\"f129939b-8f3b-4220-92a7-c4ecb4e9aff8\",\"value\":\"\",\"command\":\"verifyTitle\",\"target\":\"Google\"},{\"comment\":\"\",\"id\":\"1a503afd-367b-4f28-a8b1-01d489da5d81\",\"value\":\"var1\",\"command\":\"storeTitle\",\"target\":\"\"},{\"comment\":\"\",\"id\":\"768c0d16-b6bf-48e1-a424-b3248ebd088f\",\"value\":\"\",\"command\":\"echo\",\"target\":\"${var1}\"}]}";
		// load and forget
		utils.readSideData(sideExamplepPayload,
				Optional.<Map<String, Object>> empty(), "(?:name|id|commands)");
		Map<String, Object> result = new HashMap<String, Object>();
		utils.readSideData(sideExamplepPayload, Optional.of(result),
				"(?:name|id|commands)");
		System.err
				.println("Returned (keys): " + result.keySet() + " " + result.values());

	}

	private SideRecording sideRecording = new SideRecording();
	private SideSuite sideSuite = new SideSuite();
	private SideTest sideTest = new SideTest();
	private SideCommand sideCommand = new SideCommand();
	private List<SideSuite> suites = new ArrayList<>();
	private List<SideCommand> commands = new ArrayList<>();
	private List<SideTest> tests = new ArrayList<>();
	private List<String> testNames = new ArrayList<>();
	private String testName = "test 1";

	@Test
	public void scratchSideRecordingTest() {

		sideCommand.setId(id());
		sideCommand.setName("name of command 1");
		sideCommand.setValue("value of command 1");
		sideCommand.setTarget("target of command 1");
		commands.add(sideCommand);

		sideTest.setId(id());
		sideTest.setName("name of test 1");
		sideTest.setCommands(commands);
		tests.add(sideTest);

		testNames.add(testName);
		sideSuite.setId(id());
		sideSuite.setTestNames(testNames);
		sideSuite.setName("name of suite 1");
		suites.add(sideSuite);

		sideRecording.setId(id());
		sideRecording.setName("name of recording 1");
		sideRecording.setUrl("url of recording 1");
		sideRecording.setUrls(new ArrayList<String>());
		sideRecording.setSuites(suites);
		sideRecording.setTests(tests);
		System.err
				.println("Created side test recording: " + sideRecording.toString());
	}

	private String id() {
		return UUID.randomUUID().toString();
	}

	@Test
	// https://www.baeldung.com/gson-deserialization-guide
	public void deserializeTypeTest() {
		utils.setDebug(true);
		System.err.println("loadSideDataTest");
		String sideExamplepPayload = "{\"id\":\"837d3acd-285e-478a-8d46-817df0a5b4d9\",\"name\":\"Google<br>\",\"url\":\"https://www.google.com \t\",\"tests\":[{\"id\":\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\",\"name\":\"Google\",\"commands\":[{\"id\":\"160c2276-d9b3-4523-bdf3-b914111ca407\",\"comment\":\"\",\"command\":\"open\",\"target\":\"/images\",\"value\":\"\"},{\"id\":\"856ac533-41f0-4091-813d-6f865cf72985\",\"comment\":\"\",\"command\":\"open\",\"target\":\"/\",\"value\":\"\"}]}],\"suites\":[{\"id\":\"05e89807-cb33-4ca6-8ca4-10e1cdf127c3\",\"name\":\"Default Suite\",\"testNames\":[\"ae13d6ad-c3f2-4fb8-aaeb-14af40f2b3b9\"]}],\"urls\":[\"https://www.google.co.in\",\"https://www.google.co.in\"]}";
		// load and print
		SideRecording sideRecording = new Gson().fromJson(sideExamplepPayload,
				SideRecording.class);
		assertThat(sideRecording, notNullValue());
		List<String> urls = sideRecording.getUrls();
		assertThat(urls.get(0), notNullValue());
		System.err
				.println("Deserialized url from side recording: " + urls.toString());
		suites = sideRecording.getSuites();
		sideSuite = suites.get(0);
		assertThat(sideSuite, notNullValue());
		assertThat(sideSuite.getName(), notNullValue());
		System.err.println("Deserialized suite name: " + sideSuite.getName());
		assertThat(sideSuite.getId(), notNullValue());
		System.err.println("Deserialized suite id: " + sideSuite.getId());
		testNames = sideSuite.getTestNames(); // can not name tests 'testNames'
		try {
			assertThat(testNames.get(0), notNullValue());
			System.err.println("Deserialized suite: " + suites.get(0).toString());
		} catch (NullPointerException e) {
			System.err.println("Failed to Deserialized test names from suite.");
		}

		tests = sideRecording.getTests();
		sideTest = tests.get(0);
		assertThat(sideTest.getName(), notNullValue());
		assertThat(sideTest.getId(), notNullValue());
		assertThat(sideTest, notNullValue());
		commands = sideTest.getCommands();
		sideCommand = commands.get(0);
		assertThat(sideCommand, notNullValue());

		System.err.println("Deserialized side command: " + sideCommand.toString());

		// still expect a NPE
		System.err.println(
				"Deserialized side test recording: " + sideRecording.toString());
	}
}
