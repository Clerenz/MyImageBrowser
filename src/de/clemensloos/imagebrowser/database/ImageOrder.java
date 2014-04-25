package de.clemensloos.imagebrowser.database;


public enum ImageOrder {

	ORDER_BY_DATE_ASC(" ORDER BY imagedate ASC"),
	ORDER_BY_DATE_DESC(" ORDER BY imagedate DESC");

	public String sql;


	ImageOrder(String sql) {
		this.sql = sql;
	}

}
