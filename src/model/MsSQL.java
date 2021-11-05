package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MsSQL {
	
	private String companyName;
	
	public MsSQL() {
	      super();
	}
	
	public MsSQL(String companyName) {
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
		String url = "jdbc:sqlserver://localhost;integratedSecurity=true;"
        + "database=" + this.companyName +";";
        
		try {
			conn = DriverManager.getConnection(url);
			// System.out.println("Database connected: " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public Connection connect(String company) throws SQLException {

		setCompanyName(company);
		Connection conn = null;
		String url = "jdbc:sqlserver://localhost;integratedSecurity=true;"
        + "database=" + this.companyName +";";
        
		try {
			conn = DriverManager.getConnection(url);
			// System.out.println("Database connected: " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConnection(Connection conn) throws SQLException {

		if (conn != null) {
			conn.close();
			// System.out.println("Database disconnected");
		}
	}
}
