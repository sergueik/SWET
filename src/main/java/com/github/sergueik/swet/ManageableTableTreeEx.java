package com.github.sergueik.swet;

/**
 * Copyright 2018 Serguei Kouzmine
 */

import org.eclipse.swt.SWT;

// import org.eclipse.swt.custom.*;
import org.eclipse.swt.custom.TableTree;

import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TypedListener;

// origin: http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableTree.htm
// based on: http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesTableTreeTableTree.htm
//TODO: compare with org.eclipse.swt.custom.TableTree

@SuppressWarnings("unused")
public class ManageableTableTreeEx {

	private static class TableTree extends Composite {

		Table table;
		TableTreeItem[] items = EMPTY_ITEMS;
		Image plusImage, minusImage, sizeImage;
		boolean inDispose = false;

		static final TableTreeItem[] EMPTY_ITEMS = new TableTreeItem[0];

		static final String[] EMPTY_TEXTS = new String[0];

		static final Image[] EMPTY_IMAGES = new Image[0];

		public TableTree(Composite parent, int style) {

			super(parent, SWT.NONE);
			table = new Table(this, style);
			setBackground(table.getBackground());
			setForeground(table.getForeground());
			setFont(table.getFont());
			table.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event e) {
					onMouseDown(e);
				}
			});

			table.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					onSelection(e);
				}
			});

			table.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event e) {
					onSelection(e);
				}

			});

			addListener(SWT.Dispose, new Listener() {
				public void handleEvent(Event e) {
					onDispose();
				}
			});

			addListener(SWT.Resize, new Listener() {
				public void handleEvent(Event e) {
					onResize();
				}
			});

			addListener(SWT.FocusIn, new Listener() {
				public void handleEvent(Event e) {
					onFocusIn();
				}
			});
		}

		int addItem(TableTreeItem item, int index) {

			if (index < 0 || index > items.length)
				throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);
			TableTreeItem[] newItems = new TableTreeItem[items.length + 1];
			System.arraycopy(items, 0, newItems, 0, index);
			newItems[index] = item;
			System.arraycopy(items, index, newItems, index + 1, items.length - index);
			items = newItems;
			if (index == items.length - 1)
				return table.getItemCount();
			else
				return table.indexOf(items[index + 1].tableItem);
		}

		public void addSelectionListener(SelectionListener listener) {

			if (listener == null)
				throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

			TypedListener typedListener = new TypedListener(listener);

			addListener(SWT.Selection, typedListener);

			addListener(SWT.DefaultSelection, typedListener);

		}

		public void addTreeListener(TreeListener listener) {

			if (listener == null)
				throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

			TypedListener typedListener = new TypedListener(listener);

			addListener(SWT.Expand, typedListener);

			addListener(SWT.Collapse, typedListener);

		}

		public Point computeSize(int wHint, int hHint) {

			return table.computeSize(wHint, hHint, true);

		}

		public Rectangle computeTrim(int x, int y, int width, int height) {

			return table.computeTrim(x, y, width, height);

		}

		public void deselectAll() {

			table.deselectAll();

		}

		void expandItem(TableTreeItem item) {
			if (item == null || item.getExpanded())
				return;
			expandItem(item.parentItem);
			item.setExpanded(true);
			Event event = new Event();
			event.item = item;
			notifyListeners(SWT.Expand, event);
		}

		public int getItemCount() {
			return items.length;
		}

		public int getItemHeight() {
			return table.getItemHeight();
		}

		public TableTreeItem[] getItems() {
			TableTreeItem[] newItems = new TableTreeItem[items.length];
			System.arraycopy(items, 0, newItems, 0, items.length);
			return newItems;
		}

		public TableTreeItem[] getSelection() {
			TableItem[] selection = table.getSelection();
			TableTreeItem[] result = new TableTreeItem[selection.length];
			for (int i = 0; i < selection.length; i++) {
				result[i] = (TableTreeItem) selection[i].getData();
			}
			return result;
		}

		public int getSelectionCount() {
			return table.getSelectionCount();
		}

		public Table getTable() {
			return table;
		}

		void createImages() {
			int itemHeight = sizeImage.getBounds().height;
			int indent = Math.min(6, (itemHeight - 9) / 2);
			indent = Math.max(0, indent);
			int size = Math.max(10, itemHeight - 2 * indent);
			size = ((size + 1) / 2) * 2; // size must be an even rowsber
			int midpoint = indent + size / 2;
			Color foreground = getForeground();

			Color plusMinus = getDisplay()
					.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);

			Color background = getBackground();

			PaletteData palette = new PaletteData(new RGB[] { foreground.getRGB(),
					background.getRGB(), plusMinus.getRGB() });
			/* hand drawn plus image */
			ImageData imageData = new ImageData(itemHeight, itemHeight, 4, palette);
			imageData.transparentPixel = 1;
			plusImage = new Image(getDisplay(), imageData);
			GC gc = new GC(plusImage);
			gc.setBackground(background);
			gc.fillRectangle(0, 0, itemHeight, itemHeight);
			gc.setForeground(plusMinus);
			gc.drawRectangle(indent, indent, size, size);
			gc.setForeground(foreground);
			gc.drawLine(midpoint, indent + 2, midpoint, indent + size - 2);
			gc.drawLine(indent + 2, midpoint, indent + size - 2, midpoint);
			gc.dispose();
			/* hand drawn minus image */

			palette = new PaletteData(new RGB[] { foreground.getRGB(),
					background.getRGB(), plusMinus.getRGB() });
			imageData = new ImageData(itemHeight, itemHeight, 4, palette);
			imageData.transparentPixel = 1;
			minusImage = new Image(getDisplay(), imageData);
			gc = new GC(minusImage);
			gc.setBackground(background);
			gc.fillRectangle(0, 0, itemHeight, itemHeight);
			gc.setForeground(plusMinus);
			gc.drawRectangle(indent, indent, size, size);
			gc.setForeground(foreground);
			gc.drawLine(indent + 2, midpoint, indent + size - 2, midpoint);
			gc.dispose();

		}

		Image getPlusImage() {
			if (plusImage == null)
				createImages();
			return plusImage;
		}

		Image getMinusImage() {
			if (minusImage == null)
				createImages();
			return minusImage;
		}

		public int indexOf(TableTreeItem item) {
			for (int i = 0; i < items.length; i++) {
				if (item == items[i])
					return i;
			}
			return -1;
		}

		void onDispose() {
			inDispose = true;
			for (int i = 0; i < items.length; i++) {
				items[i].dispose();
			}
			inDispose = false;
			if (plusImage != null)
				plusImage.dispose();
			if (minusImage != null)
				minusImage.dispose();
			if (sizeImage != null)
				sizeImage.dispose();
			plusImage = minusImage = sizeImage = null;
		}

		void onResize() {
			Rectangle area = getClientArea();
			table.setBounds(0, 0, area.width, area.height);
		}

		void onSelection(Event e) {
			Event event = new Event();
			TableItem tableItem = (TableItem) e.item;
			TableTreeItem item = getItem(tableItem);
			event.item = item;
			if (e.type == SWT.Selection && e.detail == SWT.CHECK && item != null) {
				event.detail = SWT.CHECK;
				item.checked = tableItem.getChecked();
			}
			notifyListeners(e.type, event);
		}

		public TableTreeItem getItem(Point point) {
			TableItem item = table.getItem(point);
			if (item == null)
				return null;
			return getItem(item);
		}

		TableTreeItem getItem(TableItem tableItem) {
			if (tableItem == null)
				return null;
			for (int i = 0; i < items.length; i++) {
				TableTreeItem item = items[i].getItem(tableItem);
				if (item != null)
					return item;
			}
			return null;
		}

		void onFocusIn() {
			table.setFocus();
		}

		void onMouseDown(Event event) {
			/* when user clicked on the image expand, collapse the tree. */
			TableItem[] items = table.getItems();
			for (int i = 0; i < items.length; i++) {
				Rectangle rect = items[i].getImageBounds(0);
				if (rect.contains(event.x, event.y)) {
					TableTreeItem item = (TableTreeItem) items[i].getData();
					event = new Event();
					event.item = item;
					item.setExpanded(!item.getExpanded());
					if (item.getExpanded()) {
						notifyListeners(SWT.Expand, event);
					} else {
						notifyListeners(SWT.Collapse, event);

					}

					return;

				}

			}

		}

		public void removeAll() {
			setRedraw(false);
			for (int i = items.length - 1; i >= 0; i--) {
				items[i].dispose();
			}
			items = EMPTY_ITEMS;
			setRedraw(true);
		}

		void removeItem(TableTreeItem item) {
			int index = 0;
			while (index < items.length && items[index] != item)
				index++;
			if (index == items.length)
				return;
			TableTreeItem[] newItems = new TableTreeItem[items.length - 1];
			System.arraycopy(items, 0, newItems, 0, index);
			System.arraycopy(items, index + 1, newItems, index,
					items.length - index - 1);
			items = newItems;
		}

		public void removeSelectionListener(SelectionListener listener) {
			if (listener == null)
				throw new SWTError(SWT.ERROR_NULL_ARGUMENT);
			removeListener(SWT.Selection, listener);
			removeListener(SWT.DefaultSelection, listener);
		}

		public void removeTreeListener(TreeListener listener) {
			if (listener == null)
				throw new SWTError(SWT.ERROR_NULL_ARGUMENT);
			removeListener(SWT.Expand, listener);
			removeListener(SWT.Collapse, listener);
		}

		public void selectAll() {
			table.selectAll();
		}

		public void setBackground(Color color) {
			super.setBackground(color);
			table.setBackground(color);
			if (sizeImage != null) {
				GC gc = new GC(sizeImage);
				gc.setBackground(getBackground());
				Rectangle size = sizeImage.getBounds();
				gc.fillRectangle(size);
				gc.dispose();
			}
		}

		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			table.setEnabled(enabled);
		}

		public void setFont(Font font) {
			super.setFont(font);
			table.setFont(font);
		}

		public void setForeground(Color color) {
			super.setForeground(color);
			table.setForeground(color);
		}

		public void setMenu(Menu menu) {
			super.setMenu(menu);
			table.setMenu(menu);
		}

		public void setSelection(TableTreeItem[] items) {
			TableItem[] tableItems = new TableItem[items.length];
			for (int i = 0; i < items.length; i++) {
				if (items[i] == null)
					throw new SWTError(SWT.ERROR_NULL_ARGUMENT);
				if (!items[i].getVisible())
					expandItem(items[i]);
				tableItems[i] = items[i].tableItem;
			}
			table.setSelection(tableItems);
		}

		public void setToolTipText(String string) {
			super.setToolTipText(string);
			table.setToolTipText(string);
		}

		public void showItem(TableTreeItem item) {
			if (item == null)
				throw new SWTError(SWT.ERROR_NULL_ARGUMENT);
			if (!item.getVisible())
				expandItem(item);
			TableItem tableItem = item.tableItem;
			table.showItem(tableItem);
		}

		public void showSelection() {
			table.showSelection();
		}
	}

	/*
	 * 
	 * (c) Copyright IBM Corp. 2000, 2001.
	 * 
	 * All Rights Reserved
	 *  
	 */

	/**
	 * 
	 * A TableTreeItem is a selectable user interface object
	 * 
	 * that represents an item in a heirarchy of items in a
	 * 
	 * TableTree.
	 *  
	 */

	class TableTreeItem extends Item {
		TableItem tableItem;
		TableTree parent;
		TableTreeItem parentItem;
		TableTreeItem[] items = TableTree.EMPTY_ITEMS;
		// Note: exists org.eclipse.swt.custom.TableTree
		String[] texts = TableTree.EMPTY_TEXTS;
		Image[] images = TableTree.EMPTY_IMAGES;
		boolean expanded;
		boolean checked;

		public TableTreeItem(TableTree parent, int style) {
			this(parent, style, parent.getItemCount());
		}

		public TableTreeItem(TableTree parent, int style, int index) {
			this(parent, null, style, index);
		}

		public TableTreeItem(TableTreeItem parent, int style) {
			this(parent, style, parent.getItemCount());
		}

		public TableTreeItem(TableTreeItem parent, int style, int index) {
			this(parent.getParent(), parent, style, index);
		}

		TableTreeItem(TableTree parent, TableTreeItem parentItem, int style,
				int index) {
			super(parent, style);
			this.parent = parent;
			this.parentItem = parentItem;
			if (parentItem == null) {
				int tableIndex = parent.addItem(this, index);
				tableItem = new TableItem(parent.getTable(), style, tableIndex);
				tableItem.setData(this);
				addCheck();
				if (parent.sizeImage == null) {
					int itemHeight = parent.getItemHeight();
					parent.sizeImage = new Image(null, itemHeight, itemHeight);
					GC gc = new GC(parent.sizeImage);
					gc.setBackground(parent.getBackground());
					gc.fillRectangle(0, 0, itemHeight, itemHeight);
					gc.dispose();
					tableItem.setImage(0, parent.sizeImage);
				}
			} else {
				parentItem.addItem(this, index);
			}
		}

		void addCheck() {
			Table table = parent.getTable();
			if ((table.getStyle() & SWT.CHECK) == 0)
				return;
			tableItem.setChecked(checked);
		}

		void addItem(TableTreeItem item, int index) {
			if (item == null)
				throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

			if (index < 0 || index > items.length)
				throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);

			/* Now that item has a sub-node it must provide a cue that it can be expanded */

			if (items.length == 0 && index == 0) {
				if (tableItem != null) {
					Image image = expanded ? parent.getMinusImage()
							: parent.getPlusImage();
					tableItem.setImage(0, image);
				}
			}

			TableTreeItem[] newItems = new TableTreeItem[items.length + 1];
			System.arraycopy(items, 0, newItems, 0, index);
			newItems[index] = item;
			System.arraycopy(items, index, newItems, index + 1, items.length - index);
			items = newItems;
			if (expanded)
				item.setVisible(true);
		}

		public Rectangle getBounds(int index) {
			if (tableItem != null) {
				return tableItem.getBounds(index);
			} else {
				return new Rectangle(0, 0, 0, 0);
			}
		}

		public boolean getChecked() {
			if (tableItem == null) {
				return checked;
			}
			return tableItem.getChecked();
		}

		public Display getDisplay() {
			TableTree parent = this.parent;
			if (parent == null)
				throw new SWTError(SWT.ERROR_WIDGET_DISPOSED);
			return parent.getDisplay();
		}

		public boolean getExpanded() {
			return expanded;
		}

		public Image getImage() {
			return getImage(0);
		}

		public Image getImage(int index) {
			if (0 < index && index < images.length)
				return images[index];
			return null;
		}

		int getIndent() {
			if (parentItem == null)
				return 0;
			return parentItem.getIndent() + 1;
		}

		public int getItemCount() {
			return items.length;
		}

		public TableTreeItem[] getItems() {
			TableTreeItem[] newItems = new TableTreeItem[items.length];
			System.arraycopy(items, 0, newItems, 0, items.length);
			return newItems;
		}

		TableTreeItem getItem(TableItem tableItem) {
			if (tableItem == null)
				return null;
			if (this.tableItem == tableItem)
				return this;
			for (int i = 0; i < items.length; i++) {
				TableTreeItem item = items[i].getItem(tableItem);
				if (item != null)
					return item;
			}
			return null;
		}

		public TableTree getParent() {
			return parent;
		}

		public TableTreeItem getParentItem() {
			return parentItem;
		}

		public String getText() {
			return getText(0);
		}

		public String getText(int index) {
			if (0 <= index && index < texts.length)
				return texts[index];
			return null;
		}

		boolean getVisible() {
			return tableItem != null;
		}

		public int indexOf(TableTreeItem item) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] == item)
					return i;
			}
			return -1;
		}

		int expandedIndexOf(TableTreeItem item) {
			int index = 0;
			for (int i = 0; i < items.length; i++) {
				if (items[i] == item)
					return index;
				if (items[i].expanded)
					index += items[i].visibleChildrenCount();
				index++;
			}
			return -1;
		}

		int visibleChildrenCount() {
			int count = 0;
			for (int i = 0; i < items.length; i++) {
				if (items[i].getVisible()) {
					count += 1 + items[i].visibleChildrenCount();
				}
			}
			return count;
		}

		public void dispose() {
			for (int i = items.length - 1; i >= 0; i--) {
				items[i].dispose();
			}
			super.dispose();
			if (!parent.inDispose) {
				if (parentItem != null) {
					parentItem.removeItem(this);
				} else {
					parent.removeItem(this);
				}
				if (tableItem != null)
					tableItem.dispose();
			}
			items = null;
			parentItem = null;
			parent = null;
			images = null;
			texts = null;
			tableItem = null;
		}

		void removeItem(TableTreeItem item) {
			int index = 0;
			while (index < items.length && items[index] != item)
				index++;
			if (index == items.length)
				return;
			TableTreeItem[] newItems = new TableTreeItem[items.length - 1];
			System.arraycopy(items, 0, newItems, 0, index);
			System.arraycopy(items, index + 1, newItems, index,
					items.length - index - 1);
			items = newItems;

			if (items.length == 0) {
				if (tableItem != null)
					tableItem.setImage(0, null);
			}
		}

		public void setChecked(boolean checked) {

			if (tableItem != null) {

				tableItem.setChecked(checked);

			}

			this.checked = checked;

		}

		public void setExpanded(boolean expanded) {

			if (items.length == 0)
				return;

			this.expanded = expanded;

			if (tableItem == null)
				return;

			parent.setRedraw(false);

			for (int i = 0; i < items.length; i++) {

				items[i].setVisible(expanded);

			}

			Image image = expanded ? parent.getMinusImage() : parent.getPlusImage();

			tableItem.setImage(0, image);

			parent.setRedraw(true);

		}

		public void setImage(int index, Image image) {

			int columnCount = Math.max(parent.getTable().getColumnCount(), 1);

			if (index <= 0 || index >= columnCount)
				return;

			if (images.length < columnCount) {

				Image[] newImages = new Image[columnCount];

				System.arraycopy(images, 0, newImages, 0, images.length);

				images = newImages;

			}

			images[index] = image;

			if (tableItem != null)
				tableItem.setImage(index, image);

		}

		public void setImage(Image image) {

			setImage(0, image);

		}

		public void setText(int index, String text) {

			int columnCount = Math.max(parent.getTable().getColumnCount(), 1);

			if (index < 0 || index >= columnCount)
				return;

			if (texts.length < columnCount) {

				String[] newTexts = new String[columnCount];

				System.arraycopy(texts, 0, newTexts, 0, texts.length);

				texts = newTexts;

			}

			texts[index] = text;

			if (tableItem != null)
				tableItem.setText(index, text);

		}

		public void setText(String string) {

			setText(0, string);

		}

		void setVisible(boolean show) {

			if (parentItem == null)
				return; // this is a root and can not be toggled between visible and
			// hidden

			if (getVisible() == show)
				return;

			if (show) {

				if (!parentItem.getVisible())
					return; // parentItem must already be visible

				// create underlying table item and set data in table item to stored
				// data

				Table table = parent.getTable();

				int parentIndex = table.indexOf(parentItem.tableItem);

				int index = parentItem.expandedIndexOf(this) + parentIndex + 1;

				if (index < 0)
					return;

				tableItem = new TableItem(table, getStyle(), index);

				tableItem.setData(this);

				tableItem.setImageIndent(getIndent());

				addCheck();

				// restore fields to item

				// ignore any images in the first column

				int columnCount = Math.max(table.getColumnCount(), 1);

				for (int i = 0; i < columnCount; i++) {

					if (i < texts.length && texts[i] != null)
						setText(i, texts[i]);

					if (i < images.length && images[i] != null)
						setImage(i, images[i]);

				}

				// display the children and the appropriate [+]/[-] symbol as
				// required

				if (items.length != 0) {

					if (expanded) {

						tableItem.setImage(0, parent.getMinusImage());

						for (int i = 0, length = items.length; i < length; i++) {

							items[i].setVisible(true);

						}

					} else {

						tableItem.setImage(0, parent.getPlusImage());

					}

				}

			} else {

				for (int i = 0, length = items.length; i < length; i++) {

					items[i].setVisible(false);

				}

				// remove row from table

				tableItem.dispose();

				tableItem = null;

			}

		}

	}

	private int rows = 3;
	private int cols = 3;

	public void setRows(int value) {
		this.rows = value;
	}

	public void setCols(int value) {
		this.cols = value;
	}

	public void run() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("TableTree Test");
		createContents(shell);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new FillLayout());

		TableTree tableTree = new TableTree(shell, SWT.NONE);
		Table table = tableTree.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		for (int col = 0; col < cols; col++) {
			new TableColumn(table, SWT.LEFT).setText("Column " + (col + 1));
		}

		for (int row = 0; row < rows; row++) {
			TableTreeItem parent = new TableTreeItem(tableTree, SWT.NONE);
			parent.setText(0, "Parent " + (row + 1));
			parent.setText(1, "Data");
			parent.setText(2, "More data");

			// Add children items
			for (int j = 0; j < rows; j++) {
				// Create a child item and add data to the columns
				TableTreeItem child = new TableTreeItem(parent, SWT.NONE);
				child.setText(0, "Child " + (j + 1));
				child.setText(1, "Some child data");
				child.setText(2, "More child data");
			}
			// Expand the parent item
			parent.setExpanded(true);
		}

		// Pack the columns
		TableColumn[] columns = table.getColumns();
		for (int i = 0, n = columns.length; i < n; i++) {
			columns[i].pack();
		}
	}

	public static void main(String[] args) {
		ManageableTableTreeEx manageableTableTreeEx = new ManageableTableTreeEx();
		manageableTableTreeEx.setRows(3);
		manageableTableTreeEx.setCols(3);
		manageableTableTreeEx.run();
	}
}
