package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.MsSQL;

public class BuildAllTestScripts {

	public static void main(String[] args) {
		
		String company = args[0];
		String fileName = args[1];
		String selectSql = "SELECT * FROM"
				+ " jdadatabase.INFORMATION_SCHEMA.TABLES"
				+ " WHERE TABLE_TYPE = 'BASE TABLE'"
				+ " order by TABLE_NAME";

		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		try {
			connMSSQL = dbMSSQL.connect();
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
			ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(3);
		    	BuildTestScript bts = new BuildTestScript(company, fileName, dbMSSQL, connMSSQL);
		    	bts.testScript();
		    }
		    dbMSSQL.closeConnection(connMSSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}