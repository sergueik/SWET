package custom.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ImageLabel extends Container {

	static final int hgap = 16;

	// no need since the label's.getImage() retrieves the image
	// private Image image;
	public final Label image;
	public final Label text;
	private int alignment = SWT.TOP;

	// ==[ Constructor
	// ]=================================================================================

	public ImageLabel(Composite parent, int style) {
		super(parent, style);

		// Children
		image = new Label(this, SWT.NONE);
		text = new Label(this, SWT.WRAP);

	}

	// ==[ Layout
	// ]======================================================================================

	@Override
	protected Point onComputeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		// System.out.println("imageLabel.computeSize(" + wHint + "," + hHint +
		// ")");
		Point imgPrefSize = image.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		// System.out.println("\tImage Width: " + imgPrefSize.x);

		if (wHint != SWT.DEFAULT)
			wHint = Math.max(0, wHint - imgPrefSize.x - hgap);

		// System.out.println("\tReduced wHint: " + wHint);

		Point textPrefSize = text.computeSize(wHint, hHint);

		// System.out.println("\tImage Height: " + imgPrefSize.y);
		// System.out.println("\tText Height: " + textPrefSize.y);

		int height = Math.max(imgPrefSize.y, textPrefSize.y);
		int width = imgPrefSize.x + hgap + textPrefSize.x;

		// System.out.println("\t-> Result: " + width + "," + height);

		return new Point(width, height);
	}

	@Override
	protected void onLayout(Composite composite, boolean flushCache) {

		Rectangle bounds = composite.getBounds();

		Point imgPrefSize = image.computeSize(SWT.DEFAULT, bounds.height);
		Point textPrefSize = text.computeSize(bounds.width - imgPrefSize.x,
				SWT.DEFAULT);

		int y = 0;
		if (alignment == SWT.CENTER)
			y = (bounds.height - textPrefSize.y) / 2;
		else if (alignment == SWT.BOTTOM)
			y = bounds.height - textPrefSize.y;

		image.setBounds(0, 0, imgPrefSize.x, imgPrefSize.y);
		text.setBounds(imgPrefSize.x + hgap, y, textPrefSize.x, textPrefSize.y);

	}

	// ==[ Getter & Setter
	// ]=============================================================================

	// public Label getImageLabel() {
	// return image;
	// }
	//
	// public Label getTextLabel() {
	// return text;
	// }

	public void setImage(Image newImage) {
		checkWidget();
		// image = newImage;
		image.setImage(newImage);
	}

	public void setText(String content) {
		checkWidget();
		text.setText(content);
	}

	public void setVerticalAlignment(int alignment) {
		checkWidget();
		if (alignment != SWT.TOP && alignment != SWT.CENTER
				&& alignment != SWT.BOTTOM)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		this.alignment = alignment;
		// TODO: locate and clone implementing class
		// requestLayout();
	}

}