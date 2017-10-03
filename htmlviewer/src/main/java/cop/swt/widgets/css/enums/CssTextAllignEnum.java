package cop.swt.widgets.css.enums;

import cop.swt.widgets.interfaces.IName;

public enum CssTextAllignEnum implements IName {
	LEFT("left"),
	RIGHT("right"),
	CENTER("center"),
	JUSTIFY("justify");

	private String name;

	private CssTextAllignEnum(String name) {
		this.name = name;
	}

	/*
	 * IName
	 */

	@Override
	public String getName() {
		return name;
	}
}
