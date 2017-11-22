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

	final static int MAX_PERCENTAGE = 30;
	final static int DELAY = 100;
	private static ProgressBar progressBar;

	public static void main(String[] a) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		progressBar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMinimum(0);
		progressBar.setMaximum(MAX_PERCENTAGE);

		Button button = new Button(shell, SWT.CENTER);
		button.setText("Click to start");
		button.setBounds(shell.getClientArea());
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent event) {
				button.setEnabled(false);
				progressBar.setSelection(0);
				Thread longRunningOperation = new LongRunningOperation(display,
						progressBar);
				longRunningOperation.start();
				int percentage = (int) shell.getData("percentage");

				System.err.println(
						String.format("button %.2f", 100.0 * percentage / MAX_PERCENTAGE));
				if (percentage == MAX_PERCENTAGE) {
					try {
						longRunningOperation.wait();
					} catch (InterruptedException e) {

					}
					button.setEnabled(true);
				}
				// this sets button enabled immediately
				// button.setEnabled(true);
			}
		});

		shell.setData("percentage", 0);
		shell.setSize(300, 200);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			try {
				int percentage = (int) shell.getData("percentage");
				if (percentage == MAX_PERCENTAGE) {
					// shell.close();
				}
			} catch (SWTException e) {
				// ignore
			}
		}
	}

	static class LongRunningOperation extends Thread {
		private Display display;
		private Shell parentShell = null;
		private ProgressBar progressBar;
		private int percentage;

		public LongRunningOperation(Display display, ProgressBar progressBar) {
			this.display = display;
			this.progressBar = progressBar;
			this.parentShell = progressBar.getShell();
		}

		public void run() {
			System.out
					.println("Hello from thread: \t" + Thread.currentThread().getName());
			for (percentage = 0; percentage <= MAX_PERCENTAGE; percentage++) {
				try {
					Thread.sleep(DELAY);
					System.err.println(
							String.format("%.2f", 100.0 * percentage / MAX_PERCENTAGE));
				} catch (InterruptedException e) {
				}
				display.asyncExec(new Runnable() {
					public void run() {
						if (progressBar.isDisposed())
							return;
						progressBar.setSelection(progressBar.getSelection() + 1);
						parentShell.setData("percentage", percentage);
					}

				});

			}
			System.out
					.println("Bye from thread: \t" + Thread.currentThread().getName());
			return;
		}
	}

}
