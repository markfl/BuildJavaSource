package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.MsSQL;

public class CreateJavaClass {

	public static void main(String[] args) {

		DBClassBuilder dbcb;
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		String company = args[0];
		String DB = args[1];
		String fileName = new String();
		String fileNameSave = new String();
		
		boolean firstRecord = true;
		int classesCreated = 0;		
		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		String selectSql = "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";

		try {
			connMSSQL = dbMSSQL.connect();
			dbcb = new DBClassBuilder();
			dbcb.setConn(connMSSQL);
			dbcb.setCompanyName(company);
			dbcb.setDataBase(DB);
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(1);
		    	if (!fileNameSave.equals("") && !fileNameSave.equals(fileName)) {
		    		dbcb.setCompanyName(company);
		    		dbcb.setDataBase(DB);
		    		dbcb.setFileName(fileNameSave);
		    		dbcb.setAllFields(fields);
		    		dbcb.executeAllfunctions();
		    		classesCreated++;
					System.out.println(fileName + " completed normally.");
					dbcb.setNumberOfFields(0);
					dbcb.setNumberOfKeyFields(0);
		    		fields.clear();
		    		dbcb.setAllFields(fields);
		    		firstRecord = true;
		    		dbcb = new DBClassBuilder();
					dbcb.setConn(connMSSQL);
					dbcb.setCompanyName(company);
					dbcb.setDataBase(DB);
		    	}
		    	dbcb.setFileName(fileName);
		    	fields = dbcb.getFileFields(firstRecord, resultsSelect, fields);
			    firstRecord = false;
				fileNameSave = fileName;
		    }
		    // Do for last file
		    dbcb.setCompanyName(company);
		    dbcb.setDataBase(DB);
		    dbcb.setFileName(fileName);
		    dbcb.setAllFields(fields);
		    dbcb.executeAllfunctions();
		    classesCreated++;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			dbMSSQL.closeConnection(connMSSQL);
			System.out.println("Program completed normally, " + classesCreated + " classes created.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}