package com.github.sergueik.swet;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

// based on: https://github.com/lcaron/opal/blob/master/src/main/java/org/mihalis/opal/login/LoginDialog.java

public class LoginDialogEx {
	@SuppressWarnings("deprecation")
	static final Logger logger = (Logger) Logger.getInstance(LoginDialogEx.class);
	private Image image;
	private String description;
	private String login;
	private String password;
	private List<String> autorizedLogin;
	// private boolean displayRememberPassword;
	// private boolean rememberPassword;
	// private LoginDialogVerifier verifier;

	private Text passwordText;
	private boolean displayPassword = false;

	private Shell shell;
	private static Display display;

	private boolean returnedValue;
	private Button buttonOk;

	public LoginDialogEx() {
		// this.displayRememberPassword = true;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		// Create login dialog
		logger.info("Initialized logger.");
		display = new Display();
		LoginDialogEx loginDialog = new LoginDialogEx();
		loginDialog.open();
	}

	public boolean open() {
		/*
		if (this.verifier == null) {
			throw new IllegalArgumentException(
					"Please set a verifier before opening the dialog box");
		}
		*/
		buildDialog();
		openShell();

		return this.returnedValue;
	}

	private void buildDialog() {
		buildShell();
		// buildImage();
		buildDescription();
		buildLogin();
		buildPassword();
		/*
		if (this.displayRememberPassword) {
			buildRememberPassword();
		}
		*/
		buildDisplayPassword();
		buildButtons();
	}

	private void buildShell() {
		shell = new Shell(display, SWT.SYSTEM_MODAL | /* SWT.TITLE | */ SWT.BORDER);
		// https://stackoverflow.com/questions/6004134/setting-size-of-inner-region-of-java-swt-shell-window
		shell.setSize(400, 60);
		shell.setText("Login");
		shell.setLayout(new GridLayout(4, false));
	}

