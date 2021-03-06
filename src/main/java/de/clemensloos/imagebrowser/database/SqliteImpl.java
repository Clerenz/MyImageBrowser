package de.clemensloos.imagebrowser.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteImpl extends SqlInterface {

	private String driver = "org.sqlite.JDBC";
	private String urlPrefix = "jdbc:sqlite:";
	private String projectName;
	
	public SqliteImpl(String projectName) {
		
		this.projectName = projectName;
		
	}
	
	@Override
	public void connect() throws ClassNotFoundException, SQLException {

		Class.forName(driver);
		
		String url = urlPrefix + projectName + ".sqlite";
		connection = DriverManager.getConnection(url);
		
		statement = connection.createStatement();
		
		isConnected = true;
		
	}

}
