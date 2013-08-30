package de.clemensloos.imagebrowser.database;


import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.clemensloos.imagebrowser.types.Image;


public abstract class SqlInterface {

	protected boolean isConnected = false;
	protected Connection connection;
	protected Statement statement;

	private static SimpleDateFormat sdf = new SimpleDateFormat("y-MM-d k:m:s");
	private static SimpleDateFormat sdf_day = new SimpleDateFormat("y-MM-d");
	

	public SqlInterface() {

	}


	public abstract void connect() throws ClassNotFoundException, SQLException;


	public boolean isConnected() {
		return this.isConnected;
	}


	public boolean checkTablesExist() {

		try {
			statement.executeQuery("SELECT image_id FROM images LIMIT 1;");

		} catch (SQLException sqlException) {
			return false;
		}

		return true;

	}


	public void addImage(Image image) throws SQLException {

		String s = createSqlInsert(image);
		
		statement.execute( s );
		
		addImageDate(image);
		
	}
	
	
	public void addImageDate(Image image) throws SQLException {
		
		String day = sdf_day.format(image.imagedate);
		System.out.println(day);
		ResultSet rs = statement.executeQuery("SELECT num_images FROM days WHERE day = '"+day+"' LIMIT 1;");
		if(rs.next()) {
			int i = rs.getInt("num_images") + 1;
			statement.execute("UPDATE days SET num_images = "+i+" WHERE day = '"+day+"';");
		}
		else {
			statement.execute("INSERT INTO days VALUES ('"+day+"', 1);");
		}
		
	}
	
	
	public void relocateImage(Image image, String filepath, String filename) throws SQLException {
		
		statement.execute("UPDATE images SET filepath = '"+filepath+"', filename = '"+filename+"' WHERE image_id = "+image.image_id+";");
		
	}
	
	
	public List<Image> getImages() throws SQLException {
		
		ResultSet rs = statement.executeQuery("SELECT * FROM images");
		List<Image> images = new ArrayList<Image>();
		while(rs.next()) {
			images.add(new Image(rs));
		}
		
		return images;
	}
	
	
	private int getNextImageId() throws SQLException {
		
		int i = 0;
		ResultSet rs = statement.executeQuery("SELECT image_id FROM images ORDER BY image_id DESC LIMIT 1");
		if(rs.next()) {
			i = rs.getInt("image_id");
			i++;
		}
		
		return i;
	}
	
	
	public String createSqlInsert(Image image) throws SQLException {

		int image_id = getNextImageId();
		image.image_id = image_id;

		String sql = "INSERT INTO images VALUES (" +
				image_id + ", " +
				"'" + image.filename + "', " +
				"'" + image.filepath + "', " +
				image.filesize + ", " +
				"'" + sdf.format(image.filedate) + "', " +
				"'" + sdf.format(image.imagedate) + "', " +
				image.imagewidth + ", " +
				image.imageheight + ", " +
				image.imagerating + " );";
		return sql;

	}


	public void clearTables() throws SQLException {

		statement.clearBatch();
		
		statement.addBatch("DROP TABLE IF EXISTS images_persons;");
		statement.addBatch("DROP TABLE IF EXISTS images_tags;");
		statement.addBatch("DROP TABLE IF EXISTS groups_persons;");
		
		statement.addBatch("DROP TABLE IF EXISTS images;");
		statement.addBatch("DROP TABLE IF EXISTS tags;");
		statement.addBatch("DROP TABLE IF EXISTS persons;");
		statement.addBatch("DROP TABLE IF EXISTS groups;");
		
		statement.addBatch("DROP TABLE IF EXISTS events;");
		
		statement.executeBatch();

	}


	public void createTables() throws SQLException {

		statement.clearBatch();
		
		statement.addBatch("CREATE TABLE images ( " +
				"image_id INTEGER NOT NULL, " +
				"filename VARCHAR(50) NOT NULL, " +
				"filepath VARCHAR(255), " +
				"filesize INTEGER, " +
				"filedate TIMESTAMP, " +
				"imagedate TIMESTAMP, " +
				"imagewidth SMALLINT, " +
				"imageheight SMALLINT, " +
				"imagerating TINYINT DEFAULT 0, " +
				"PRIMARY KEY (image_id));");
		
		statement.addBatch("CREATE TABLE tags ( tag VARCHAR(50) NOT NULL, PRIMARY KEY (tag));");
		statement.addBatch("CREATE TABLE persons ( person VARCHAR(50) NOT NULL, PRIMARY KEY (person));");
		statement.addBatch("CREATE TABLE groups ( groupname VARCHAR(50) NOT NULL, PRIMARY KEY (groupname));");
		
		statement.addBatch("CREATE TABLE images_tags ( image_id INTEGER, tag VARCHAR(50), " +
				"FOREIGN KEY (image_id) REFERENCES images(image_id) on delete cascade on update restrict, " +
				"FOREIGN KEY (tag) REFERENCES tags(tag) on delete cascade on update cascade);");
		
		statement.addBatch("CREATE TABLE images_persons ( image_id INTEGER, person VARCHAR(50), " +
				"FOREIGN KEY (image_id) REFERENCES images(image_id) on delete cascade on update restrict, " +
				"FOREIGN KEY (person) REFERENCES persons(person) on delete cascade on update cascade);");
		
		statement.addBatch("CREATE TABLE groups_persons ( groupname VARCHAR(50), person VARCHAR(50), " +
				"FOREIGN KEY (groupname) REFERENCES groups(groupname) on delete cascade on update cascade, " +
				"FOREIGN KEY (person) REFERENCES persons(person) on delete cascade on update cascade);");
		
		statement.addBatch("CREATE TABLE events ( event VARCHAR(50) NOT NULL, " +
				"datestart DATETIME NOT NULL, " +
				"dateend DATETIME NOT NULL, " +
				"PRIMARY KEY (event));");
		
		statement.addBatch("CREATE TABLE days ( day DATE, num_images INTEGER DEFAULT 1, " +
				"PRIMARY KEY (day));");
		
		statement.executeBatch();

	}


	public void shutDown() throws SQLException {

		connection.close();

	}

}