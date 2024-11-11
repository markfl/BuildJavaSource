package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.CheckTime;
import model.DBClassBuilder;
import model.MsSQL;

public class CreateJavaClass {

	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();

		DBClassBuilder dbcb;
		
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		String company = args[0];
		String DB = args[1];
		String includeFile = new String();
		if (args.length >= 3) {
			includeFile = args[2];
		}
		String fileName = new String();
		String fileNameSave = new String();
		
		boolean firstRecord = true;
		int classesCreated = 0;		
		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		String selectSql1 = "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";
		
		String selectSql2 
				= "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE INFORMATION_SCHEMA.COLUMNS.TABLE_NAME = '"
				+ includeFile + "' ";

		try {
			connMSSQL = dbMSSQL.connect();
			dbcb = new DBClassBuilder(company, connMSSQL, DB, company);
			PreparedStatement checkStmtSelect = null;
			if (args.length >= 3) {
				checkStmtSelect = connMSSQL.prepareStatement(selectSql2);
			} else {
				checkStmtSelect = connMSSQL.prepareStatement(selectSql1);
			}
			
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(1);
		    	if (!fileNameSave.equals("") && !fileNameSave.equals(fileName)) {
		    		dbcb.setFileName(fileNameSave);
		    		dbcb.setAllFields(fields);
		    		dbcb.executeAllfunctions(true);
		    		classesCreated++;
					System.out.println(fileName + " completed normally.");
					dbcb.setNumberOfFields(0);
					dbcb.setNumberOfKeyFields(0);
		    		fields.clear();
		    		dbcb.setAllFields(fields);
		    		firstRecord = true;
		    		//dbcb.closeConnection();
		    		dbcb = new DBClassBuilder(company, connMSSQL, DB, company);
		    	}
		    	dbcb.setFileName(fileName);
		    	fields = dbcb.getFileFields(firstRecord, resultsSelect, fields);
			    firstRecord = false;
				fileNameSave = fileName;
		    }
		    resultsSelect.close();
		    checkStmtSelect.close();
		    // Do for last file
		    dbcb.setCompanyName(company);
		    dbcb.setDataBase(DB);
		    dbcb.setFileName(fileName);
		    dbcb.setAllFields(fields);
		    dbcb.executeAllfunctions(true);
		    System.out.println(fileName + " completed normally.");
		    classesCreated++;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			dbMSSQL.closeConnection(connMSSQL);
			connMSSQL.close();
			System.out.println("Build Java program completed normally, " + classesCreated + " classes created.");
			String returnString = ct.calculateElapse("Build Java");
			System.out.println(returnString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}