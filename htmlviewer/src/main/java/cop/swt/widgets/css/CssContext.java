package cop.swt.widgets.css;

public final class CssContext extends CssSet {

	@Override
	public StringBuilder append(StringBuilder buf) {
		if (buf == null || isEmpty())
			return buf;

		buf.append("style=\"");
		super.append(buf).append("\"");

		return buf;
	}
}
