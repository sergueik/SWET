package cop.swt.widgets;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

public class SimpleHtmlViewer extends HtmlViewer<String> {
	public SimpleHtmlViewer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void println(String html) {
		addTextLn(html);
	}

	@Override
	public void print(String html) {
		addText(html);
	}

	@Override
	public void print(Collection<String> html) {
		int size = html.size();
		int i = 0;

		for (String part : html)
			addText(part, ++i < size);

		refresh();
	}
}
