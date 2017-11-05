package org.swet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jface.dialogs.ErrorDialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.swet.Utils;

/**
 * Exception dialog for Selenium WebDriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ExceptionDialogNewEx {

	private MultiStatus status;
	private Shell shell = null;
	private Display display = null;
	private static boolean debug = false;
	private Utils utils = Utils.getInstance();

	private static ExceptionDialogNewEx instance = new ExceptionDialogNewEx();

	public static ExceptionDialogNewEx getInstance() {
		return instance;
	}

	private static void testFunction() throws Exception {
		throw new Exception("This is a test exception");
	}

	public void render(Throwable e) {
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
		if (debug) {
			System.err.println("Show the dialog");
		}
		ErrorDialog.openError(shell, "Error", "Exception thrown", status);
		if (debug) {
			System.err.println("Shown the dialog");
		}
	}

	private ExceptionDialogNewEx() {
		display = Display.getCurrent();
    try {
      shell = Display.getCurrent().getActiveShell();      
    } catch (NullPointerException e){
      shell = new Shell(display);
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

	@SuppressWarnings("unused")
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
			ExceptionDialogNewEx x = ExceptionDialogNewEx.getInstance();
			x.render(e);
		}
	}

}