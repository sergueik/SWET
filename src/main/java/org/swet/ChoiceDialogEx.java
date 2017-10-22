package org.swet;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.mihalis.opal.breadcrumb.Breadcrumb;
import org.mihalis.opal.breadcrumb.BreadcrumbItem;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.regex.Pattern;
import org.passer.ChoiceItem;
import org.passer.ChoicesDialog;

public class ChoiceDialogEx {
	private static Display display;
	private static Shell shell;
	private static Shell parentShell = null;
	private static Display parentDisplay = null;

	public static void main(String[] args) {
		// Create choices
		display = (parentDisplay != null) ? parentDisplay : new Display();
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