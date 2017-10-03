package cop.swt.widgets;

import static common.extensions.BitExtension.clearBits;
import static org.eclipse.swt.SWT.H_SCROLL;
import static org.eclipse.swt.SWT.V_SCROLL;

import java.util.Collection;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import cop.swt.widgets.document.HtmlDocument;

public abstract class HtmlViewer<T> {
	private final Browser browser;
	private final HtmlDocument html;

	public HtmlViewer(Composite parent, int style) {
		this(parent, style, new HtmlDocument());
	}

	public HtmlViewer(Composite parent, int style, HtmlDocument html) {
		this.html = html;
		this.browser = new Browser(parent, checkStyle(style));
	}

	protected static int checkStyle(int style) {
		return clearBits(style, H_SCROLL | V_SCROLL);
	}

	public void clear() {
		html.clear();
		refresh();
	}

	public boolean isEmpty() {
		return html.isEmpty();
	}

	public void println() {
		html.println();
		refresh();
	}

	public void setText(String html) {
		this.html.setText(html);
		refresh();
	}

	public String getText() {
		return html.getText();
	}

	public void refresh() {
		browser.setText(html.toString());
	}

	protected void addText(String html) {
		addText(html, false);
		refresh();
	}

	protected void addTextLn(String html) {
		addText(html, true);
		refresh();
	}

	protected void addText(String html, boolean newLine) {
		this.html.addText(html);

		if (newLine)
			this.html.println();
	}

	public void setLayout(Layout layout) {
		browser.setLayout(layout);
	}

	public void setLayoutData(Object layoutData) {
		browser.setLayoutData(layoutData);
	}

	/*
	 * abstract
	 */

	public abstract void print(Collection<T> html);

	public abstract void print(T html);

	public abstract void println(T html);
}
