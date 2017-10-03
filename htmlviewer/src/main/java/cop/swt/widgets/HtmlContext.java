package cop.swt.widgets;

import static cop.swt.widgets.HtmlExtension.printBuffer;
import common.extensions.StringExtension;
import cop.swt.widgets.css.CssContext;
import cop.swt.widgets.interfaces.IAppendable;

public class HtmlContext implements IAppendable {
	private String styleId;
	private final CssContext style;

	public HtmlContext() {
		this(new CssContext());
	}

	public HtmlContext(CssContext style) {
		this.style = style;
	}

	public CssContext getStyle() {
		return style;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
		this.style.clear();
	}

	@Override
	public boolean isEmpty() {
		return style.isEmpty() && StringExtension.isEmpty(styleId);
	}

	@Override
	public StringBuilder append(StringBuilder buf) {
		if (buf == null || isEmpty())
			return buf;

		if (!StringExtension.isEmpty(styleId))
			buf.append("id=\"" + styleId + "\"");
		if (!style.isEmpty())
			style.append(StringExtension.isEmpty(styleId) ? buf : buf.append(" "));

		return buf;
	}

	/*
	 * Object
	 */

	@Override
	public String toString() {
		return printBuffer(this);
	}
}
