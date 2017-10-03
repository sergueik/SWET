package cop.swt.widgets.templates;

import cop.swt.widgets.HtmlTag;
import cop.swt.widgets.document.HtmlDocument;

public interface IHtmlTemplate<T> {
	HtmlTag getDelimeter();

	HtmlDocument getHtmlDocument();

	String getHtml(T obj);
}
