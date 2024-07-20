package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.CheckTime;
import model.DBTestBuilder;
import model.MsSQL;

public class BuildAllTestScripts {

	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();
	
		String company = args[0];
		String dataBase = args[1];
		String fileName;
		String selectSql = "SELECT * FROM "
				+ company 
				+ ".INFORMATION_SCHEMA.TABLES"
				+ " WHERE TABLE_TYPE = 'BASE TABLE'"
				+ " order by TABLE_NAME";

		int classesCreated = 0;
		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		try {
			connMSSQL = dbMSSQL.connect();
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
			ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(3);
		    	DBTestBuilder bts = new DBTestBuilder(company, dataBase, fileName, dbMSSQL, connMSSQL);
		    	bts.testScript();
		    	System.out.println("Class test_" + fileName + " created");
		    	classesCreated++;
		    }
		    dbMSSQL.closeConnection(connMSSQL);
		    // System.out.println("Program completed normally, " + classesCreated + " classes created.");
		    String returnString = ct.calculateElapse("Build Test");
			returnString = returnString + " " + classesCreated + " classes created.";
			System.out.println(returnString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}