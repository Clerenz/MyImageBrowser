package de.clemensloos.imagebrowser.gui;

import java.io.File;
import java.util.List;

public interface ImageBrowserGui {
	
	public abstract void log(String message);
	
	public abstract void dropImages(List<File> files);
	
	public abstract void shutDown();
	
	public abstract void selectAll();

	public abstract void deselectAll();
	
	public void refreshGuiComponents();
	
	public void showUserMessage(String message);
	
}
