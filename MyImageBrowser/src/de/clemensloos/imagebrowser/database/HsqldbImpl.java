package de.clemensloos.imagebrowser.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class HsqldbImpl extends SqlInterface {

	private String driver = "org.hsqldb.jdbcDriver";
	private String urlPrefix = "jdbc:hsqldb:file:/";
	private String projectPath;
	private String projectName;
	
	public HsqldbImpl(String projectPath, String projectName) {
		
		this.projectPath = projectPath;
		this.projectName = projectName;
		
	}
	
	@Override
	public void connect() throws ClassNotFoundException, SQLException {

		Class.forName(driver);
		
		String url = urlPrefix + projectPath + "\\" + projectName;
		String user = "sa";
		String pass = "";
		connection = DriverManager.getConnection(url, user, pass);
		
		statement = connection.createStatement();
		
		isConnected = true;
		
	}

}
