package org.swet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestConfigurationParserTest {
	/**
	 * Testing of the Selenium WebDriver Elementor Tool (SWET) Configuration table helper
	 *  @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
	 */

	private static boolean skipHeaders = true;
	private static String defaultConfig = "test.configuration";

	@Test
	public void mainTest() {
		String configuPath = String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), defaultConfig);
		List<Object> result = flatten(
				TestConfigurationParser.getConfiguration(configuPath));
		assertThat(result.toArray(), is(new Object[] { "A1", "A2", "A3", "B1", "B2",
				"B3", "C1", "C2", "C3", "D1", "D2", "D3" }));
	}

	// https://stackoverflow.com/questions/40186270/java-flatten-an-array-using-recursion
	private static List<Object> flatten(Object input) {
		List<Object> result = new ArrayList<Object>();
		if (input.getClass().isArray()) {
			for (int pos = 0; pos < Array.getLength(input); pos++) {
				result.addAll(flatten(Array.get(input, pos)));
			}
		} else if (input instanceof List) {
			for (Object element : (List<?>) input) {
				result.addAll(flatten(element));
			}
		} else {
			result.add(input);
		}
		return result;
	}
}
