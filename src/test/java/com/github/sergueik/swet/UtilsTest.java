package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

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
import com.github.sergueik.swet.Utils;

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

	@Test
	public void testDataKeys() {
		List<Object> subkeys = Arrays.asList(expected);
		// subkeys.remove("Url");
		Set<Object> dataSet = new HashSet<Object>(subkeys);
		assertTrue(new HashSet<Object>(elementData.keySet()).containsAll(dataSet));
	}

	@Ignore
	// need to sort
	@Test
	public void test2DataKeys() {
		assertArrayEquals(expected, elementData.keySet().toArray());
	}

	@Test
	public void testWriteDataJSON() {
		// assertArrayEquals(expected, elementData.keySet().toArray());
		// expected.length = expected.length - 1;
		result = utils.writeDataJSON(elementData, "{}");
		System.err.println("Result: " + result);
	}
}