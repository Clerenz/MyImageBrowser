package de.clemensloos.imagebrowser.types;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;


public class Image extends JPanel implements Comparable<Image>{

	
	private static final long serialVersionUID = 1L;
	
	public int image_id = -1;
	public String filename;

	public String filepath;
	public long filesize;
	public long filedate;

	public long imagedate;
	public int imagewidth = 1;
	public int imageheight = 1;
	
	public int imagerating = 0;
	
	
	public Image(File f, int x, int y, int diameter) throws ImageProcessingException, IOException {
		
		super();
		
		loadFromFile(f);
		
		initComponent(x, y, diameter);
	}


	public Image(File f) throws ImageProcessingException, IOException {
		
		super();
		
		loadFromFile(f);
	}
	
	
	public Image(ResultSet rs) throws SQLException {
		super();

		image_id = rs.getInt("image_id");
		filename = rs.getString("filename");

		filepath = rs.getString("filepath");
		filesize = rs.getLong("filesize");
		filedate = rs.getTimestamp("filedate").getTime();

		imagedate = rs.getTimestamp("imagedate").getTime();
		imagewidth = rs.getInt("imagewidth");
		imageheight = rs.getInt("imageheight");
		
		imagerating = rs.getInt("imagerating");
		
	}
	
		
	private void loadFromFile(File f) throws ImageProcessingException, IOException {
		
		filename = f.getName();
	
		filepath = f.getParent();
		filesize = f.length();
		filedate = f.lastModified();
		
		Metadata metadata = ImageMetadataReader.readMetadata(f);
		Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
		imagedate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).getTime();
		directory = metadata.getDirectory(JpegDirectory.class);
		try {
			imagewidth = directory.getInt(JpegDirectory.TAG_JPEG_IMAGE_WIDTH);
			imageheight = directory.getInt(JpegDirectory.TAG_JPEG_IMAGE_HEIGHT);
		} catch (MetadataException e) {
			directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			try {
				imagewidth = directory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
				imageheight = directory.getInt(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
			} catch (MetadataException e1) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e1.printStackTrace();
			}
		}
	}
	
	
	public Component initComponent(int x, int y, int diameter) {
		
		
		ImagePanel label = new ImagePanel();
		
		Dimension d = new Dimension(diameter, diameter);
		label.setSize(d);
		label.setMaximumSize(d);
		label.setMinimumSize(d);
		label.setPreferredSize(d);
		label.setForeground(Color.white);
		label.setBackground(Color.black);
		label.setOpaque(true);
		
		add(label);
		
		setBounds(x, y, diameter, diameter);
		
		return this;
	}
	
	
	public class ImagePanel extends JPanel{

	    private static final long serialVersionUID = 1L;
		private BufferedImage image;

	    public ImagePanel() {
	       try {                
	          image = ImageIO.read(new File(filepath + "\\" + filename));
	       } catch (IOException ex) {
	           // TODO 
	    	   ex.printStackTrace();
	       }
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, imagewidth, imageheight, null);
	    }

	}


	@Override
	public String toString() {
		return filepath + "\\" + filename;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + image_id;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Image)) {
			return false;
		}
		Image other = (Image) obj;
		if (image_id != other.image_id) {
			return false;
		}
		return true;
	}


	@Override
	public int compareTo(Image o) {
		if(this.imagedate == o.imagedate) {
			return 0;			
		}
		if(this.imagedate < o.imagedate) {
			return 1;			
		}
		return -1;
	}



}
