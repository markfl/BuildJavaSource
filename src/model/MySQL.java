package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
	
	private String companyName;
	
	public MySQL() {
	      super();
	}
	
	public MySQL(String companyName) {
	      super();
	      setCompanyName(companyName);
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public Connection connect() throws SQLException {
		
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String URL = "jdbc:mysql://localhost:3306/" + this.companyName;
			conn = DriverManager.getConnection(URL, "root", "root");
			// System.out.println("Database connected: " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Connection connect(String userName, String passWord) throws SQLException {
		
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String URL = "jdbc:mysql://localhost:3306/" + this.companyName;
			conn = DriverManager.getConnection(URL, userName, passWord);
			// System.out.println("Database connected: " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Connection connect(String company, String userName, String passWord) throws SQLException {
		
		setCompanyName(company);
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String URL = "jdbc:mysql://localhost:3306/" + this.companyName;
			conn = DriverManager.getConnection(URL, userName, passWord);
			// System.out.println("Database connected: " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConnection(Connection conn) throws SQLException {

		if (conn != null)
			// System.out.println("Database disconnected");
			conn.close();
	}
}