	private void buildImage() {
		final Canvas canvas = new Canvas(this.shell, SWT.DOUBLE_BUFFERED);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				false, 4, 1);
		gridData.widthHint = 400;
		gridData.heightHint = 60;
		canvas.setLayoutData(gridData);
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(final PaintEvent e) {
				e.gc.drawImage(LoginDialogEx.this.image == null
						? createDefaultImage(e.width, e.height) : LoginDialogEx.this.image,
						0, 0);
			}
		});

	}

	private Image createDefaultImage(final int w, final int h) {
		final Display display = Display.getCurrent();
		final Color backgroundColor = new Color(display, 49, 121, 242);
		final Color gradientColor1 = new Color(display, 155, 185, 245);
		final Color gradientColor2 = new Color(display, 53, 123, 242);

		final Image img = new Image(display, w, h);
		final GC gc = new GC(img);
		gc.setAdvanced(true);
		gc.setAntialias(SWT.ON);
		gc.setBackground(backgroundColor);
		gc.fillRectangle(0, 0, w, h);

		final Path curveShape = new Path(display);
		curveShape.moveTo(0, h * .6f);
		curveShape.cubicTo(w * .167f, h * 1.2f, w * .667f, h * -.5f, w, h * .75f);
		curveShape.lineTo(w, h);
		curveShape.lineTo(0, h);
		curveShape.lineTo(0, h * .8f);
		curveShape.close();

		final Pattern pattern = new Pattern(display, 0, 0, 1, h * 1.2f,
				gradientColor1, gradientColor2);
		gc.setBackgroundPattern(pattern);
		gc.fillPath(curveShape);

		final Font font = new Font(display, "Arial Bold", 30, SWT.NONE);
		gc.setFont(font);
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		final Point textSize = gc.stringExtent("Login");
		gc.drawString("Login", (int) (w * .05f), (h - textSize.y) / 2, true);

		font.dispose();
		curveShape.dispose();
		pattern.dispose();
		backgroundColor.dispose();
		gradientColor1.dispose();
		gradientColor2.dispose();
		gc.dispose();
		return img;
	}

	private void buildDescription() {
		final Label label = new Label(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 4, 1);
		gridData.verticalIndent = 5;
		gridData.horizontalIndent = 5;
		label.setLayoutData(gridData);
		// final Font bold = SWTGraphicUtil.buildFontFrom(label, SWT.BOLD);
		// label.setFont(bold);
		// SWTGraphicUtil.addDisposer(label, bold);

		if (this.description == null || this.description.trim().equals("")) {
			label.setText(" ");
		} else {
			label.setText(this.description);
		}
	}

	private void buildLogin() {
		final Label label = new Label(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.END, GridData.END, false,
				false, 1, 1);
		gridData.horizontalIndent = 35;
		gridData.verticalIndent = 15;
		label.setLayoutData(gridData);
		label.setText("Name");

		if (this.autorizedLogin != null && !this.autorizedLogin.isEmpty()) {
			// Combo
			buildLoginCombo();
		} else {
			// Text
			buildLoginText();
		}

	}

	private void buildLoginCombo() {
		final Combo combo = new Combo(this.shell, SWT.BORDER | SWT.READ_ONLY);

		combo.setLayoutData(
				new GridData(GridData.FILL, GridData.END, true, false, 3, 1));
		for (final String loginToAdd : this.autorizedLogin) {
			combo.add(loginToAdd);
		}
		combo.setText(this.login == null ? "" : this.login);
		combo.setFocus();
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				LoginDialogEx.this.login = combo.getText();
				changeButtonOkState();
			}
		});
	}

	private void buildLoginText() {
		final Text text = new Text(this.shell, SWT.BORDER);
		text.setText(this.login == null ? "" : this.login);
		text.setLayoutData(
				new GridData(GridData.FILL, GridData.END, true, false, 3, 1));
		text.setFocus();
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				LoginDialogEx.this.login = text.getText();
				changeButtonOkState();
			}
		});
	}

	private void buildPassword() {
		final Label label = new Label(this.shell, SWT.NONE);
		final GridData gridData = new GridData(GridData.END, GridData.CENTER, false,
				false, 1, 1);
		gridData.horizontalIndent = 35;
		label.setLayoutData(gridData);
		label.setText("Password");

		// Control styles cannot be changed after creation time
		// operate echoChar instead
		final Text text = new Text(this.shell, SWT.SINGLE | SWT.BORDER);
		// final Text text = new Text(this.shell, SWT.PASSWORD | SWT.BORDER);
		passwordText = text;
		text.setText(this.password == null ? "" : this.password);
		text.setLayoutData(
				new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		text.setEchoChar(new Character((char) 0x2a));
		logger.info("Echo character: " + text.getEchoChar());

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				//
				LoginDialogEx.this.password = ((Text) e.getSource()).getText();
				changeButtonOkState();
			}
		});
	}

	private void changeButtonOkState() {
		final boolean loginEntered = this.login != null
				&& !this.login.trim().equals("");
		final boolean passwordEntered = this.password != null
				&& !this.password.trim().equals("");
		this.buttonOk.setEnabled(loginEntered && passwordEntered);
	}
	/*
		private void buildRememberPassword() {
			final Button checkbox = new Button(this.shell, SWT.CHECK);
			//
			final GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER,
	 true,false,4,1);gridData.horizontalIndent=35;checkbox.setLayoutData(gridData);checkbox.setText("Remember password");checkbox.setSelection(this.rememberPassword);
	
	}
	*/

	private void buildDisplayPassword() {
		final Button checkbox = new Button(this.shell, SWT.CHECK);

		final GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER,
				/* grab excess horizontal space */ true,
				/* grab excess vertical space */ false, 4, 1);
		gridData.horizontalIndent = 35;
		checkbox.setLayoutData(gridData);
		checkbox.setText("Display password");
		checkbox.setSelection(this.displayPassword);
		checkbox.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
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

	}

	private void buildButtons() {
		buildOkButton();
		buildCancelButton();
	}

	private void buildOkButton() {
		this.buttonOk = new Button(this.shell, SWT.PUSH);
		final GridData gdOk = new GridData(GridData.END, GridData.CENTER, true,
				false, 3, 1);
		gdOk.verticalIndent = 60;
		gdOk.minimumWidth = 80;
		this.buttonOk.setLayoutData(gdOk);
		this.buttonOk.setText("OK");
		this.buttonOk.setEnabled(false);

		this.buttonOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				try {/*
							LoginDialog.this.verifier.authenticate(LoginDialog.this.login,
							LoginDialog.this.password);
							*/
					LoginDialogEx.this.returnedValue = true;
					LoginDialogEx.this.shell.dispose();
				} catch (final Exception e) {
					// Dialog.error("Login failed", e.getMessage());
					for (final Control control : LoginDialogEx.this.shell.getChildren()) {
						if (control instanceof Text || control instanceof Combo) {
							control.setFocus();
							break;
						}
					}
				}
			}
		});
	}

	private void buildCancelButton() {
		final Button buttonCancel = new Button(this.shell, SWT.PUSH);
		final GridData gdCancel = new GridData(GridData.FILL, GridData.CENTER,
				false, false);
		gdCancel.widthHint = 80;
		gdCancel.verticalIndent = 60;
		buttonCancel.setLayoutData(gdCancel);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				LoginDialogEx.this.returnedValue = false;
				LoginDialogEx.this.shell.dispose();
			}
		});
	}

	/**
	 * Open the shell
	 */
	private void openShell() {
		this.shell.setDefaultButton(this.buttonOk);
		this.shell.pack();
		this.shell.open();
		// SWTGraphicUtil.centerShell(this.shell);
		while (!this.shell.isDisposed()) {
			if (!this.shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
			}
		}
	}

	public Image getImage() {
		return this.image;
	}

	public String getDescription() {
		return this.description;
	}

	public String getLogin() {
		return this.login == null ? null : this.login.trim();
	}

	public String getPassword() {
		return this.password == null ? null : this.password.trim();
	}

	public List<String> getAutorizedLogin() {
		return this.autorizedLogin;
	}

	/*
		public boolean isDisplayRememberPassword() {
			return this.displayRememberPassword;
		}
	
		public boolean isRememberPassword() {
			return this.rememberPassword;
		}
	*/
	/**
	 * @return the verifier associated to this box
	 */

	/*
	public LoginDialogVerifier getVerifier() {
		return this.verifier;
	}
	*/

	public void setImage(final Image value) {
		image = value;
	}

	public void setDescription(final String value) {
		description = value;
	}

	public void setLogin(final String value) {
		login = value;
	}

	public void setPassword(final String value) {
		password = value;
	}

	public void setAutorizedLogin(final List<String> autorizedLogin) {
		this.autorizedLogin = autorizedLogin;
	}

	public void setAutorizedLogin(final String... autorizedLogin) {
		this.autorizedLogin = Arrays.asList(autorizedLogin);
	}

	/*
	public void setDisplayRememberPassword(
			final boolean value) {
		displayRememberPassword = value;
	}
	
	
	public void setRememberPassword(final boolean value) {
		rememberPassword = value;
	}
	*/
	/*
	public void setVerifier(final LoginDialogVerifier verifier) {
		this.verifier = verifier;
	}
	*/
}
