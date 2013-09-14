package de.clemensloos.imagebrowser.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.clemensloos.imagebrowser.types.Image;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Image image;
	BufferedImage bi = null;

	public ImagePanel(Image image, int x, int y, int diameter) {

		super();

		this.image = image;

		Dimension d = new Dimension(diameter, diameter);
		setSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setPreferredSize(d);
		setForeground(Color.white);
		setBackground(Color.black);
		setOpaque(true);

		setBounds(x, y, diameter, diameter);
		
		new ImageWorker().execute();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bi != null) {
			g.drawImage(bi, 0, 0, getWidth(), getHeight(), 0, 0,
					image.imagewidth, image.imageheight, null);
		}
	}

	public class ImageWorker extends SwingWorker<Integer, Integer> {

		@Override
		protected Integer doInBackground() throws Exception {
			try {
				bi = ImageIO.read(new File(image.filepath + "\\"
						+ image.filename));
			} catch (IOException ex) {
				// TODO
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void done() {
			ImagePanel.this.repaint();
		}

	}

}
