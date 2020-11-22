package org.mihalis.opal.multi;

public class MultiChoiceDefaultLabelProvider
		implements MultiChoiceLabelProvider {

	@Override
	public String getText(final Object element) {
		return element == null ? "" : element.toString();
	}

}