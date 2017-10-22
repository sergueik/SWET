package org.passer;

/*******************************************************************************
 * Copyright (c) 2015 Fabian Prasser
 *******************************************************************************/

/**
 * Item for the choices dialog
 * @author Fabian Prasser
 */
public class ChoiceItem {

	private String text;
	private String tooltipText;

	public ChoiceItem(String text, String tooltipText) {
		checkNull(text);
		checkNull(tooltipText);
		this.text = text;
		this.tooltipText = tooltipText;
	}

	public String getText() {
		return text;
	}

	public String getTooltipText() {
		return tooltipText;
	}

	public void setText(String data) {
		checkNull(data);
		this.text = data;
	}

	public void setTooltipText(String data) {
		checkNull(tooltipText);
		this.tooltipText = data;
	}

	private void checkNull(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("Null is not a valid argument");
		}
	}
}