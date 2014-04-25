package de.clemensloos.imagebrowser.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


public class RatingButton extends JButton {

	private static final long serialVersionUID = 1L;
	boolean showing = true;

	Integer myValue;
	ImageBrowser2DGui myGui;


	RatingButton(ImageBrowser2DGui gui, Integer value) {
		super("<html>" + value + "</html>");

		this.myValue = value;
		myGui = gui;

		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (showing) {
					myGui.imageBrowser.removeRating(myValue);
					setText("<html><strike>" + myValue + "</strike></html>");
					showing = false;
					myGui.loadImages();
				}
				else {
					myGui.imageBrowser.addRating(myValue);
					setText("<html>" + myValue + "</html>");
					showing = true;
					myGui.loadImages();
				}
			}
		});

	}

}
