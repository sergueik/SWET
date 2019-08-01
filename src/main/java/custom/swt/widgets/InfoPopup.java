package custom.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import custom.swt.widgets.PopupDialog;


// origin: https://github.com/cosbi-research/SWTCustomComponents/blob/master/src/custom/swt/widgets/InfoPopup.java
public class InfoPopup extends PopupDialog {

	private static Display display;
	private static Shell shell;

	private Composite popupContent;
	private Composite parent;
	private String content;

	public InfoPopup(Composite parent, String textToBeDisplayed) {
		super(parent.getShell(), SWT.ON_TOP, true, false, false, false, false, null,
				null);
		content = textToBeDisplayed;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.parent = parent;
		// parent.setBackground(white);
		popupContent = new Composite(parent, SWT.NONE);
		// popupContent.setBackground(white);
		disposeContent();
		updateContent();
		return popupContent;
	}

	private void disposeContent() {
		for (Control child : popupContent.getChildren())
			child.dispose();
	}

	private void updateContent() {

		GridLayout gridLayout = new GridLayout(1, false);
		popupContent.setLayout(gridLayout);

		Label label = new Label(popupContent, SWT.NONE);
		label.setText(content);
	}

	@Override
	protected Color getBackground() {
		return parent.getBackground();
	}

	@Override
	protected void adjustBounds() {
		getShell().setBounds(x, y, getShell().getSize().x, getShell().getSize().y);
	}

	private int x;
	private int y;

	public void setPosition(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	@Override
	public boolean close() {
		boolean ret = super.close();
		return ret;
	}

}