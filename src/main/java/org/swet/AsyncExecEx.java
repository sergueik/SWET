package org.swet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

// origin: 
// http://www.java2s.com/Code/JavaAPI/org.eclipse.swt.widgets/DisplayasyncExecRunnablerun.htm
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTandThread.htm
public class AsyncExecEx {

	final static int MIN_PERCENTAGE = 0;
	final static int MAX_PERCENTAGE = 30;
	final static int DELAY = 100;
	private static ProgressBar progressBar;
	private static Button button;
	private static Thread longRunningOperation;

	public static void main(String[] a) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		progressBar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMinimum(MIN_PERCENTAGE);
		progressBar.setMaximum(MAX_PERCENTAGE);

		button = new Button(shell, SWT.CENTER);
		button.setText("Click to start");
		button.setBounds(shell.getClientArea());
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent event) {
				button.setEnabled(false);
				progressBar.setSelection(MIN_PERCENTAGE);
				longRunningOperation = new LongRunningOperation(display, progressBar,
						button);
				longRunningOperation.start();
				// NOTE: this sets button enabled immediately - only valid for sync
				// executions
				// button.setEnabled(true);
			}
		});

		shell.setData("percentage", MIN_PERCENTAGE);
		shell.setSize(300, 200);
		shell.open();
		int percentage = MIN_PERCENTAGE;
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			try {
				percentage = (int) shell.getData("percentage");
			} catch (SWTException e) {
				// ignore
			}
			if (percentage == MAX_PERCENTAGE) {
				// shell.close();
				/*
				try {
					longRunningOperation.wait();
				} catch (InterruptedException | IllegalMonitorStateException
						| NullPointerException e) {
				} */
			}
		}
	}

	static class LongRunningOperation extends Thread {
		private Display display;
		private Shell parentShell = null;
		private ProgressBar progressBar;
		private Button button;
		private int percentage;

		public LongRunningOperation(Display display, ProgressBar progressBar,
				Button button) {
			this.display = display;
			this.progressBar = progressBar;
			this.parentShell = progressBar.getShell();
			this.button = button;
		}

		public void run() {
			/*	System.out
						.println("Hello from thread: \t" + Thread.currentThread().getName());
			*/
			for (percentage = MIN_PERCENTAGE; percentage <= MAX_PERCENTAGE; percentage++) {
				try {
					Thread.sleep(DELAY);
					/*	System.err.println(
								String.format("%.2f", 100.0 * percentage / MAX_PERCENTAGE));
					*/
				} catch (InterruptedException e) {
				}
				display.asyncExec(new Runnable() {
					public void run() {
						if (percentage == MIN_PERCENTAGE) {
							// NOTE: not reliable
							button.setEnabled(false);
						}
						if (progressBar.isDisposed())
							return;
						progressBar.setSelection(progressBar.getSelection() + 1);
						// progressBar.setSelection(percentage);
						parentShell.setData("percentage", percentage);
						if (percentage == MAX_PERCENTAGE) {
							button.setEnabled(true);
						}
					}
				});
			}
			/*	System.out
						.println("Bye from thread: \t" + Thread.currentThread().getName());
			*/ return;
		}
	}

}
