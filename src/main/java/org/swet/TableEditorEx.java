package org.swet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;

// origin : 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/TableEditorexample.htm
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableSimpleDemo.htm 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableEditor.htm
// http://www.java2s.com/Tutorial/Java/0280__SWT/TableCellEditorComboTextandButton.htm

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

public class TableEditorEx {

	private static Map<String, String> methodTable = new HashMap<>();
	static { // these are free-hand
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

	private static Map<String, Method> locatorTable = new HashMap<>();

	public static void main(String[] args) {

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
		for (int i = 0; i < 3; i++) {
			new TableItem(table, SWT.NONE);
		}
		TableItem[] items = table.getItems();

		for (int i = 0; i < 3; i++) {
			TableItem item = items[i];
			item.setText(new String[] { String.format("Element %d name", i),
					String.format("Action %d", i),
					String.format("Selector type of element %d", i),
					String.format("Selector %d", i) });

			TableEditor editor1 = new TableEditor(table);
			CCombo combo1 = new CCombo(table, SWT.NONE);
			combo1.setText("CHOOSE");
			for (String keyword : methodTable.values()) {
				combo1.add(keyword);
				System.err.println(keyword);
			}
			editor1.grabHorizontal = true;
			editor1.setEditor(combo1, items[i], 1);

			TableEditor editor2 = new TableEditor(table);
			CCombo combo2 = new CCombo(table, SWT.NONE);
			combo2.setText("CHOOSE");
			for (String locator : locatorTable.keySet()) {
				combo2.add(locator);
				System.err.println(locator);
			}
			editor2.grabHorizontal = true;
			editor2.setEditor(combo2, items[i], 2);

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
		display.dispose();
	}
}
