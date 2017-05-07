package org.swet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

//origin: http://stackoverflow.com/questions/16113313/swt-button-dropdown-control
//http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet67.java
public class BrowserSelectEx {
	private final static int buttonWidth = 240;
	private final static int buttonHeight = 40; // minimal
	private final static int formWidth = 750;
	private final static int formHeight = 280;
	/*
	private static Map<String, String> browserNames = new HashMap<>();
	static {
		browserNames.put("chrome.exe", "Google Chrome");
		browserNames.put("iexplore.exe", "Internet Explorer");
		browserNames.put("firefox.exe", "Mozilla Firefox");
		browserNames.put("opera.exe", "Opera");
		browserNames.put("safari", "Safari");
	}
	*/
	private static ArrayList<String> browserNames = new ArrayList<String>(
			Arrays.asList(new String[] { "Chrome", "Firefox", "Internet Explorer",
					"Edge", "Safari" }));

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);

		final ToolBar toolBar = new ToolBar(shell, SWT.BORDER | SWT.RIGHT);

		Rectangle clientArea = shell.getClientArea();
		toolBar.setLocation(clientArea.x, clientArea.y);
		final GridData gridDataNext = new GridData(GridData.FILL, GridData.END,
				true, false);
		gridDataNext.widthHint = buttonWidth;
		gridDataNext.heightHint = buttonHeight;

		final ToolItem toolItem = new ToolItem(toolBar, SWT.DROP_DOWN | SWT.RIGHT);
		toolItem.setToolTipText("Select the browser");
		toolItem.setText("Chrome");
		toolItem.setWidth(buttonWidth);

		final Menu menu = new Menu(shell, SWT.POP_UP);
		for (String browserName : /* browserNames.keySet() */ browserNames) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(browserName);
			menuItem.setData(browserName);
			menuItem.setEnabled((browserName.indexOf("Safari") == -1));
			// utils.InstalledBrowsers.isInstalled(browserName);
			/* 
			menuItem.setText(browserNames.get(browserName));
			menuItem.setData((String) browserName);
			menuItem.setEnabled((browserNames.get(browserName).indexOf(" ") >= 0));
			*/
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					System.err.println(event.widget.getData());
					toolItem.setText((String) event.widget.getData());
				}
			});
		}

		toolItem.addListener(SWT.Selection, event -> {
			if (event.detail == SWT.ARROW || event.detail == SWT.HOT) {
				Rectangle rectangle = toolItem.getBounds();
				Point point = toolBar
						.toDisplay(new Point(rectangle.x, rectangle.y + rectangle.height));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);
			}
		});

		toolBar.pack();
		toolBar.setSize(buttonWidth, buttonHeight);
		shell.pack();
		shell.setSize(formWidth, formHeight);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		menu.dispose();
		display.dispose();
	}
}