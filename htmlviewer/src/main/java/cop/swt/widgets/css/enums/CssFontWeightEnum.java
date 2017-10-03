package cop.swt.widgets.css.enums;

import cop.swt.widgets.interfaces.IName;

public enum CssFontWeightEnum implements IName {
	NORMAL("normal"),
	BOLD("bold"),
	BOLDER("bolder"),
	LIGHTER("lighter"),
	W100("100"),
	W200("200"),
	W300("300"),
	W400("400"),
	W500("500"),
	W600("600"),
	W700("700"),
	W800("800"),
	W900("900"),
	INHERIT("inherit");

	private String name;

	private CssFontWeightEnum(String name) {
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
