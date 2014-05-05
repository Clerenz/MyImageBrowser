package de.clemensloos.imagebrowser.gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class MyKeyEventDispatcher implements KeyEventDispatcher {

	
	private ImageBrowser2DGui gui;
	private boolean active = true;
	
	
	public MyKeyEventDispatcher(ImageBrowser2DGui gui) {
		this.gui = gui;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		
		if (!active) {
			return false;
		}
		
		if(e.getExtendedKeyCode() == KeyEvent.VK_TAB) {
			if(e.getID() == KeyEvent.KEY_RELEASED) {
				gui.hideAndShowToolbars(e.isShiftDown());
			}
			return true;
		}
		
		if(e.getExtendedKeyCode() == KeyEvent.VK_A && e.isControlDown()) {
			if(e.getID() == KeyEvent.KEY_RELEASED ) {
				System.out.println("Go select all"); // XXX
			}
			return true;
		}
		
		return false;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

}
