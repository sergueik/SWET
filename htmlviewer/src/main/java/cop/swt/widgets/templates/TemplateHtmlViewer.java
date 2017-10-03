package cop.swt.widgets.templates;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import cop.swt.widgets.HtmlViewer;

public class TemplateHtmlViewer<T> extends HtmlViewer<T> {
	private final IHtmlTemplate<T> template;

	public TemplateHtmlViewer(Composite parent, int style,
			IHtmlTemplate<T> template) {
		super(parent, style, template.getHtmlDocument());

		this.template = template;
	}

	/*
	 * HtmlViewer
	 */

	@Override
	public void println(T obj) {
		addTextLn(template.getHtml(obj));
	}

	@Override
	public void print(T obj) {
		try {
			super.addText(template.getHtml(obj));
		} catch (Exception e) {
		}
	}

	@Override
	public void print(Collection<T> objs) {
		int i = 0;

		for (T obj : objs) {
			if (i++ > 0)
				addText("\n" + template.getDelimeter() + "\n");

			addText(template.getHtml(obj));
		}

		refresh();
	}
}
