package org.mihalis.opal.multi;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

public abstract class MultiChoiceSelectionListener<T>
		implements SelectionListener {
	private final MultiChoiceEx<T> parent;

	public MultiChoiceSelectionListener(final MultiChoiceEx<T> parent) {
		this.parent = parent;
	}

	// see
	// org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	@Override
	public final void widgetSelected(final SelectionEvent e) {
		final Button button = (Button) e.widget;
		handle(parent, parent.getLastModified(), button.getSelection(),
				parent.getPopup());
	}

	// see
	// org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	@Override
	public final void widgetDefaultSelected(final SelectionEvent inutile) {
	}

	public abstract void handle(MultiChoiceEx<T> parent, T receiver,
			boolean selected /* was the check box checked */, Shell popup);
}