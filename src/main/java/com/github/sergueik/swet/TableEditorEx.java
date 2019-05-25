package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

// org.apache.log4j.Category 
// was deprecated and replaced by  
// org.apache.log4j.Logger;
import org.apache.log4j.Logger;
// 
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * TestSuite Excel export Table Viewer class for Selenium WebDriver Elementor Tool (SWET)
 * 
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
// some based on
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableEditor.htm
public class TableEditorEx {

	private static TableItem checkedItem = null;
	private static Table table;
	private static Display display;
	private static Shell shell;
	@SuppressWarnings("unused")
	private static Shell parentShell = null;

	private Utils utils = Utils.getInstance();
	// need to defer initialization to avoid org.eclipse.swt.SWTException: Invalid
	// thread access
	// private static ExceptionDialogEx exceptionDialogEx = null;
	// private static Label label;

	// Legacy SWD "Element selected By" keys to By, NgBy, AngularBy methods
	private static Map<String, String> selectorFromSWD = new HashMap<>();

	// Currently free-hand, may eventually become
	// public methods of the keyword-driven framework class
	private static Map<String, String> keywordTable = new HashMap<>();
	private static String[] columnHeaders = { "#", "Element", "Action Keyword",
			"Selector Choice", "Selector Value", "Param 1", "Param 2", "Param 3" };

	private static Map<String, Map<String, String>> testData = new HashMap<>();

	private static String testSuitePath; // TODO: rename
	private static String yamlFilePath = null;
	private static String defaultYamlFile = "sample.yaml";
	private static String path = null;

