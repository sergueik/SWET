package org.swet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.openqa.selenium.By;

import org.swet.YamlHelper;

// origin : 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/TableEditorexample.htm
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableSimpleDemo.htm 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableEditor.htm
// http://www.java2s.com/Tutorial/Java/0280__SWT/TableCellEditorComboTextandButton.htm
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesTableEditor.htm
// http://www.java2s.com/Tutorial/Java/0280__SWT/UsingTableEditor.htm

public class TableEditorEx {

	private static Map<String, String> elementSelectedByToLocatorTable = new HashMap<>();
	static {
		// temporarily converting legacy SWD "Element selected By" keys to selectorTable keys
		elementSelectedByToLocatorTable.put("ElementXPath", "xpath");
		elementSelectedByToLocatorTable.put("ElementCssSelector", "cssSelector");
		elementSelectedByToLocatorTable.put("ElementText", "text");
		elementSelectedByToLocatorTable.put("ElementId", "id");
		// TODO:
		// elementSelectedByToLocatorTable.put("ElementLinkText", "linkText");
		elementSelectedByToLocatorTable.put("ElementTagName", "tagName");
	}
	private static Map<String, String> methodTable = new HashMap<>();
	static {
		// these are currently free-hand, would become discoverable methods of
		// keyword-driven framework class
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

	private static Configuration testCase = null;
	private static Map<String, HashMap<String, String>> testData = new HashMap<>();
	private static LinkedHashMap<String, Integer> sortedElementSteps = new LinkedHashMap<>();
	private static Map<String, Integer> elementSteps = new HashMap<>();
	private static Map<String, Method> locatorTable = new HashMap<>();
	private static Map<String, String> elementData = new HashMap<>();
	private static String yamlFilePath = null;

	public static void main(String[] args) {

		yamlFilePath = (args.length == 0)
				? String.format("%s/%s", System.getProperty("user.dir"), "sample.yaml")
				: args[0];

		if (yamlFilePath != null) {
			System.err.println("Loading " + yamlFilePath);
			testCase = YamlHelper.loadConfiguration(yamlFilePath);
			testData = testCase.getElements();
			YamlHelper.printConfiguration(testCase);
		}

		elementSteps = testData.keySet().stream().collect(Collectors.toMap(o -> o,
				o -> Integer.parseInt(testData.get(o).get("ElementStepNumber"))));
		sortedElementSteps = sortByValue(elementSteps);
		for (String stepId : sortedElementSteps.keySet()) {
			elementData = testData.get(stepId);
			// Append row into the TableEditor
			System.out.println(String.format("Loading step %d(%s) %s %s %s",
					Integer.parseInt(elementData.get("ElementStepNumber")),
					elementData.get("CommandId"), elementData.get("ElementCodeName"),
					elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy"))));
		}
		try {
			locatorTable.put("cssSelector",
					By.class.getMethod("cssSelector", String.class));
			locatorTable.put("xpath", By.class.getMethod("xpath", String.class));
			locatorTable.put("id", By.class.getMethod("id", String.class));
			locatorTable.put("name", By.class.getMethod("name", String.class));
		} catch (NoSuchMethodException e) {
		}

		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		//
		final Table table = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);

		table.setHeaderVisible(true);
		String[] titles = { "Element", "Action Keyword", "SelectorType", "Selector",
				"Param 1", "Param 2", "Param 3" };

		for (int titleItem = 0; titleItem < titles.length; titleItem++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[titleItem]);
		}

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}
		int blankRows = 3;
		int tableSize = sortedElementSteps.keySet().size();

		for (int i = 0; i < tableSize + blankRows; i++) {
			new TableItem(table, SWT.NONE);
		}
		/*
		TableItem[] items = table.getItems();
				for (int i = 0; i < blankRows; i++) {
					TableItem item = items[i];
					appendBlankRowToTable(table, item, i);
				}
		*/
		appendRowToTable(table, sortedElementSteps);

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
		display.dispose();
	}

	private static void appendRowToTable(Table table,
			LinkedHashMap<String, Integer> steps) {

		TableItem[] items = table.getItems();
		int cnt = 0;
		for (String stepId : sortedElementSteps.keySet()) {

			TableItem item = items[cnt];
			elementData = testData.get(stepId);
			// Append row into the TableEditor
			/*
			System.out.println(String.format("Loading step %d %s", stepId,
					elementData.get("ElementCodeName")));
			    */
			item.setText(new String[] { elementData.get("ElementCodeName"),
					String.format("Action %d", cnt), elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy")) });

			TableEditor editor2 = new TableEditor(table);
			CCombo combo2 = new CCombo(table, SWT.NONE);
			combo2.setText("CHOOSE");
			for (String locator : locatorTable.keySet()) {
				combo2.add(locator);
				// System.err.println(locator);
			}
			int combo2_select = new ArrayList<String>(locatorTable.keySet())
					.indexOf(elementSelectedByToLocatorTable
							.get(elementData.get("ElementSelectedBy")));
			System.err.println(String.format("Selecting: %d for %s", combo2_select,
					elementSelectedByToLocatorTable
							.get(elementData.get("ElementSelectedBy"))));
			combo2.select(combo2_select);
			editor2.grabHorizontal = true;
			editor2.setEditor(combo2, item, 2);

			cnt = cnt + 1;
		}

		return;

	}

	private static void appendBlankRowToTable(Table table, TableItem item,
			int index) {

		item.setText(new String[] { String.format("Element %d name", index),
				String.format("Action %d", index),
				String.format("Selector type of element %d", index),
				String.format("Selector %d", index) });

		TableEditor editor1 = new TableEditor(table);
		CCombo combo1 = new CCombo(table, SWT.NONE);
		combo1.setText("CHOOSE");
		for (String keyword : methodTable.values()) {
			combo1.add(keyword);
			// System.err.println(keyword);
		}
		// combo1.select(2);
		int combo1_select = new ArrayList<String>(methodTable.values())
				.indexOf("clickLink");
		System.err.println("Selecting: " + combo1_select);
		combo1.select(combo1_select);
		editor1.grabHorizontal = true;
		editor1.setEditor(combo1, item, 1);

		TableEditor editor2 = new TableEditor(table);
		CCombo combo2 = new CCombo(table, SWT.NONE);
		combo2.setText("CHOOSE");
		for (String locator : locatorTable.keySet()) {
			combo2.add(locator);
			// System.err.println(locator);
		}
		int combo2_select = new ArrayList<String>(locatorTable.keySet())
				.indexOf("xpath");
		combo2.select(combo2_select);
		editor2.grabHorizontal = true;
		editor2.setEditor(combo2, item, 2);
		return;

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
