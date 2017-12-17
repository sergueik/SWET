package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import javax.swing.ImageIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

// origin: https://github.com/Vladimir-Novick/System-Tray
////////////////////////////////////////////////////////////////////////////
//	Copyright 2006, 2017 : Vladimir Novick    https://www.linkedin.com/in/vladimirnovick/  
//
//         https://github.com/Vladimir-Novick/System-Tray
//
//    NO WARRANTIES ARE EXTENDED. USE AT YOUR OWN RISK. 
//
// To contact the author with suggestions or comments, use  :vlad.novick@gmail.com
//
////////////////////////////////////////////////////////////////////////////

public class SystemTrayEx {

	public SystemTrayEx() {
		super();
	}

	private static TrayItem trayItem = null;

	private static Image imageStarted = null;
	private static Image imageStopped = null;

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImage(String path, String description) {
		java.net.URL imgURL = SystemTrayEx.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// -------------
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new RowLayout());

		// -------------

		final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setMessage("SGcombo System Agent Goes Here!");

		Tray tray = display.getSystemTray();

		if (tray != null) {
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.setImage(imageStarted);
			final Menu menu = new Menu(shell, SWT.POP_UP);

			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText("Stop Agent");

			Image imagemenu = new Image(display, "./src/main/resources/images/stop.gif");
			menuItem.setImage(imagemenu);

			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					stopAgent();
				}
			});

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText("Start Agent");
			imagemenu = new Image(display, "./src/main/resources/images/start.gif");
			menuItem.setImage(imagemenu);

			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					startAgent();
					trayItem.setImage(imageStarted);
				}
			});

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText("Show Status");

			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText("Shutdown");

			menuItem.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {

					shell.close(); // calls dispose() - see note below

				}

			});

			trayItem.addListener(SWT.MouseEnter, new Listener() {
				public void handleEvent(Event e) {
					tip.setVisible(true);
				}
			});

			trayItem.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					menu.setVisible(true);
				}
			});
		}

		shell.pack();
		shell.open();

		shell.setVisible(false);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		trayItem.dispose();
		display.dispose();

	}

	protected static void startAgent() {
		trayItem.setImage(imageStarted);
	}

	protected static void stopAgent() {
		trayItem.setImage(imageStopped);
	}

}
