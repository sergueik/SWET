package cop.swt.widgets.css.enums;

import cop.swt.widgets.interfaces.IName;

public enum CssFontStyleEnum implements IName {
	NORMAL("normal"), ITALIC("italic"), OBLIQUE("oblique"), INHERIT("inherit");

	private String name;

	private CssFontStyleEnum(String name) {
		this.name = name;
	}

	// ========== IName ==========

	@Override
	public String getName() {
		return name;
	}
}
