package custom.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class Container extends Composite {

	public Container(Composite parent, int style) {
		super(parent, style);
		/*
		setLayout(new LayoutDelegate().layout(this::onLayout)
				.computeSize(this::onComputeSize));
				*/
	}

	protected void onLayout(Composite composite, boolean flushCache) {

	}

	protected Point onComputeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		return new Point(0, 0);
	}

	public void dispose() {
		if (isDisposed())
			return;

		super.dispose();
		onDisposed();
	}

	protected void onDisposed() {
	}

}
