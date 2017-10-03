package cop.swt.widgets.css.enums;

import cop.swt.widgets.interfaces.IName;

public enum CssFontSizeEnum implements IName {
	PT("pt");

	private String name;

	private CssFontSizeEnum(String name) {
		this.name = name;
	}

	// ========== IName ==========

	@Override
	public String getName() {
		return name;
	}
}
