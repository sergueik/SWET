package cop.swt.widgets.css;

public abstract class CssGroup extends CssSet {
	private final String name;
	private final String marker;

	public CssGroup(String name, String marker) {
		this.name = name;
		this.marker = marker;
	}

	@Override
	public StringBuilder append(StringBuilder buf) {
		if (buf == null || isEmpty())
			return buf;

		buf.append(marker).append(name).append("{");
		super.append(buf).append("}");

		return buf;
	}
}
