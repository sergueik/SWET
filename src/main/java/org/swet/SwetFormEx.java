package org.swet;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.mihalis.opal.header.Header;

/**
 * Alternative Top Level Form for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class SwetFormEx {

	private Display display;
	private Shell shell;
	private Button buttonClose;
	private Browser tipArea;
	private static String fontName = null;

	private final static int buttonWidth = 90;
	private final static int buttonHeight = 40;

	public SwetFormEx() {
	}

	public void open(final Shell parent, Display... parentDisplay) {
		this.shell = new Shell(parent,
				SWT.SYSTEM_MODAL | SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.RESIZE);
		this.shell.setText("Selenium Webdriver Elementor Tool");
		this.shell.setLayout(new GridLayout(2, false));

		buildLeftColumn();
		buildButtons();

		this.shell.setDefaultButton(this.buttonClose);
		this.shell.pack();
		this.shell.open();

		if (parentDisplay != null) {
			display = parentDisplay[0];
		} else {
			display = this.shell.getDisplay();
		}
		shell.setLocation(0, 0);

		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void buildLeftColumn() {
		final Composite composite = new Composite(this.shell, SWT.NONE);
		final GridData gd = new GridData(GridData.FILL, GridData.BEGINNING, false,
				true, 1, 1);
		composite.setLayoutData(gd);
		final FillLayout compositeLayout = new FillLayout();
		compositeLayout.marginWidth = 2;
		composite.setLayout(compositeLayout);
	}

	// http://aniszczyk.org/2007/08/09/resizing-images-using-swt/
	private Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
				0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}

	private void buildButtons() {
		final Composite composite = new Composite(this.shell, SWT.NONE);
		int numberOfColumns = 2;

		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				false, false, numberOfColumns, 1));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		composite.setLayout(new GridLayout(7, false));

		final Button buttonLaunch = new Button(composite, SWT.BORDER | SWT.PUSH);
		final GridData gridDatabuttonLaunch = new GridData(GridData.FILL,
				GridData.CENTER, false, false);
		buttonLaunch.setLayoutData(gridDatabuttonLaunch);
		buttonLaunch.setText("Launch");
		String launchImage = "launch_36.png";
		buttonLaunch.setImage(resize(
				new Image(display,
						this.getClass().getClassLoader()
								.getResourceAsStream(String.format("images/%s", launchImage))),
				24, 24));
		buttonLaunch.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
			}
		});

		final Button buttonFind = new Button(composite, SWT.PUSH);
		buttonFind.setText("Find");
		final GridData gridDataFind = new GridData(GridData.END, GridData.CENTER,
				false, false);

		buttonFind.setLayoutData(gridDataFind);
		String findImage = "find_36.png";
		buttonFind.setImage(resize(
				new Image(display,
						this.getClass().getClassLoader()
								.getResourceAsStream(String.format("images/%s", findImage))),
				28, 28));
		buttonFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
			}

		});
		buttonFind.setEnabled(false);

		final Button buttonGenerate = new Button(composite, SWT.PUSH);
		buttonGenerate.setText("Generate");
		final GridData gridDataGenerate = new GridData(GridData.FILL,
				GridData.CENTER, false, false);
		buttonGenerate.setLayoutData(gridDataGenerate);
		String generateImage = "flowchart.png";
		buttonGenerate.setImage(resize(
				new Image(display, this.getClass().getClassLoader()
						.getResourceAsStream(String.format("images/%s", generateImage))),
				28, 28));
		buttonGenerate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
			}

		});
		buttonGenerate.setEnabled(false);

		final Button buttonOpen = new Button(composite, SWT.PUSH);
		buttonOpen.setText("Open");
		final GridData gridDataOpen = new GridData(GridData.FILL, GridData.CENTER,
				false, false);
		String openImage = "open_36.png";
		buttonOpen.setImage(resize(
				new Image(display,
						this.getClass().getClassLoader()
								.getResourceAsStream(String.format("images/%s", openImage))),
				24, 24));

		buttonOpen.setLayoutData(gridDataOpen);
		buttonOpen.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
			}

		});

		final Button buttonSave = new Button(composite, SWT.PUSH);
		buttonSave.setText("Save");
		final GridData gridDataSave = new GridData(GridData.FILL, GridData.CENTER,
				false, false);
		String saveImage = "save_36.png";
		buttonSave.setImage(resize(
				new Image(display,
						this.getClass().getClassLoader()
								.getResourceAsStream(String.format("images/%s", saveImage))),
				24, 24));

		buttonSave.setLayoutData(gridDataSave);
		buttonSave.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
			}

		});
		buttonSave.setEnabled(false);

		final CLabelEx buttonPrefs = new CLabelEx(shell, display);
		buttonPrefs.render(composite);
		/*
		final Button buttonPrefs = new Button(composite, SWT.PUSH);
		buttonPrefs.setText("Config");
		final GridData gridDataPrefs = new GridData(GridData.FILL, GridData.CENTER,
				false, false);
		String gearImage = "gear_36.png";
		buttonPrefs.setImage(resize(
				new Image(display,
						this.getClass().getClassLoader()
								.getResourceAsStream(String.format("images/%s", gearImage))),
				24, 24));
		
		buttonPrefs.setLayoutData(gridDataPrefs);
		buttonPrefs.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(final SelectionEvent e) {
			}
		
		});
		*/
		buttonClose = new Button(composite, SWT.BORDER | SWT.PUSH);
		buttonClose.setText("Close");
		final GridData gridDataClose = new GridData(GridData.FILL, GridData.CENTER,
				false, false);
		gridDataClose.widthHint = buttonWidth;
		gridDataClose.heightHint = buttonHeight;
		buttonClose.setLayoutData(gridDataClose);
		buttonClose.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SwetFormEx.this.shell.dispose();
			}

		});
		this.buttonClose = buttonClose;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		Locale.setDefault(Locale.ENGLISH);
		final SwetFormEx SwetFormEx = new SwetFormEx();
		SwetFormEx.open(shell, display);
		display.dispose();
	}

	// http://stackoverflow.com/questions/10794190/how-to-add-text-and-image-both-in-a-swt-label
	private static class CLabelEx {

		private Image image;
		private Shell shell;
		private Display display;
		private final static int buttonWidth = 120;
		private final static int buttonHeight = 28;

		@SuppressWarnings("unused")
		public CLabelEx(Shell parentShell, Display parentDisplay) {
			shell = parentShell;
			display = parentDisplay;
			image = new Image(display,
					this.getClass().getClassLoader().getResourceAsStream(
							String.format("images/%s", "find.png")));
		}

		public void dispose() {
			if (image != null) {
				image.dispose();
				image = null;
			}
		}

		public void render(Composite composite) {

			CLabel label = new CLabel(composite, SWT.BORDER|SWT.PUSH);
			label.setImage(image);

			label.setBackground(image);
			label.setBounds(0, 0, 36, 36);
			label.setText("Text");
			label.pack();
			composite.pack();
		}

	}
}
