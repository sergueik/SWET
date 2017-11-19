package org.swet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.swet.ExceptionDialogEx;
import org.swet.Utils;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * TestSuite Excel export Table Viewer class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
public class TableEditorEx {

	private static Table table;
	private static Display display;
	private static Shell shell;
	private Utils utils = Utils.getInstance();
	private static Shell parentShell = null;
	// need to defer initialization to avoid org.eclipse.swt.SWTException: Invalid
	// thread access
	// private static ExceptionDialogEx exceptionDialogEx = null;
	private static Label label;

	// Legacy SWD "Element selected By" keys to By, NgBy, AngularBy methods
	private static Map<String, String> selectorFromSWD = new HashMap<>();

	// Currently free-hand, may eventually become
	// public methods of the keyword-driven framework class
	private static Map<String, String> keywordTable = new HashMap<>();
	private static String[] columnHeaders = { "Element", "Action Keyword",
			"Selector Choice", "Selector Value", "Param 1", "Param 2", "Param 3" };

	private static Map<String, Map<String, String>> testData = new HashMap<>();

	private static String testSuitePath; // TODO: rename
	private static String yamlFilePath = null;
	private static String defaultYamlFile = "sample.yaml";
	private static String path = null;

	TableEditorEx(Display parentDisplay, Shell parent) {
		display = (parentDisplay != null) ? parentDisplay : new Display();
		shell = new Shell(display);

		if (parent != null) {
			parentShell = parent;
		}
		Map<String, Map<String, String>> internalConfiguration = YamlHelper
				.loadData(String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), "internalConfiguration.yaml"));

		selectorFromSWD = internalConfiguration.get("SWDSelectors");
		keywordTable = internalConfiguration.get("Keywords");
		// TODO: enable loading mixed content
		// Map<String,List<String>> columnHeaders =
		// internalConfiguration.get("Column Headers");
	}

	public void render() {
		if (yamlFilePath != null) {
			System.err.println("Loading " + yamlFilePath);
			Configuration _testCase = YamlHelper.loadConfiguration(yamlFilePath);
			testData = _testCase.getElements();
			// YamlHelper.printConfiguration(_testCase);
		}

		List<String> sortedSteps = sortSteps(testData);

		shell.setLayout(new FormLayout());
		label = new Label(shell, SWT.BORDER);
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0);
		labelData.right = new FormAttachment(100);
		labelData.bottom = new FormAttachment(100);
		label.setLayoutData(labelData);

		Menu menuBar = new Menu(shell, SWT.BAR);
		MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);

		MenuItem fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
		fileSaveItem.setText("&Save");
		fileSaveItem.addSelectionListener(new fileSaveItemListener());

		MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("E&xit");

		fileExitItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				shell.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				shell.close();
				shell.dispose();
			}
		});

		MenuItem helpMenuHeader = new MenuItem(menuBar, SWT.PUSH);
		helpMenuHeader.setText("&Help");

		helpMenuHeader.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				label.setText("No help available yet");
			}
		});

		shell.setMenuBar(menuBar);

		table = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);

		table.setHeaderVisible(true);

		for (int titleItem = 0; titleItem < columnHeaders.length; titleItem++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(columnHeaders[titleItem]);
		}

		for (int i = 0; i < columnHeaders.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}

		int blankRows = 1;
		int tableSize = sortedSteps.size();
		for (int i = 0; i < tableSize + blankRows; i++) {
			new TableItem(table, SWT.NONE);
		}

		TableItem[] items = table.getItems();

		appendRowToTable(table, sortedSteps);

		for (int i = tableSize; i < tableSize + blankRows; i++) {
			TableItem item = items[i];
			appendBlankRowToTable(table, item, i);
		}

		for (int titleItem = 0; titleItem < columnHeaders.length; titleItem++) {
			table.getColumn(titleItem).pack();
		}

		// http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fcustom%2FTableEditor.html
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, text.getText());
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		packTable(table);
		shell.pack();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();
	}

	static class fileSaveItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {

			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterNames(new String[] { "Excel 2003 Files (*.xls)",
					"Excel 2007-2013 Files (*.xlsx)" });
			dialog.setFilterExtensions(new String[] { "*.xls", "*.xlsx" });
			dialog.setFilterPath(
					(path == null) ? System.getProperty("user.home") : path);
			if (testSuitePath != null) {
				dialog.setFileName(testSuitePath);
				path = new String(testSuitePath);
			} //
			testSuitePath = dialog.open();
			if (testSuitePath != null) {

				List<Map<Integer, String>> tableData = new ArrayList<>();
				Map<Integer, String> rowData = new HashMap<>();

				TableItem[] tableItems = table.getItems();
				int numColumns = table.getColumnCount();
				// to get the the value of the first row at the 2nd column
				for (int row = 0; row != tableItems.length; row++) {
					TableItem tableItem = tableItems[row];
					rowData = new HashMap<>();
					for (int col = 0; col != numColumns; col++) {
						rowData.put(col, tableItem.getText(col));
					}
					tableData.add(rowData);
				}

				ReadWriteExcelFileEx.setExcelFileName(testSuitePath);
				ReadWriteExcelFileEx.setSheetName("test123");
				ReadWriteExcelFileEx.setTableData(tableData);

				try {
					if (testSuitePath.matches(".*\\.xlsx$")) {
						ReadWriteExcelFileEx.writeXLSXFile();
						ReadWriteExcelFileEx.readXLSXFile();
					} else {
						ReadWriteExcelFileEx.writeXLSFile();
						ReadWriteExcelFileEx.readXLSFile();
					}
					label.setText(String.format("Saved to \"%s\"", testSuitePath));
					label.update();
					System.out.println(String.format("Saved to \"%s\"", testSuitePath));
				} catch (Exception e) {
					ExceptionDialogEx.getInstance().render(e);
				}
			} else {
				if (path != null) {
					testSuitePath = new String(path);
				}
			}
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			label.setText("Saved");
			label.update();
		}
	}

	private static void appendRowToTable(Table table, List<String> stepIds) {

		TableItem[] tableItems = table.getItems();
		int cnt = 0;
		for (String stepId : stepIds) {
			// Append row into the TableEditor
			TableItem tableItem = tableItems[cnt];
			Map<String, String> elementData = testData.get(stepId);
			String selectorChoice = selectorFromSWD
					.get(elementData.get("ElementSelectedBy"));
			String selectorValue = elementData
					.get(elementData.get("ElementSelectedBy"));
			tableItem
					.setText(
							new String[] {
									String.format("%s (step %d)",
											elementData.get("ElementCodeName"),
											Integer.parseInt(elementData.get("ElementStepNumber"))),
									String.format("Action %d", cnt), selectorChoice,
									selectorValue });
			// some columns need to be converted to selects

			TableEditor keywordChoiceEditor = new TableEditor(table);
			CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
			keywordChoiceCombo.setText("Choose..");
			for (String keyword : keywordTable.keySet()) {
				keywordChoiceCombo.add(keyword);
			}
			// NOTE: none of options is initially selected
			keywordChoiceEditor.grabHorizontal = true;
			int keywordChoiceColumn = 1;
			keywordChoiceCombo.setData("column", keywordChoiceColumn);
			keywordChoiceCombo.setData("item", tableItem);
			keywordChoiceEditor.setEditor(keywordChoiceCombo, tableItem,
					keywordChoiceColumn);
			keywordChoiceCombo.addModifyListener(new keywordChoiceListener());

			TableEditor selectorChoiceEditor = new TableEditor(table);
			CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
			for (String locator : selectorFromSWD.values()) {
				selectorChoiceCombo.add(locator);
			}
			int currentSelector = new ArrayList<String>(selectorFromSWD.values())
					.indexOf(selectorFromSWD.get(elementData.get("ElementSelectedBy")));

			selectorChoiceCombo.select(currentSelector);
			selectorChoiceEditor.grabHorizontal = true;
			int selectorChoiceColumn = 2;
			selectorChoiceCombo.setData("item", tableItem);
			selectorChoiceCombo.setData("column", selectorChoiceColumn);
			selectorChoiceEditor.setEditor(selectorChoiceCombo, tableItem,
					selectorChoiceColumn);
			selectorChoiceCombo.addModifyListener(new selectorChoiceListener());
			cnt = cnt + 1;
		}
		return;
	}

	private static void appendRowToTable(Table table,
			LinkedHashMap<String, Integer> steps) {

		TableItem[] tableItems = table.getItems();
		int cnt = 0;
		for (String stepId : steps.keySet()) {
			// Append row into the TableEditor
			TableItem tableItem = tableItems[cnt];
			Map<String, String> elementData = testData.get(stepId);
			String selectorChoice = selectorFromSWD
					.get(elementData.get("ElementSelectedBy"));
			String selectorValue = elementData
					.get(elementData.get("ElementSelectedBy"));
			tableItem
					.setText(
							new String[] {
									String.format("%s (step %d)",
											elementData.get("ElementCodeName"),
											Integer.parseInt(elementData.get("ElementStepNumber"))),
									String.format("Action %d", cnt), selectorChoice,
									selectorValue });
			// some columns need to be converted to selects

			TableEditor keywordChoiceEditor = new TableEditor(table);
			CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
			keywordChoiceCombo.setText("Choose..");
			for (String keyword : keywordTable.keySet()) {
				keywordChoiceCombo.add(keyword);
			}
			// NOTE: none of options is initially selected
			keywordChoiceEditor.grabHorizontal = true;
			int keywordChoiceColumn = 1;
			keywordChoiceCombo.setData("column", keywordChoiceColumn);
			keywordChoiceCombo.setData("item", tableItem);
			keywordChoiceEditor.setEditor(keywordChoiceCombo, tableItem,
					keywordChoiceColumn);
			keywordChoiceCombo.addModifyListener(new keywordChoiceListener());

			TableEditor selectorChoiceEditor = new TableEditor(table);
			CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
			for (String locator : selectorFromSWD.values()) {
				selectorChoiceCombo.add(locator);
			}
			int currentSelector = new ArrayList<String>(selectorFromSWD.values())
					.indexOf(selectorFromSWD.get(elementData.get("ElementSelectedBy")));

			selectorChoiceCombo.select(currentSelector);
			selectorChoiceEditor.grabHorizontal = true;
			int selectorChoiceColumn = 2;
			selectorChoiceCombo.setData("item", tableItem);
			selectorChoiceCombo.setData("column", selectorChoiceColumn);
			selectorChoiceEditor.setEditor(selectorChoiceCombo, tableItem,
					selectorChoiceColumn);
			selectorChoiceCombo.addModifyListener(new selectorChoiceListener());
			cnt = cnt + 1;
		}
		return;
	}

	static class selectorChoiceListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent event) {
			CCombo combo = (CCombo) event.widget;
			int column = (int) combo.getData("column");
			String oldValue = ((TableItem) combo.getData("item")).getText(column);
			String newValue = combo.getText();
			// System.err.println(String.format("Updating %s = %s", oldValue,
			// newValue));
			if (selectorFromSWD.containsValue(newValue)) {
				((TableItem) combo.getData("item")).setText(column, newValue);
			}
		}
	}

	private static void appendBlankRowToTable(Table table, TableItem item,
			int index) {

		item.setText(new String[] { String.format("Element %d name", index),
				String.format("Action keyword %d", index), "",
				String.format("Selector value", index) });

		TableEditor keywordChoiceEditor = new TableEditor(table);
		CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
		keywordChoiceCombo.setText("Choose..");
		for (String keyword : keywordTable.keySet()) {
			keywordChoiceCombo.add(keyword);
		}
		// NOTE: none of options is initially selected
		keywordChoiceEditor.grabHorizontal = true;
		int keywordChoiceColumn = 1;
		keywordChoiceCombo.setData("column", keywordChoiceColumn);
		keywordChoiceCombo.setData("item", item);
		keywordChoiceEditor.setEditor(keywordChoiceCombo, item,
				keywordChoiceColumn);
		keywordChoiceCombo.addModifyListener(new keywordChoiceListener());

		TableEditor selectorChoiceEditor = new TableEditor(table);
		CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
		selectorChoiceCombo.setText("Choose");
		for (String locator : selectorFromSWD.values()) {
			selectorChoiceCombo.add(locator);
		}
		// NOTE: none of options is initially selected
		selectorChoiceEditor.grabHorizontal = true;
		int selectorChoiceColumn = 2;
		selectorChoiceCombo.setData("item", item);
		selectorChoiceCombo.setData("column", selectorChoiceColumn);
		selectorChoiceEditor.setEditor(selectorChoiceCombo, item,
				selectorChoiceColumn);
		selectorChoiceCombo.addModifyListener(new selectorChoiceListener());
		return;
	}

	static class keywordChoiceListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent event) {
			CCombo combo = (CCombo) event.widget;
			int column = (int) combo.getData("column");
			String oldValue = ((TableItem) combo.getData("item")).getText(column);
			String newValue = combo.getText();
			// System.err.println(String.format("Updating %s = %s", oldValue,
			// newValue));
			if (keywordTable.containsKey(newValue)) {
				((TableItem) combo.getData("item")).setText(column, newValue);
			}
		}
	}

	public void setData(String key, String value) {
		Map<String, String> _configData = new HashMap<>();
		utils.readData(value, Optional.of(_configData));
		testData.put(key, _configData);
		/*
		System.err.println(String.format("setData %s -> \n %s", key,
				utils.writeDataJSON(testData.get(key), "{}")));
		*/
	}

	// origin:
	// https://github.com/bp-FLN/SWT-Tools/blob/master/src/swt/TableTools.java
	public static void packTable(Table table) {
		for (TableColumn tc : table.getColumns()) {
			tc.pack();
		}
	}

	public static void main(String[] args) {

		yamlFilePath = (args.length == 0)
				? String.format("%s/src/main/resources/%s",
						System.getProperty("user.dir"), defaultYamlFile)
				: String.format("%s/%s", System.getProperty("user.dir"), args[0]);

		TableEditorEx o = new TableEditorEx(null, null);
		o.render();
		display.dispose();
	}

	private static class ReadWriteExcelFileEx {

		private static List<Map<Integer, String>> tableData = new ArrayList<>();
		private static Map<Integer, String> rowData = new HashMap<>();

		public static void setTableData(List<Map<Integer, String>> data) {
			tableData = data;
		}

		private static String excelFileName = null; // name of excel file
		private static String sheetName = "Sheet1"; // name of the sheet

		public static void setSheetName(String data) {
			ReadWriteExcelFileEx.sheetName = data;
		}

		public static void setExcelFileName(String data) {
			ReadWriteExcelFileEx.excelFileName = data;
		}

		public static void readXLSFile() throws IOException {

			InputStream ExcelFileToRead = new FileInputStream(excelFileName);
			HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);
			HSSFSheet sheet = wb.getSheetAt(0);
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
						System.err.print(cell.getStringCellValue() + " ");
					} else if (type == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
						System.err.print(cell.getNumericCellValue() + " ");
					} else {
						System.err.print("? ");
						// TODO: Boolean, Formula, Errors
					}
				}
				System.out.println();
			}
		}

		public static void readXLSXFile() throws IOException {

			InputStream ExcelFileToRead = new FileInputStream(excelFileName);
			XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead);
			XSSFWorkbook test = new XSSFWorkbook();
			XSSFSheet sheet = wb.getSheetAt(0);
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
						System.err.print(cell.getStringCellValue() + " ");
					} else if (type == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
						System.err.print(cell.getNumericCellValue() + " ");
					} else {
						// TODO: Boolean, Formula, Errors
						System.err.print("? ");
					}
				}
				System.out.println();
			}
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

			// @SuppressWarnings("resource")
			XSSFWorkbook wbObj = new XSSFWorkbook();
			XSSFSheet sheet = wbObj.createSheet(sheetName);
			for (int row = 0; row < tableData.size(); row++) {
				XSSFRow rowObj = sheet.createRow(row);
				rowData = tableData.get(row);
				for (int col = 0; col < rowData.size(); col++) {
					XSSFCell cell = rowObj.createCell(col);
					cell.setCellValue(rowData.get(col));
					System.err
							.println("Writing " + row + " " + col + "  " + rowData.get(col));
				}
			}
			FileOutputStream fileOut = new FileOutputStream(excelFileName);
			wbObj.write(fileOut);
			wbObj.close();
			fileOut.flush();
			fileOut.close();
		}
	}

	// for (Object o : sortedSteps.entrySet() )

	private List<String> sortSteps(Map<String, Map<String, String>> testData) {

		List<String> sortedSteps = new ArrayList<>();
		Map<String, Integer> elementSteps = testData.values().stream()
				.collect(Collectors.toMap(o -> o.get("CommandId"),
						o -> Integer.parseInt(o.get("ElementStepNumber"))));

		// alternative
		/*
		elementSteps = testData.keySet().stream().collect(Collectors.toMap(o -> o,
				o -> Integer.parseInt(testData.get(o).get("ElementStepNumber"))));
		*/
		List<Entry<String, Integer>> stepNumbers = new ArrayList<>();
		stepNumbers.addAll(elementSteps.entrySet());
		/*
		for (Entry<String, Integer> e : elementSteps.entrySet()) {
			o.add(e);
		}
		*/
		Collections.sort(stepNumbers, new StepNumberComparator());
		return stepNumbers.stream().map(e -> e.getKey()).collect(Collectors.toList());
		/*
				for (Entry<String, Integer> e : o) {
					sortedSteps.add(e.getKey());
					System.err.println(String.format("%d %s", e.getValue(), e.getKey()));
				}
				return sortedSteps;
				*/
	}

	private static class StepNumberComparator
			implements Comparator<Entry<String, Integer>> {
		public int compare(Entry<String, Integer> obj_left,
				Entry<String, Integer> obj_right) {
			return obj_left.getValue().compareTo(obj_right.getValue());

		}
	}
}
