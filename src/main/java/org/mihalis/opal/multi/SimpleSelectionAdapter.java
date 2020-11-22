package org.mihalis.opal.multi;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class SimpleSelectionAdapter implements SelectionListener {

	// see
	// org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		this.handle(e);

	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		this.handle(e);

	}

	public abstract void handle(SelectionEvent e);

}