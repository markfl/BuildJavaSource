package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.BuildCopyScripts;
import model.CheckTime;
import model.DBCopyBuilder;
import model.MsSQL;

public class BuildAllCopyScriptsByLibrary {

	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();
		
		String company = args[0];
		String database = args[1];
		String includeLibrary = new String();
		if (args.length >= 3) {
			includeLibrary = args[2];
		}
		
		Collection<ArrayList<String>> allCopyFiles = new ArrayList<ArrayList<String>>();
		ArrayList<String> allCopyFile = new ArrayList<String>();
		BuildCopyScripts bcs = new BuildCopyScripts(true);
		bcs.setCompany(company);
		bcs.setDatabase(database);
		int countTotal = 0;
		bcs.setCompany(company);
		bcs.setDatabase(database);

		String companySql = new String();
		if (includeLibrary.isEmpty())
			companySql = "Select * from rawdatafiles"
					   + " Where company = '" + company + "'"
					   + " Order by library, filename";
		else
			companySql = "Select * from rawdatafiles"
					   + " Where company = '" + company + "'"
					   + " And library = '" + includeLibrary + "'"
					   + " Order by library, filename";
		
		String library = new String();
		String longLibraryName = new String();
		String dataSource = new String();
		String fileName = new String();
		String longFileName = new String();
		String saveDataSource = new String();
		try {
			Connection connMSSQLLibList = null;
			MsSQL dbMSSQLLibList = new MsSQL("liblist");
			connMSSQLLibList = dbMSSQLLibList.connect();
			PreparedStatement checkStmt = connMSSQLLibList.prepareStatement(companySql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultsSelect = checkStmt.executeQuery();
			while (resultsSelect.next()) {
				
				library = resultsSelect.getString(2);
				longLibraryName = resultsSelect.getString(3);
				dataSource = resultsSelect.getString(4);
				fileName = resultsSelect.getString(5);
				longFileName = resultsSelect.getString(6);
				System.out.println(library + " " + longLibraryName + " " + dataSource  + " " + fileName  + " " + longFileName);
				if (!saveDataSource.isEmpty() && !saveDataSource.equals(dataSource)) {
					if (!allCopyFiles.isEmpty()) {
						bcs.setAllCopyFiles(allCopyFiles);
						bcs.createSQLCount(company);
						bcs.BuildRunAll(allCopyFiles, saveDataSource);
					}
				}
				bcs.setLibrary(longLibraryName);
				bcs.setFileName(fileName);
				DBCopyBuilder dbcb = new DBCopyBuilder(company, database, library, fileName, longLibraryName, longFileName, dataSource);
				dbcb.BuildCopyClass();
				if (dbcb.getClassBuilt()) {
					countTotal++;
					String className = "copy_" + longFileName.trim(); 
					System.out.println("Class " + className.trim() + " created.");
					allCopyFile.add(library);
					allCopyFile.add(fileName);
					allCopyFiles.add(allCopyFile);
					allCopyFile = new ArrayList<String>();
					bcs.BuildRun();
				}
				saveDataSource = dataSource;
			} // end while
			bcs.setAllCopyFiles(allCopyFiles);
			bcs.createSQLCount(company);
			bcs.BuildRunAll(allCopyFiles, dataSource);
			resultsSelect.close();
			checkStmt.close();
			dbMSSQLLibList.closeConnection(connMSSQLLibList);
			connMSSQLLibList.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String returnString = ct.calculateElapse("Build Copy");
		returnString = returnString + " " + countTotal + " classes created.";
		System.out.println(returnString);
	}
}