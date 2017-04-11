package org.swet;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Element attribute editor form for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ComplexFormEx {

	private Shell shell;
	private String commandId;
	private Display display;
	private static Shell parentShell = null;
	private String dataKey = "CurrentCommandId";
	private final static int formWidth = 750;
	private final static int formHeight = 280;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28  ;
	private static Boolean updated = false;
	private static String result = null;
	private static HashMap<String, String> elementData = new HashMap<String, String>(); // empty

	ComplexFormEx(Display parentDisplay, Shell parent) {
		display = (parentDisplay != null) ? parentDisplay : new Display();
		shell = new Shell(display);
		if (parent != null) {
			parentShell = parent;
			commandId = parent.getData(dataKey).toString();
			// parent sets the elementData explicitly
		}
		new Utils().readData(Optional.of(elementData));
		if (!elementData.containsKey("ElementSelectedBy")) {
			elementData.put("ElementSelectedBy", "none");
		}
	}

	public void render() {
		shell.open();
		shell.setText(String.format("Element Locators", commandId));
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		shell.setLayout(gl);
		final Label titleData = new Label(shell, SWT.SINGLE | SWT.NONE);
		titleData
				.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		titleData.setText((elementData.containsKey("ElementCodeName"))
				? elementData.get("ElementCodeName") : "Element selector details");

		GridComposite gridComposite = new GridComposite(shell);
		gridComposite.renderData(elementData);
		gridComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		gridComposite.pack();

		RowComposite rowComposite = new RowComposite(shell);
		rowComposite.pack();
		shell.pack();
		shell.setSize(formWidth, formHeight);

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (updated) {
					if (parentShell != null) {
						// NOTE: not currently reached
						parentShell.setData("result", result);
						parentShell.setData("updated", true);
					} else {
						System.err
								.println("Handle Close: updating the parent shell: " + result);
					}
				}
				shell.dispose();
			}
		});

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private static class RowComposite extends Composite {

		private final Button buttonSave;
		private final Button buttonDelete;
		private final Button buttonCancel;

		public RowComposite(Composite composite) {

			super(composite, SWT.NO_FOCUS);
			this.setLayoutData(
					new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
			final GridLayout gridLayout = new GridLayout();
			gridLayout.marginWidth = 2;
			this.setLayout(new GridLayout(3, false));
      /*
			RowLayout rowLayout = new RowLayout();
			rowLayout.wrap = false;
			rowLayout.pack = false;
			this.setLayout(rowLayout);
      */
			buttonSave = new Button(this, SWT.BORDER | SWT.PUSH);
			buttonSave.setText("Save");
      GridData gridDataSave = new GridData(GridData.FILL, GridData.CENTER,
					false, false);
			gridDataSave.widthHint = buttonWidth;
			gridDataSave.heightHint = buttonHeight;
			buttonSave.setLayoutData(gridDataSave);

			buttonDelete = new Button(this, SWT.PUSH);
			buttonDelete.setText("Delete");
      GridData gridDataDelete = new GridData(GridData.FILL, GridData.CENTER,
					false, false);
			gridDataDelete.widthHint = buttonWidth;
			gridDataDelete.heightHint = buttonHeight;
			buttonDelete.setLayoutData(gridDataDelete);
			buttonDelete.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (parentShell != null) {
						// deleting the element
						parentShell.setData("result", "{}");
						parentShell.setData("updated", true);
					}
					composite.dispose();
				}
			});

			buttonSave.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					result = new Utils().writeDataJSON(elementData, "{}");
					updated = true;
					if (parentShell != null) {
						if (result != "{}") {
							// System.err.println("Handle OK: updating the parent shell: " +
							// result);
							parentShell.setData("result", result);
							parentShell.setData("updated", true);
						}
					}
					composite.dispose();
				}
			});

			buttonCancel = new Button(this, SWT.PUSH);
			buttonCancel.setText("Cancel");
      GridData gridDataCancel = new GridData(GridData.FILL, GridData.CENTER,
					false, false);
			gridDataCancel.widthHint = buttonWidth;
			gridDataCancel.heightHint = buttonHeight;
			buttonCancel.setLayoutData(gridDataCancel);

			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					composite.dispose();
				}
			});

			// context menu
			buttonCancel.addMenuDetectListener(new MenuDetectListener() {
				@Override
				public void menuDetected(MenuDetectEvent event) {
					// System.err.println("Context menu ");
				}
			});
		}
	}

	private static class GridComposite extends Composite {

		private final static int labelWidth = 70;

		public GridComposite(Composite composite) {
			super(composite, SWT.BORDER);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			this.setLayout(gridLayout);
		}

		private static void doSelection(Button button) {
			if (button.getSelection()) {
				String selectedKey = (String) button.getData("key");
				if (selectedKey != null && selectedKey != ""
						&& elementData.containsKey(selectedKey)) {
					// System.out.println("Process selection of key " + selectedKey);
					elementData.replace("ElementSelectedBy", selectedKey);
				} else {
					// System.out.println(
					// 		String.format("Skip processing of key '%s'", selectedKey));
				}
				// } else {
				// System.out.println("do work for deselection " + button);
			}
			/*
			  // Java 6 style
			  idRadio.addListener(SWT.Selection, new Listener() {
			    public void handleEvent(Event event) {
			      switch (event.type) {
			      case SWT.Selection:
			        Button button = ((Button) event.widget);
			        if (button.getSelection()) {
			          System.out.println(button.getText() + " selected (*)");
			        }
			        break;
			      }
			    }
			  });
			*/
		}

		public void renderData(HashMap<String, String> data) {

			List<String> locators = Arrays.asList("ElementCssSelector",
					"ElementXPath", "ElementId", "ElementText");
			Listener listener = new Listener() {
				public void handleEvent(Event event) {
					doSelection(((Button) event.widget));
				}
			};
			for (String locatorKey : locators) {
				if (data.containsKey(locatorKey)) {
					// label the radio
					String locatorKeyLabel = locatorKey.replace("Element", "");
					final Button locatorRadio = new Button(this, SWT.RADIO);
					locatorRadio.setSelection(true);
					locatorRadio.setText(locatorKeyLabel);
					locatorRadio.setData("key", locatorKey);
					locatorRadio.setSelection(
							locatorKey.contains((String) data.get("ElementSelectedBy")));

					locatorRadio.setLayoutData(new GridData(labelWidth, SWT.DEFAULT));
					locatorRadio.addListener(SWT.Selection, listener);

					final Text locatorValue = new Text(this, SWT.SINGLE | SWT.BORDER);
					locatorValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					String value = data.get(locatorKey);
					if (value.length() > 0) {
						locatorValue.setText(data.get(locatorKey));
					} else {
						locatorValue.setEnabled(false);
						locatorRadio.setEnabled(false);
					}

					locatorValue.setData("key", locatorKey);
					locatorValue.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent event) {
							Text text = (Text) event.widget;
							data.replace((String) text.getData("key"), text.getText());
						}
					});
				} else {

					final Button locatorRadio = new Button(this, SWT.RADIO);
					String locatorKeyLabel = locatorKey.replace("Element", "");
					locatorRadio.setSelection(false);
					locatorRadio.setLayoutData(new GridData(labelWidth, SWT.DEFAULT));
					locatorRadio.setText(locatorKeyLabel);
					locatorRadio.setData("key", locatorKey);
					locatorRadio.setSelection(
							locatorKey.contains((String) data.get("ElementSelectedBy")));
					locatorRadio.addListener(SWT.Selection, listener);

					final Text locatorValue;
					locatorValue = new Text(this, SWT.SINGLE | SWT.BORDER);
					locatorValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					locatorValue.setText(String.format("%s...", locatorKeyLabel));
					locatorValue.setData("key", locatorKey);
					locatorValue.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent event) {
							Text text = (Text) event.widget;
							// System.err.println(text.getText());
						}
					});
				}
			}
		}
	}

	public void setData(String key, String value) {
		elementData.put(key, value);
	}

	public static void main(String[] arg) {
		ComplexFormEx o = new ComplexFormEx(null, null);
		o.render();
	}
}
