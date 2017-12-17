package com.github.sergueik.swet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.passer.ChoiceItem;
import org.passer.ChoicesDialog;

public class ChoiceDialogEx {
	private static Display display;
	private static Shell shell;

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// Create choices
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

		if (choice == -1) {
			// Choice selected, will be one of {0,1,2}
		} else {

		}
	}
}