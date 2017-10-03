package cop.swt.widgets.css;

import java.util.HashMap;
import java.util.Map;

public final class CssClass extends CssGroup {
	private static final Map<String, CssClass> map = new HashMap<String, CssClass>();

	public static CssClass createCssClass(String name) {
		CssClass obj = map.get(name);

		if (obj == null)
			map.put(name, obj = new CssClass(name));

		return obj;
	}

	private CssClass(String name) {
		super(name, "");
	}
}
