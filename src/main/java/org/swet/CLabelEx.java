package org.swet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

// http://stackoverflow.com/questions/10794190/how-to-add-text-and-image-both-in-a-swt-label
public class CLabelEx {

	private Image image;
	private Shell shell;
	private Display display;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;

	@SuppressWarnings("unused")
	public CLabelEx(Shell parentShell, Display parentDisplay) {
		shell = parentShell;
		display = parentDisplay;
		image = new Image(display,
				this.getClass().getClassLoader().getResourceAsStream(
						String.format("images/%s", "document_wrench_color_36.png")));
		Composite composite = new Composite(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.END, GridData.CENTER, false,
				false);
		gridData.widthHint = buttonWidth;
		gridData.heightHint = buttonHeight;

		// composite.setLayoutData(gridData);
		int numberOfColumns = 2;

		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				false, false, numberOfColumns, 1));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		composite.setLayout(new GridLayout(4, false));

		// CLabel label = new CLabel(composite, SWT.BORDER);
		// label.setImage(image);

		// label.setBackground(image);
		// label.setBounds(0, 0, 36, 36);
		// label.setText("Text");
		// label.pack();
		composite.pack();
	}

	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	public void render() {

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		CLabelEx o = new CLabelEx(shell, display);
		o.render();
		o.dispose();
		display.dispose();
	}
}