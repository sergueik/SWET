package org.swet;

import java.io.IOException;

import java.lang.RuntimeException;
import static java.lang.String.format;

/**
 * Configuration test for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class YamlConfigRunnerEx {
	private static String yamlFile = null;

	public static void main(String[] args) throws IOException {

		yamlFile = (args.length == 0) ? String.format("%s/src/main/resources/%s",
				System.getProperty("user.dir"), "sample.yaml") : args[0];

		Configuration config = YamlHelper.loadConfiguration(yamlFile);
		YamlHelper.printConfiguration(config);
	}
}
