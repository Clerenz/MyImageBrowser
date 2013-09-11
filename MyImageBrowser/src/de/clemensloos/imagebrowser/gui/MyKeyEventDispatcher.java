package de.clemensloos.imagebrowser.gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class MyKeyEventDispatcher implements KeyEventDispatcher {

	
	private ImageBrowser2DGui gui;
	
	
	public MyKeyEventDispatcher(ImageBrowser2DGui gui) {
		this.gui = gui;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		
		if(e.getExtendedKeyCode() == KeyEvent.VK_TAB) {
			if(e.getID() == KeyEvent.KEY_RELEASED) {
				gui.hideAndShowToolbars(e.isShiftDown());
			}
			return true;
		}
		return false;
	}

}
