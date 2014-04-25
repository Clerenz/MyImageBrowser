package de.clemensloos.imagebrowser;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drew.imaging.ImageProcessingException;

import de.clemensloos.imagebrowser.database.SqlInterface;
import de.clemensloos.imagebrowser.database.SqlSelectBuilder;
import de.clemensloos.imagebrowser.database.SqliteImpl;
import de.clemensloos.imagebrowser.gui.ImageBrowserGui;
import de.clemensloos.imagebrowser.types.ImgDate;
import de.clemensloos.imagebrowser.types.ImgEvent;
import de.clemensloos.imagebrowser.types.ImgGroup;
import de.clemensloos.imagebrowser.types.Image;
import de.clemensloos.imagebrowser.types.ImgPerson;
import de.clemensloos.imagebrowser.types.ImgTag;


public class ImageBrowser {

	private Logger log = LogManager.getLogger("de.clemensloos.myimagebrowser");

	private String propertiesFile = "/properties.xml";

	private ImageBrowserGui imageBrowserGui;
	private SqlInterface sqlInterface;
	
	private SqlSelectBuilder selectBuilder;

	private MyProperties props;


	public ImageBrowser(ImageBrowserGui imageBrowserGui) {

		log.trace("Start application ImageBrowser");

		this.imageBrowserGui = imageBrowserGui;

		props = new MyProperties(this, propertiesFile, false);
		String projectFolder = props.getProp("last_project_folder");
		String projectName = props.getProp("last_project_name");

		// sqlInterface = new HsqldbImpl(projectFolder, projectName);
		sqlInterface = new SqliteImpl(projectFolder, projectName);

		try {
			sqlInterface.connect();

			if (!sqlInterface.checkTablesExist()) {
				sqlInterface.clearTables();
				sqlInterface.createTables();
			}

		} catch (ClassNotFoundException | SQLException e) {
			log.error(e.getMessage(), e);
			return;
		}

		sqlInterface.checkTablesExist();
		
		selectBuilder = new SqlSelectBuilder(sqlInterface.getConnection());
		
	}


	public List<Image> getImages() {

		try {
			return sqlInterface.getImages( selectBuilder.build() );
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}


	public List<ImgDate> getDates() {

		try {
			return sqlInterface.getDates();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	
	public List<ImgEvent> getEvents() {
		
		try {
			return sqlInterface.getEvents();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}


	public void createTag(ImgTag tag) {
		try {
			sqlInterface.createTag(tag);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	public void deleteTag(ImgTag tag) {
		try {
			sqlInterface.deleteTag(tag);
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	public List<ImgTag> getTags() {

		try {
			return sqlInterface.getTags();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}


	public List<ImgGroup> getGroups() {

		try {
			return sqlInterface.getGroups();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}


	public List<ImgPerson> getPersons() {

		try {
			return sqlInterface.getPersons();
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
			return;
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

		imageBrowserGui.shutDown();

		try {
			sqlInterface.shutDown();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}


	public String getProperty(String key) {

		return props.getProp(key);
	}
	
	// SQL SELECT BUILDER =====================================================
	
	public void setTags(List<ImgTag> tags) {
		selectBuilder.setTags(tags);
	}
	
	public void setPersons(List<ImgPerson> persons) {
		selectBuilder.setPersons(persons);
	}
	
	public void clearGroup() {
		selectBuilder.clearGroup();
	}
	
	public void setGroup(ImgGroup group, boolean wholeGroup) {
		try {
			selectBuilder.setGroup(group, wholeGroup);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setEvent(String event) {
		selectBuilder.setEvent(event);
	}
	
	public void clearEvent() {
		selectBuilder.clearEvent();
	}
	
	public void addDate(ImgDate d) {
		selectBuilder.addDate(d);					
	}
	
	public void removeDate(ImgDate d) {
		selectBuilder.removeDate(d);					
	}
		
	public void clearDates() {
		selectBuilder.clearDates();
	}
	
	public void removeRating(Integer i) {
		selectBuilder.removeRating(i);
	}
	
	public void addRating(Integer i) {
		selectBuilder.addRating(i);
	}

}
