package com.github.sergueik.swet;

/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.awt.Dimension;
import java.awt.Toolkit;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.github.sergueik.swet.ConfigData;
import com.github.sergueik.swet.ConfigDataSerializer;

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
	private static Map<String, String> configDataMap = new HashMap<>();
	private static Utils utils = Utils.getInstance();

	// NOTE: use the same DOM for Browser config options to simplify code
	// the values for "Browser" hash are not used
	private static Map<String, Map<String, String>> configOptions = new HashMap<>();
	private static Map<String, String> templates = new HashMap<>();

	@SuppressWarnings("deprecation")
	static final Logger logger = (Logger) Logger.getInstance(ConfigFormEx.class);
	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);

	static Map<String, String> browserApps = new HashMap<>();

	static {

		if (osName.matches("mac os x")) {
			browserApps.put("Chrome", "Google Chrome");
			browserApps.put("Firefox", "Firefox");
			browserApps.put("Safari", "Safari");
		} else if (osName.matches("windows")) {
			browserApps.put("Chrome", "chrome.exe");
			browserApps.put("Firefox", "firefox.exe");
			browserApps.put("Internet Explorer", "iexplore.exe");
			browserApps.put("Safari", "safari.exe");
		} else {
			browserApps.put("Chrome", "google-chrome");
			browserApps.put("Firefox", "firefox");
		}
	}

	ConfigFormEx(Display parentDisplay, Shell parent) {

		utils.initializeLogger();
		logger.info("Initialized logger.");

		Map<String, String> browserOptions = new HashMap<>();
		/*
		for (String browser : new ArrayList<String>(Arrays.asList(new String[] {
				"Chrome", "Firefox", "Internet Explorer", "Edge", "Safari" }))) {
			browserOptions.put(browser, "");
		}
		
		 */

		if (osName.matches("windows")) {
			List<String> foundBrowsers = OSUtils.findInstalledBrowsers(); // sets
																																		// is64bit
			logger.info("Found Browsers: " + foundBrowsers);
			for (String value : new ArrayList<String>(Arrays.asList(new String[] {
					"Chrome", "Firefox", "Internet Explorer", "Safari" }))) {
				if (foundBrowsers.contains((Object) browserApps.get(value))) {
					browserOptions.put(value, "");
				}
			}
		} else {
			// os x, Linux
			for (String value : new ArrayList<String>(Arrays.asList(new String[] {
					"Chrome", "Firefox", "Internet Explorer", "Safari" }))) {
				if (osName.matches("mac os x")) {
					if (OSUtils.findAppInPath(browserApps.get(value) + ".app")) {
						browserOptions.put(value, "");
					}
				} else {
					if (OSUtils.findAppInPath(browserApps.get(value))) {
						browserOptions.put(value, "");
					}
				}
			}
		}
		System.err
				.println("Broswer options: " + new ArrayList(browserOptions.keySet()));

		configOptions.put("Browser", browserOptions);
		// offer templates embedded in the application jar and
		// make rest up to customer
		configDataMap.put("Template", "Core Selenium Java (embedded)");

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
		dirPath = osName.startsWith("windows") ? OSUtils.getDesktopPath()
				: System.getProperty("user.home");

		utils.readData(
				parent != null ? parentShell.getData("CurrentConfig").toString()
						: "{ \"Browser\": \"Chrome\", "
								+ "\"Template\": \"Core Selenium Java (embedded)\", "
								+ String.format("\"Template Directory\": \"%s\", ",
										dirPath.replace("/", "\\").replace("\\", "\\\\"))
								+ "\"Template Path\": \"\"}",
				Optional.of(configDataMap));
		if (configDataMap.containsKey("Template Directory")) {
			dirPath = configDataMap.get("Template Directory");
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
		gridComposite.renderData(configDataMap);
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
				String templateLabel = configDataMap.get("Template");
				String configKey = null;
				String data = null;
				if (templateLabel != ""
						&& !(templateLabel.matches(".*\\(embedded\\)"))) {
					data = configOptions.get("Template").get(templateLabel);
					configKey = "Template Path";
					logger.info(String.format(
							"Saving the selected user template path \"%s\": \"%s\"",
							templateLabel, data));
				} else {
					configKey = "Template";
					data = templateLabel;
					logger.info(String.format(
							"Saving the selected embedded template name: \"%s\"",
							templateLabel));

				}
				if (configDataMap.containsKey(configKey)) {
					configDataMap.replace(configKey, data);
				} else {
					configDataMap.put(configKey, data);
				}
				String result = utils.writeDataJSON(configDataMap, "{}");

				// serialization
				ConfigData configDataObj = new ConfigData();
				if (configDataMap.containsKey("Base URL")) {
					configDataObj.setBaseURL(configDataMap.get("Base URL"));
				}
				if (configDataMap.containsKey("Template Directory")) {
					configDataObj
							.setTemplateDirectory(configDataMap.get("Template Directory"));
				}
				if (configDataMap.containsKey("Template")) {
					configDataObj.setTemplateName(configDataMap.get("Template"));
				}
				if (configDataMap.containsKey("Browser")) {
					configDataObj.setBrowser(configDataMap.get("Browser"));
				}
				if (configDataMap.containsKey("Template Path")) {
					configDataObj.setTemplatePath(configDataMap.get("Template Path"));
				}

				Gson gson = new GsonBuilder()
						.registerTypeAdapter(ConfigData.class, new ConfigDataSerializer())
						.create();
				System.err.println(
						"Saving Configuration Object: " + gson.toJson(configDataObj));

				if (parentShell != null) {
					parentShell.setData("CurrentConfig", result);
					parentShell.setData("CurrentConfigObj", gson.toJson(configDataObj));
					parentShell.setData("updated", true);
				}
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

		// info popup example
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

			directory.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent event) {
					Text text = (Text) event.widget;
					logger.info(String.format("%s = %s", (String) text.getData("key"),
							text.getText()));
				}

				@Override
				public void focusGained(FocusEvent event) {
				}
			});
			directory.addTraverseListener(new TraverseListener() {
				@Override
				public void keyTraversed(final TraverseEvent e) {
					if (e.detail == SWT.TRAVERSE_TAB_NEXT
							|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
						// logger.info("Processing key traversal");
						// this would prevent traversal away via TAB keys
						// e.doit = false;

						e.doit = true;
						// can not cast from TraverseEvent to Event
						// traverse((Event) e);
					}
				}

			});

			directory.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					Text text = (Text) event.widget;
					String key = (String) text.getData("key");
					String value = text.getText();
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
			// set initial focus to directory selection button
			browse.setFocus();
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
						logger.info(String.format("Browser: %s = %s",
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
					logger.info(String.format("Setting index of %s to %d",
							configDataMap.get(configKey),
							Arrays.asList(items).indexOf(configDataMap.get(configKey))));
					configValue.select(
							Arrays.asList(items).indexOf(configDataMap.get(configKey)));
					configValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					configValue.setData("key", configKey);

					configValue.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent event) {
							Combo o = (Combo) event.widget;
							data.replace((String) o.getData("key"), o.getText());
						}
					});
					// set initial focus to template selection
					configValue.setFocus();
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
						// see also:
						// http://gamedev.sleptlate.org/blog/107-swt-listeners-are-incompatible-with-java-lambdas/
						configValue.addListener(SWT.FocusOut, event -> {
							Text text = (Text) event.widget;
							logger.info(String.format("%s = %s", (String) text.getData("key"),
									text.getText()));
						});

						// TODO: defer to FocusEvent
						configValue.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event) {
								Text text = (Text) event.widget;
								String key = (String) text.getData("key");
								String value = text.getText();
								if (data.containsKey(key)) {
									data.replace(key, value);
								} else {
									data.put(key, value);
								}
							}
						});

					}
				}
			}
		}
	}

	public void setData(String key, String value) {
		configDataMap.put(key, value);
	}

	@SuppressWarnings("unused")
	public static void main(String[] arg) {
		ConfigFormEx configFormEx = new ConfigFormEx(null, null);
		ConfigFormEx.debug = true;
		configFormEx.render();
	}
}