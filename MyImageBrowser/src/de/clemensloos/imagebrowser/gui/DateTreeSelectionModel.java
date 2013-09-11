package de.clemensloos.imagebrowser.gui;


import java.util.Enumeration;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import de.clemensloos.imagebrowser.types.Date;


class DateTreeSelectionModel extends DefaultTreeSelectionModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageBrowser2DGui gui;


	DateTreeSelectionModel(ImageBrowser2DGui gui) {
		super();
		this.gui = gui;
	}


	@Override
	public void addSelectionPath(TreePath p) {
		if (p.getPathCount() == 1) {
			clearSelection();
			gui.imageBrowser.clearDates();
		}
		else if (p.getPathCount() == 2 || p.getPathCount() == 3) {
			Enumeration<?> e = ((MutableTreeNode) p.getLastPathComponent()).children();
			while (e.hasMoreElements()) {
				addSelectionPath(p.pathByAddingChild(e.nextElement()));
			}
			super.addSelectionPath(p);
		}
		else if (p.getPathCount() == 4) {
			gui.imageBrowser.addDate((Date) p.getLastPathComponent());
			super.addSelectionPath(p);
		}
		gui.loadImages();
	}


	@Override
	public void removeSelectionPath(TreePath p) {
		if (p.getPathCount() == 2 || p.getPathCount() == 3) {
			removeChildSelectionPath(p);
		}
		if (p.getPathCount() == 3 || p.getPathCount() == 4) {
			removeParentSelectionPath(p);
		}
		if (p.getPathCount() == 4) {
			gui.imageBrowser.removeDate((Date) p.getLastPathComponent());
		}
		super.removeSelectionPath(p);
		gui.loadImages();
	}


	private void removeChildSelectionPath(TreePath p) {
		if (p.getPathCount() == 2 || p.getPathCount() == 3) {
			Enumeration<?> e = ((MutableTreeNode) p.getLastPathComponent()).children();
			while (e.hasMoreElements()) {
				removeChildSelectionPath(p.pathByAddingChild(e.nextElement()));
			}
		}
		else if (p.getPathCount() == 4) {
			gui.imageBrowser.removeDate((Date) p.getLastPathComponent());
		}
		super.removeSelectionPath(p);
	}


	private void removeParentSelectionPath(TreePath p) {
		if (p.getPathCount() == 3 || p.getPathCount() == 4) {
			removeParentSelectionPath(p.getParentPath());
		}
		super.removeSelectionPath(p);
	}


	@Override
	public void setSelectionPath(TreePath arg0) {
		if (isPathSelected(arg0)) {
			removeSelectionPath(arg0);
		}
		else {
			addSelectionPath(arg0);
		}
	}

}
