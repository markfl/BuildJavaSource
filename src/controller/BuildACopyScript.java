package controller;

import java.util.ArrayList;

import model.BuildCopyScripts;
import model.DBCopyBuilder;

public class BuildACopyScript {
	
	public static void main(String[] args) {
		
		String company = args[0];
		String database = args[1];
		String library = args[2];
		String filename = args[3];
		BuildCopyScripts bcs = new BuildCopyScripts();
		ArrayList<String> allCopyFile = new ArrayList<String>();
		
		DBCopyBuilder dbCB = new DBCopyBuilder(company, database, library, filename);
		dbCB.BuildCopyClass();
		//dbCB.WriteClass();
		bcs.setCompany(company);
		bcs.setLibrary(library);
		bcs.setDatabase(database);
		bcs.setFileName(filename);
		bcs.createSQLCount(company);
		allCopyFile.add(filename);
		bcs.setAllCopyFile(allCopyFile);
		bcs.BuildRunAll(allCopyFile);
		System.out.println("Program Completed. Class copy" + filename + " created.");
	}
}