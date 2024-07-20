package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.DBClassBuilder;
import model.MsSQL;

public class CreateAJavaClass {
	
	public static void main(String[] args) {
		
		String company = args[0];
		String DB = args[1];
		String fileName = args[2];
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		DBClassBuilder dbcb = new DBClassBuilder(company);
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		boolean firstRecord = true;
		
		String selectSql = "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE INFORMATION_SCHEMA.COLUMNS.TABLE_NAME= 'ALLTYPES' "
				+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";
		
		try {
			
			connMSSQL = dbMSSQL.connect();
			dbcb.setConnMSSQL(connMSSQL);
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
			// checkStmtSelect.setString(1, fileName.trim());
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(1);
		    	fields = dbcb.getFileFields(firstRecord, resultsSelect, fields);
		    	firstRecord = false;
		    }
		
		    dbcb = new DBClassBuilder(company);
		    dbcb.setCompanyName(company);
		    dbcb.setDataBase(DB);
		    dbcb.setFileName(fileName);
		    dbcb.executeAllfunctions(true);
		    System.out.println("Program Completed. Class copy" + fileName + " created.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
