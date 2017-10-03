package cop.swt.widgets.css.enums;

import cop.swt.widgets.interfaces.IName;

public enum CssTextDecorationEnum implements IName {
	NONE("none"),
	UNDERLINE("underline"),
	OVERLINE("overline"),
	LINE_THROUGH("line-through"),
	BLINK("blink");

	private String name;

	private CssTextDecorationEnum(String name) {
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
