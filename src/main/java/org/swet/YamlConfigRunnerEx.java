package org.swet;

import java.io.IOException;

/**
 * Configuration test for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class YamlConfigRunnerEx {

	private static String yamlFile = null;

	public static void main(String[] args) throws IOException {

		yamlFile = (args.length == 0)
				? String.format("%s/%s", System.getProperty("user.dir"), "sample.yaml")
				: args[0];

		Configuration config = YamlHelper.loadConfiguration(yamlFile);
		YamlHelper.printConfiguration(config);
	}
}
