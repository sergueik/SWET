package org.swet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

// origin: http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/JFacesIconAndMessageDialog.htm

class IconAndMessageDialogEx extends IconAndMessageDialog {

	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		(new IconAndMessageDialogEx(shell)).open();
	}

	public static final int I_DUNNO_ID = IDialogConstants.CLIENT_ID;
	public static final String I_DUNNO_LABEL = "I Dunno";
	private Image image;
	private Label label;
	private String message;
	
	public IconAndMessageDialogEx(Shell parent) {
		super(parent);

		// Create the image
		try {
			image = new Image(parent.getDisplay(), new FileInputStream("facepalm.png"));
		} catch (FileNotFoundException e) {
		}

		// Set the default message
		message = "Are you sure you want to do something that dumb?";
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean close() {
		if (image != null)
			image.dispose();
		return super.close();
	}

	protected Control createDialogArea(Composite parent) {
		createMessageArea(parent);

		// Create a composite to hold the label
		Composite composite = new Composite(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);
		composite.setLayout(new FillLayout());

		// Create the label for the "hidden" message
		label = new Label(composite, SWT.LEFT);

		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, IDialogConstants.YES_LABEL,
				true);
		createButton(parent, IDialogConstants.NO_ID, IDialogConstants.NO_LABEL,
				false);
		createButton(parent, I_DUNNO_ID, I_DUNNO_LABEL, false);
	}

	protected void buttonPressed(int buttonId) {
		// If they press I Dunno, close the dialog
		if (buttonId == I_DUNNO_ID) {
			setReturnCode(buttonId);
			close();
		} else {
			// Otherwise, have some fun
			label.setText("Yeah, right. You know nothing.");
		}
	}

	protected Image getImage() {
		return image;
	}
}