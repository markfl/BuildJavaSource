package controller;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.CheckTime;
import model.DBClassBuilder;
import model.MsSQL;

public class UpdateJSONWithDescription {

	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();
		
		String company = args[0];
		String DB = args[1];
		String flores_Company = "flores_" + company;
		
		String selectSql = "Select whftxt "
				         + "From qdbasedict "
				         + "Where whlib = ? And whfile = ? And whflde = ?";
		
		try {
			//MsSQL dbMSSQL = new MsSQL(company);
			//Connection connMSSQL = dbMSSQL.connect();
			MsSQL dbFlores = new MsSQL(flores_Company);
			Connection connFlores = dbFlores.connect();
			PreparedStatement checkStmt = connFlores.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			//Creating a File object for directory
		    File directoryPath = new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + company + "\\src\\com\\database\\" + DB + "\\");
		    //List of all files and directories
		    String contents[] = directoryPath.list();
		    for(int i=0; i<contents.length; i++) {
		    	int a = contents[i].indexOf(".");
				String currentFileName = contents[i].substring(0, a);
				DBClassBuilder dbcb = new DBClassBuilder(company);
				dbcb.setDataBase(DB);
		    	dbcb.setFileName(currentFileName);
		    	dbcb.setCompanyName(company);
		    	if (dbcb.readJSON()) {
		    		Collection<ArrayList<String>> allNewFields = new ArrayList<ArrayList<String>>();
		    		Boolean updateJSON = false;
		    		String fileName = new String();
	    			String fieldName = new String();
	    			String fieldType = new String();
	    			int fieldSize = 0;
	    			int fieldDec = 0;
	    			Boolean fieldIsKey = false;
	    			String getter = new String();
	    			String setter = new String();
	    			String description = new String();
	    			String longLibraryName = new String();
	    			String shortLibraryName = new String();
		    		for (ArrayList<String> elements : dbcb.getAllFields()) {
		    			int count = 0;
		    			for (String field : elements) {
		    				count++;
		    				switch (count) {
		    					case 1:
		    						fileName = field;
		    						break;
		    					case 2:
		    						fieldName = field;
		    						break;
		    					case 3:
		    						fieldType = field;
		    						break;
		    					case 4:
		    						fieldSize = Integer.parseInt(field);
		    						break;
		    					case 5:
		    						fieldDec = Integer.parseInt(field);
		    						break;
		    					case 6:
		    						fieldIsKey = Boolean.valueOf(field);
		    						break;
		    					case 7:
		    						setter = field;
		    						break;
		    					case 8:
		    						getter = field;
		    						break;
		    					case 9:
		    						description = field;
		    						break;
		    					case 10:
		    						longLibraryName = field;
		    						break;
		    					case 11:
		    						shortLibraryName = field;
		    						break;
		    						
		    				}
		    			}
		    			// get description from QDBASEDICT
				    	
					    checkStmt.setString(1, shortLibraryName);
					    checkStmt.setString(2, fileName);
					    checkStmt.setString(3, fieldName);
					    ResultSet resultsSelect = checkStmt.executeQuery();
					    if (resultsSelect.next()) {
					    	description = resultsSelect.getString(1).trim();
					    	updateJSON = true;
					    }
					    Collection<String> fieldList = new ArrayList<String>();
						fieldList.add(fileName);
						fieldList.add(fieldName);
						fieldList.add(fieldType);
						fieldList.add(Integer.toString(fieldSize));
						fieldList.add(Integer.toString(fieldDec));
						if (fieldIsKey) {
							fieldList.add("1");
						} else {
							fieldList.add("0");
						}
						fieldList.add(setter);
						fieldList.add(getter);
						fieldList.add(description);
						fieldList.add(longLibraryName);
						fieldList.add(shortLibraryName);
						allNewFields.add((ArrayList<String>) fieldList);
		    		}
		    		if (updateJSON) {
		    			dbcb.setLongLibraryName(longLibraryName);
						dbcb.setShortLibraryName(shortLibraryName);
		    			dbcb.setAllFields(allNewFields);
		    			dbcb.buildJSON();
		    			System.out.println("JSON updated for file " + fileName + " in library " + shortLibraryName);
		    		}
		    	}
	      	}
		    
			dbFlores.closeConnection(connFlores);
			connFlores.close();
			System.out.println("Update JSON program completed normally.");
			String returnString = ct.calculateElapse("Update JSON");
			System.out.println(returnString);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
