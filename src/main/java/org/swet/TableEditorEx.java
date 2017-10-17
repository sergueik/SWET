package org.swet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openqa.selenium.By;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
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

import org.swet.YamlHelper;

/**
 * TestSuite Excel export Table Viewer class for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
public class TableEditorEx {

	static Display display;
	static Shell shell;
	private static Shell parentShell = null;


	private Menu menuBar, fileMenu, helpMenu;
	private MenuItem fileMenuHeader, helpMenuHeader;
	private MenuItem fileExitItem, fileSaveItem, helpGetHelpItem;
	private static Label label;

	private static Map<String, String> configData = new HashMap<>();

  // Converting legacy SWD "Element selected By" keys to
  // selectorTable keys
	private static Map<String, String> elementSelectedByToselectorChoiceTable = new HashMap<>();
	static {
		elementSelectedByToselectorChoiceTable.put("ElementXPath", "xpath");
		elementSelectedByToselectorChoiceTable.put("ElementCssSelector",
				"cssSelector");
		elementSelectedByToselectorChoiceTable.put("ElementText", "text");
		elementSelectedByToselectorChoiceTable.put("ElementId", "id");
		// TODO:
		elementSelectedByToselectorChoiceTable.put("ElementLinkText", "linkText");
		elementSelectedByToselectorChoiceTable.put("ElementTagName", "tagName");
	}

  // Currently free-hand, may become discoverable methods of
  // keyword-driven framework class
	private static Map<String, String> methodTable = new HashMap<>();
	static {
		methodTable.put("CLICK", "clickButton");
		methodTable.put("CLICK_BUTTON", "clickButton");
		methodTable.put("CLICK_CHECKBOX", "clickCheckBox");
		methodTable.put("CLICK_LINK", "clickLink");
		methodTable.put("CLICK_RADIO", "clickRadioButton");
		methodTable.put("CLOSE_BROWSER", "closeBrowser");
		methodTable.put("CREATE_BROWSER", "openBrowser");
		methodTable.put("ELEMENT_PRESENT", "elementPresent");
		methodTable.put("GET_ATTR", "getElementAttribute");
		methodTable.put("GET_TEXT", "getElementText");
		methodTable.put("GOTO_URL", "navigateTo");
		methodTable.put("SELECT_OPTION", "selectDropDown");
		methodTable.put("SET_TEXT", "enterText");
		methodTable.put("SEND_KEYS", "enterText");
		methodTable.put("SWITCH_FRAME", "switchFrame");
		methodTable.put("VERIFY_ATTR", "verifyAttribute");
		methodTable.put("VERIFY_TEXT", "verifyText");
		methodTable.put("CLEAR_TEXT", "clearText");
		methodTable.put("WAIT", "wait");
	}

	private static Map<String, Map<String, String>> testData = new HashMap<>();
	private static LinkedHashMap<String, Integer> sortedElementSteps = new LinkedHashMap<>();
	private static Map<String, Integer> elementSteps = new HashMap<>();
	private static Map<String, Method> selectorChoiceTable = new HashMap<>();
	private static Map<String, String> elementData = new HashMap<>();
	private static Configuration testCase = null;
	private static String testSutePath; // TODO: rename
	private static String yamlFilePath = null;

	TableEditorEx(Display parentDisplay, Shell parent) {
		display = (parentDisplay != null) ? parentDisplay : new Display();
		shell = new Shell(display);

		if (parent != null) {
			parentShell = parent;
			// parent sets the elementData explicitly
		}
		try {
			// NOTE: values of selectorChoiceTable are never used
			selectorChoiceTable.put("cssSelector",
					By.class.getMethod("cssSelector", String.class));
			selectorChoiceTable.put("xpath",
					By.class.getMethod("xpath", String.class));
			selectorChoiceTable.put("id", By.class.getMethod("id", String.class));
			selectorChoiceTable.put("linkText",
					By.class.getMethod("linkText", String.class));
			selectorChoiceTable.put("name", By.class.getMethod("name", String.class));
			// "text" is achieved indirectly.
			selectorChoiceTable.put("text",
					By.class.getMethod("xpath", String.class));
		} catch (NoSuchMethodException e) {
		}

	}

	public void render() {
		if (yamlFilePath != null) {
			System.err.println("Loading " + yamlFilePath);
			testCase = YamlHelper.loadConfiguration(yamlFilePath);
			testData = testCase.getElements();
			YamlHelper.printConfiguration(testCase);
		}
		elementSteps = testData.keySet().stream().collect(Collectors.toMap(o -> o,
				o -> Integer.parseInt(testData.get(o).get("ElementStepNumber"))));
		sortedElementSteps = sortByValue(elementSteps);
		/*
		for (String stepId : sortedElementSteps.keySet()) {
			elementData = testData.get(stepId);
			System.out.println(String.format("Loading step %d(%s) %s %s %s",
					Integer.parseInt(elementData.get("ElementStepNumber")),
					elementData.get("CommandId"), elementData.get("ElementCodeName"),
					elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy"))));
		}
		*/

		// display = new Display();
		// shell = new Shell(display);
		shell.setLayout(new FormLayout()); // new FillLayout());
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

		MenuItem fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
		fileExitItem.setText("E&xit");

		MenuItem helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText("&Help");

		Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		helpMenuHeader.setMenu(helpMenu);

		MenuItem helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
		helpGetHelpItem.setText("&Get Help");

		fileExitItem.addSelectionListener(new fileExitItemListener());
		fileSaveItem.addSelectionListener(new fileSaveItemListener());
		// helpGetHelpItem.addSelectionListener(new helpGetHelpItemListener());

		shell.setMenuBar(menuBar);

		//
		final Table table = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);

		table.setHeaderVisible(true);
		String[] titles = { "Element", "Action Keyword", "Selector Choice",
				"Selector Value", "Param 1", "Param 2", "Param 3" };

		for (int titleItem = 0; titleItem < titles.length; titleItem++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[titleItem]);
		}

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}

		int blankRows = 1;
		int tableSize = sortedElementSteps.keySet().size();
		for (int i = 0; i < tableSize + blankRows; i++) {
			new TableItem(table, SWT.NONE);
		}

		TableItem[] items = table.getItems();

		appendRowToTable(table, sortedElementSteps);

		for (int i = tableSize; i < tableSize + blankRows; i++) {
			TableItem item = items[i];
			appendBlankRowToTable(table, item, i);
		}

		for (int titleItem = 0; titleItem < titles.length; titleItem++) {
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
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		shell.dispose();
	}

	static class fileExitItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {
			shell.close();
			shell.dispose();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			shell.close();
			shell.dispose();
		}
	}

	static class fileSaveItemListener implements SelectionListener {
		public void widgetSelected(SelectionEvent event) {

			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterNames(new String[] { "Excel Files", "All Files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.xls", "*.*" });
			String homeDir = System.getProperty("user.home");
			dialog.setFilterPath(homeDir); // Windows path
			String path = null;
			if (testSutePath != null) {
				dialog.setFileName(testSutePath);
				path = new String(testSutePath);
			} //
			testSutePath = dialog.open();
			if (testSutePath != null) {
				System.out
						.println(String.format("Saved to \"%s\"", testSutePath));
			} else {
				if (path != null) {
					testSutePath = new String(path);
				}
			}
			label.setText(String.format("Saved to \"%s\"", testSutePath));
			label.update();
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			label.setText("Saved");
			label.update();
		}
	}

	private static void appendRowToTable(Table table,
			LinkedHashMap<String, Integer> steps) {

		TableItem[] items = table.getItems();
		int cnt = 0;
		for (String stepId : sortedElementSteps.keySet()) {

			// Append row into the TableEditor
			TableItem item = items[cnt];
			elementData = testData.get(stepId);
			System.out.println(String.format("Loading step %d(%s) %s %s %s",
					Integer.parseInt(elementData.get("ElementStepNumber")),
					elementData.get("CommandId"), elementData.get("ElementCodeName"),
					elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy"))));

			item.setText(new String[] { elementData.get("ElementCodeName"),
					String.format("Action %d", cnt), elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy")) });

			// some columns require combo selects

			TableEditor keywordChoiceEditor = new TableEditor(table);
			CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
			for (String keyword : methodTable.values()) {
				keywordChoiceCombo.add(keyword);
				// System.err.println(keyword);
			}
			int keywordChoiceCombo_select = new ArrayList<String>(
					methodTable.values()).indexOf("clickLink");
			// System.err.println("Selecting: " + keywordChoiceCombo_select);
			keywordChoiceCombo.select(keywordChoiceCombo_select);
			keywordChoiceEditor.grabHorizontal = true;
			keywordChoiceEditor.setEditor(keywordChoiceCombo, item, 1);

			TableEditor selectorChoiceEditor = new TableEditor(table);
			CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
			for (String locator : selectorChoiceTable.keySet()) {
				selectorChoiceCombo.add(locator);
				// System.err.println(locator);
			}
			int selectorChoiceCombo_select = new ArrayList<String>(
					selectorChoiceTable.keySet())
							.indexOf(elementSelectedByToselectorChoiceTable
									.get(elementData.get("ElementSelectedBy")));
			System.err.println(String.format("Selecting: %d for %s",
					selectorChoiceCombo_select, elementSelectedByToselectorChoiceTable
							.get(elementData.get("ElementSelectedBy"))));
			selectorChoiceCombo.select(selectorChoiceCombo_select);
			selectorChoiceEditor.grabHorizontal = true;
			selectorChoiceEditor.setEditor(selectorChoiceCombo, item, 2);

			cnt = cnt + 1;
		}
		return;
	}

	private static void appendBlankRowToTable(Table table, TableItem item,
			int index) {

		item.setText(new String[] { String.format("Element %d name", index),
				String.format("Action keyword %d", index), "",
				String.format("Selector value", index) });

		TableEditor keywordChoiceEditor = new TableEditor(table);
		CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
		keywordChoiceCombo.setText("Choose");
		for (String keyword : methodTable.values()) {
			keywordChoiceCombo.add(keyword);
		}
		keywordChoiceEditor.grabHorizontal = true;
		keywordChoiceEditor.setEditor(keywordChoiceCombo, item, 1);

		TableEditor selectorChoiceEditor = new TableEditor(table);
		CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
		selectorChoiceCombo.setText("Choose");
		for (String locator : selectorChoiceTable.keySet()) {
			selectorChoiceCombo.add(locator);
		}
		selectorChoiceEditor.grabHorizontal = true;
		selectorChoiceEditor.setEditor(selectorChoiceCombo, item, 2);
		return;

	}

	public void setData(String key, String value) {
		new Utils().readData(value, Optional.of(configData));
		testData.put(key, configData);
	}

	public static void main(String[] args) {
		yamlFilePath = (args.length == 0)
				? String.format("%s/%s", System.getProperty("user.dir"), "sample.yaml")
				: args[0];
		TableEditorEx o = new TableEditorEx(null, null);
		o.render();
		display.dispose();
	}

	// TODO: move to Utils.java
	// sorting example from
	// http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
			Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new));
	}
}
