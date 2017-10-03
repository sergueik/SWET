package cop.swt.widgets;

import static org.eclipse.swt.SWT.FILL;
import static org.eclipse.swt.SWT.NONE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.LoggerFactory;

import cop.swt.widgets.SimpleHtmlViewer;

public class SimpleViewer extends Composite {
	private SimpleHtmlViewer htmlViewer;

	public SimpleViewer(Composite parent, int style) {
		super(parent, style);

		LoggerFactory.getLogger(getClass()).debug("SimpleViewer");

		setLayout(new GridLayout());
		// setBackground(WHITE);

		createPartControl();
	}

	protected SimpleHtmlViewer getHtmlViewer() {
		return htmlViewer;
	}

	private void createPartControl() {
		createHtmlViewerPart();
		refresh();
	}

	private void createHtmlViewerPart() {
		htmlViewer = new SimpleHtmlViewer(this, NONE);
		htmlViewer.setLayoutData(new GridData(FILL, FILL, true, true));
	}

	public void refresh() {
		refreshCore();
	}

	protected void refreshCore() {
		htmlViewer.clear();
		htmlViewer.print(SimpleGenerator.lines);
	}

	private static final class SimpleGenerator extends Thread {
		private static final int SIZE = 200;
		public static final List<String> lines = new ArrayList<>(SIZE);

		static {
			for (int i = 0; i < SIZE; i++)
				lines.add("This author <a href=\"mailto:abba-best@mail.ru\">Cherednik, Oleg</a> is a line number " + (i + 1));
		}

		/*
		 * Runnable
		 */

		@Override
		public void run() {
		}
	}
}