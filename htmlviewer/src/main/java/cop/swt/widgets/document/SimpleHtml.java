package cop.swt.widgets.document;

public class SimpleHtml extends HtmlDocument {
	public void println(String html) {
		partBody.append(html).append(HTML_TAG_NEW_LINE).append("\n");
	}

	public void print(String html) {
		partBody.append(html);
	}
}
