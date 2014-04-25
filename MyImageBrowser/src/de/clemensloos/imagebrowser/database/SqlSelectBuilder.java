package de.clemensloos.imagebrowser.database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.clemensloos.imagebrowser.types.ImgDate;
import de.clemensloos.imagebrowser.types.ImgGroup;
import de.clemensloos.imagebrowser.types.ImgPerson;
import de.clemensloos.imagebrowser.types.ImgTag;


public class SqlSelectBuilder {


	private List<ImgTag> tags = new ArrayList<ImgTag>();
	private List<ImgPerson> persons = new ArrayList<ImgPerson>();
	private List<String> groupPersons = new ArrayList<String>();
	private boolean wholeGroup = false;

	private String event = "";
	private List<ImgDate> dates = new ArrayList<ImgDate>();
	
	private List<Integer> rating = new ArrayList<Integer>();
	
	private ImageOrder order = ImageOrder.ORDER_BY_DATE_ASC;

	private Connection connection;


	public SqlSelectBuilder(Connection connection) {
		this.connection = connection;
		
		rating.add(-100);
		rating.add(-1);
		rating.add(0);
		rating.add(1);
	}


	public String build() {

		String where = "";

		for (ImgTag s : tags) {
			where += " AND image_id IN (SELECT image_id FROM images_tags WHERE tag = '" + s + "')";
		}

		for (ImgPerson s : persons) {
			where += " AND image_id IN (SELECT image_id FROM images_persons WHERE person = '" + s + "')";
		}

		if (wholeGroup) {
			for (String s : groupPersons) {
				where += " AND image_id IN (SELECT image_id FROM images_persons WHERE person = '" + s + "')";
			}
		}
		else if (groupPersons.size() > 0) {
			where += " AND (";
			String or = "";
			for (String s : groupPersons) {
				or += " OR image_id IN (SELECT image_id FROM images_persons WHERE person = '" + s + "')";
			}
			or = or.replaceFirst(" OR ", " ");
			where += or + " )";
		}

		if (!event.equals("")) {
			where += " AND imagedate > (SELECT datestart FROM events WHERE event = '" + event
					+ "') AND imagedate < (SELECT dateend FROM events WHERE event = '" + event + "')";
		}

		if (dates.size() > 0) {
			where += " AND (";
			String or = "";
			for (ImgDate d : dates) {
				or += " OR (imagedate > " + d.longDateStart + " AND imagedate < " + d.longDateEnd + ")";
			}
			or = or.replaceFirst(" OR ", " ");
			where += or + " )";
		}
		
		if (rating.size() > 0) {
			where += " AND (";
			String or = "";
			for (Integer i : rating) {
				or += " OR (imagerating = " + i + ")";
			}
			or = or.replaceFirst(" OR ", " ");
			where += or + " )";
		}

		where = where.replaceFirst(" AND ", " WHERE ");

		String s = "SELECT * FROM images" + where + order.sql + ";";

//		System.out.println(s); // XXX
		
		return s;

	}


	public void clearAll() {
		tags.clear();
		persons.clear();
		groupPersons.clear();
		event = "";
	}


	public void addTag(ImgTag tag) {
		this.tags.add(tag);
	}


	public void removeTag(ImgTag tag) {
		if (this.tags.contains(tag)) {
			this.tags.remove(tag);
		}
	}


	public void setTags(List<ImgTag> tags) {
		this.tags = tags;
	}


	public void clearTags() {
		this.tags.clear();
	}


	public void addPerson(ImgPerson person) {
		this.persons.add(person);
	}


	public void removePerson(ImgPerson person) {
		if (this.persons.contains(person)) {
			this.persons.remove(person);
		}
	}


	public void setPersons(List<ImgPerson> persons) {
		this.persons = persons;
	}


	public void clearPersons() {
		this.persons.clear();
	}


	public void setGroup(ImgGroup group, boolean wholeGroup) throws SQLException {
		this.wholeGroup = wholeGroup;
		this.groupPersons.clear();
		ResultSet rs = connection.createStatement().executeQuery(
				"SELECT person FROM groups_persons WHERE groupname = '" + group + "'");
		while (rs.next()) {
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


	public void addDate(ImgDate d) {
		if(!dates.contains(d)) {
			dates.add(d);			
		}
	}
	
	
	public void removeDate(ImgDate d) {
		if(dates.contains(d)) {
			dates.remove(d);
		}
	}


	public void setDates(List<ImgDate> dates) {
		clearDates();
		this.dates = dates;
	}


	public void clearDates() {
		dates.clear();
	}
	
	
	public void removeRating(Integer i) {
		if(rating.contains(i)) {
			rating.remove(i);
		}
	}
	
	
	public void addRating(Integer i) {
		if(!rating.contains(i)) {
			rating.add(i);
		}
	}
	
	public void setOrder(ImageOrder order) {
		this.order = order;
	}

}
