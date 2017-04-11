package org.swet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.swet.Utils;

/**
 * Exception dialog for Selenium Webdriver Elementor Tool
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ExceptionDialogEx {

	private MultiStatus status;
	private Shell shell = null;
	private Display display;
	private static boolean debug = false;

	public static void main(String[] arg) {
		debug = true;
		try {
			testFunction();
		} catch (Exception e) {
			if (debug) {
				System.err.println("Processing exception:");
				e.printStackTrace();
			}

			if (debug) {
				System.err.println("Create the Exception dialog");
			}
			(new ExceptionDialogEx(null, null, e)).execute();
		}
	}

	private static void testFunction() throws Exception {
		throw new Exception("This is a test exception");
	}

	public void execute() {
		if (debug) {
			System.err.println("Show the dialog");
		}
		ErrorDialog.openError(shell, "Error", "Exception thrown", status);
		if (debug) {
			System.err.println("Shown the dialog");
		}
		// TODO: next does not show the dialog
		/*
		  import org.eclipse.jdt.internal.ui.dialogs.ProblemDialog;
		  ProblemDialog dialog = new ProblemDialog(shell, "Error", "Exception thrown", new Image(dev, new Utils().getResourcePath("launch.png"));
		      status, 0);
		  // dialog.setDefaultImages();
		  dialog.create();
		  dialog.open();
		*/
	}

	public ExceptionDialogEx(Display parentDisplay, Shell parentShell,
			Throwable e) {
		display = (parentDisplay != null) ? parentDisplay : new Display();
		shell = new Shell(display);
		if (parentShell != null) {
			shell = parentShell;
			// commandId = parent.getData(dataKey).toString();
		} else {
			shell = Display.getCurrent().getActiveShell();
		}
		// Collect the exception stack trace
		if (debug) {
			System.err.println("Begin Collecting the exception stack trace");
		}
		Exception eCause = (Exception) e.getCause();
		if (eCause != null) {
			if (debug) {
				System.err.println("Cause: " + eCause.toString());
			}
			status = createMultiStatus(e.getLocalizedMessage(), eCause);
		} else {
			status = createMultiStatus(e.getLocalizedMessage(), e);
		}
	}

	private static MultiStatus createMultiStatus(String description,
			Throwable t) {
		List<Status> childStatuses = new ArrayList<>();

		for (StackTraceElement stackTrace : t.getStackTrace()) {
			if (debug) {
				System.err.println(
						String.format("Adding stack trace: %s", stackTrace.toString()));
			}
			Status status = new Status(IStatus.ERROR, "org.swet",
					stackTrace.toString());
			childStatuses.add(status);
		}
		for (StackTraceElement stackTrace : Thread.currentThread()
				.getStackTrace()) {
			if (debug) {
				System.err.println(
						String.format("Adding stack trace: %s", stackTrace.toString()));
			}
			Status status = new Status(IStatus.ERROR, "org.swet",
					stackTrace.toString());
			childStatuses.add(status);
		}

		String summary = (description != null) ? description : t.toString();
		MultiStatus status = new MultiStatus("org.swet", IStatus.ERROR,
				childStatuses.toArray(new Status[] {}),
				(summary.length() > 120) ? summary.substring(0, 120) : summary, t);
		return status;
	}
}