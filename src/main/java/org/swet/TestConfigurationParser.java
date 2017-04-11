package org.swet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Configuration table helper for Selenium Webdriver Elementor Tool (SWET)
 *  @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class TestConfigurationParser {

	public static Scanner loadTestData(final String fileName) {
		Scanner scanner = null;
		System.err
				.println(String.format("Reading configuration file: '%s'", fileName));
		try {
			scanner = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			// fail(String.format("File '%s' was not found.", fileName));
			System.err.println(
					String.format("Configuration file was not found: '%s'", fileName));
			e.printStackTrace();
		}
		return scanner;
	}

	public static List<String[]> getConfiguration(final String fileName) {
		ArrayList<String[]> listOfData = new ArrayList<>();
		Scanner scanner = loadTestData(fileName);
		String separator = "|";
		while (scanner.hasNext()) {
			String line = scanner.next();
			String[] data = line.split(Pattern.compile("(\\||\\|/)")
					.matcher(separator).replaceAll("\\\\$1"));
			for (String entry : data) {
				System.err.println("data entry: " + entry);
			}
			listOfData.add(data);
		}
		scanner.close();
		return listOfData;
	}
}
