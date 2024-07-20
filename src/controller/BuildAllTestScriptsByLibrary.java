package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.CheckTime;
import model.DBTestBuilder;
import model.MsSQL;

public class BuildAllTestScriptsByLibrary {

	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();

		String company = args[0];
		String dataBase = args[1];
		String includeLibrary = new String();
		if (args.length >= 3) {
			includeLibrary = args[2];
		}
		Connection connLibrary = null;
		Connection connFloresFileLibrary = null;
		String libraryList = company + "liblistreseq";
		MsSQL dbLibList = new MsSQL("liblist");
		MsSQL dbFloresFileList = new MsSQL("flores_" + company);
		try {
			connLibrary = dbLibList.connect();
			connFloresFileLibrary = dbFloresFileList.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String companySql = new String();
		if (includeLibrary.isEmpty())
			companySql = "Select * from " + libraryList
					   + " Where runoption = 'y'"
					   + " Order by sequence, library";
		else
			companySql = "Select * from " + libraryList
					   + " Where library = '" + includeLibrary + "'";
		
		int classesCreated = 0;
		int totalClassesCreated = 0;
		
		PreparedStatement checkStmt1;
		PreparedStatement checkStmt2;
		try {
			checkStmt1 = connLibrary.prepareStatement(companySql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultsSelect1 = checkStmt1.executeQuery();
			while (resultsSelect1.next()) {
				String library = resultsSelect1.getString(3).trim();
				Connection connMSSQL = null;
				MsSQL dbMSSQL = new MsSQL(company + "_" + library);
				connMSSQL = dbMSSQL.connect();
				String dbFileSql = "Select atfile from qdspfdbas"
				                 + " Where atlib = '" + library + "'"
				                 + " And atftyp = 'P' And atdtat = 'D'";
				checkStmt2 = connFloresFileLibrary.prepareStatement(dbFileSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet resultsSelect2 = checkStmt2.executeQuery();
				while (resultsSelect2.next()) {
					String fileName = resultsSelect2.getString(1).toLowerCase().trim();
					String longFileName = library + "_" + fileName;
					DBTestBuilder bts = new DBTestBuilder(company, dataBase, longFileName, library, fileName, dbMSSQL, connMSSQL);
					bts.testScript();
					System.out.println("Class test_" + longFileName + " created");
					classesCreated++;
					totalClassesCreated++;
				}
				System.out.println(classesCreated + " classes created for library " + library + ".");
				classesCreated = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String returnString = ct.calculateElapse("Build Test");
		returnString = returnString + " " + totalClassesCreated + " total classes created.";
		System.out.println(returnString);
	}
}