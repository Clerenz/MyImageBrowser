package de.clemensloos.imagebrowser.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.clemensloos.imagebrowser.types.Image;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Image image;
	BufferedImage bi = null;
	
	int imagePosX = 0;
	int imagePosY = 0;
	int imageSizeX = 0;
	int imageSizeY = 0;

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
		
		setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) { }
			
			@Override
			public void mousePressed(MouseEvent e) { }
			
			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) { }
		});
		
		calcImageSizes();
		
		new ImageWorker().execute();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bi != null) {
			g.drawImage(bi, imagePosX, imagePosY, imageSizeX, imageSizeY, 0, 0,
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
	
	
	private void calcImageSizes() {
		
		if(image.imagewidth >= image.imageheight) {
			imageSizeX = getWidth();
		}
		else {
			imageSizeX = (int)(getWidth() * ( (double)image.imagewidth / (double)image.imageheight));
			imagePosX = (getWidth() - imageSizeX) / 2;
			imageSizeX += imagePosX;
		}
		
		if(image.imagewidth <= image.imageheight) {
			imageSizeY = getHeight();
		}
		else {
			imageSizeY = (int)(getHeight() / ((double)image.imagewidth / (double)image.imageheight ));
			imagePosY = (getHeight() - imageSizeY) / 2;
			imageSizeY += imagePosY;
		}
		
	}


}
