package com.github.sergueik.swet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Configuration table helper for Selenium WebDriver Elementor Tool (SWET)
 *  @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TestConfigurationParser {

	private static boolean skipHeaders = true;
	private static String defaultConfig = "test.configuration";
	private static Scanner scanner;

	public static void main(String[] args) {
		String configuPath = (args.length == 0)
				? String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), defaultConfig)
				: String.format("%s/%s", System.getProperty("user.dir"), args[0]);
		TestConfigurationParser.getConfiguration(configuPath);

	}

	@SuppressWarnings("resource")
	public static Scanner loadTestData(final String filename) {
		Scanner scanner = null;
		System.err
				.println(String.format("Reading configuration file: '%s'", filename));
		try {
			scanner = new Scanner(new File(filename)).useDelimiter("(?:\\r?\\n)+");
		} catch (FileNotFoundException e) {
			System.err.println(
					String.format("Configuration file was not found: '%s'", filename));
			e.printStackTrace();
		}
		return scanner;
	}

	public static List<String[]> getConfiguration(final String filename) {
		List<String[]> result = new LinkedList<>();
		scanner = loadTestData(filename);
		List<String> separators = new ArrayList<String>(
				Arrays.asList(new String[] { "|", "\t", ";", "," }));
		String separator = String
				.format("(?:%s)",
						String.join("|",
								separators.stream().map(o -> Pattern.compile("(\\||/)")
										.matcher(o).replaceAll("\\\\$1"))
										.collect(Collectors.toList())));
		int lineNum = 0;
		// System.err.println("separator:" + separator);
		while (scanner.hasNext()) {
			String line = scanner.next();
			// System.err.println("line: " + line);
			// skip comments
			if (line.matches("^#.*$")) {
				continue;
			}
			lineNum++;
			// skip headers
			if (skipHeaders) {
				if (lineNum == 1) {
					continue;
				}
			}

			String[] columns = line.split(separator);
			for (String column : columns) {
				System.err.println("data column: " + column);
			}
			result.add(columns);
		}
		scanner.close();
		return result;
	}
}
