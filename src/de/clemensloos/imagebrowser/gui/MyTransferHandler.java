package de.clemensloos.imagebrowser.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


public class MyTransferHandler extends TransferHandler {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ImageBrowserGui parent;
	
	
	
	public MyTransferHandler(ImageBrowserGui parent) {
		
		this.parent = parent;
		
	}
	

	@Override
	protected Transferable createTransferable(JComponent c) {
		return null;
	}


	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		
		return info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
					&& info.isDrop();
	}


	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.NONE;
	}


	@Override
	public boolean importData(TransferHandler.TransferSupport info) {
		Component target = info.getComponent();
		target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		try {

			if (info.getDataFlavors()[0].equals(DataFlavor.javaFileListFlavor)) {
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				parent.dropImages(files);
				return true;
			}

		} catch (UnsupportedFlavorException e) {
			// ignore (is checked before)
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	protected void exportDone(JComponent c, Transferable t, int act) {
//		c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	
}
