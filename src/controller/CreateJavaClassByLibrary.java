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

public class CreateJavaClassByLibrary {

	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();

		DBClassBuilder dbcb;

		String company = args[0];
		String DB = args[1];
		String includeLibrary = new String();
		if (args.length >= 3) {
			includeLibrary = args[2];
		}
		Connection connLibrary = null;
		connLibrary = null;
		String libraryList = company + "liblistreseq";
		MsSQL dbLibList = new MsSQL("liblist");
		try {
			connLibrary = dbLibList.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String companySql = new String();
		String countSQL = new String();
		String fileName = new String();
		String longFileName = new String();
		String fileNameSave = new String();
		int libCount = 0;
		int currentCount = 0;
		ArrayList<String> allReturnStrings = new ArrayList<String>();
		if (includeLibrary.isEmpty()) {
			companySql = "Select * from " + libraryList
					   + " Where runoption = 'y'"
					   + " Order by sequence, library";
			countSQL = "Select count(*) as numberOfRecords from " + libraryList
					   + " Where runoption = 'y'";
		} else {
			companySql = "Select * from " + libraryList
					   + " Where library = '" + includeLibrary + "'";
		}

		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		int classesCreated = 0;
		int totalClassesCreated = 0;
		boolean firstRecord = true;
		
		PreparedStatement checkStmt1;
		try {
			String selectSql = 
					  "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
					+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
					+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
					+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
					+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
					+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
					+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
					+ "FROM INFORMATION_SCHEMA.COLUMNS "
					+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
					+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";
			PreparedStatement checkStmt3 = connLibrary.prepareStatement(countSQL);;
			ResultSet resultsSelect3 = checkStmt3.executeQuery();
			resultsSelect3.next();
			libCount = resultsSelect3.getInt(1);
			if (libCount > 1) {
				System.out.println(libCount + " libraries to build." );
			}
			checkStmt1 = connLibrary.prepareStatement(companySql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultsSelect1 = checkStmt1.executeQuery();
			while (resultsSelect1.next()) {
				classesCreated = 0;
				String libraryName = resultsSelect1.getString(3).trim().toLowerCase();
				includeLibrary = company + "_" + libraryName;
				try {
					MsSQL dbMSSQL = new MsSQL(includeLibrary);
					Connection connMSSQL = dbMSSQL.connect();
					fileNameSave = new String();
					System.out.println("Build starting for library " + includeLibrary);
					dbcb = new DBClassBuilder(company, connMSSQL, DB, includeLibrary);
					PreparedStatement checkStmt2 = connMSSQL.prepareStatement(selectSql);
				    ResultSet resultsSelect2 = checkStmt2.executeQuery();
				    Boolean createAClass = false;
				    while (resultsSelect2.next()) {
				    	fileName = resultsSelect2.getString(1);
				    	longFileName = libraryName + "_" + fileName.trim();
				    	if (!fileNameSave.equals("") && !fileNameSave.equals(fileName)) {
				    		dbcb.setFileName(fileNameSave);
				    		dbcb.setAllFields(fields);
				    		dbcb.executeAllfunctions(true);
				    		classesCreated++;
				    		totalClassesCreated++;
							System.out.println(libraryName + " " + fileName + " completed normally.");
							createAClass = false;
				    		firstRecord = true;
				    		fields = new ArrayList<ArrayList<String>>();
				    		dbcb = new DBClassBuilder(company, connMSSQL, DB, includeLibrary);
				    	}
				    	dbcb.setFileName(fileName);
				    	dbcb.setLongFileName(longFileName);
				    	fields = dbcb.getFileFields(firstRecord, resultsSelect2, fields);
					    firstRecord = false;
						fileNameSave = fileName;
						createAClass = true;
				    } // end while
				    resultsSelect2.close();
				    checkStmt2.close();
				    // Check for any classes created
				    if (createAClass) {
					    // Do for last file
					    dbcb.setDataBase(DB);
					    dbcb.setFileName(fileName);
					    dbcb.setLongFileName(longFileName);
					    dbcb.setAllFields(fields);
					    dbcb.executeAllfunctions(true);
					    System.out.println(fileName + " completed normally.");
					    classesCreated++;
					    fields = new ArrayList<ArrayList<String>>();
			    		dbcb = new DBClassBuilder(company, connMSSQL, DB, includeLibrary);
			    	} else {
			    		System.out.println("No classes created for library " + libraryName);
			    	}
				    dbMSSQL.closeConnection(connMSSQL);
				    connMSSQL.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				String returnString = "Build Java program completed normally for library " + includeLibrary + ", " + classesCreated + " classes created.";
				currentCount += 1;
				System.out.println(currentCount + " libraries created. " + (libCount - currentCount) + " to go." );
				allReturnStrings.add(returnString);
				System.out.println(returnString);
			}
			resultsSelect1.close();
			checkStmt1.close();
			dbLibList.closeConnection(connLibrary);
			connLibrary.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(String returnAllString : allReturnStrings) {
			System.out.println(returnAllString);
		}
		String returnString = ct.calculateElapse("Build Java") + " " + totalClassesCreated + " classes created.";
		System.out.println(returnString);
		
	}
}