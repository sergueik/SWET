package cop.swt.widgets.templates;

import static common.extensions.StringExtension.isEmpty;

import java.util.StringTokenizer;

import cop.swt.widgets.HtmlTag;

public abstract class HtmlTemplate<T> implements IHtmlTemplate<T> {
	protected static final String MACRO_DELIMETER = "%";
	protected static final HtmlTag PART_DELIMETER = HtmlTag.create("hr");

	private final String macroDelimeter;
	private final HtmlTag partDelimeter;
	private final String template;

	protected HtmlTemplate() {
		this(null, null);
	}

	protected HtmlTemplate(String macroDelimeter, HtmlTag partDelimeter) {
		this.macroDelimeter = isEmpty(macroDelimeter) ? MACRO_DELIMETER
				: macroDelimeter;
		this.partDelimeter = (partDelimeter == null) ? PART_DELIMETER
				: partDelimeter;
		this.template = getTemplate();
	}

	protected final String getMacro(String macro) {
		return isEmpty(macro) ? "" : (macroDelimeter + macro + macroDelimeter);
	}

	protected abstract String getTemplate();

	protected abstract boolean setMacroValue(StringBuilder buf, String name,
			T obj);

	/**
	 * HtmlTemplate
	 */

	@Override
	public final HtmlTag getDelimeter() {
		return partDelimeter;
	}

	@Override
	public final String getHtml(T obj) {
		if (obj == null || isEmpty(template))
			return "";

		StringTokenizer st = new StringTokenizer(template, macroDelimeter);
		StringBuilder buf = new StringBuilder(1024);

		while (st.hasMoreTokens()) {
			String token = st.nextToken();

			if (!setMacroValue(buf, token, obj))
				buf.append(token);
		}

		return buf.toString();
	}
}
