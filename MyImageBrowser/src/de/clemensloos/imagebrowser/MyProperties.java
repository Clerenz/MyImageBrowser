package de.clemensloos.imagebrowser;


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


	public MyProperties(ImageBrowser imageBrowser, String propertiesFile) {

		this.imageBrowser = imageBrowser;
		this.propertiesFile = propertiesFile;

		url = getClass().getResource(propertiesFile);
		properties = new Properties();
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

}
