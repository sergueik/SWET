package cop.swt.widgets.css;

import static cop.swt.widgets.HtmlExtension.printBuffer;

import java.util.HashSet;
import java.util.Set;

import cop.swt.widgets.interfaces.IAppendable;

public class CssStyleSet implements IAppendable {
	private final Set<CssSet> groups = new HashSet<CssSet>();

	public CssStyleSet() {}

	public void add(CssSet group) {
		if (group != null && !group.isEmpty())
			groups.add(group);
	}

	@Override
    public boolean isEmpty() {
		for (CssSet group : groups)
			if (!group.isEmpty())
				return false;

		return true;
	}

	/*
	 * IAppendable
	 */

	@Override
	public StringBuilder append(StringBuilder buf) {
		if (buf == null || groups.isEmpty())
			return buf;

		boolean newLine = false;

		for (CssSet group : groups) {
			if (group.isEmpty())
				continue;

			if (newLine)
				buf.append("\n");
			else
				newLine = true;

			group.append(buf);
		}

		return buf;
	}

	@Override
	public String toString() {
		return printBuffer(this);
	}
}
