package org.swet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Listener;

import org.swet.RenderTemplate;
import org.swet.OSUtils;

/**
 * Session configuration editor form for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ConfigFormEx {

	private static Shell shell;
	private static Shell parentShell = null;
	private static Display display;
	private final static int formWidth = 656;
	private final static int formHeight = 248;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;
	private final static int labelWidth = 150;
	private static String osName = OSUtils.getOsName();
	private static HashMap<String, String> configData = new HashMap<String, String>();
	// NOTE: use the same DOM for Browser config options to simplify code
	// the values for "Browser" hash are not used
	private static HashMap<String, HashMap<String, String>> configOptions = new HashMap<String, HashMap<String, String>>();
	private static HashMap<String, String> templates = new HashMap<String, String>();

	ConfigFormEx(Display parentDisplay, Shell parent) {
		HashMap<String, String> browserOptions = new HashMap<String, String>();
		for (String browser : new ArrayList<String>(Arrays.asList(new String[] {
				"Chrome", "Firefox", "Internet Explorer", "Edge", "Safari" }))) {
			browserOptions.put(browser, "unused");
		}
		configOptions.put("Browser", browserOptions);
		configData.put("Template", "Core Selenium Java (embedded)");

		// TODO: Keep few twig templates embedded in the application jar and
		// make rest up to customer
		configOptions.put("Template", new HashMap<String, String>());
		String dirPath = String.format("%s/src/main/resources/templates",
				System.getProperty("user.dir"));
		templates = configOptions.get("Template");
		(new RenderTemplate()).listFilesForFolder(new File(dirPath), "embedded",
				templates);
		configOptions.replace("Template", templates);
		display = (parentDisplay != null) ? parentDisplay : new Display();
		// shell = new Shell(display);
		shell = new Shell(display, SWT.CENTER | SWT.SHELL_TRIM/* | ~SWT.RESIZE */);
		if (parent != null) {
			parentShell = parent;
		}
		// http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
		if (osName.toLowerCase().startsWith("windows")) {
			try {
				dirPath = OSUtils.getDesktopPath();
			} catch (Exception e) {
				dirPath = System.getProperty("user.home");
			}
		} else {
			dirPath = System.getProperty("user.home");
		}
		new Utils().readData(
				parent != null ? parentShell.getData("CurrentConfig").toString()
						: "{ \"Browser\": \"Chrome\", "
								+ "\"Template\": \"Core Selenium Java (embedded)\", "
								+ String.format("\"Template Directory\": \"%s\", ",
										dirPath.replace("/", "\\").replace("\\", "\\\\"))
								+ "\"Template Path\": \"\"}",
				Optional.of(configData));
		if (configData.containsKey("Template Directory")) {
			dirPath = configData.get("Template Directory");
			if (dirPath != "") {
				templates = configOptions.get("Template");
				(new RenderTemplate()).listFilesForFolder(new File(dirPath),
						"user defined", templates);
				configOptions.replace("Template", templates);
			}
		}
	}

	public void render() {
		shell.open();
		shell.setText("Session Configuration");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		shell.setLayout(gridLayout);

		GridComposite gridComposite = new GridComposite(shell);
		gridComposite.renderData(configData);
		gridComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		gridComposite.pack();

		RowComposite rowComposite = new RowComposite(shell);
		// rowComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		rowComposite.pack();
		shell.pack();
		shell.setSize(formWidth, formHeight);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private static class DirBrowseComposite extends Composite {

		public DirBrowseComposite(Composite composite) {
			super(composite, SWT.NONE);
			this.setSize(this.getParent().getBounds().width,
					this.getParent().getBounds().height /* buttonHeight*/ );
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout gridLayout = new GridLayout(3, true);
			gridLayout.makeColumnsEqualWidth = false;
			this.setLayout(gridLayout);
		}

		public void renderData(HashMap<String, String> data, String configKey) {

      Label valueLabel = new Label(this, SWT.NONE);
			valueLabel.setLayoutData(new GridData(labelWidth, SWT.DEFAULT));
			valueLabel.setText(configKey);

			final Text valueText = new Text(this, SWT.SINGLE | SWT.BORDER);

			GridData gridDatevalueText = new GridData(GridData.FILL,
					GridData.CENTER, true, false);
			gridDatevalueText.widthHint = this.getBounds().width - 70;
			gridDatevalueText.heightHint = buttonHeight;
			valueText.setLayoutData(gridDatevalueText);
			valueText.setData("key", configKey);
			if (data.containsKey(configKey)) {
				valueText.setText(data.get(configKey));
			}
			valueText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					Text text = (Text) event.widget;
					System.err.println(String.format("Updating %s = %s",
							(String) text.getData("key"), text.getText()));
					if (data.containsKey((String) text.getData("key"))) {
						data.replace((String) text.getData("key"), text.getText());
					} else {
						data.put((String) text.getData("key"), text.getText());
					}
				}
			});

			final Button browse = new Button(this, SWT.PUSH);
			GridData gridDataBrowse = new GridData(GridData.FILL,
					GridData.CENTER, false, false);
			gridDataBrowse.widthHint = 70;
			gridDataBrowse.heightHint = buttonHeight;
			browse.setLayoutData(gridDataBrowse);
			browse.setText("Browse");
			// ?? browse.setValue("Browse");

			browse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dialog = new DirectoryDialog(shell);

					dialog.setFilterPath(valueText.getText());
					dialog.setText(String.format("%s Dialog", configKey));
					dialog.setMessage("Select a directory");

					String dir = dialog.open();
					Text text = valueText;
					if (dir != null) {
						text.setText(dir);
						System.err.println(String.format("Browser: %s = %s",
								(String) text.getData("key"), text.getText()));
					}
				}
			});
		}
	}

	private static class RowComposite extends Composite {

		final Button buttonSave;

		public RowComposite(Composite composite) {
			super(composite, SWT.NO_FOCUS);

			this.setLayoutData(
					new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
			final GridLayout gridLayout = new GridLayout();
			gridLayout.marginWidth = 2;
			this.setLayout(new GridLayout(1, false));
			buttonSave = new Button(this, SWT.BORDER | SWT.PUSH);
			buttonSave.setText("Save");
			GridData gridDataSave = new GridData(GridData.FILL, GridData.CENTER,
					false, false);
			gridDataSave.widthHint = buttonWidth;
			gridDataSave.heightHint = buttonHeight;

			buttonSave.setLayoutData(gridDataSave);

			buttonSave.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					String templateLabel = configData.get("Template");
					if (templateLabel != "") {
						String templateAbsolutePath = configOptions.get("Template")
								.get(templateLabel);
						String configKey = "Template Path";
						if (configData.containsKey(configKey)) {
							configData.replace(configKey, templateAbsolutePath);
						} else {
							configData.put(configKey, templateAbsolutePath);
						}
						System.err.println(String.format(
								"Saving the path to the selected template \"%s\": \"%s\" \"%s\"",
								templateLabel, templateAbsolutePath,
								configData.get(configKey)));
					}
					String result = new Utils().writeDataJSON(configData, "{}");
					if (parentShell != null) {
						parentShell.setData("CurrentConfig", result);
						parentShell.setData("updated", true);
					} else {
						System.err.println("Updating the parent shell: " + result);
					}
					composite.dispose();
				}
			});
		}
	}

	private static class GridComposite extends Composite {

		public GridComposite(Composite composite) {
			super(composite, SWT.BORDER);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			this.setLayout(gridLayout);
		}

		public void renderData(HashMap<String, String> data) {
			for (String configKey : Arrays.asList("Browser", "Base URL",
					"Template Directory", "Template")) {
				if (configOptions.containsKey(configKey)) {
					final Label configLabel = new Label(this, SWT.NONE);
					configLabel.setText(configKey);

					final Combo configValue = new Combo(this, SWT.READ_ONLY);
					// Set<String> itemsSet = configOptions.get(configKey).keySet();
					// String[] items = (String[])itemsSet.toArray();
					String[] items = (String[]) configOptions.get(configKey).keySet()
							.toArray(new String[0]);
					configValue.setItems(items);
					System.err.println(String.format("Setting index of %s to %d",
							configData.get(configKey),
							Arrays.asList(items).indexOf(configData.get(configKey))));
					configValue
							.select(Arrays.asList(items).indexOf(configData.get(configKey)));
					configValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					configValue.setData("key", configKey);

					configValue.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							Combo o = (Combo) event.widget;
							data.replace((String) o.getData("key"), o.getText());
							// TODO: validation process
							if (configValue.getText().equals("Safari")) {
							} else {
								/*
								configValue.add("Not Applicable");
								configValue.setText("Not Applicable");
								*/
							}
						}
					});
					/*
						configValue.addModifyListener(new ModifyListener() {
						@Override
						public void modifyText(ModifyEvent event) {
							Combo o = (Combo) event.widget;
							data.replace((String) o.getData("key"), o.getText());
						}
					});
					
					*/
				} else {

					if (configKey.indexOf("Directory") >= 0) {

						DirBrowseComposite dirBrowseComposite3 = new DirBrowseComposite(
								this);
						GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
								true);
						gridData.horizontalSpan = 2;
						dirBrowseComposite3.setLayoutData(gridData);
						dirBrowseComposite3.renderData(data, configKey);
						dirBrowseComposite3.pack();
					} else {

						final Label configLabel = new Label(this, SWT.NONE);
						configLabel.setText(configKey);
						final Text configValue;
						configValue = new Text(this, SWT.SINGLE | SWT.BORDER);
						configValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
						if (data.containsKey(configKey)) {
							configValue.setText(data.get(configKey));
						} else {
							// configValue.setText(String.format("%s...", configKey));
						}
						configValue.setData("key", configKey);
						configValue.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event) {
								Text text = (Text) event.widget;
								System.err.println(String.format("%s = %s",
										(String) text.getData("key"), text.getText()));
								if (data.containsKey((String) text.getData("key"))) {
									data.replace((String) text.getData("key"), text.getText());
								} else {
									data.put((String) text.getData("key"), text.getText());
								}
							}
						});

					}
				}
			}
		}
	}

	public void setData(String key, String value) {
		configData.put(key, value);
	}

	public static void main(String[] arg) {
		ConfigFormEx o = new ConfigFormEx(null, null);
		o.render();
	}
}
