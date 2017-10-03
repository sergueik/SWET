package cop.swt.widgets;

import static common.extensions.StringExtension.isEmpty;
import static cop.swt.widgets.HtmlExtension.closeTag;
import static cop.swt.widgets.HtmlExtension.openTag;

import java.util.HashMap;
import java.util.Map;

public final class HtmlTag {
	/**
		 * HTML <code>open</code> tag definition.<br>
		 * Default value: <b>'<'</b>
		 */
	public static final String HTML_TAG_OPEN = "<";
	/**
	 * HTML <code>close</code> tag definition.<br>
	 * Default value: <b>'>'</b>
	 */
	public static final String HTML_TAG_CLOSE = ">";
	/**
	 * HTML <code>encode</code> tag marker definition.<br>
	 * Default value: <b>'/'</b>
	 */
	public static final String HTML_TAG_END = "/";

	private static final Map<String, HtmlTag> map = new HashMap<String, HtmlTag>();
	private final String name;

	public static HtmlTag create(String name) {
		if (isEmpty(name))
			return null;

		HtmlTag tag = map.get(name);

		if (tag == null)
			map.put(name, tag = new HtmlTag(name));

		return tag;
	}

	private HtmlTag(String name) {
		this.name = name;
	}

	public StringBuilder append(StringBuilder buf, String content,
			HtmlContext context) {
		if (buf != null && !isEmpty(content))
			open(buf, context).append(content).append(close());

		return buf;
	}

	public String open() {
		return openTag(name);
	}

	public String close() {
		return closeTag(name);
	}

	public StringBuilder open(StringBuilder buf, HtmlContext context) {
		return openTag(buf, name, context);
	}

	/*
	 * Object
	 */

	@Override
	public String toString() {
		return open();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HtmlTag other = (HtmlTag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
