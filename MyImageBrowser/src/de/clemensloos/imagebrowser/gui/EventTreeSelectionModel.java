package de.clemensloos.imagebrowser.gui;


import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;


class EventTreeSelectionModel extends DefaultTreeSelectionModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageBrowser2DGui gui;


	public EventTreeSelectionModel(ImageBrowser2DGui gui) {
		this.gui = gui;
	}


	@Override
	public void removeSelectionPath(TreePath p) {
		setSelectionPath(p);
	}


	@Override
	public void addSelectionPath(TreePath p) {
		setSelectionPath(p);
	}


	@Override
	public void setSelectionPath(TreePath p) {
		if (p.getPathCount() == 3) {
			if (!isPathSelected(p)) {
				super.setSelectionPath(p);
				gui.imageBrowser.setEvent(p.getLastPathComponent().toString());
			}
			else {
				super.removeSelectionPath(p);
				gui.imageBrowser.clearEvent();
			}
		}
		else {
			super.clearSelection();
			gui.imageBrowser.clearEvent();
		}
		gui.loadImages();
	}

}
