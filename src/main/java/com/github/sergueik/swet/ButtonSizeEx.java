package com.github.sergueik.swet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

// origin:
// https://www.eclipsezone.com/servlet/JiveServlet/download/18120-65152-91995796-4076/ButtonSizeSnippet.java
public class ButtonSizeEx {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		GridLayout layout = new GridLayout(2, true);
		shell.setLayout(layout);

		Text txt = new Text(shell, SWT.BORDER | SWT.SINGLE);
		txt.setText("Text here");
		txt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));

		Button b = new Button(shell, SWT.PUSH);
		b.setText("B1");
		b.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		txt = new Text(shell, SWT.BORDER | SWT.MULTI);
		txt.setText("something else here");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		txt.setLayoutData(data);

		b = new Button(shell, SWT.PUSH);
		b.setText("B2 Wide Wide Wide Wide Button");
		b.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		// this does not change the heght of the button
		b = new Button(shell, SWT.PUSH);
		// https://stackoverflow.com/questions/4547209/multi-line-button-text-in-swt-windowbuilder
		// b = new Button(shell, SWT.WRAP | SWT.PUSH);

		// alternatively
		final int style = OS.GetWindowLong(b.handle, OS.GWL_STYLE);
		OS.SetWindowLong(b.handle, OS.GWL_STYLE, style | OS.BS_MULTILINE);

		// https://markmail.org/thread/qlfoejwbue24rtne

		// unrelated
		// https://www.codota.com/code/java/methods/com.sun.jna.platform.win32.User32/GetWindowText
		int windowTextLength = 512;
		char[] windowText = new char[windowTextLength];
		User32 user32 = User32.INSTANCE;
		HWND hwnd = new HWND();
		hwnd.setPointer(new Pointer(b.handle));
		user32.GetWindowText(hwnd, windowText, windowTextLength);
		System.err.println("GetWindow text: " + windowText.toString());

		b.setText("B3\nTall Button");
		b.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		GridData buttonGridData = (GridData) b.getLayoutData();
		Point bSize = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		buttonGridData.widthHint = bSize.x;
		buttonGridData.heightHint = (int) (bSize.y * 2.5);
		System.err.println(
				String.format("Manually adjusting size to: width: %d height: %d",
						buttonGridData.widthHint, buttonGridData.heightHint));
		b.setLayoutData(buttonGridData);
		// does not work.
		b.setSize(new Point(buttonGridData.widthHint, buttonGridData.heightHint));
		shell.pack();

		// NOTE: there appears to be no SetWindowText in user32
		// user32.SetWindowText(hwnd, titleText, windowTextMaxLength);
		// however
		// http://www.javased.com/?source_dir=file.monitor/com.sun.jna.examples.win32/src/com/sun/jna/examples/win32/User32.java

		// Get the Maximum height and width for all buttons
		Point y = new Point(0, 0);
		Control[] ca = shell.getChildren();
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] instanceof Button) {
				Button _b = (Button) ca[i];
				_b.setData("name", ellipsisText(_b.getText(), 10));
				Point tmp = ((Button) ca[i]).computeSize(SWT.DEFAULT, SWT.DEFAULT);
				bSize.x = Math.max(bSize.x, tmp.x);
				bSize.y = Math.max(bSize.y, tmp.y);
				System.err.println(String.format("Measuring %s: width: %d height: %d",
						_b.getData("name"), tmp.x, tmp.y));
			}
		}
		System.err.println(String.format(
				"Computed the maximum height and width for all buttons:  %d, %d",
				bSize.x, bSize.y));
		// Set the height and width for all buttons to the Maximum size
		// NOTE: hacking height
		bSize.y = (int) (1.7 * bSize.y);
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] instanceof Button) {
				Button _b = (Button) ca[i];
				System.err.println("Resizing " + _b.getData("name"));
				GridData gData = (GridData) _b.getLayoutData();
				gData.widthHint = bSize.x;
				gData.heightHint = bSize.y;
			}
		}

		shell.pack();
		Rectangle screen = display.getMonitors()[0].getBounds();
		// center on the screen
		shell.setBounds(screen.width / 2 - 320, screen.height / 2 - 200, 640, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static String ellipsisText(String text, int size) {

		String _text = text.replaceAll("\n", " ");
		return String.format("%s%s",
				(_text.length() > size) ? _text.substring(0, size) : _text,
				(OSUtils.getOsName().equals("windows")) ? "..." : "\u2026");
	}

	private static String ellipsisText(String text) {
		return ellipsisText(text, 10);
	}
}
