package custom.swt.widgets;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;

// origin: https://raw.githubusercontent.com/dfuchss/swt-utils/master/src/main/java/org/fuchss/swt/widgets/MultilineButton.java
public class MultilineButton extends Button {

	public MultilineButton(Composite parent, int style) {
		super(parent, style | SWT.WRAP | SWT.PUSH | SWT.LEFT);
		// wrong layout class
		// this.setLayoutData(
		// new GridData(SWT.WRAP | SWT.PUSH | SWT.LEFT, SWT.LEFT, true, false));
	}

	// Disable the check that prevents subclassing of SWT components
	@Override
	protected final void checkSubclass() {
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		Point size = super.computeSize(wHint, hHint, changed);
		GC gc = new GC(this);

		String text = this.getText();
		Point mlSize = gc.textExtent(text, SWT.DRAW_DELIMITER | SWT.LEFT);
		Point simpleSize = gc.textExtent(text.replace('\n', ' '));

		gc.dispose();

		size.x -= simpleSize.x - mlSize.x;
		size.y += mlSize.y - simpleSize.y;

		return size;
	}

	// origin:
	// https://www.programcreek.com/java-api-examples/org.eclipse.swt.graphics.Font
	private Font createFont(final ResourceManager resourceManager) {
		final Font defaultFont = resourceManager.getDevice().getSystemFont();

		if (defaultFont == null) {
			return null;
		}

		final FontData fd[] = FontDescriptor.copy(defaultFont.getFontData());
		if (fd == null) {
			return null;
		}

		for (final FontData f : fd) {
			if (this.fontSize > 0) {
				f.setHeight(this.fontSize);
			}
		}
		return resourceManager.createFont(FontDescriptor.createFrom(fd));
	}

	private final static int fontSize = 9;

	// testing the widget

	private static Shell shell;
	static Display display = new Display();

	private final static int formWidth = 250;
	private final static int formHeight = 120;

	public static void main(String[] args) {
		shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final MultilineButton button = new MultilineButton(shell, SWT.PUSH); // NONE?
		// TODO: display.getSystemFont()
		Font font = new Font(display, "Helvetica", 9, SWT.NORMAL);
		// https://www.programcreek.com/java-api-examples/?class=org.eclipse.swt.SWT&method=LEFT
		button.setFont(font);
		button.addSelectionListener(new SelectionListener() {
			// required
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			// required
			public void widgetSelected(SelectionEvent e) {
				// TODO: close Shell
				shell.dispose();
			}
		});
		button.setText("start\ncontinue\nmore action\nfinishing\nfinished");
		shell.pack();
		shell.setSize(formWidth, formHeight);

		shell.open();
		while (!shell.isDisposed()) { // Event loop.
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}