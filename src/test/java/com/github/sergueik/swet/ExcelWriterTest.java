package com.github.sergueik.swet;

/**
 * Copyright 2019 Serguei Kouzmine
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for Selenium WebDriver Elementor Tool (SWET) Excel file writer 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// see also the forum where few initially failing implementations were shown
// http://software-testing.ru/forum/index.php?/topic/38373-apache-poi-dobavlenie-novoj-stranitcy-v-suschestvuiuschij-fa/
// see also:
// https://smearg.wordpress.com/2013/01/23/powershell-и-excel-часть-1-заполнение-таблицы/
// https://powershell.org/forums/topic/dynamically-create-worksheets-in-excel/
// http://ntcoder.com/bab/2018/01/18/powershell-tidbits-creating-excel-workbook-and-filling-out-data-into-worksheets/
public class ExcelWriterTest {
	private static List<String> dummyKeys = new ArrayList<>();
	private static List<String> dummyValues = new ArrayList<>();
	private static String fileName = "dummy.xlsx";
	private static Workbook wb = null;

	@AfterClass
	public static void cleanup() throws IOException {
		File file = new File(fileName);

		if (!file.delete()) {
			throw new IOException("Delete " + file.getAbsolutePath() + "failed ");
		}
	}

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
		dummyValues.add("50");
		dummyValues.add("60");
		addSheet("Sheet 2", dummyKeys, dummyValues, fileName);

		wb = new XSSFWorkbook(new FileInputStream(fileName));
		assertThat(wb.getNumberOfSheets(), greaterThan(1));
		wb.close();
		addSheet("Sheet 1", dummyKeys, dummyValues, fileName);
		// assertTrue(supportedKeywords.containsAll(keywordTable.keySet()));
		// assertFalse(keywordTable.keySet().containsAll(supportedKeywords));
		wb = new XSSFWorkbook(new FileInputStream(fileName));
		assertThat(wb.getNumberOfSheets(), equalTo(2));
		wb.close();
	}

	public static void addSheet(String sheetName, List<String> keys,
			List<String> values, String fileName)
			throws IOException, EncryptedDocumentException, InvalidFormatException {

		if (Files.exists(FileSystems.getDefault()
				.getPath(System.getProperty("user.dir"), fileName),
				new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {

			System.out.println("File exists: " + fileName);
			wb = new XSSFWorkbook(new FileInputStream(fileName));
		} else {
			wb = new XSSFWorkbook();
		}
		/*
		$idx = 1
		$o = new-object -ComObject 'excel.application'
		$o.visible = $true
		$excel = $o.workbooks.add()
		$excel.worksheets.item(1).delete()
		
		@('A1','B2','C3') | foreach-object  {
		$group = $_
		$sheet = $excel.worksheets.item($idx)
		$sheet.name = $group
		$idx++
		$excel.worksheets.add($idx)
		}
		
		
		*/
		Sheet sheet = wb.getSheet(sheetName) != null ? wb.getSheet(sheetName)
				: wb.createSheet(sheetName);

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
		wb.close();
		outputStream.close();
	}

}
