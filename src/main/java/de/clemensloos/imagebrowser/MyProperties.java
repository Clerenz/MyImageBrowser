package de.clemensloos.imagebrowser;


import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;


public class MyProperties {

	private ImageBrowser imageBrowser;
	private String propertiesFile;

	private Properties properties;
	private URL url;


	public MyProperties(ImageBrowser imageBrowser, String propertiesFile, boolean create) {

		this.imageBrowser = imageBrowser;
		this.propertiesFile = propertiesFile;
		
		properties = new Properties();

		if(create) {
			File f = new File(propertiesFile);
			if(!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO
					imageBrowser.log("Error creating " + propertiesFile + ": " + e.getMessage());
				}
			}
		}

		url = getClass().getResource(propertiesFile);
		try {
			InputStream is = url.openStream();
			properties.loadFromXML(is);
		} catch (IOException e) {
			// TODO
			imageBrowser.log("Error reading " + propertiesFile + ": " + e.getMessage());
		}
	}


	public String getProp(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			imageBrowser.log("Key '" + key + "' not found in " + propertiesFile);
		}
		return value;
	}


	public String getProp(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			value = defaultValue;
			setProp(key, value);
		}
		return value;
	}
	
	
	public void setProp(String key, String value) {
		properties.setProperty(key, value);
		try {
			OutputStream os = new FileOutputStream(new File(url.toURI()));
			properties.storeToXML(os, "MyImageBrowser");
			os.close();
		} catch (IOException | URISyntaxException e) {
			// TODO
			imageBrowser.log("Error writing " + propertiesFile + ": " + e.getMessage());
		}
	}


	public int getPropInt(String key) {
		int value = Integer.MIN_VALUE;
		String valueString = properties.getProperty(key);
		try {
			value = Integer.parseInt(valueString);
		} catch (NumberFormatException e) {
			imageBrowser.log("Key '" + key + "' does not contain an integer value.");
		} catch (NullPointerException e) {
			imageBrowser.log("Key '" + key + "' not found in " + propertiesFile);
		}
		return value;
	}
	
	
	public int getPropInt(String key, int defaultValue) {
		int value = defaultValue;
		String valueString = properties.getProperty(key);
		try {
			value = Integer.parseInt(valueString);
		} catch (NumberFormatException e) {
			setProp(key, value);
		} catch (NullPointerException e) {
			setProp(key, value);			
		}
		return value;
	}
	
	
	public void setProp(String key, int value) {
		setProp(key, "" + value);
	}
	
	
	public Point getPropPoint(String key, Point defaultValue) {
		Point p = defaultValue;
		String valueString = properties.getProperty(key);
		try {
			String[] valueSplit = valueString.split(",");
			p = new Point(Integer.parseInt(valueSplit[0]), Integer.parseInt(valueSplit[1]));
		} catch (Exception e) {
			setProp(key, defaultValue);
		}
		return p;
	}
	
	
	public void setProp(String key, Point value) {
		setProp(key, value.x+","+value.y);
	}

}
