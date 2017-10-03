package cop.swt.widgets.css;

import java.util.HashMap;
import java.util.Map;

public final class CssId extends CssGroup {
	private static final Map<String, CssId> map = new HashMap<String, CssId>();

	public static CssId createCssId(String name) {
		CssId obj = map.get(name);

		if (obj == null)
			map.put(name, obj = new CssId(name));

		return obj;
	}

	private CssId(String name) {
		super(name, "#");
	}
}
