package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.DBClassBuilder;
import model.MsSQL;

public class ReplaceFileClass {

	public static void main(String[] args) {

		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		
		String company = args[0];
		String DB = args[1];
		String fileToReplace = args[2];
		Connection connMSSQL = null;
		DBClassBuilder dbcb = new DBClassBuilder(company);
		MsSQL dbMSSQL = new MsSQL(company);
		String selectSql = "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE INFORMATION_SCHEMA.COLUMNS.TABLE_NAME=? " 
				+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";

		dbcb.setCompanyName(company);
		dbcb.setDataBase(DB);
		try {
			connMSSQL = dbMSSQL.connect();
			dbcb.setConnMSSQL(connMSSQL);
			boolean firstRecord = true;
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
			checkStmtSelect.setString(1, fileToReplace);
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	dbcb.setFileName(fileToReplace);
		    	fields = dbcb.getFileFields(firstRecord, resultsSelect, fields);
			    firstRecord = false;
		    }
		    dbcb.setCompanyName(company);
		    dbcb.setDataBase(DB);
		    dbcb.setFileName(fileToReplace);
		    dbcb.setAllFields(fields);
		    dbcb.executeAllfunctions(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			dbMSSQL.closeConnection(connMSSQL);
			System.out.println("Program completed normally, " + fileToReplace + " was created.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}