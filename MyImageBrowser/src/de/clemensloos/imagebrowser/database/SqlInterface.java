package de.clemensloos.imagebrowser.database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.clemensloos.imagebrowser.types.Date;
import de.clemensloos.imagebrowser.types.Event;
import de.clemensloos.imagebrowser.types.Group;
import de.clemensloos.imagebrowser.types.Image;
import de.clemensloos.imagebrowser.types.Person;
import de.clemensloos.imagebrowser.types.Tag;


public abstract class SqlInterface {

	protected boolean isConnected = false;
	protected Connection connection;
	protected Statement statement;

//	private static SimpleDateFormat sdf = new SimpleDateFormat("y-MM-d k:m:s");
	private static SimpleDateFormat sdf_day = new SimpleDateFormat("y-MM-dd");


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
	
	
	public Connection getConnection() {
		if(isConnected) {
			return connection;
		}
		else {
			return null;
		}
	}


	public void addImage(Image image) throws SQLException {

		String s = createSqlInsert(image);

		statement.execute(s);

		addImageDate(image);

	}


	public void addImageDate(Image image) throws SQLException {

		String day = sdf_day.format(image.imagedate);
		ResultSet rs = statement.executeQuery("SELECT num_images FROM days WHERE day = '" + day + "' LIMIT 1;");
		if (rs.next()) {
			int i = rs.getInt("num_images") + 1;
			statement.execute("UPDATE days SET num_images = " + i + " WHERE day = '" + day + "';");
		}
		else {
			statement.execute("INSERT INTO days VALUES ('" + day + "', 1);");
		}
	}


	public void relocateImage(Image image, String filepath, String filename) throws SQLException {

		statement.execute("UPDATE images SET filepath = '" + filepath + "', filename = '" + filename
				+ "' WHERE image_id = " + image.image_id + ";");

	}
	
	
	public List<Image> getImages(String select) throws SQLException {

		ResultSet rs = statement.executeQuery(select);
		List<Image> images = new ArrayList<Image>();
		while (rs.next()) {
			images.add(new Image(rs));
		}

		return images;
	}


	public List<Date> getDates() throws SQLException {

		ResultSet rs = statement.executeQuery("SELECT * FROM days ORDER BY day ASC");
		List<Date> dates = new ArrayList<Date>();
		while (rs.next()) {
			String date = rs.getString("day");
			int numImages = rs.getInt("num_images");
			dates.add(new Date(date, numImages));
		}
		return dates;
	}


	public void addEvent() throws SQLException {

		String event = "Third Event";
		
		Calendar c = new GregorianCalendar();
		c.set(2013, 0, 1, 0, 0, 0);
		long start = c.getTimeInMillis();
		c.set(2013, 11, 31, 23, 59, 59);
		long end = c.getTimeInMillis();

		statement.execute("INSERT INTO events VALUES ('" + event + "', " + start + ", " + end + ")");

	}


	public List<Event> getEvents() throws SQLException {
		
		ResultSet rs = statement.executeQuery("SELECT * FROM events ORDER BY datestart ASC");
		List<Event> events = new ArrayList<Event>();
		while (rs.next()) {

			String event = rs.getString("event");
			long datestart = rs.getTimestamp("datestart").getTime();
			long dateend = rs.getTimestamp("dateend").getTime();

			events.add(new Event(event, datestart, dateend));
		}

		return events;
	}
	
	
	public void createTag(Tag tag) throws SQLException {
		
		statement.executeUpdate("INSERT INTO tags VALUES ('" + tag.tagname + "');");
	}
	
	
	public void deleteTag(Tag tag) throws SQLException {
		
		statement.executeUpdate("DELETE FROM tags WHERE tag = '" + tag.tagname + "'");
	}


	public List<Tag> getTags() throws SQLException {

		ResultSet rs = statement.executeQuery("SELECT * FROM tags ORDER BY tag ASC");
		List<Tag> tags = new ArrayList<Tag>();
		while (rs.next()) {
			Tag tag = new Tag(rs.getString("tag"));
			tags.add(tag);
		}
		return tags;
	}


	public List<Group> getGroups() throws SQLException {

		ResultSet rs = statement.executeQuery("SELECT * FROM groups ORDER BY groupname ASC");
		List<Group> groups = new ArrayList<Group>();
		while (rs.next()) {
			Group group = new Group(rs.getString("groupname"));
			groups.add(group);
		}
		return groups;
	}


	public List<Person> getPersons() throws SQLException {

		ResultSet rs = statement.executeQuery("SELECT * FROM persons ORDER BY person ASC");
		List<Person> persons = new ArrayList<Person>();
		while (rs.next()) {
			Person person = new Person(rs.getString("person"));
			persons.add(person);
		}
		return persons;
	}


	private int getNextImageId() throws SQLException {

		int i = 0;
		ResultSet rs = statement.executeQuery("SELECT image_id FROM images ORDER BY image_id DESC LIMIT 1");
		if (rs.next()) {
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
				"'" + image.filedate + "', " +
				"'" + image.imagedate + "', " +
//				"'" + sdf.format(image.filedate) + "', " +
//				"'" + sdf.format(image.imagedate) + "', " +
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

		statement.close();
		connection.close();

	}

}
