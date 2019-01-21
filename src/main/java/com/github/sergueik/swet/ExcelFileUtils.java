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

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

/**
 * Excel export and load class for 
 * Selenium WebDriver Elementor Tool (SWET) 
 * TableViewer
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
public class ExcelFileUtils {

	// https://www.journaldev.com/7128/log4j2-example-tutorial-configuration-levels-appenders
	@SuppressWarnings("deprecation")
	static final Logger logger = (Logger) Logger
			.getInstance(ExcelFileUtils.class);

	private static List<Map<Integer, String>> tableData = new ArrayList<>();

	public static void setTableData(List<Map<Integer, String>> data) {
		tableData = data;
	}

	private static Map<Integer, String> rowData = new HashMap<>();

	// name of excel file
	private static String excelFileName = null;

	public static void setExcelFileName(String data) {
		ExcelFileUtils.excelFileName = data;
	}

	private static String sheetFormat = "Excel 2007"; // format of the sheet

	public static void setSheetFormat(String data) {
		ExcelFileUtils.sheetFormat = data;
	}

	// name of the sheet
	private static String sheetName = "Sheet1";

	public static void setSheetName(String data) {
		ExcelFileUtils.sheetName = data;
	}

	public static void readXLSFile() throws Exception {

		try (InputStream fileInputStream = new FileInputStream(excelFileName)) {
			HSSFWorkbook hddfwb = new HSSFWorkbook(fileInputStream);
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
					} else if (type == org.apache.poi.ss.usermodel.CellType.BOOLEAN) {
						logger.info(cell.getBooleanCellValue() + " ");
					} else {
						logger.info("? ");
						// NOTE: not parsing either of
						// org.apache.poi.ss.usermodel.CellType.FORMULA
						// org.apache.poi.ss.usermodel.CellType.ERROR
					}
				}
				logger.info("");
			}
			hddfwb.close();
			fileInputStream.close();
		} catch (IOException e) {
			String message = String.format("Exception reading XLS file %s\n",
					excelFileName) + e.getMessage();
			logger.info(message);
			// NOTE: throw exceptions with user friendly messages to be rendered
			// by the master app
			throw new Exception(message);
		}
	}

	public static void readXLSXFile() throws Exception {

		try (InputStream fileInputStream = new FileInputStream(excelFileName)) {
			XSSFWorkbook xssfwb = new XSSFWorkbook(fileInputStream);
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
					} else if (type == org.apache.poi.ss.usermodel.CellType.BOOLEAN) {
						logger.info(cell.getBooleanCellValue() + " ");
					} else {
						// NOTE: not parsing either of
						// org.apache.poi.ss.usermodel.CellType.FORMULA
						// org.apache.poi.ss.usermodel.CellType.ERROR
						logger.info("? ");
					}
				}
				logger.info("");
			}
			xssfwb.close();
			fileInputStream.close();
		} catch (IOException e) {
			String message = String.format("Exception reading XLSX file %s\n",
					excelFileName) + e.getMessage();
			logger.info(message);
			// NOTE: throw exceptions with user friendly messages to be rendered
			// by the master app
			throw new Exception(message);
		}
	}

	public static void writeXLSFile() throws Exception {

		HSSFWorkbook hddfwb = new HSSFWorkbook();
		HSSFSheet sheet = hddfwb.createSheet(sheetName);

		for (int row = 0; row < tableData.size(); row++) {
			HSSFRow hssfrow = sheet.createRow(row);
			rowData = tableData.get(row);
			for (int col = 0; col < rowData.size(); col++) {
				HSSFCell hssfcell = hssfrow.createCell(col);
				hssfcell.setCellValue(rowData.get(col));
			}
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				excelFileName)) {
			hddfwb.write(fileOutputStream);
			hddfwb.close();
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			String message = String.format("Exception saving XLS file %s\n",
					excelFileName) + e.getMessage();
			logger.info(message);
			// NOTE: throw exceptions with user friendly messages to be rendered
			// by the master app
			throw new Exception(message);
		}
	}

	// NOTE: throw exceptions with user friendly messages to be rendered
	// by the master app
	public static void writeXLSXFile() throws Exception {

		XSSFWorkbook xssfwb = new XSSFWorkbook();
		XSSFSheet sheet = xssfwb.createSheet(sheetName);
		for (int row = 0; row < tableData.size(); row++) {
			XSSFRow xddfrow = sheet.createRow(row);
			rowData = tableData.get(row);
			for (int col = 0; col < rowData.size(); col++) {
				XSSFCell cell = xddfrow.createCell(col);
				cell.setCellValue(rowData.get(col));
				logger.info("Writing " + row + " " + col + "  " + rowData.get(col));
			}
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				excelFileName)) {

			xssfwb.write(fileOutputStream);
			xssfwb.close();
			fileOutputStream.flush();
			fileOutputStream.close();

		} catch (IOException e) {
			String message = String.format("Exception saving XLSX file %s\n",
					excelFileName) + e.getMessage();
			logger.info(message);
			// NOTE: throw exceptions with user friendly messages to be rendered
			// by the master app
			throw new Exception(message);

		}
	}
}
