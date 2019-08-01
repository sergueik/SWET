package custom.swt.widgets;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

// origin: https://github.com/brotenet/swt-ext/blob/master/src/org/eclipse/swt/controls/collective/MultiSelectCombo.java
public class MultiSelectCombo extends Canvas {
	private static final long serialVersionUID = 1L;
	private Text txtValues;
	private String[] items = new String[] {};
	private String separator = ",";
	private Image image = new Image(null, MultiSelectCombo.class
			.getResourceAsStream("/org/eclipse/swt/controls/collective/down.png"));
	private Button btnMenu;

	@SuppressWarnings("serial")
	public MultiSelectCombo(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		txtValues = new Text(this, SWT.BORDER);
		txtValues.setEditable(false);
		txtValues
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		btnMenu = new Button(this, SWT.NONE);
		btnMenu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Point control_point = ((Control) event.widget).toDisplay(1,
						1 + ((Control) event.widget).getBounds().height);
				Robot robot = null;
				try {
					robot = new Robot();
				} catch (AWTException exception) {
					exception.printStackTrace();
				}
				robot.mouseMove(control_point.x, control_point.y);

				Menu menu = new Menu(btnMenu);
				for (String item_text : getItems()) {
					MenuItem item = new MenuItem(menu, SWT.NONE);
					item.setText(item_text);
					item.addListener(SWT.Selection, new Listener() {

						@Override
						public void handleEvent(Event event) {
							if ((txtValues.getText().trim().length() > 0)
									& !txtValues.getText().trim().endsWith(getSeparator())) {
								txtValues.setText(txtValues.getText().trim() + getSeparator());
							}
							if (!txtValues.getText().contains(
									((MenuItem) event.widget).getText() + getSeparator())) {
								txtValues.setText(txtValues.getText()
										+ ((MenuItem) event.widget).getText() + getSeparator());
							} else {
								txtValues.setText(txtValues.getText().replace(
										((MenuItem) event.widget).getText() + getSeparator(), ""));
							}
							if (txtValues.getText().endsWith(getSeparator())) {
								txtValues.setText(txtValues.getText().substring(0,
										txtValues.getText().length() - 1));
							}
						}
					});
				}
				menu.setVisible(true);

			}
		});
		btnMenu.setImage(image);
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		ArrayList<String> elements = new ArrayList<String>(Arrays.asList(items));
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).trim().length() < 1) {
				elements.remove(i);
			}
		}
		this.items = elements.toArray(new String[elements.size()]);
	}

	public String getText() {
		return txtValues.getText();
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public void setForeground(Color color) {
		txtValues.setForeground(color);
	}

	@Override
	public Color getForeground() {
		return txtValues.getForeground();
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		btnMenu.setImage(getImage());
	}

	public void setFont(Font font) {
		txtValues.setFont(font);
	}

	public Font getFont() {
		return txtValues.getFont();
	}

	public void setValues(String[] values) {
		txtValues.setText("");
		for (String value : values) {
			txtValues.setText(txtValues.getText() + value.trim() + getSeparator());
		}
		if (txtValues.getText().endsWith(getSeparator())) {
			txtValues.setText(
					txtValues.getText().substring(0, txtValues.getText().length() - 1));
		}
	}

	public String[] getValues() {
		return getText().split(getSeparator());
	}
}