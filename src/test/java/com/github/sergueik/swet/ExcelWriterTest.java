package com.github.sergueik.swet;
/**
 * Copyright 2019 Serguei Kouzmine
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

/**
 * Test for Selenium WebDriver Elementor Tool (SWET) Excel file writer 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// see also the forum where the initially failing implementation was shown
// http://software-testing.ru/forum/index.php?/topic/38373-apache-poi-dobavlenie-novoj-stranitcy-v-suschestvuiuschij-fa/
public class ExcelWriterTest {
	private static List<String> dummyKeys = new ArrayList<>();
	private static List<String> dummyValues = new ArrayList<>();
	private static String fileName = "dummy.xlsx";

	@Test
	public void supportedKeywordsContainsKeywordTableTest()
			throws IOException, EncryptedDocumentException, InvalidFormatException {
		dummyKeys.add("key 1");
		dummyKeys.add("key 2");
		dummyKeys.add("key 3");
		dummyKeys.add("key 4");
		dummyValues.add("10");
		dummyValues.add("20");
		dummyValues.add("30");
		dummyValues.add("40");
		addSheet("Sheet 1", dummyKeys, dummyValues, fileName);
		dummyKeys.clear();
		dummyValues.clear();
		dummyKeys.add("key 1");
		dummyKeys.add("key 2");
		dummyValues.add("10");
		dummyValues.add("20");
		addSheet("Sheet 2", dummyKeys, dummyValues, fileName);
		// assertTrue(supportedKeywords.containsAll(keywordTable.keySet()));
		// assertFalse(keywordTable.keySet().containsAll(supportedKeywords));
	}

	public static void addSheet(String sheetName, List<String> keys,
			List<String> values, String fileName)
			throws IOException, EncryptedDocumentException, InvalidFormatException {
		Workbook wb = null;
		if (Files.exists(FileSystems.getDefault()
				.getPath(System.getProperty("user.dir"), fileName),
				new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {

			System.out.println("File exists: " + fileName);
			wb = WorkbookFactory.create(new FileInputStream(fileName));
		} else {
			wb = new XSSFWorkbook();
		}
		Sheet sheet = wb.createSheet(sheetName);

		Row r0 = sheet.createRow(0);
		Cell c0 = r0.createCell(0);
		c0.setCellValue("Key");
		Cell c1 = r0.createCell(1);
		c1.setCellValue("Value");

		Row a;

		List<Integer> valuesInt = new ArrayList<>();
		for (String s : values)
			valuesInt.add(Integer.valueOf(s));

		for (int i = 0; i < keys.size(); i++) {
			a = sheet.createRow(i + 1);
			String name = keys.get(i);
			a.createCell(0).setCellValue(name);
		}

		for (int j = 0; j < valuesInt.size(); j++) {
			a = sheet.getRow(j + 1);
			Integer price = valuesInt.get(j);
			a.createCell(1).setCellValue(price);
		}

		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:B" + (valuesInt.size())));

		FileOutputStream outputStream = new FileOutputStream(fileName);

		wb.write(outputStream);
		outputStream.close();
	}

}