package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2018 Serguei Kouzmine
 */

import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Category;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.github.sergueik.swet.Utils;

/**
 * Element attribute editor form for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
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
	private final static int buttonHeight = 28;
	private static Boolean updated = false;
	private static String result = null;
	private static Map<String, String> elementData = new HashMap<>();
	private static Utils utils = Utils.getInstance();

	@SuppressWarnings("deprecation")
	static final Category logger = Category.getInstance(ComplexFormEx.class);
	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);

	// selectorTable keys
	private static Map<String, String> mapSWD2CoreSelenium = new HashMap<>();
	static {
		mapSWD2CoreSelenium.put("ElementXPath", "xpath");
		mapSWD2CoreSelenium.put("ElementCssSelector", "cssSelector");
		mapSWD2CoreSelenium.put("ElementText", "text");
		mapSWD2CoreSelenium.put("ElementId", "id");
		// TODO:
		mapSWD2CoreSelenium.put("ElementLinkText", "linkText");
		mapSWD2CoreSelenium.put("ElementTagName", "tagName");
	}

	ComplexFormEx(Display parentDisplay, Shell parent) {
		utils.initializeLogger();
		logger.info("Initialized logger.");
		display = (parentDisplay != null) ? parentDisplay : new Display();
		shell = new Shell(display);
		if (parent != null) {
			parentShell = parent;
			commandId = parent.getData(dataKey).toString();
			// parent sets the elementData explicitly
		}
		utils.readData(Optional.of(elementData));
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
						logger.info("Updating parent: " + result);
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

			// final GridLayout gridLayout = new GridLayout();
			// gridLayout.marginWidth = 2;
			// this.setLayout(new GridLayout(3, false));

			GridLayoutFactory.swtDefaults().numColumns(3).margins(2, 2).applyTo(this);

			/*
			RowLayout rowLayout = new RowLayout();
			rowLayout.wrap = false;
			rowLayout.pack = false;
			this.setLayout(rowLayout);
			*/
			buttonSave = new Button(this, SWT.BORDER | SWT.PUSH);
			buttonSave.setText("Save");

			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
					.hint(buttonWidth, buttonHeight).grab(false, false)
					.applyTo(buttonSave);

			buttonDelete = new Button(this, SWT.PUSH);
			buttonDelete.setText("Delete");

			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
					.hint(buttonWidth, buttonHeight).grab(false, false)
					.applyTo(buttonDelete);

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
					result = utils.writeDataJSON(elementData, "{}");
					updated = true;
					if (parentShell != null) {
						if (result != "{}") {
							logger.info("Updating parent: " + result);
							parentShell.setData("result", result);
							parentShell.setData("updated", true);
						}
					}
					composite.dispose();
				}
			});

			buttonCancel = new Button(this, SWT.PUSH);
			buttonCancel.setText("Cancel");

			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
					.hint(buttonWidth, buttonHeight).grab(false, false)
					.applyTo(buttonCancel);

			buttonCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					composite.dispose();
				}
			});

			// context menu
			buttonCancel.addMenuDetectListener(new MenuDetectListener() {
				@Override
				public void menuDetected(MenuDetectEvent event) {
					// logger.debug("Context menu ");
				}
			});
		}
	}

	private static class GridComposite extends Composite {

		private final static int labelWidth = 96;

		public GridComposite(Composite composite) {
			super(composite, SWT.BORDER);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			this.setLayout(gridLayout);
		}

		private static void doSelection(Button button) {
			if (button.getSelection()) {
				String key = (String) button.getData("key");
				if (key != null && key != "" && elementData.containsKey(key)) {
					elementData.replace("ElementSelectedBy", key);
					logger.info("Set ElementSelectedBy: " + key);
				} else {
					// System.out.println(
					// String.format("Skip processing of key '%s'", selectedKey));
				}
			}
			/*
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

		public void renderData(Map<String, String> data) {

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
					// TODO: font size
					String locatorKeyLabel = mapSWD2CoreSelenium.get(locatorKey);
					final Button locatorRadio = new Button(this, SWT.RADIO);
					locatorRadio.setSelection(true);
					locatorRadio.setText(locatorKeyLabel);
					locatorRadio.setData("key", locatorKey);
					locatorRadio
							.setSelection(locatorKey.contains(data.get("ElementSelectedBy")));

					GridDataFactory.swtDefaults().hint(labelWidth, buttonHeight)
							.applyTo(locatorRadio);
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
					// see also:
					// http://blog.vogella.com/2013/11/21/the-small-details-in-life-the-focus-restore/
					locatorValue.addListener(SWT.FocusOut, new Listener() {
						@Override
						public void handleEvent(Event event) {
							Text text = (Text) event.widget;
							logger.info(String.format("%s = %s", (String) text.getData("key"),
									text.getText()));
						}
					});

				} else {

					final Button locatorRadio = new Button(this, SWT.RADIO);
					String locatorKeyLabel = mapSWD2CoreSelenium.get(locatorKey);
					locatorRadio.setSelection(false);
					GridDataFactory.swtDefaults().hint(labelWidth, buttonHeight)
							.applyTo(locatorRadio);

					// locatorRadio.setLayoutData(new GridData(labelWidth, SWT.DEFAULT));
					locatorRadio.setText(locatorKeyLabel);
					locatorRadio.setData("key", locatorKey);
					locatorRadio
							.setSelection(locatorKey.contains(data.get("ElementSelectedBy")));
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
							// logger.debug(text.getText());
						}
					});
				}
			}
		}
	}

	public void setData(String key, String value) {
		elementData.put(key, value);
	}

	@SuppressWarnings("unused")
	public static void main(String[] arg) {
		ComplexFormEx o = new ComplexFormEx(null, null);
		o.render();
	}
}