	@SuppressWarnings("deprecation")
	// https://www.journaldev.com/7128/log4j2-example-tutorial-configuration-levels-appenders
	static final Logger logger = (Logger) Logger.getInstance(TableEditorEx.class);
	private static StringBuilder loggingSb = new StringBuilder();
	@SuppressWarnings("unused")
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);

	TableEditorEx(Display parentDisplay, Shell parent) {
		utils.initializeLogger();
		logger.info("Initialized logger.");

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
		Button buttonSave;
		Button buttonCancel;
		int buttonWidth = 120;
		int buttonHeight = 28;
		if (yamlFilePath != null) {
			logger.info("Loading " + yamlFilePath);
			Configuration _testCase = YamlHelper.loadConfiguration(yamlFilePath);
			testData = _testCase.getElements();
			// YamlHelper.printConfiguration(_testCase);
		}

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);

		Composite tableComposite = new Composite(shell,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		tableComposite.setLayout(gridLayout);

		List<String> sortedSteps = utils.sortSteps(testData, "CommandId",
				"ElementStepNumber");
		// http://www.java2s.com/Tutorial/Java/0280__SWT/TableWithCheckBoxCell.htm
		// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesCellEditors.htm

		table = new Table(tableComposite,
				SWT.CHECK | SWT.LEFT | SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// see also:
		// http://qaru.site/questions/1487998/how-to-find-index-of-swt-table-column
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

		// can call methods in embedding class
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
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					checkedItem = (TableItem) event.item;
					checkedItem.setChecked(checkedItem.getChecked());
					System.err.println("Checkbox: " + checkedItem.toString()
					/* will contain index like : "TableItem {2}" */ + " "
							+ checkedItem.getChecked() + " " + table
									.getSelectionIndex() /* will return index after a row was selected, -1 otherwise*/ );

				}
			}
		});
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
											text.dispose();
											e.doit = false;
											break;
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
											break;
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

		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableComposite
				.setSize(tableComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		tableComposite.pack();

		Composite buttonComposite = new Composite(shell, SWT.NO_FOCUS);

		buttonComposite.setLayoutData(
				new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonSave = new Button(buttonComposite, SWT.BORDER | SWT.PUSH);
		buttonSave.setText("Save");
		// for Clipboard one may use TextTransfer and tab-separated values or
		// RTFTransfer with
		// {\\rtf\\ansi\n{\n\\trowd\\trgaph30\\trleft-30\\trrh316\\clvertalb\\cellx1280\\clvertalb\\cellx2560\\pard\\plain\\intbl\n\\ql
		// A_1\\cell\\ql
		// B_1\\cell\\row\n\\trowd\\trgaph30\\trleft-30\\trrh316\\clvertalb\\cellx1280\\clvertalb\\cellx2560\\pard\\plain\\intbl\n\\ql
		// A_2\\cell\\ql B_2\\cell\\row\n}\n}
		// see also com.lowagie.text.rtf.RtfWriter2
		// com.lowagie.itext-rtf
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false).applyTo(buttonSave);

		// label = new Label(shell, SWT.BORDER);

		buttonSave.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
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

					ExcelFileUtils.setExcelFileName(testSuitePath);
					ExcelFileUtils.setSheetName("SWT Test");
					ExcelFileUtils.setTableData(tableData);

					try {
						if (testSuitePath.matches(".*\\.xlsx$")) {
							ExcelFileUtils.setSheetFormat("Excel 2007");
							ExcelFileUtils.writeXLSXFile();
							ExcelFileUtils.readXLSXFile();
						} else {
							ExcelFileUtils.setSheetFormat("Excel 2003");
							ExcelFileUtils.writeXLSFile();
							ExcelFileUtils.readXLSFile();
						}
						// TODO: create the for status update indicator label on the app
						// label.setText(String.format("Saved to \"%s\"", testSuitePath));
						// label.update();
					} catch (Exception e) {
						ExceptionDialogEx.getInstance().render(e);
					}
				} else {
					if (path != null) {
						testSuitePath = new String(path);
					}
				}
			}
		});

		// buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		buttonCancel = new Button(buttonComposite, SWT.PUSH);
		buttonCancel.setText("Cancel");

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false)
				.applyTo(buttonCancel);

		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				buttonComposite.dispose();
				shell.dispose();
			}
		});

		buttonComposite.pack();

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();
	}

	public void setData(String key, String value) {
		Map<String, String> _configData = new HashMap<>();
		utils.readData(value, Optional.of(_configData));
		testData.put(key, _configData);
		logger.info(String.format("setData %s -> \n %s", key,
				utils.writeDataJSON(testData.get(key), "{}")));

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

	private static void appendRowToTable(Table table, List<String> stepIds) {

		TableItem[] tableItems = table.getItems();
		int cnt = 0;
		for (String stepId : stepIds) {

			// get element data
			TableItem tableItem = tableItems[cnt];
			Map<String, String> elementData = testData.get(stepId);
			String selectorChoice = selectorFromSWD
					.get(elementData.get("ElementSelectedBy"));

			String selectorValue = elementData
					.get(elementData.get("ElementSelectedBy"));

			// Append row into the TableEditor
			tableItem.setText(new String[] { elementData.get("ElementStepNumber"),
					elementData.get("ElementCodeName"), String.format("Action %d", cnt),
					selectorChoice, selectorValue });
			// some columns need to be converted to selects

			TableEditor keywordChoiceEditor = new TableEditor(table);
			CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
			keywordChoiceCombo.setText("Choose..");
			for (String keyword : keywordTable.keySet()) {
				keywordChoiceCombo.add(keyword);
			}
			// NOTE: none of options is initially selected
			keywordChoiceEditor.grabHorizontal = true;
			int keywordChoiceColumn = 2;
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
			// java.lang.ClassCastException: java.util.LinkedHashMap$LinkedValues
			// cannot be cast to java.util.List
			selectorChoiceCombo.select(new ArrayList<String>(selectorFromSWD.values())
					.indexOf(selectorFromSWD.get(elementData.get("ElementSelectedBy"))));
			selectorChoiceEditor.grabHorizontal = true;
			int selectorChoiceColumn = 3;
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
			logger.info(String.format("Updating %s = %s", oldValue, newValue));
			if (selectorFromSWD.containsValue(newValue)) {
				((TableItem) combo.getData("item")).setText(column, newValue);
			}
		}
	}

	private static void appendBlankRowToTable(Table table, TableItem item,
			int index) {

		item.setText(new String[] { String.format("%d", index), "Element name",
				"Action keyword", "", "Selector value" });

		TableEditor keywordChoiceEditor = new TableEditor(table);
		CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
		keywordChoiceCombo.setText("Choose..");
		for (String keyword : keywordTable.keySet()) {
			keywordChoiceCombo.add(keyword);
		}
		// NOTE: none of options can be currently pre-selected
		keywordChoiceEditor.grabHorizontal = true;
		int keywordChoiceColumn = 2;
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
		int selectorChoiceColumn = 3;
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
			logger.info(String.format("Updating %s = %s", oldValue, newValue));
			if (keywordTable.containsKey(newValue)) {
				((TableItem) combo.getData("item")).setText(column, newValue);
			}
		}
	}

	// origin:
	// https://github.com/bp-FLN/SWT-Tools/blob/master/src/swt/TableTools.java
	public static void packTable(Table table) {
		for (TableColumn tc : table.getColumns()) {
			tc.pack();
		}
	}
}
