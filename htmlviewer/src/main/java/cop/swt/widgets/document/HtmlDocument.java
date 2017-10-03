package cop.swt.widgets.document;

import common.extensions.StringExtension;
import cop.swt.widgets.HtmlTag;
import cop.swt.widgets.css.CssStyleSet;

public class HtmlDocument {
	protected static final HtmlTag HTML_PART_HTML = HtmlTag.create("html");
	protected static final HtmlTag HTML_PART_HEAD = HtmlTag.create("head");
	protected static final HtmlTag HTML_PART_TITLE = HtmlTag.create("title");
	protected static final HtmlTag HTML_PART_STYLE = HtmlTag.create("style");
	protected static final HtmlTag HTML_PART_BODY = HtmlTag.create("body");
	protected static final HtmlTag HTML_TAG_NEW_LINE = HtmlTag.create("br");

	private final String partHead;
	private static final String partBodyOpen = "<body style=\"overflow:auto\">";
	protected final StringBuilder partBody = new StringBuilder(1024);

	public HtmlDocument() {
		this(null, null);
	}

	public HtmlDocument(CssStyleSet styles) {
		this(null, styles);
	}

	public HtmlDocument(String title, CssStyleSet styles) {
		partHead = createHeadPart(title, styles);
	}

	private void clearBodyPart() {
		partBody.replace(0, partBody.length(), "");
	}

	private static String createHeadPart(String title, CssStyleSet styles) {
		if (StringExtension.isEmpty(title) && (styles == null || styles.isEmpty()))
			return null;

		StringBuilder buf = new StringBuilder(255);

		buf.append(HTML_PART_HEAD.open());
		addTitlePart(buf, title);
		addStylesPart(buf, styles);
		buf.append("\n").append(HTML_PART_HEAD.close());

		return buf.toString();
	}

	private static void addTitlePart(StringBuilder buf, String title) {
		if (!StringExtension.isEmpty(title))
			buf.append("\n").append(HTML_PART_TITLE.open()).append(title)
					.append(HTML_PART_TITLE.close());
	}

	private static void addStylesPart(StringBuilder buf, CssStyleSet styles) {
		if (styles == null || styles.isEmpty())
			return;

		buf.append("\n").append(HTML_PART_STYLE.open());
		styles.append(buf.append("\n"));
		buf.append("\n").append(HTML_PART_STYLE.close());
	}

	public boolean isEmpty() {
		return partBody.length() == 0;
	}

	public void println() {
		partBody.append(HTML_TAG_NEW_LINE).append("\n");
	}

	public void addHtml(String html) {
		partBody.append(html);
	}

	public void addNewLine() {
		partBody.append(HTML_TAG_NEW_LINE);
	}

	protected void addHeadPart(StringBuilder buf) {
		if (!StringExtension.isEmpty(partHead))
			buf.append("\n").append(partHead);
	}

	protected void addBodyPart(StringBuilder buf) {
		if (partBody.length() == 0)
			return;

		buf.append("\n").append(partBodyOpen).append("\n");
		buf.append(partBody).append("\n").append(HTML_PART_BODY.close());
	}

	public String getHtml() {
		StringBuilder buf = new StringBuilder(1024);

		buf.append(HTML_PART_HTML.open());
		addHeadPart(buf);
		addBodyPart(buf);
		buf.append("\n").append(HTML_PART_HTML.close());

		return buf.toString();
	}

	public void setText(String html) {
		clearBodyPart();
		partBody.append(html);
	}

	public String getText() {
		return partBody.toString();
	}

	public void clear() {
		clearBodyPart();
	}

	public void addText(String html) {
		partBody.append(html);
	}

	/*
	 * Object
	 */

	@Override
	public String toString() {
		return getHtml();
	}
}
