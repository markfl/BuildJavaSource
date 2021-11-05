package model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Oracle {
	
	private String companyName;
	
	public Oracle() {
	      super();
	}
	
	public Oracle(String companyName) {
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
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","hr","hr");
			// System.out.println("Database connected: " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	public Connection connect(String userName, String passWord) throws SQLException {

		Connection conn = null;
		
		try {
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","hr","hr");
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
