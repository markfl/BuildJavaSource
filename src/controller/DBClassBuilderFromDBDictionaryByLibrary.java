package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.CheckTime;
import model.DBClassBuilder;
import model.GetFileFieldData;
import model.MsSQL;

public class DBClassBuilderFromDBDictionaryByLibrary {
	
	private static DBClassBuilder dbcb;
	private static GetFileFieldData gffd;
	private static CheckName cn;
	
	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();

		String company = args[0];
		String DB = args[1];
		String includeLibrary = new String();
		if (args.length >= 3) {
			includeLibrary = args[2];
		}

		cn = new CheckName();
		Connection connLibrary = null;
		Connection connFloresLibrary = null;
		String libraryList = company + "liblistreseq";
		MsSQL dbLibList = new MsSQL("liblist");
		MsSQL dbFlores = new MsSQL("flores_" + company);
		try {
			connLibrary = dbLibList.connect();
			connFloresLibrary = dbFlores.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String companySql = new String();
		String countSQL = new String();
		int libCount = 0;
		int totalClassesCreated = 0;
		
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
		String selectSql1 = "Select atlib, atfile from qdspfdbas "
				  	      + "Where atfila = '*PHY' And atdtat = 'D' "
				  	      + "And atlib = ? "
				  	      + "Order by atlib, atfile";
		try {
			PreparedStatement checkStmt;
			if (includeLibrary.isEmpty()) {
				checkStmt = connLibrary.prepareStatement(countSQL);
				ResultSet resultsSelect = checkStmt.executeQuery();
				resultsSelect.next();
				libCount = resultsSelect.getInt(1);
			} else libCount = 1;
			if (libCount > 1) {
				System.out.println(libCount + " libraries to build." );
			}
			checkStmt = connLibrary.prepareStatement(companySql);
			ResultSet resultsSelect = checkStmt.executeQuery();
			while (resultsSelect.next()) {
				includeLibrary = resultsSelect.getString(4).trim().toLowerCase();
				PreparedStatement checkStmt1 = connFloresLibrary.prepareStatement(selectSql1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				checkStmt1.setString(1, includeLibrary.trim());
				ResultSet resultsSelect1 = checkStmt1.executeQuery();
				while (resultsSelect1.next()) {
					String shortLibraryName = resultsSelect1.getString(1).trim().toLowerCase();
					String shortFileName = resultsSelect1.getString(2).trim().toLowerCase();
					String longLibraryName = company + "_" + shortLibraryName;
					MsSQL dbMSSQL = new MsSQL(longLibraryName);
					Connection connMSSQL = dbMSSQL.connect();
					dbcb = new DBClassBuilder(company, connMSSQL, DB, longLibraryName, shortLibraryName);
					dbcb.setFileName(shortFileName);
					gffd = new GetFileFieldData(company.trim(), shortLibraryName, shortFileName, DB, cn, connFloresLibrary);
				    fields = gffd.getFieldData(fields);
				    dbcb.setAllFields(fields);
				    dbcb.setLongFileName(shortLibraryName.trim() + "_" + shortFileName.trim());
				    dbcb.getFileIndexString();
				    dbcb.executeAllfunctions(true);
				    System.out.println(shortFileName + " completed normally.");
				    fields = new ArrayList<ArrayList<String>>();
					totalClassesCreated++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String returnString = ct.calculateElapse("Build Java") + " " + totalClassesCreated + " classes created.";
		System.out.println(returnString);
	}
}
