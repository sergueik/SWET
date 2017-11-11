package org.swet;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for Selenium WebDriver Elementor Tool (SWET)
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ListJavaFonts {

	public static void main(String[] args) {

		List<String> monospaceFontFamilyNames = new ArrayList<>();
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		String[] fontFamilyNames = graphicsEnvironment
				.getAvailableFontFamilyNames();

		BufferedImage bufferedImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = bufferedImage.createGraphics();

		for (String fontFamilyName : fontFamilyNames) {
			boolean isMonospaced = true;
			//
			int fontStyle = Font.PLAIN;
			int fontSize = 12;
			Font font = new Font(fontFamilyName, fontStyle, fontSize);
			@SuppressWarnings("serial")
			List<Integer> codePoints = new ArrayList<Integer>() {
				{
					add(108); /* l */
					add(109); /* m */
					add(119); /* w */
					add(49); /* 1 */
					add(52); /* 4 */
				}
			};
			FontMetrics fontMetrics = graphics.getFontMetrics(font);

			int firstCharacterWidth = 0;
			boolean hasFirstCharacterWidth = false;
			for (int codePoint : codePoints) {
				if (Character.isValidCodePoint(codePoint)
						&& (Character.isLetter(codePoint)
								|| Character.isDigit(codePoint))) {
					char character = (char) codePoint;
					int characterWidth = fontMetrics.charWidth(character);
					if (hasFirstCharacterWidth) {
						if (characterWidth != firstCharacterWidth) {
							isMonospaced = false;
							break;
						}
					} else {
						firstCharacterWidth = characterWidth;
						hasFirstCharacterWidth = true;
					}
				}
			}

			if (isMonospaced) {
				monospaceFontFamilyNames.add(fontFamilyName);
			}
		}

		graphics.dispose();
		for (String fontFamily : monospaceFontFamilyNames) {
			System.out.println(fontFamily);
		}
	}
}