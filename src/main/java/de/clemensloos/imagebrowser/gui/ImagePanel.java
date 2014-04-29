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

import net.coobird.thumbnailator.Thumbnails;
import de.clemensloos.imagebrowser.ImageBrowser;
import de.clemensloos.imagebrowser.types.Image;


public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	static final int THUMB_MIN = 125;
	static final int THUMB_SMA = 250;
	static final int THUMB_MED = 500;
	static final int THUMB_MAX = 1000;
	

	Image image;

	String realImgPath;
	int thumbSize;
	BufferedImage bi = null;
	int position;

	int imagePosX = 0;
	int imagePosY = 0;
	int imageSizeX = 0;
	int imageSizeY = 0;
	
	private ImageWorker imageWorker = new ImageWorker();


	public ImagePanel(Image image, int x, int y, int diameter, int position) {

		super();

		this.image = image;
		this.position = position;

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
			public void mouseReleased(MouseEvent e) {
			}


			@Override
			public void mousePressed(MouseEvent e) {
			}


			@Override
			public void mouseExited(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
			}


			@Override
			public void mouseEntered(MouseEvent e) {
				setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			}


			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		calcImageSizes();

		selectImageSizing();

		imageWorker.execute();
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bi != null) {
			g.drawImage(bi, imagePosX, imagePosY, imageSizeX, imageSizeY, 0, 0,
					bi.getWidth(), bi.getHeight(), null);
		}
	}


	public class ImageWorker extends SwingWorker<Integer, Integer> {
		
		@Override
		protected Integer doInBackground() throws Exception {
			
			try {
				
				File thumb = new File(realImgPath);
				if (thumb.exists()) {
					bi = ImageIO.read(thumb);
				}
				else {
					bi = ImageIO.read(new File(image.filepath + "\\" + image.filename));
					Thumbnails
							.of(bi)
							.size(thumbSize, thumbSize)
							.toFile(realImgPath);
					bi = ImageIO.read(thumb);
				}
				
				
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

		if (image.imagewidth >= image.imageheight) {
			imageSizeX = getWidth();
		}
		else {
			imageSizeX = (int) (getWidth() * ((double) image.imagewidth / (double) image.imageheight));
			imagePosX = (getWidth() - imageSizeX) / 2;
			imageSizeX += imagePosX;
		}

		if (image.imagewidth <= image.imageheight) {
			imageSizeY = getHeight();
		}
		else {
			imageSizeY = (int) (getHeight() / ((double) image.imagewidth / (double) image.imageheight));
			imagePosY = (getHeight() - imageSizeY) / 2;
			imageSizeY += imagePosY;
		}

	}


	private void selectImageSizing() {

		if (imageSizeX < THUMB_MIN && imageSizeY < THUMB_MIN) {
			realImgPath = ImageBrowser.projectName + "\\thumbs\\" + image.image_id + "_" + THUMB_MIN + ".jpg";
			thumbSize = THUMB_MIN;
		}
		else if (imageSizeX < THUMB_SMA && imageSizeY < THUMB_SMA) {
			realImgPath = ImageBrowser.projectName + "\\thumbs\\" + image.image_id + "_" + THUMB_SMA + ".jpg";
			thumbSize = THUMB_SMA;
		}
		else if (imageSizeX < THUMB_MED && imageSizeY < THUMB_MED) {
			realImgPath = ImageBrowser.projectName + "\\thumbs\\" + image.image_id + "_" + THUMB_MED + ".jpg";
			thumbSize = THUMB_MED;
		}
		else if (imageSizeX < THUMB_MAX && imageSizeY < THUMB_MAX) {
			realImgPath = ImageBrowser.projectName + "\\thumbs\\" + image.image_id + "_" + THUMB_MAX + ".jpg";
			thumbSize = THUMB_MAX;
		}

	}

}
