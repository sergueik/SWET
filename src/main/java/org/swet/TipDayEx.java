package org.swet;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Shell;
import org.mihalis.opal.header.Header;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Monitor;

/**
 * Utilities for Selenium Webdriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on Tip of the Day (c) Laurent Caron

public class TipDayEx {

	private Display display;
	private Shell shell;
	private Button buttonClose;
	private Browser tipArea;
	private static String fontName = null;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;

	@SuppressWarnings("unused")
	public TipDayEx() {
	}

	private static int index = -1;

	public int getIndex() {
		return TipDayEx.index;
	}

	private final List<String> tips = new ArrayList<String>();

	public List<String> getTips() {
		return this.tips;
	}

	public TipDayEx addTip(final String tip) {
		this.tips.add(tip);
		return this;
	}

	private boolean showOnStartup = true;

	public boolean isShowOnStartup() {
		return this.showOnStartup;
	}

	private Image image;

	public void setImage(final Image image) {
		this.image = image;
	}

	public Image getImage() {
		return this.image;
	}

	public void open(final Shell parent, Display... parentDisplay) {
		if (TipDayEx.index == -1) {
			TipDayEx.index = new Random().nextInt(this.tips.size());
		}
		this.shell = new Shell(parent,
				SWT.SYSTEM_MODAL | SWT.TITLE | SWT.BORDER | SWT.CLOSE | SWT.RESIZE);
		this.shell.setText("Tip of the day");
		this.shell.setLayout(new GridLayout(2, false));

		this.shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					TipDayEx.this.shell.dispose();
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = false;
					break;
				}
			}
		});
		buildLeftColumn();
		buildTip();
		buildButtons();

		this.shell.setDefaultButton(this.buttonClose);
		this.shell.pack();
		this.shell.open();

		if (parentDisplay != null) {
			display = parentDisplay[0];
		} else {

			display = this.shell.getDisplay();
		}
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);

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
		final Label label = new Label(composite, SWT.NONE);
		if (this.image == null) {
			this.image = new Image(display, this.getClass().getClassLoader()
					.getResourceAsStream("images/light1.png"));
			shell.addListener(SWT.Close, new Listener() {
				@Override
				public void handleEvent(Event event) {
					image.dispose();
				}
			});

		}
		label.setImage(this.image);
	}

	private void buildTip() {
		final Group group = new Group(this.shell, SWT.NONE);
		final GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.widthHint = 300;
		gd.heightHint = 120;
		group.setLayoutData(gd);
		group.setText("Did you know");
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 15;
		group.setLayout(fillLayout);

		this.tipArea = new Browser(group, SWT.BORDER);
		fillTipArea();
	}

	private void fillTipArea() {
		if (TipDayEx.fontName == null) {
			final FontData[] fontData = Display.getDefault().getSystemFont()
					.getFontData();
			if (fontData != null && fontData.length > 0) {
				TipDayEx.fontName = String.format("\"%s\"", fontData[0].getName());
			} else {
				TipDayEx.fontName = "Arial,​\"Helvetica Neue\",​Helvetica,​sans-serif";
			}
		}
		String text = String.format(
				"<html><body bgcolor=\"#EFEFEF\" text=\"#000000\" style='font-family:%s;font-size=14px\'>%s</body></html>",
				TipDayEx.fontName, this.tips.get(TipDayEx.index));
		System.err.println(text);
		this.tipArea.setText(text);
	}

	private void buildButtons() {
		final Composite composite = new Composite(this.shell, SWT.NONE);
		int numberOfColumns = 2;

		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				false, false, numberOfColumns, 1));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		composite.setLayout(new GridLayout(4, false));

		final Button checkBox = new Button(composite, SWT.CHECK);
		checkBox.setLayoutData(
				new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		checkBox.setText("Show at startup");
		checkBox.setSelection(this.showOnStartup);
		checkBox.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				showOnStartup = checkBox.getSelection();
			}
		});

		final Button buttonPrevious = new Button(composite, SWT.PUSH);
		buttonPrevious.setText("Previous Tip");
		final GridData gridDataPrevious = new GridData(GridData.END,
				GridData.CENTER, this.showOnStartup ? false : true, false);
		gridDataPrevious.widthHint = buttonWidth;
		gridDataPrevious.heightHint = buttonHeight;

		buttonPrevious.setLayoutData(gridDataPrevious);
		buttonPrevious.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (TipDayEx.index == 0) {
					setIndex(tips.size() - 1);
				} else {
					setIndex(TipDayEx.index - 1);
				}
			}

		});

		final Button buttonNext = new Button(composite, SWT.PUSH);
		buttonNext.setText("Next Tip");
		final GridData gridDataNext = new GridData(GridData.FILL, GridData.CENTER,
				false, false);
		gridDataNext.widthHint = buttonWidth;
		gridDataNext.heightHint = buttonHeight;

		buttonNext.setLayoutData(gridDataNext);
		buttonNext.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (TipDayEx.index == tips.size() - 1) {
					setIndex(0);
				} else {
					setIndex(TipDayEx.index + 1);
				}
			}

		});

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
				TipDayEx.this.shell.dispose();
			}

		});
		this.buttonClose = buttonClose;
	}

	public void setIndex(final int index) {
		if (index < 0 || index >= this.tips.size()
				|| this.tips.get(index) == null) {
			throw new IllegalArgumentException("Index should be between 0 and "
					+ (this.tips.size() - 1) + " (entered value:" + index + ")");
		}

		TipDayEx.index = index;
		if (this.tipArea != null && !this.tipArea.isDisposed()) {
			fillTipArea();
		}
	}

	public void setShowOnStartup(final boolean showOnStartup) {
		this.showOnStartup = showOnStartup;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		Locale.setDefault(Locale.ENGLISH);
		final TipDayEx tipDayEx = new TipDayEx();
		for (String tipMessage : new String[] { "This is the first tip",
				"This is the second tip", "This is the third tip",
				"This is the forth tip", "This is the fifth tip" }) {
			tipDayEx.addTip(String.format(
					"<h4>%s</h4>" + "<b>%s</b> " + "<u>%s</u> " + "<i>%s</i> " + "%s "
							+ "%s<br/>" + "<p color=\"#A00000\">%s</p>",
					tipMessage, tipMessage, tipMessage, tipMessage, tipMessage,
					tipMessage, tipMessage));

		}

		tipDayEx.open(shell, display);
		display.dispose();
	}

}