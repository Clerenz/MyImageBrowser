package de.clemensloos.imagebrowser.types;


import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;


public class Image implements Comparable<Image>{

	

	public int image_id = -1;
	public String filename;

	public String filepath;
	public long filesize;
	public long filedate;

	public long imagedate;
	public int imagewidth = 1;
	public int imageheight = 1;
	
	public int imagerating = 0;


	public Image(File f) throws ImageProcessingException, IOException {

		filename = f.getName();

		filepath = f.getParent();
		filesize = f.length();
		filedate = f.lastModified();
		
		Metadata metadata = ImageMetadataReader.readMetadata(f);
		Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
		imagedate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL).getTime();
		

	}


	public Image(ResultSet rs) throws SQLException {

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
