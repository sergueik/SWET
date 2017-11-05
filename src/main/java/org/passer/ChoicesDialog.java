package org.passer;

// origin: https://github.com/prasser/swtchoices
/*******************************************************************************
 * Copyright (c) 2015 Fabian Prasser
 *******************************************************************************/

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.swet.Utils;

public class ChoicesDialog {

	private final Shell parent;
	private String title = "";
	private String message = "";
	private Image image = null;
	private ChoiceItem[] choices;
	private int choice = -1;
	private final int style;
	private boolean showArrows = true;
	private ChoiceItem defaultChoice = null;
	private Utils utils = Utils.getInstance();

	public ChoicesDialog(Shell parent, int style) {
		this.checkStyle(style);
		this.parent = parent;
		this.style = style;
	}

	public ChoiceItem[] getChoices() {
		return choices;
	}

	public Image getImage() {
		return image;
	}

	public String getMessage() {
		return message;
	}

	public String getTitle() {
		return title;
	}

	public boolean getShowArrows() {
		return showArrows;
	}

	public int open() {

		Display display = parent.getDisplay();
		final Shell shell = new Shell(parent,
				style | SWT.TITLE | SWT.CLOSE | SWT.BORDER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 15;
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.marginRight = 15;
		shell.setLayout(layout);
		shell.setText(title);

		if (this.image != null) {
			Label lblImage = new Label(shell, SWT.NONE);
			lblImage.setImage(this.image);
			lblImage.setLayoutData(
					new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		}

		Label lblMessage = new Label(shell, SWT.NONE);
		lblMessage.setText(this.message);
		lblMessage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
				this.image != null ? 1 : 2, 1));

		final Image imageArrow;
		if (showArrows) {
			imageArrow = new Image(display,
					utils.getResourceStream(String.format("images/%s", "arrow.png")));

			shell.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					if (imageArrow != null && !imageArrow.isDisposed()) {
						imageArrow.dispose();
					}
				}
			});
		} else {
			imageArrow = null;
		}

		int index = 0;
		for (ChoiceItem item : choices) {

			if (this.showArrows) {
				Label lblImage = new Label(shell, SWT.NONE);
				lblImage.setImage(imageArrow);
				lblImage.setLayoutData(
						new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
			}

			Button btnChoice = new Button(shell,
					((item == defaultChoice) ? SWT.DEFAULT : 0) | SWT.PUSH);
			btnChoice.setText(item.getText());
			btnChoice.setToolTipText(item.getTooltipText());
			btnChoice.setLayoutData(
					new GridData(SWT.FILL, SWT.FILL, true, false, showArrows ? 1 : 2, 1));

			if (item == defaultChoice) {
				btnChoice.setFocus();
			}

			final int choice = index++;
			btnChoice.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					ChoicesDialog.this.choice = choice;
					shell.close();
				}
			});
		}

		shell.pack();
		center(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return choice;
	}

	public void setChoices(ChoiceItem... choices) {
		checkNull(choices);
		if (choices.length < 1) {
			throw new IllegalArgumentException(
					"You must provide at least one choice");
		}
		this.choices = choices;
	}

	public ChoiceItem getDefaultChoice() {
		return this.defaultChoice;
	}

	public void setDefaultChoice(ChoiceItem data) {
		this.defaultChoice = data;
	}

	public void setImage(Image data) {
		this.image = data;
	}

	public void setShowArrows(Boolean data) {
		this.showArrows = data;
	}

	public void setMessage(String data) {
		this.checkNull(data);
		this.message = data;
	}

	public void setTitle(String title) {
		this.checkNull(title);
		this.title = title;
	}

	private void center(Shell shell) {

		Shell parent = (Shell) shell.getParent();
		Rectangle bounds = parent.getBounds();
		Point size = shell.getSize();

		int x = bounds.x + bounds.width / 2 - size.x / 2;
		int y = bounds.y + bounds.height / 2 - size.y / 2;
		// System.err.println("ChoiceDialog: center: x=" + x + " y=" + y);
		shell.setLocation(x, (y < 0) ? 0 : y);
	}

	private void checkNull(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("Null is not a valid argument");
		}
	}

	private void checkStyle(int style) {
		if ((style & ~(SWT.APPLICATION_MODAL | SWT.PRIMARY_MODAL | SWT.SYSTEM_MODAL
				| SWT.MODELESS)) != 0) {
			throw new SWTException("Unsupported style");
		}
		if (Integer.bitCount(style) > 1) {
			throw new SWTException(
					"Unsupports only one of APPLICATION_MODAL, PRIMARY_MODAL, SYSTEM_MODAL or SWT.MODELESS");
		}
	}
}
