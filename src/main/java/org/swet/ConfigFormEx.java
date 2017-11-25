package org.swet;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import custom.swt.widgets.InfoPopup;

/**
 * Session configuration editor form for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ConfigFormEx {

	private static Shell shell;
	private static Shell parentShell = null;
	private static Display display;

	private final static int formWidth = 476;
	private final static int formHeight = 248;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;
	private final static int labelWidth = 150;

	private static Boolean debug = false;

	private static String osName = OSUtils.getOsName();
	private static Map<String, String> configData = new HashMap<>();
	private static Utils utils = Utils.getInstance();

	// NOTE: use the same DOM for Browser config options to simplify code
	// the values for "Browser" hash are not used
	private static Map<String, Map<String, String>> configOptions = new HashMap<>();
	private static Map<String, String> templates = new HashMap<>();

	ConfigFormEx(Display parentDisplay, Shell parent) {
		Map<String, String> browserOptions = new HashMap<>();
		for (String browser : new ArrayList<String>(Arrays.asList(new String[] {
				"Chrome", "Firefox", "Internet Explorer", "Edge", "Safari" }))) {
			browserOptions.put(browser, "unused");
		}
		configOptions.put("Browser", browserOptions);
		// offer templates embedded in the application jar and
		// make rest up to customer
		configData.put("Template", "Core Selenium Java (embedded)");

		configOptions.put("Template", new HashMap<String, String>());
		templates = configOptions.get("Template");
		// Scan the template directory and build the hash of template name / path
		// options.
		TemplateCache templateCache = TemplateCache.getInstance();
		templateCache.fillEmbeddedTemplateCache();
		templates.putAll(TemplateCache.getCache());
		configOptions.replace("Template", templates);

		display = (parentDisplay != null) ? parentDisplay : new Display();
		// shell = new Shell(display);
		shell = new Shell(display, SWT.CENTER | SWT.SHELL_TRIM/* | ~SWT.RESIZE */);
		if (parent != null) {
			parentShell = parent;
		}
		// http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
		String dirPath = null;
		dirPath = (osName.toLowerCase().startsWith("windows"))
				? OSUtils.getDesktopPath() : System.getProperty("user.home");

		utils.readData(
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
				// Scan the template directory and build the options hash with template
				// name / absolute path
				templateCache.fillTemplateDirectoryCache(new File(dirPath),
						"user defined", templates);

				configOptions.replace("Template", templates);
			}
		}
	}

	public void render() {
		shell.open();
		shell.setText("Session Configuration");

		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(shell);

		GridComposite gridComposite = new GridComposite(shell);
		gridComposite.renderData(configData);
		gridComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		gridComposite.pack();

		Composite rowComposite = new Composite(shell, SWT.NO_FOCUS);

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP)
				.applyTo(rowComposite);

		GridLayoutFactory.swtDefaults().margins(2, 2).equalWidth(false)
				.numColumns(2).applyTo(rowComposite);

		Button buttonSave = new Button(rowComposite, SWT.BORDER | SWT.PUSH);
		buttonSave.setText("Save");

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false).applyTo(buttonSave);

		buttonSave.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String templateLabel = configData.get("Template");
				String configKey = null;
				String data = null;
				if (templateLabel != ""
						&& !(templateLabel.matches(".*\\(embedded\\)"))) {
					data = configOptions.get("Template").get(templateLabel);
					configKey = "Template Path";
					System.err.println(String.format(
							"Saving the selected user template path \"%s\": \"%s\"",
							templateLabel, data));
				} else {
					configKey = "Template";
					data = templateLabel;
					System.err.println(String.format(
							"Saving the selected embedded template name: \"%s\"",
							templateLabel));

				}
				if (configData.containsKey(configKey)) {
					configData.replace(configKey, data);
				} else {
					configData.put(configKey, data);
				}
				String result = utils.writeDataJSON(configData, "{}");
				if (parentShell != null) {
					parentShell.setData("CurrentConfig", result);
					parentShell.setData("updated", true);
				}
				// System.err.println("Updated parent shell: " + result);
			}
		});

		Button buttonCancel = new Button(rowComposite, SWT.PUSH);
		buttonCancel.setText("Cancel");

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false)
				.applyTo(buttonCancel);

		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				rowComposite.dispose();
				shell.dispose();
			}
		});

		rowComposite.pack();
		shell.pack();
		shell.setSize(formWidth, formHeight);

		// infopopup example
		/*
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle shellBounds = shell.getBounds();
			InfoPopup infoPopup = new InfoPopup(shell, "test of the info popup");
			infoPopup.setPosition(new Point(shellBounds.x + shellBounds.width - 200,
					shellBounds.y + shellBounds.height - 30));
			infoPopup.open();
		*/
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private static class DirBrowseComposite extends Composite {

		private Shell shell;
		private int browseButtonWidth = 60;

		public DirBrowseComposite(Composite composite) {
			super(composite, SWT.NONE);
			shell = composite.getShell();
			this.setSize(this.getParent().getBounds().width,
					this.getParent().getBounds().height /* buttonHeight*/ );

			GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.TOP)
					.grab(true, false).applyTo(this);
			GridLayoutFactory.swtDefaults().equalWidth(false).numColumns(3)
					.applyTo(this);
		}

		public void renderData(Map<String, String> data, String configKey) {

			Label label = new Label(this, SWT.NONE);
			label.setLayoutData(new GridData(labelWidth, SWT.DEFAULT));
			label.setText(configKey);

			final Text directory = new Text(this, SWT.SINGLE | SWT.BORDER);

			GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true,
					false);
			gridData.widthHint = this.getBounds().width - browseButtonWidth;
			gridData.heightHint = buttonHeight;
			directory.setLayoutData(gridData);

			directory.setData("key", configKey);
			if (data.containsKey(configKey)) {
				directory.setText(data.get(configKey));
			}
			directory.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					Text sender = (Text) event.widget;
					String value = sender.getText();
					String key = (String) sender.getData("key");
					// System.err.println(String.format("Updating %s = %s",
					// (String) key, value));
					if (data.containsKey(key)) {
						data.replace(key, value);
					} else {
						data.put(key, value);
					}
				}
			});

			final Button browse = new Button(this, SWT.PUSH);
			gridData = new GridData(GridData.FILL, GridData.CENTER, false, false);
			gridData.widthHint = browseButtonWidth;
			gridData.heightHint = buttonHeight;
			browse.setLayoutData(gridData);
			browse.setText("Browse");
			browse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dialog = new DirectoryDialog(shell);

					dialog.setFilterPath(directory.getText());
					dialog.setText(String.format("%s Dialog", configKey));
					dialog.setMessage("Select a directory");

					String dir = dialog.open();
					Text text = directory;
					if (dir != null) {
						text.setText(dir);
						System.err.println(String.format("Browser: %s = %s",
								(String) text.getData("key"), text.getText()));
					}
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

		public void renderData(Map<String, String> data) {
			for (String configKey : Arrays.asList("Browser", "Base URL",
					"Template Directory", "Template")) {
				if (configOptions.containsKey(configKey)) {
					final Label configLabel = new Label(this, SWT.NONE);
					configLabel.setText(configKey);
					// http://www.codejava.net/java-se/swing/jcombobox-basic-tutorial-and-examples
					// http://stackoverflow.com/questions/19800033/java-swt-list-make-it-unselectable
					final Combo configValue = new Combo(this, SWT.READ_ONLY);
					String[] items = configOptions.get(configKey).keySet()
							.toArray(new String[0]);
					configValue.setItems(items);
					// System.err.println(String.format("Setting index of %s to %d",
					// configData.get(configKey),
					// Arrays.asList(items).indexOf(configData.get(configKey))));
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

	@SuppressWarnings("unused")
	public static void main(String[] arg) {
		ConfigFormEx configFormEx = new ConfigFormEx(null, null);
		ConfigFormEx.debug = true;
		configFormEx.render();
	}
}