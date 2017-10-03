package cop.swt.widgets;

import static common.extensions.StringExtension.isEmpty;
import static cop.swt.widgets.HtmlTag.HTML_TAG_CLOSE;
import static cop.swt.widgets.HtmlTag.HTML_TAG_END;
import static cop.swt.widgets.HtmlTag.HTML_TAG_OPEN;

import java.io.IOException;

import cop.swt.widgets.enums.HtmlColorEnum;
import cop.swt.widgets.interfaces.IAppendable;

public final class HtmlExtension {
	/**
	 * Closed constructor
	 */
	private HtmlExtension() {}

	public static String openTag(String tag) {
		return HTML_TAG_OPEN + tag + HTML_TAG_CLOSE;
	}

	public static String closeTag(String tag) {
		return HTML_TAG_OPEN + HTML_TAG_END + tag + HTML_TAG_CLOSE;
	}

	public static StringBuilder openTag(StringBuilder buf, String tag, HtmlContext context) {
		if (buf == null || isEmpty(tag))
			return buf;

		buf.append(HTML_TAG_OPEN);
		buf.append(tag);

		if (context != null && !context.isEmpty())
			context.append(buf.append(" "));

		buf.append(HTML_TAG_CLOSE);

		return buf;
	}

	public static String printBuffer(IAppendable buf) {
		try {
			if (buf != null && !buf.isEmpty())
				return buf.append(new StringBuilder()).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}
}
