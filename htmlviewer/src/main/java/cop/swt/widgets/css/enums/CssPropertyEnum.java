package cop.swt.widgets.css.enums;

import cop.swt.widgets.interfaces.IName;

public enum CssPropertyEnum implements IName {
	CSS_TEXT_ALIGN("text-align"),
	CSS_TEXT_COLOR("color"),
	CSS_TEXT_DECORATION("text-decoration"),

	CSS_FONT_SIZE("font-size"),
	CSS_FONT_WEIGHT("font-weight"),
	CSS_FONT_STYLE("font-style"),

	CSS_ID("id");

	private String name;

	private CssPropertyEnum(String name) {
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
