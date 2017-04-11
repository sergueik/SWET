package org.swet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.swet.Utils;
import org.swet.RenderTemplate;

/**
 * Generated source display form for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

class ScrolledTextEx {

	protected Shell shell;
	private Display display;
	private String payload = "Nothing here\nyet...";
	private final static int width = 700;
	private final static int height = 400;
	private StyledText styledText;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;

	public JavaLineStyler lineStyler = new JavaLineStyler();

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
			template.setTemplateName("templates/example3.twig");
			try {
				payload = template.renderTest();
			} catch (Exception e) {
				ExceptionDialogEx o = new ExceptionDialogEx(display, shell, e);
				// show the error dialog with exception trace
				o.execute();
			}
		}
		shell.setText("Generated QA source");
		shell.setLayout(new GridLayout(2, false));
		styledText = createStyledText();
		styledText.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		styledText.setText(payload);
		Button buttonSave = new Button(shell, SWT.BORDER | SWT.PUSH);

		GridData gridDataSave = new GridData(GridData.FILL, GridData.CENTER, false,
				false);
		gridDataSave.widthHint = buttonWidth;
		gridDataSave.heightHint = buttonHeight;

		buttonSave.setLayoutData(gridDataSave);

		buttonSave.setText("Save");

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
						Files.write(Paths.get(filePath),
								(List<String>) Arrays.asList(payload.split("\n")),
								Charset.forName("UTF-8"));
					} catch (IOException e) {
						new ExceptionDialogEx(display, shell, e).execute();
					}
					styledText.dispose();
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

	public static void main(String[] arg) {
		ScrolledTextEx o = new ScrolledTextEx(new Display(), null);
	}
}
