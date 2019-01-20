package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// org.apache.log4j.Category 
// was deprecated and replaced by  
// org.apache.log4j.Logger;
import org.apache.log4j.Logger;
// 
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel export and load class for Selenium WebDriver Elementor Tool (SWET) TableViewer
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
public class ExcelFileUtils {

	private static List<Map<Integer, String>> tableData = new ArrayList<>();
	private static Map<Integer, String> rowData = new HashMap<>();
	@SuppressWarnings("deprecation")
	// https://www.journaldev.com/7128/log4j2-example-tutorial-configuration-levels-appenders
	static final Logger logger = (Logger) Logger
			.getInstance(ExcelFileUtils.class);

	public static void setTableData(List<Map<Integer, String>> data) {
		tableData = data;
	}

	private static String excelFileName = null; // name of excel file
	private static String sheetName = "Sheet1"; // name of the sheet

	public static void setSheetName(String data) {
		ExcelFileUtils.sheetName = data;
	}

	public static void setExcelFileName(String data) {
		ExcelFileUtils.excelFileName = data;
	}

	public static void readXLSFile() throws IOException {

		InputStream ExcelFileToRead = new FileInputStream(excelFileName);
		HSSFWorkbook hddfwb = new HSSFWorkbook(ExcelFileToRead);
		HSSFSheet sheet = hddfwb.getSheetAt(0);
		HSSFRow row;
		HSSFCell cell;

		Iterator<Row> rows = sheet.rowIterator();

		while (rows.hasNext()) {

			row = (HSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();

			while (cells.hasNext()) {

				cell = (HSSFCell) cells.next();
				CellType type = cell.getCellTypeEnum();

				if (type == org.apache.poi.ss.usermodel.CellType.STRING) {
					logger.info(cell.getStringCellValue() + " ");
				} else if (type == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
					logger.info(cell.getNumericCellValue() + " ");
				} else {
					logger.info("? ");
					// TODO: Boolean, Formula, Errors
				}
			}
			logger.info("");
		}
		hddfwb.close();
	}

	public static void readXLSXFile() throws IOException {

		InputStream ExcelFileToRead = new FileInputStream(excelFileName);
		XSSFWorkbook xssfwb = new XSSFWorkbook(ExcelFileToRead);
		// XSSFWorkbook test = new XSSFWorkbook();
		XSSFSheet sheet = xssfwb.getSheetAt(0);
		XSSFRow row;
		XSSFCell cell;
		Iterator<Row> rows = sheet.rowIterator();
		while (rows.hasNext()) {
			row = (XSSFRow) rows.next();
			Iterator<Cell> cells = row.cellIterator();
			while (cells.hasNext()) {
				cell = (XSSFCell) cells.next();
				CellType type = cell.getCellTypeEnum();
				if (type == org.apache.poi.ss.usermodel.CellType.STRING) {
					logger.info(cell.getStringCellValue() + " ");
				} else if (type == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
					logger.info(cell.getNumericCellValue() + " ");
				} else {
					// TODO: Boolean, Formula, Errors
					logger.info("? ");
				}
			}
			logger.info("");
		}
		xssfwb.close();
	}

	public static void writeXLSFile() throws IOException {

		HSSFWorkbook wbObj = new HSSFWorkbook();
		HSSFSheet sheet = wbObj.createSheet(sheetName);

		for (int row = 0; row < tableData.size(); row++) {
			HSSFRow rowObj = sheet.createRow(row);
			rowData = tableData.get(row);
			for (int col = 0; col < rowData.size(); col++) {
				HSSFCell cellObj = rowObj.createCell(col);
				cellObj.setCellValue(rowData.get(col));
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);
		wbObj.write(fileOut);
		wbObj.close();
		fileOut.flush();
		fileOut.close();
	}

	public static void writeXLSXFile() throws IOException {

		// Exception in thread "main" 
		// java.lang.NoClassDefFoundError: org/apache/commons/collections4/ListValuedMap
		// https://stackoverflow.com/questions/39670382/apache-poi-error-loading-xssfworkbook-class
		XSSFWorkbook xssfwb = new XSSFWorkbook();
		XSSFSheet sheet = xssfwb.createSheet(sheetName);
		for (int row = 0; row < tableData.size(); row++) {
			XSSFRow rowObj = sheet.createRow(row);
			rowData = tableData.get(row);
			for (int col = 0; col < rowData.size(); col++) {
				XSSFCell cell = rowObj.createCell(col);
				cell.setCellValue(rowData.get(col));
				logger.info("Writing " + row + " " + col + "  " + rowData.get(col));
			}
		}
		FileOutputStream fileOut = new FileOutputStream(excelFileName);
		xssfwb.write(fileOut);
		xssfwb.close();
		fileOut.flush();
		fileOut.close();
	}
}
