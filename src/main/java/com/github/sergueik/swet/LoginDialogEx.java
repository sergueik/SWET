package com.github.sergueik.swet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// based on: https://github.com/lcaron/opal/blob/master/src/main/java/org/mihalis/opal/login/LoginDialog.java

public class LoginDialogEx {
	static final Logger logger = LoggerFactory.getLogger(LoginDialogEx.class);
	private String description = "Custom Login Dialog";
	private String login;
	private String password;

	private int stickyHeight = 240;
	private Text passwordText;
	private boolean displayPassword = false;

	private Shell shell;
	private static Display display;

	private boolean returnedValue;
	private Button buttonOk;

	public boolean getReturnedValue() {
		return returnedValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String value) {
		description = value;
	}

	public String getLogin() {
		return login == null ? null : login.trim();
	}

	public void setLogin(final String value) {
		login = value;
	}

	public String getPassword() {
		return password == null ? null : password.trim();
	}

	public void setPassword(final String value) {
		password = value;
	}

	/*
	 * public LoginDialogVerifier getVerifier() { return verifier; } public void
	 * setVerifier(final LoginDialogVerifier value) { verifier = value; }
	 */

	public LoginDialogEx() {
	}

	public static void main(String[] args) {
		// Create login dialog
		logger.info("Initialized logger.");
		display = new Display();
		LoginDialogEx loginDialog = new LoginDialogEx();

		if (loginDialog.open()) {
			logger.info(String.format("User: %s Password: %s", loginDialog.getLogin(),
					loginDialog.getPassword()));
		} else {
			logger.info("Login canceled");
		}
	}

	public boolean open() {
		/*
		 * if (verifier == null) { throw new IllegalArgumentException(
		 * "Please set a verifier before opening the dialog box"); }
		 */
		shell = new Shell(display,
				SWT.SYSTEM_MODAL | /* SWT.TITLE | */ SWT.BORDER | SWT.RESIZE);
		// TODO: manage size
		// https://www.eclipse.org/articles/Article-Understanding-Layouts/Understanding-Layouts.htm
		// http://www.docjar.org/docs/api/org/eclipse/swt/layout/GridData.html
		// https://stackoverflow.com/questions/6004134/setting-size-of-inner-region-of-java-swt-shell-window
		shell.setSize(400, stickyHeight);
		shell.setBounds(20, 20, 420, stickyHeight + 20);
		shell.setFullScreen(false);
		shell.setText("Login");
		shell.setLayout(new GridLayout(4, false));
		shell.addListener(SWT.Resize, rzListener);
		final Label label = new Label(shell, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, true,
				false, 4, 1);
		gridData.verticalIndent = 5;
		gridData.horizontalIndent = 5;
		label.setLayoutData(gridData);

		if (description == null || description.trim().equals("")) {
			label.setText(" ");
		} else {
			label.setText(description);
		}
		final Label loginLabel = new Label(shell, SWT.NONE);
		gridData = new GridData(GridData.END, GridData.END, false, false, 1, 1);
		gridData.horizontalIndent = 35;
		gridData.verticalIndent = 15;
		loginLabel.setLayoutData(gridData);
		loginLabel.setText("Name");

		final Text loginText = new Text(shell, SWT.BORDER);
		loginText.setText(login == null ? "" : login);
		loginText.setLayoutData(
				new GridData(GridData.FILL, GridData.END, true, false, 3, 1));
		loginText.setFocus();
		loginText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				login = ((Text) e.getSource()).getText();
				changeButtonOkState();
			}
		});

		final Label passwordLabel = new Label(shell, SWT.NONE);
		gridData = new GridData(GridData.END, GridData.CENTER, false, false, 1, 1);
		gridData.horizontalIndent = 35;
		passwordLabel.setLayoutData(gridData);
		passwordLabel.setText("Password");

		// Control styles cannot be changed after creation time
		// operate echoChar instead
		passwordText = new Text(shell, SWT.SINGLE | SWT.BORDER);
		// final Text text = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		passwordText.setText(password == null ? "" : password);
		passwordText.setLayoutData(
				new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		passwordText.setEchoChar(new Character((char) 0x2a));
		logger.debug("Echo character: " + passwordText.getEchoChar());

		passwordText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				//
				password = ((Text) e.getSource()).getText();
				changeButtonOkState();
			}
		});
		final Button displayPasswordCheckBox = new Button(shell, SWT.CHECK);

		gridData = new GridData(GridData.BEGINNING, GridData.CENTER,
				/* grab excess horizontal space */ true,
				/* grab excess vertical space */ false, 4, 1);
		gridData.horizontalIndent = 35;
		displayPasswordCheckBox.setLayoutData(gridData);
		displayPasswordCheckBox.setText("Display password");
		displayPasswordCheckBox.setSelection(displayPassword);
		displayPasswordCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				displayPassword = !displayPassword;
				// https://www.codota.com/code/java/methods/org.eclipse.swt.widgets.Text/setEchoChar
				if (displayPassword) {
					passwordText.setEchoChar('\0');
					logger.info("Display password");
				} else {
					passwordText.setEchoChar((char) 0x2a);
					logger.info("Mask password");
				}
			}
		});

		buttonOk = new Button(shell, SWT.PUSH);
		gridData = new GridData(GridData.END, GridData.CENTER, true, false, 3, 1);
		gridData.verticalIndent = 60;
		gridData.minimumWidth = 80;
		buttonOk.setLayoutData(gridData);
		buttonOk.setText("OK");
		buttonOk.setEnabled(false);

		buttonOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				try {
					// verifier.authenticate(login, password);
					returnedValue = true;
					shell.dispose();
				} catch (final Exception e) {
					// Dialog.error("Login failed", e.getMessage());
					for (final Control control : shell.getChildren()) {
						if (control instanceof Text || control instanceof Combo) {
							control.setFocus();
							break;
						}
					}
				}
			}
		});
		final Button buttonCancel = new Button(shell, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gridData.widthHint = 80;
		gridData.verticalIndent = 60;
		buttonCancel.setLayoutData(gridData);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				returnedValue = false;
				shell.dispose();
			}
		});
		shell.setDefaultButton(buttonOk);
		shell.pack();
		shell.open();
		// SWTGraphicUtil.centerShell(shell);
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
		return returnedValue;
	}

	private boolean isNonEmpty(final String value) {
		return (value != null && !value.trim().equals(""));
	}

	private void changeButtonOkState() {
		buttonOk.setEnabled(isNonEmpty(login) && isNonEmpty(password));
	}

	// fixed height resize
	Listener rzListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Resize:
				Point ptSz = shell.getSize();
				logger.debug("Resize Event received: " + event + " sz=" + ptSz);
				if (ptSz.y != stickyHeight) {
					// stick to fixed height
					shell.setSize(ptSz.x, stickyHeight);
				}
				break;
			}
		}
	};
}
