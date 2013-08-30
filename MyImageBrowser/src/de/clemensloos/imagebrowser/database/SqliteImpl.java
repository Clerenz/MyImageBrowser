package de.clemensloos.imagebrowser.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteImpl extends SqlInterface {

	private String driver = "org.sqlite.JDBC";
	private String urlPrefix = "jdbc:sqlite:";
	private String projectPath;
	private String projectName;
	
	public SqliteImpl(String projectPath, String projectName) {
		
		this.projectPath = projectPath;
		this.projectName = projectName;
		
	}
	
	@Override
	public void connect() throws ClassNotFoundException, SQLException {

		Class.forName(driver);
		
		String url = urlPrefix + projectPath + "\\" + projectName;
//		String user = "sa";
//		String pass = "";
		connection = DriverManager.getConnection(url);
		
		statement = connection.createStatement();
		
		isConnected = true;
		
	}

}
