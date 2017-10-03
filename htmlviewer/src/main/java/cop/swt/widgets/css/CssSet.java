package cop.swt.widgets.css;

import static cop.swt.widgets.HtmlExtension.printBuffer;

import java.util.LinkedHashMap;
import java.util.Map;

import cop.swt.widgets.css.enums.CssPropertyEnum;
import cop.swt.widgets.interfaces.IAppendable;
import cop.swt.widgets.interfaces.IName;
public class CssSet implements IAppendable {
	protected static final int BASE_LENGTH = 80;

	private final Map<CssPropertyEnum, String> properties = new LinkedHashMap<CssPropertyEnum, String>();

	public void add(CssPropertyEnum property, IName value) {
		properties.put(property, value.getName());
	}

	public void add(CssPropertyEnum property, String value1, IName value2) {
		properties.put(property, value1 + value2.getName());
	}

	public String get(CssPropertyEnum property) {
		return properties.get(property);
	}

	public void clear() {
		properties.clear();
	}

	/*
	 * IAppendable
	 */

	public final boolean isEmpty() {
		return properties.isEmpty();
	}

	@Override
	public StringBuilder append(StringBuilder buf) {
		if (buf == null || properties.isEmpty())
			return buf;

		for (Map.Entry<CssPropertyEnum, String> one : properties.entrySet())
			buf.append(one.getKey().getName()).append(":").append(one.getValue()).append(";");

		return buf;
	}

	/*
	 * Object
	 */

	@Override
	public String toString() {
		String str = printBuffer(this);
		System.out.println(str);
		return str;
	}
}
