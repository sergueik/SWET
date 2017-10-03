package cop.swt.widgets.enums;

import cop.swt.widgets.interfaces.IName;

public enum HtmlColorEnum implements IName {
	BLACK("black", 0x0), 
	SILVER("silver", 0xC0C0C0), 
	GRAY("gray", 0x808080), 
	WHITE("white", 0xFFFFFF), 
	GREEN("green", 0x008000), 
	LIME(
					"lime", 0x00FF00), 
	OLIVE("olive", 0x808000), 
	YELLOW("yellow",
							0xFFFF00), 
	MAROON("maroon", 0x800000), 
	RED("red",
									0xFF0000), 
	PURPLE("purple", 0x800080), 
	FUCHSIA("fuchsia",
											0xFF00FF), NAVY("navy", 0x000080), BLUE("blue",
													0x0000FF), TEAL("teal", 0x008080), AQUA("aqua",
															0x00FFFF), DEEPPINK("deeppink",
																	0xFF1493), ALICEBLUE("aliceblue",
																			0xF0F8FF), AZURE("azure",
																					0xF0FFFF), BLANCHEDALMOND(
																							"blanchedalmond",
																							0xFFEBCD), BURLYWOOD("burlywood",
																									0xFFEBCD), CORAL("coral",
																											0xFF7F50), CYAN("cyan",
																													0x00FFFF), DARKCYAN(
																															"darkcyan",
																															0x008B8B), DARKGRAY(
																																	"darkgray",
																																	0xA9A9A9), DARKOLIVEGREEN(
																																			"darkolivegreen",
																																			0x556B2F), DARKSALMON(
																																					"darksalmon",
																																					0xE9967A), DARKTURQUOISE(
																																							"darkturquoised",
																																							0x00CED1), DIMGRAY(
																																									"dimgray",
																																									0x696969), FORESTGREEN(
																																											"forestgreen",
																																											0x228B22), GOLD(
																																													"gold",
																																													0xFFD700), GREENYELLOW(
																																															"greenyellow",
																																															0xADFF2F), INDIGO(
																																																	"indigo",
																																																	0x4B0082), LAVENDERBLUSH(
																																																			"lavenderblush",
																																																			0xFFF0F5), LIGHTCORAL(
																																																					"lightcoral",
																																																					0xF08080), LIGHTGREY(
																																																							"lightgrey",
																																																							0xD3D3D3), LIGHTSKYBLUE(
																																																									"lightskyblue",
																																																									0x87CEFA), MEDIUMPURPLE(
																																																											"mediumpurple",
																																																											0x9370D8), MEDIUMTURQUOISE(
																																																													"mediumturquoise",
																																																													0x48D1CC), MISTYROSE(
																																																															"mistyrose",
																																																															0xFFE4E1), OLDLACE(
																																																																	"oldlace",
																																																																	0xFDF5E6), ORANGERED(
																																																																			"orangered",
																																																																			0xFF4500), PALETURQUOISE(
																																																																					"paleturquoise",
																																																																					0xAFEEEE), PERU(
																																																																							"peru",
																																																																							0xCD853F), SADDLEBROWN(
																																																																									"saddlebrown",
																																																																									0x8B4513), SEASHELL(
																																																																											"seashell",
																																																																											0xFFF5EE), SLATEBLUE(
																																																																													"slateblue",
																																																																													0x6A5ACD), STEELBLUE(
																																																																															"steelblue",
																																																																															0x4682B4), TOMATO(
																																																																																	"tomato",
																																																																																	0xFF6347);

	private String name;
	private int number;

	private HtmlColorEnum(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	/*
	 * IName
	 */

	@Override
	public String getName() {
		return name;
	}
}
