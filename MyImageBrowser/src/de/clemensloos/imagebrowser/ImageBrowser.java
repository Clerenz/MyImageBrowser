package de.clemensloos.imagebrowser;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drew.imaging.ImageProcessingException;

import de.clemensloos.imagebrowser.database.SqlInterface;
import de.clemensloos.imagebrowser.database.SqliteImpl;
import de.clemensloos.imagebrowser.gui.ImageBrowserGui;
import de.clemensloos.imagebrowser.types.Image;


public class ImageBrowser {
	
	private Logger log = LogManager.getLogger("de.clemensloos.myimagebrowser");
	
	private String propertiesFile = "resources/properties.xml";
	
	private ImageBrowserGui imageBrowserGui;
	private SqlInterface sqlInterface;
	
	private MyProperties props;


	public ImageBrowser(ImageBrowserGui imageBrowserGui) {
		
		log.trace("Start application ImageBrowser");

		this.imageBrowserGui = imageBrowserGui;
		
		props = new MyProperties(this, propertiesFile);
		String projectFolder = props.getProp("last_project_folder");
		String projectName = props.getProp("last_project_name");
		
//		sqlInterface = new HsqldbImpl(projectFolder, projectName);
		sqlInterface = new SqliteImpl(projectFolder, projectName);
		
		try {
			sqlInterface.connect();
			
			if( ! sqlInterface.checkTablesExist()) {
				sqlInterface.clearTables();
				sqlInterface.createTables();		
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			log.error(e.getMessage(), e);
		}

		sqlInterface.checkTablesExist();
		
	}
	
	
	public List<Image> getImages() {
		
		try {
			return sqlInterface.getImages();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
		
	}
	
	
	public void addImage(File f) {
		
		Image image = null;
		try {
			image = new Image(f);
		} catch (ImageProcessingException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		try {
			sqlInterface.addImage(image);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	
	
	
	public void log(String message) {
		imageBrowserGui.log(message);
	}
	
	
	public void shutDown() {
		try {
			sqlInterface.shutDown();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
	

}
