package de.clemensloos.imagebrowser.gui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
	boolean sizeChanged = false;
	BufferedImage bi = null;

	int imagePosX = 0;
	int imagePosY = 0;
	int imageSizeX = 0;
	int imageSizeY = 0;

	private ImageBrowser2DGui gui;

	JButton tagImageButton;


	public ImagePanel(ImageBrowser2DGui g, Image i, int x, int y, int width, int height) {

		super(true);

		this.gui = g;

		this.image = i;

		Dimension d = new Dimension(width, height);
		setSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setPreferredSize(d);
		setForeground(Color.white);
		setBackground(Color.black);
		setOpaque(true);

		setBounds(x, y, width, height);

		if(gui.mainPanelViewMode == ImageBrowser2DGui.VIEW_MODE_MULTI) {
			if (image.selected) {
				setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			} else {
				setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
			}
		}

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}


			@Override
			public void mousePressed(MouseEvent e) {
			}


			@Override
			public void mouseExited(MouseEvent e) {
				if (!contains(e.getPoint())) {
//					setBackground(Color.BLACK); TODO
					tagImageButton.setVisible(false);
				}
			}


			@Override
			public void mouseEntered(MouseEvent e) {
//				setBackground(Color.DARK_GRAY); TODO
				tagImageButton.setVisible(true);
			}


			@Override
			public void mouseClicked(MouseEvent e) {
				
				gui.leadSelection = image.image_id;
				
				if(e.getClickCount() == 2) {
					gui.switchView();
					return;
				}
				
				if (e.isControlDown()) {
					image.selected = !image.selected;
				}
				else if (!image.selected) {
					gui.deselectAll();
					image.selected = true;
				}
				
				if (gui.mainPanelViewMode == ImageBrowser2DGui.VIEW_MODE_MULTI) {
					if (image.selected) {
						setBorder(BorderFactory.createLineBorder(Color.RED, 2));
					}
					else {
						setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
					}
				}
			}
		});

		calcImageSizes();

		selectImageSizing();

		ImageWorker imageWorker = new ImageWorker();
		imageWorker.execute();

		tagImageButton = new JButton("...");
		tagImageButton.addActionListener(new ActionListener() { // TODO
					@Override
					public void actionPerformed(ActionEvent arg0) {
						gui.showUserMessage("click");
					}
				});
		tagImageButton.setVisible(false);

		String columnSpecs = "3dlu, pref, 3dlu, pref:GROW, 3dlu, pref, 3dlu";
		String rowSpecs = "3dlu, pref, 3dlu, pref:GROW, 3dlu, pref, 3dlu";
		FormLayout layout = new FormLayout(columnSpecs, rowSpecs);
		layout.setColumnGroups(new int[][] { { 2, 6 } });
		layout.setRowGroups(new int[][] { { 2, 6 } });
		setLayout(layout);

		PanelBuilder builder = new PanelBuilder(layout, this);
		CellConstraints cc = new CellConstraints();

		builder.add(tagImageButton, cc.xy(6, 6));
	}


	public void refresh(Image image, int x, int y, int width, int height) {
		
		this.image = image;

		Dimension d = new Dimension(width, height);
		setSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setPreferredSize(d);
		setForeground(Color.white);
		setBackground(Color.black);
		if(gui.mainPanelViewMode == ImageBrowser2DGui.VIEW_MODE_SINGLE) {
			setBorder(null);
		}
//		setBorder(null);
		setOpaque(true);

		setBounds(x, y, width, height);

		calcImageSizes();

		selectImageSizing();

		ImageWorker imageWorker = new ImageWorker();
		imageWorker.execute();

	}


	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (bi != null) {
			g.drawImage(bi, imagePosX, imagePosY, imageSizeX, imageSizeY, 0, 0,
					bi.getWidth(), bi.getHeight(), null);
		}
		
		Graphics2D g2d = (Graphics2D) g;
		Color c = new Color(0, 0, 0, gui.alpha);
		g2d.setColor(c);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
	}


	public class ImageWorker extends SwingWorker<Integer, Integer> {

		@Override
		protected Integer doInBackground() throws Exception {

			try {

				if (bi == null || sizeChanged) {
					File thumb = new File(realImgPath);
					if (thumb.exists()) {
						bi = ImageIO.read(thumb);
					}
					else {
						bi = ImageIO.read(new File(image.filepath + "\\" + image.filename));
						if (thumbSize > 0) {
							Thumbnails
									.of(bi)
									.size(thumbSize, thumbSize)
									.toFile(realImgPath);
							bi = ImageIO.read(thumb);
						}
					}

					sizeChanged = false;
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

		double labelRatio = (double) getWidth() / (double) getHeight();
		double imageRatio = (double) image.imagewidth / (double) image.imageheight;

		if (labelRatio <= imageRatio) {
			imagePosX = 0;
			imageSizeX = getWidth();			
		}
		else {
			 imageSizeX = (int) (getWidth() * (imageRatio / labelRatio));
			 imagePosX = (getWidth() - imageSizeX) / 2;
			 imageSizeX += imagePosX;			
		}
		
		if (labelRatio >= imageRatio) {
			imagePosY = 0;
			imageSizeY = getHeight();
		}
		else {
			imageSizeY = (int) (getHeight() * (labelRatio / imageRatio));
			imagePosY = (getHeight() - imageSizeY) / 2;
			imageSizeY += imagePosY;
		}
		
	}


	public void select() {
		image.selected = true;
		if(gui.mainPanelViewMode == ImageBrowser2DGui.VIEW_MODE_MULTI) {
			setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		}
	}


	public void deselect() {
		image.selected = false;
		if(gui.mainPanelViewMode == ImageBrowser2DGui.VIEW_MODE_MULTI){
			setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		}
	}


	private void selectImageSizing() {

		int oldValue = thumbSize;

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
		else {
			realImgPath = "void";
			thumbSize = 0;
		}

		if (thumbSize != oldValue) {
			sizeChanged = true;
		}

	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof Image) {
			if (image.equals((Image) obj)) {
				return true;
			}
		}

		if (!(obj instanceof ImagePanel)) {
			return false;
		}

		ImagePanel other = (ImagePanel) obj;
		return image.equals(other.image);
	}

}
