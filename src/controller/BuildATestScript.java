package controller;

import java.sql.Connection;
import java.sql.SQLException;

import model.DBTestBuilder;
import model.MsSQL;

public class BuildATestScript {

	public static void main(String[] args) {
		String company = args[0];
		String dataBase = args[1];
		String fileName = args[2];
		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		try {
			connMSSQL = dbMSSQL.connect();
			DBTestBuilder bts = new DBTestBuilder(company, dataBase, fileName, dbMSSQL, connMSSQL);
	    	bts.testScript();
	    	System.out.println("Class test_" + fileName + " created");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}