package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.io.IOException;
import java.io.StringReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.Vector;

import org.apache.log4j.Logger;

import org.eclipse.jface.layout.GridDataFactory;

import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyleRange;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.RGB;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
	private Clipboard clipboard;

	private String payload = "Nothing here\nyet...";
	private final static int width = 700;
	private final static int height = 400;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;

	private StyledText styledText;
	public JavaLineStyler lineStyler = new JavaLineStyler();
	private static String defaultTemplateResourcePath = "templates/core_selenium_java.twig";

	@SuppressWarnings("deprecation")
	static final Logger logger = (Logger) Logger
			.getInstance(ScrolledTextEx.class);
	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);

	private static Utils utils = Utils.getInstance();

	ScrolledTextEx(Display parentDisplay, Shell parent) {

		utils.initializeLogger();
		logger.info("Initialized logger.");

		// NOTE: org.eclipse.swt.SWTException: Invalid thread access
		display = (parentDisplay != null) ? parentDisplay : new Display();
		clipboard = new Clipboard(display);

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
		// see also: http://www.eclipse.org/articles/StyledText%201/article1.html
		styledText = createStyledText();
		styledText.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		styledText.setText(payload);

		Composite buttonComposite = new Composite(shell, SWT.NO_FOCUS);

		buttonComposite.setLayoutData(
				new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
		GridLayout gridLayout = new GridLayout();
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.numColumns = 3;
		gridLayout.marginWidth = 2;
		// http://www.eclipse.org/articles/article.php?file=Article-Understanding-Layouts/index.html
		buttonComposite.setLayout(gridLayout);
		Button buttonSave = new Button(buttonComposite, SWT.BORDER | SWT.PUSH);

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false).applyTo(buttonSave);
		buttonSave.setText("Save in File");

		Button buttonClipboardCopy = new Button(buttonComposite, SWT.PUSH);
		buttonClipboardCopy.setText("To Clipboard");
		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false)
				.applyTo(buttonClipboardCopy);
		buttonClipboardCopy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				String rtfPayload = "{\\rtf1 " + payload + " }";

				clipboard.clearContents();
				clipboard.setContents(new String[] { payload, rtfPayload },
						new Transfer[] { TextTransfer.getInstance(),
								RTFTransfer.getInstance() });
			}
		});

		Button buttonCancel = new Button(buttonComposite, SWT.PUSH);
		buttonCancel.setText("Cancel");

		GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.hint(buttonWidth, buttonHeight).grab(false, false)
				.applyTo(buttonCancel);

		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				styledText.dispose();
				clipboard.dispose();
				buttonComposite.dispose();
				shell.dispose();
			}
		});

		shell.setSize(width, height);
		shell.setText("Generated Source");
		shell.addListener(SWT.Close, new Listener() {

			@Override
			public void handleEvent(Event event) {
				clipboard.dispose();
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

	/**
	 * Token color/style processor for Selenium WebDriver Elementor Tool (SWET)
	 */

	// origin:
	// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/JavaSourcecodeViewer.htm

	/*******************************************************************************
	 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
	 * This program and the accompanying materials are made available under the
	 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
	 * and is available at http://www.eclipse.org/legal/epl-v10.html
	 * 
	 * Contributors: IBM Corporation - initial API and implementation
	 ******************************************************************************/

	public static class JavaLineStyler implements LineStyleListener {
		JavaScanner scanner = new JavaScanner();

		int[] tokenColors;

		Color[] colors;

		@SuppressWarnings("rawtypes")
		Vector blockComments = new Vector();

		public static final int EOF = -1;
		public static final int EOL = 10;
		public static final int WORD = 0;
		public static final int WHITE = 1;
		public static final int KEY = 2;
		public static final int COMMENT = 3;
		public static final int STRING = 5;
		public static final int OTHER = 6;
		public static final int NUMBER = 7;
		public static final int MAXIMUM_TOKEN = 8; // number of colors

		public JavaLineStyler() {
			initializeColors();
			scanner = new JavaScanner();
		}

		Color getColor(int type) {
			if (type < 0 || type >= tokenColors.length) {
				return null;
			}
			return colors[tokenColors[type]];
		}

		boolean inBlockComment(int start, int end) {
			for (int i = 0; i < blockComments.size(); i++) {
				int[] offsets = (int[]) blockComments.elementAt(i);
				// start of comment in the line
				if ((offsets[0] >= start) && (offsets[0] <= end))
					return true;
				// end of comment in the line
				if ((offsets[1] >= start) && (offsets[1] <= end))
					return true;
				if ((offsets[0] <= start) && (offsets[1] >= end))
					return true;
			}
			return false;
		}

		void initializeColors() {
			Display display = Display.getDefault();
			colors = new Color[] { new Color(display, new RGB(0, 0, 0)), // black
					new Color(display, new RGB(128, 0, 0)), // red
					new Color(display, new RGB(0, 128, 0)), // green
					new Color(display, new RGB(0, 0, 128)) // blue
			};
			tokenColors = new int[MAXIMUM_TOKEN];
			tokenColors[OTHER] = 0;
			tokenColors[NUMBER] = 0;
			tokenColors[WORD] = 0;
			tokenColors[WHITE] = 0;
			tokenColors[COMMENT] = 1;
			tokenColors[STRING] = 2;
			tokenColors[KEY] = 3;
		}

		void disposeColors() {
			for (int i = 0; i < colors.length; i++) {
				colors[i].dispose();
			}
		}

		/**
		 * Event.detail line start offset (input) Event.text line text (input)
		 * LineStyleEvent.styles Enumeration of StyleRanges, need to be in order.
		 * (output) LineStyleEvent.background line background color (output)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void lineGetStyle(LineStyleEvent event) {
			Vector styles = new Vector();
			int token;
			StyleRange lastStyle;
			// If the line is part of a block comment, create one style for the
			// entire line.
			if (inBlockComment(event.lineOffset,
					event.lineOffset + event.lineText.length())) {
				styles.addElement(new StyleRange(event.lineOffset,
						event.lineText.length(), getColor(COMMENT), null));
				event.styles = new StyleRange[styles.size()];
				styles.copyInto(event.styles);
				return;
			}
			Color defaultFgColor = ((Control) event.widget).getForeground();
			scanner.setRange(event.lineText);
			token = scanner.nextToken();
			while (token != EOF) {
				if (token == OTHER) {
					// do nothing for non-colored tokens
				} else if (token != WHITE) {
					Color color = getColor(token);
					// Only create a style if the token color is different than the
					// widget's default foreground color and the token's style is
					// not
					// bold. Keywords are bolded.
					if ((!color.equals(defaultFgColor)) || (token == KEY)) {
						StyleRange style = new StyleRange(
								scanner.getStartOffset() + event.lineOffset,
								scanner.getLength(), color, null);
						if (token == KEY) {
							style.fontStyle = SWT.BOLD;
						}
						if (styles.isEmpty()) {
							styles.addElement(style);
						} else {
							// Merge similar styles. Doing so will improve
							// performance.
							lastStyle = (StyleRange) styles.lastElement();
							if (lastStyle.similarTo(style)
									&& (lastStyle.start + lastStyle.length == style.start)) {
								lastStyle.length += style.length;
							} else {
								styles.addElement(style);
							}
						}
					}
				} else if ((!styles.isEmpty()) && ((lastStyle = (StyleRange) styles
						.lastElement()).fontStyle == SWT.BOLD)) {
					int start = scanner.getStartOffset() + event.lineOffset;
					lastStyle = (StyleRange) styles.lastElement();
					// A font style of SWT.BOLD implies that the last style
					// represents a java keyword.
					if (lastStyle.start + lastStyle.length == start) {
						// Have the white space take on the style before it to
						// minimize the number of style ranges created and the
						// number of font style changes during rendering.
						lastStyle.length += scanner.getLength();
					}
				}
				token = scanner.nextToken();
			}
			event.styles = new StyleRange[styles.size()];
			styles.copyInto(event.styles);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void parseBlockComments(String text) {
			blockComments = new Vector();
			StringReader buffer = new StringReader(text);
			int ch;
			boolean blkComment = false;
			int cnt = 0;
			int[] offsets = new int[2];
			boolean done = false;

			try {
				while (!done) {
					switch (ch = buffer.read()) {
					case -1: {
						if (blkComment) {
							offsets[1] = cnt;
							blockComments.addElement(offsets);
						}
						done = true;
						break;
					}
					case '/': {
						ch = buffer.read();
						if ((ch == '*') && (!blkComment)) {
							offsets = new int[2];
							offsets[0] = cnt;
							blkComment = true;
							cnt++;
						} else {
							cnt++;
						}
						cnt++;
						break;
					}
					case '*': {
						if (blkComment) {
							ch = buffer.read();
							cnt++;
							if (ch == '/') {
								blkComment = false;
								offsets[1] = cnt;
								blockComments.addElement(offsets);
							}
						}
						cnt++;
						break;
					}
					default: {
						cnt++;
						break;
					}
					}
				}
			} catch (IOException e) {
				// ignore errors
			}
		}
	}
}
