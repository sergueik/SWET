package com.github.sergueik.swet;

/**
 * Copyright 2018 Serguei Kouzmine
 */

import static java.lang.String.format;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 *
 */
// Demo of toggle to a Busy Cursor
// during a long running process
// using org.eclipse.swt.custom.BusyIndicator .


// based on:
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesBusyIndicator.htm

final class BusyIndicatorEx {
	private static final int SLEEP_TIME = 3000;

	private void run() {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Busy Indicator Test");
		createContents(shell);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private void createContents(Shell shell) {
		shell.setLayout(new FillLayout());
		final Button button = new Button(shell, SWT.PUSH);
		button.setText("Press to Start");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				button.setText("Running...");
				// use org.eclipse.swt.custom.BusyIndicator to toggle to a Busy Cursor
				// during a long running process.
				BusyIndicator.showWhile(button.getDisplay(),
						new SleepThread(SLEEP_TIME));

				// Thread has completed; reset the button's text
				button.setText("Press to Start");
			}
		});
	}

	// dummy worker
	static private class SleepThread extends Thread {
		private long ms;

		public SleepThread(long ms) {
			this.ms = ms;
		}

		public void run() {
			try {
				sleep(ms);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void main(String[] args) {
		new BusyIndicatorEx().run();
	}
}
