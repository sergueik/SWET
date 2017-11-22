package org.swet;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.Test;
import org.swet.YamlHelper;

/**
 * Test for Selenium WebDriver Elementor Tool (SWET) YAML configuration file reader for 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class YamlHelperTest {

	private static String yamlFile = null;
	private static String[] helpTopics = { "Template-based Testsuite creation",
			"Keyword-driven Framework suite creation", "Saving and restoring" };
	private static Map<String, String> help;

	@BeforeClass
	public static void setup() throws IOException {
		yamlFile = String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), "help.yaml");
		help = YamlHelper.loadHelp(yamlFile);
	}

	@Test
	public void exactMatchTest() throws IOException {

		/*		assertThat(help.keySet().toArray(),
						is(new Object[] { "Template-based Testsuite creation",
								"Keyword-driven Framework suite creation",
								"Saving and restoring" }));
		*/
		assertThat(help.keySet().toArray(), is((Object[]) helpTopics));

	}

	@Test
	public void regexMatchTest() throws IOException {

		Pattern pattern = Pattern
				.compile("(" + StringUtils.join(helpTopics, "|") + ")");
		String result = StringUtils.join(help.keySet().toArray(), "|");
		Matcher matcher = pattern.matcher(result);
		assertTrue(matcher.find());
		pattern = Pattern
				.compile("^(?!" + StringUtils.join(helpTopics, "|") + ").*$");
		matcher = pattern.matcher(result);
		assertFalse(matcher.find());
	}
}