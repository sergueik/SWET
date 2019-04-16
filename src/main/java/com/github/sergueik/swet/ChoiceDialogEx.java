package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.passer.ChoiceItem;
import org.passer.ChoicesDialog;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ChoiceDialogEx {

	private static Display display;
	private static Shell shell;
	private static StringBuilder loggingSb = new StringBuilder();
	private static Formatter formatter = new Formatter(loggingSb, Locale.US);
	private static Utils utils = Utils.getInstance();

	@SuppressWarnings("deprecation")
	static final Logger logger = (Logger) Logger.getInstance(ChoiceDialogEx.class);

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// Create choices
		utils.initializeLogger();
		logger.info("Initialized logger.");
		display = new Display();
		shell = new Shell(display);
		ChoiceItem[] items = new ChoiceItem[] {
				new ChoiceItem("Exit and save my project",
						"Save your work in progress and exit the program"),
				new ChoiceItem("Exit and don't save",
						"Exit the program without saving your project"),
				new ChoiceItem("Don't exit", "Return to the program"), };

		ChoicesDialog dialog = new ChoicesDialog(shell, SWT.APPLICATION_MODAL);

		dialog.setTitle("Exit");
		dialog.setMessage("Do you really want to exit?");
		dialog.setImage(Display.getCurrent().getSystemImage(SWT.ICON_QUESTION));
		dialog.setChoices(items);
		dialog.setDefaultChoice(items[2]);
		dialog.setShowArrows(false);

		int choice = dialog.open();
		logger.info("Choice: " + choice);
		if (choice == -1) {
			// Choice selected, will be one of {0,1,2}
		} else {

		}
	}

}