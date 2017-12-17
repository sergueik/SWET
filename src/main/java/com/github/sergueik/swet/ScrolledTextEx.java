package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.github.sergueik.swet.ExceptionDialogEx;
import com.github.sergueik.swet.Utils;

/**
 * Generated source display form for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

class ScrolledTextEx {

	protected Shell shell;
	private Display display;
	private String payload = "Nothing here\nyet...";
	private final static int width = 700;
	private final static int height = 400;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;

	private StyledText styledText;
	public JavaLineStyler lineStyler = new JavaLineStyler();
	private static String defaultTemplateResourcePath = "templates/core_selenium_java.twig";

	ScrolledTextEx(Display parentDisplay, Shell parent) {

		// NOTE: org.eclipse.swt.SWTException: Invalid thread access
		display = (parentDisplay != null) ? parentDisplay : new Display();

		shell = new Shell(display);
		shell.setSize(20, 20);
		shell.open();

		if (parent != null) {
			payload = (String) parent.getData("payload");
		} else {
			RenderTemplate template = new RenderTemplate();
			template.setTemplateName(defaultTemplateResourcePath);
			try {
				payload = template.renderTest();
			} catch (Exception e) {
				// show the error dialog with exception trace
				ExceptionDialogEx.getInstance().render(e);
			}
		}
		shell.setText("Generated QA source");
		shell.setLayout(new GridLayout(2, false));
		styledText = createStyledText();
		styledText.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		styledText.setText(payload);
		
		Composite buttonComposite = new Composite(shell, SWT.NO_FOCUS);

		buttonComposite.setLayoutData(
				new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		buttonComposite.setLayout(new GridLayout(2, false));
		
		Button buttonSave = new Button(buttonComposite, SWT.BORDER | SWT.PUSH);

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false).applyTo(buttonSave);
		buttonSave.setText("Save");

    Button buttonCancel = new Button(buttonComposite, SWT.PUSH);
		buttonCancel.setText("Cancel");

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false)
				.applyTo(buttonCancel);

		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				styledText.dispose();
				buttonComposite.dispose();
				shell.dispose();
			}
		});


    
		shell.setSize(width, height);
		shell.setText("Generated Source");
		shell.addListener(SWT.Close, new Listener() {

			@Override
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		buttonSave.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFilterNames(new String[] { "TEXT Files", "All Files (*.*)" });
				dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
				String homeDir = System.getProperty("user.home");
				dialog.setFilterPath(homeDir);
				// TODO: remember the path
				String filePath = dialog.open();
				if (filePath != null) {
					System.out.println("Save to: " + filePath);
					try {
						Files.write(Paths.get(filePath), Arrays.asList(payload.split("\n")),
								Charset.forName("UTF-8"));
					} catch (IOException e) {
						ExceptionDialogEx.getInstance().render(e);
					}
					styledText.dispose();
					buttonComposite.dispose();
					shell.dispose();
				}
			}
		});

		try {
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private StyledText createStyledText() {
		styledText = new StyledText(shell,
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL); // SWT.WRAP
		GridData gridData = new GridData();
		styledText.setFont(
				new Font(shell.getDisplay(), "Source Code Pro Light", 10, SWT.NORMAL));
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		styledText.setLayoutData(gridData);
		styledText.addLineStyleListener(lineStyler);
		styledText.setEditable(false);
		styledText
				.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		return styledText;
	}

	private static Point center(Display display, Shell shell) {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		Point location = new Point(x, y);
		return location;
	}

	public static void main(String[] arg) {
		ScrolledTextEx o = new ScrolledTextEx(new Display(), null);
	}
}
