package org.swet;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DirecroryBrowseLayoutEx {
	private static Shell shell;
	private static HashMap<String, String> elementData = new HashMap<String, String>(); // empty
	private final static int formWidth = 750;
	private final static int formHeight = 280;
	private final static int buttonWidth = 120;
	private final static int buttonHeight = 28;
	private final static int labelWidth = 150;

	public static void main(String[] args) {
		Display display = new Display();
		shell = new Shell(display);
		shell.setSize(formWidth, formHeight);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.makeColumnsEqualWidth = false;
		shell.setLayout(gridLayout);
		/*
		Composite dirBrowseComposite = new Composite(shell, SWT.NONE);
		dirBrowseComposite.setBounds(0, 0, shell.getBounds().width, 32);
		dirBrowseComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout(3, true);
		gridLayout.makeColumnsEqualWidth = false;
		dirBrowseComposite.setLayout(gridLayout);
		Label labelDirPath = new Label(dirBrowseComposite, SWT.NONE);
		labelDirPath.setLayoutData(new GridData(labelDirPathWidth, SWT.DEFAULT));
		labelDirPath.setText("Directory");

		final Text dirPathValue = new Text(dirBrowseComposite,
				SWT.SINGLE | SWT.BORDER);

		GridData gridDateDirPathValue = new GridData(GridData.FILL, GridData.CENTER,
				true, false);
		gridDateDirPathValue.widthHint = dirBrowseComposite.getBounds().width - 120;
		gridDateDirPathValue.heightHint = buttonHeight;
		dirPathValue.setLayoutData(gridDateDirPathValue);

		final Button buttonDirBrowse = new Button(dirBrowseComposite, SWT.PUSH);
		GridData gridDataButtonDirBrowse = new GridData(GridData.FILL,
				GridData.CENTER, true, false);
		gridDataButtonDirBrowse.widthHint = 90;
		gridDataButtonDirBrowse.heightHint = buttonHeight;
		buttonDirBrowse.setLayoutData(gridDataButtonDirBrowse);
		buttonDirBrowse.setText("Browse");

		buttonDirBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);

				dlg.setFilterPath(dirPathValue.getText());
				dlg.setText("Template Directory Dialog");
				dlg.setMessage("Select a directory");

				String dir = dlg.open();
				if (dir != null) {
					dirPathValue.setText(dir);
				}
			}
		});

		dirBrowseComposite.pack();
		*/

    Composite dirBrowseComposite = new Composite(shell, SWT.NONE);
		dirBrowseComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		DirBrowseComposite dirBrowseComposite2 = new DirBrowseComposite(dirBrowseComposite);
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

		public DirBrowseComposite(Composite composite) {
			super(composite, SWT.NONE);
			this.setSize(this.getParent().getBounds().width, this.getParent().getBounds().height /* buttonHeight*/ );
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
					DirectoryDialog dlg = new DirectoryDialog(shell);

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

		public void renderData(HashMap<String, String> data) {
			// Create the children of the composite.
		}
	}
}
