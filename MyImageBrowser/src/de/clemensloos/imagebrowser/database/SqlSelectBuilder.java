package de.clemensloos.imagebrowser.database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.clemensloos.imagebrowser.types.Group;
import de.clemensloos.imagebrowser.types.Person;
import de.clemensloos.imagebrowser.types.Tag;


public class SqlSelectBuilder {

	private List<Tag> tags = new ArrayList<Tag>();
	private List<Person> persons = new ArrayList<Person>();
	private List<String> groupPersons = new ArrayList<String>();
	private boolean wholeGroup = false;

	private String event = "";
	
	private Connection connection;


	public SqlSelectBuilder(Connection connection) {
		this.connection = connection;
	}

	
	public String build() {
		
		String where = "";
		
		for (Tag s : tags) {
			where += " AND image_id IN (SELECT image_id FROM images_tags WHERE tag = '"+s+"')";
		}
		
		if ( ! event.equals("")) {
			where += " AND imagedate > (SELECT datestart FROM events WHERE event = '"+event+"') AND imagedate < (SELECT dateend FROM events WHERE event = '"+event+"')";
		}
		
		for (Person s : persons) {
			where += " AND image_id IN (SELECT image_id FROM images_persons WHERE person = '"+s+"')";
		}
		
		if (wholeGroup) {
			for (String s : groupPersons) {
				where += " AND image_id IN (SELECT image_id FROM images_persons WHERE person = '"+s+"')";
			}
		}
		else if (groupPersons.size() > 0) {
			where += " AND (";
			String or = "";
			for (String s : groupPersons) {
				or += " OR image_id IN (SELECT image_id FROM images_persons WHERE person = '"+s+"')";
			}
			or = or.replaceFirst(" OR ", " ");
			where += or + " )";
		}
		
		where = where.replaceFirst(" AND ", " WHERE ");
		
		String s = "SELECT * FROM images" + where + ";";
		
		return s;
		
	}
	
	
	public void clearAll() {
		tags.clear();
		persons.clear();
		groupPersons.clear();
		event = "";
	}


	public void addTag(Tag tag) {
		this.tags.add(tag);
	}


	public void removeTag(Tag tag) {
		if (this.tags.contains(tag)) {
			this.tags.remove(tag);
		}
	}


	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}


	public void clearTags() {
		this.tags.clear();
	}
	
	
	public void addPerson(Person person) {
		this.persons.add(person);
	}


	public void removePerson(Person person) {
		if (this.persons.contains(person)) {
			this.persons.remove(person);
		}
	}


	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}


	public void clearPersons() {
		this.persons.clear();
	}
	
	
	public void setGroup(Group group, boolean wholeGroup) throws SQLException {
		this.wholeGroup = wholeGroup;
		this.groupPersons.clear();
		ResultSet rs = connection.createStatement().executeQuery("SELECT person FROM groups_persons WHERE groupname = '"+group+"'");
		while(rs.next()) {
			this.groupPersons.add(rs.getString("person"));
		}
	}
	
	
	public void clearGroup() {
		this.groupPersons.clear();
	}
	
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	
	public void clearEvent() {
		this.event = "";
	}

}
