package org.swet;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DirecroryBrowseLayoutEx {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		int formWidth = 750;
		int formHeight = 280;
		shell.setSize(formWidth, formHeight);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.makeColumnsEqualWidth = false;
		shell.setLayout(gridLayout);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		DirBrowseComposite dirBrowseComposite2 = new DirBrowseComposite(composite);
		dirBrowseComposite2.pack();
		DirBrowseComposite dirBrowseComposite3 = new DirBrowseComposite(shell);
		dirBrowseComposite3.pack();
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private static class DirBrowseComposite extends Composite {

		int buttonWidth = 120;
		int buttonHeight = 28;
		int labelWidth = 150;

		public DirBrowseComposite(Composite composite) {
			super(composite, SWT.NONE);
			this.setSize(this.getParent().getBounds().width,
					this.getParent().getBounds().height /* buttonHeight*/ );
			this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout gridLayout = new GridLayout(3, true);
			gridLayout.makeColumnsEqualWidth = false;
			this.setLayout(gridLayout);
			Label labelDirPath = new Label(this, SWT.NONE);
			// gridDateLabel.widthHint = labelWidth;
			// gridDateLabel.heightHint = buttonHeight;
			labelDirPath.setLayoutData(new GridData(labelWidth, SWT.DEFAULT));
			labelDirPath.setText("Template Directory");

			final Text dirPathValue = new Text(this, SWT.SINGLE | SWT.BORDER);

			GridData gridDateDirPathValue = new GridData(GridData.FILL,
					GridData.CENTER, true, false);
			gridDateDirPathValue.widthHint = this.getBounds().width - 70;
			gridDateDirPathValue.heightHint = buttonHeight;
			dirPathValue.setLayoutData(gridDateDirPathValue);

			final Button buttonDirBrowse = new Button(this, SWT.PUSH);
			GridData gridDataButtonDirBrowse = new GridData(GridData.FILL,
					GridData.CENTER, false, false);
			gridDataButtonDirBrowse.widthHint = 70;
			gridDataButtonDirBrowse.heightHint = buttonHeight;
			buttonDirBrowse.setLayoutData(gridDataButtonDirBrowse);
			buttonDirBrowse.setText("Browse");

			buttonDirBrowse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					DirectoryDialog dlg = new DirectoryDialog(composite.getShell());

					dlg.setFilterPath(dirPathValue.getText());
					dlg.setText("Template Directory Dialog");
					dlg.setMessage("Select a directory");

					String dir = dlg.open();
					if (dir != null) {
						dirPathValue.setText(dir);
					}
				}
			});
		}

		public void renderData(Map<String, String> data) {
			// Create the children of the composite.
		}
	}

}
