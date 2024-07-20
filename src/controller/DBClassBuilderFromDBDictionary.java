package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.FileFieldCheck;
import model.MsSQL;

public class DBClassBuilderFromDBDictionary {

	private static FileFieldCheck ffc;
	private static Connection connMSSQL;
	private static String library;
	private static String company;
	private static String db;
	private static String includeLibrary;
	private static ArrayList<String> allPhysicalKeyFieldNames = new ArrayList<String>();
	private static ArrayList<String> allLogicalKeyFieldNames = new ArrayList<String>();
	private static Collection<ArrayList<String>> allLogicalKeyFieldList = new ArrayList<ArrayList<String>>();
	private static Collection<ArrayList<String>> logicalFileKeyField = new ArrayList<ArrayList<String>>();
	private static Map<String, Map<String, String>> keyFieldListMap = new HashMap<>();
	private static Map<String, String> keyFldLstMap = new HashMap<>();
	private static ArrayList<String> logicalFiles = new ArrayList<String>();
	
	public static void main(String[] args) {
		
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		
		library = args[0];
		company = args[1];
		db = args[2];
		includeLibrary = new String();
		if (args.length >= 4) {
			includeLibrary = args[3];
		}
		
		connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(library);
		try {
			connMSSQL = dbMSSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ffc = new FileFieldCheck(company);
		ffc.setConn(library);
		ffc.setDataBase(db);
		String selectSql = new String();
		if (includeLibrary.isEmpty())
			selectSql = "Select atfile, atlib from qdspfdbas "
					  + "Where atfila = '*PHY' And atdtat = 'D' "
					  + "Order by atlib, atfile";
		else
			selectSql = "Select atfile, atlib from qdspfdbas "
					  + "Where atfila = '*PHY' And atdtat = 'D' "
					  + "And atlib = '" + includeLibrary + "' "
					  + "Order by atlib, atfile";
		
		String physicalFileName = new String();
    	String libraryName = new String();
    	String fileName = new String();
    	int classesCreated = 0;
		try {	
			PreparedStatement checkStmt = connMSSQL.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    ResultSet resultsSelect = checkStmt.executeQuery();
		    if (resultsSelect.first()) {
		    	physicalFileName = resultsSelect.getString(1).trim().toLowerCase();
		    	fileName = ffc.checkFieldName(physicalFileName);
		    	libraryName = resultsSelect.getString(2).trim().toLowerCase();		    	fields = getFieldData(physicalFileName, libraryName, fields);
		    	buildJavaClass(fileName, libraryName, fields);
		    	fields = new ArrayList<ArrayList<String>>();
		    	classesCreated++;
		    	System.out.println("Class " + fileName.trim() + " from library " + libraryName.trim() + " created.");
			    while (resultsSelect.next()) {
			    	physicalFileName = resultsSelect.getString(1).toLowerCase();
			    	fileName = ffc.checkFieldName(physicalFileName);
			    	libraryName = resultsSelect.getString(2).toLowerCase();
			    	fields = getFieldData(physicalFileName, libraryName, fields);
			    	buildJavaClass(fileName, libraryName, fields);
			    	fields = new ArrayList<ArrayList<String>>();
			    	classesCreated++;
			    	System.out.println("Class " + fileName.trim() + " from library " + libraryName.trim() + " created.");
			    }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Program completed normally, " + classesCreated + " classes created.");
	}
	
	static public void buildJavaClass(String physicalFileName, String physicalLibraryName, Collection<ArrayList<String>> fields) {
		
		String selectSql = "Select whrefi, whreli from qadspdbr " +
				           "Where whrli = ? And whrfi = ? " +
				           "Order by whrli, whrfi";
		PreparedStatement checkStmt;
		try {
			checkStmt = connMSSQL.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			checkStmt.setString(1, physicalLibraryName.trim());
		    checkStmt.setString(2, physicalFileName.trim());
		    getPhysicalFiles(physicalFileName, physicalLibraryName);
		    ResultSet resultsSelect = checkStmt.executeQuery();
		    if (resultsSelect.first()) {
		    	String fileName = resultsSelect.getString(1).trim().toLowerCase();
		    	if (!fileName.isEmpty()) {
					String libraryName = resultsSelect.getString(2).trim().toLowerCase();
					getLogicalFiles(fileName, libraryName);
			    	while (resultsSelect.next()) {
			    		fileName = resultsSelect.getString(1).trim().toLowerCase();
						libraryName = resultsSelect.getString(2).trim().toLowerCase();
			    		getLogicalFiles(fileName, libraryName);
			    	}
		    	}
		    }
		    ffc.setConn(library);
			ffc.setCompanyName(company);
			ffc.setDataBase(db);
			ffc.setFileName(physicalFileName.toLowerCase().trim());
			ffc.setAllFields(fields);
			ffc.setNumberOfFields(fields.size());
			ffc.getFileIndexString();
			ffc.BuildAllKeyFields();
			ffc.setAllLogicalKeyFieldNames(ffc.getAllLogicalKeyFieldNames());
			ffc.setAllLogicalKeyFieldList(ffc.getAllLogicalKeyFieldList());
			//ffc.setAllLogicalKeyFields(ffc.getLogicalFileKeyField());
			ffc.setKeyFieldListMap(ffc.getKeyFieldListMap());;
			ffc.setLogicalFiles(logicalFiles);
			ffc.executeAllfunctions(false);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public Collection<ArrayList<String>> getFieldData(String fileName, String libraryName, Collection<ArrayList<String>> fields) {
		
		Collection<String> currentFields = new ArrayList<String>();
		String selectSql 
			= "Select whfile, whlib, whflde, whfldb, whfldd, whfldt, whfldp, whftxt "
			+ "from qdbasedict Where whfile = ? And whlib = ? "
			+ "Order by whfobo";
		String charType = "A";
		String varCharType = "varchar";
		
		try {
			PreparedStatement checkStmt = connMSSQL.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    checkStmt.setString(1, fileName);
		    checkStmt.setString(2, libraryName);
		    ResultSet resultsSelect = checkStmt.executeQuery();
		    if (resultsSelect.first()) {
		    	fileName = resultsSelect.getString(1).trim().toLowerCase();
		    	fileName = ffc.checkFieldName(fileName);
		    	libraryName = resultsSelect.getString(2).trim().toLowerCase();
		    	String fieldName = resultsSelect.getString(3).trim().toLowerCase();
		    	fieldName = ffc.checkFieldName(fieldName);
		    	String fieldType = new String();
		    	int fieldSize = 0;
		    	int decimal = 0;
		    	String fieldTypeTest = resultsSelect.getString(6).trim();
		    	if (fieldTypeTest.equals(charType) || fieldTypeTest.equals(varCharType)) {
		    		fieldType = "String";
		    		fieldSize = resultsSelect.getInt(4);
		    	} 
		    	fieldSize = resultsSelect.getInt(5);
				decimal = resultsSelect.getInt(7);
	    		if (decimal == 0) {
	    			if (fieldSize < 10)
	    				fieldType = "int";
	    			else
	    				fieldType = "long";
	    		} else {
	    			fieldType = "double";
	    		}
				String fieldText = resultsSelect.getString(8);
				currentFields.add(fileName);
				currentFields.add(libraryName);
				currentFields.add(fieldName);
				currentFields.add(Integer.toString(fieldSize));
				currentFields.add(fieldType);
				currentFields.add(Integer.toString(decimal));
				currentFields.add(fieldText);
				fields = getFileFields(true, currentFields, fields);
				currentFields = new ArrayList<String>();
				while (resultsSelect.next()) {
					fields = getFileData(false, resultsSelect, fields);
				}
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fields;
	}
	
	static private Collection<ArrayList<String>> getFileData(Boolean firstRecord, ResultSet resultsSelect, Collection<ArrayList<String>> fields) {
		
		Collection<String> currentFields = new ArrayList<String>();
		
		String charType = "A";
		String varCharType = "varchar";
		
		String fileName;
		try {
			fileName = resultsSelect.getString(1).trim().toLowerCase();
	    	fileName = ffc.checkFieldName(fileName);
	    	String libraryName = resultsSelect.getString(2).trim().toLowerCase();
	    	String fieldName = resultsSelect.getString(3).trim().toLowerCase();
	    	fieldName = ffc.checkFieldName(fieldName);
	    	String fieldType = new String();
	    	int fieldSize = 0;
	    	int decimal = 0;
	    	String fieldTypeTest = resultsSelect.getString(6).trim();
	    	if (fieldTypeTest.equals(charType) || fieldTypeTest.equals(varCharType)) {
	    		fieldType = "String";
	    		fieldSize = resultsSelect.getInt(4);
	    	} else {
		    	fieldSize = resultsSelect.getInt(5);
				decimal = resultsSelect.getInt(7);
	    		if (decimal == 0) {
	    			if (fieldSize < 10)
	    				fieldType = "int";
	    			else
	    				fieldType = "long";
	    		} else {
	    			fieldType = "double";
	    		}
	    	}
			String fieldText = resultsSelect.getString(8);
			currentFields.add(fileName);
			currentFields.add(libraryName);
			currentFields.add(fieldName);
			currentFields.add(Integer.toString(fieldSize));
			currentFields.add(fieldType);
			currentFields.add(Integer.toString(decimal));
			currentFields.add(fieldText.trim());
			fields = getFileFields(true, currentFields, fields);
			currentFields = new ArrayList<String>();
			while (resultsSelect.next()) {
				fields = getFileData(false, resultsSelect, fields);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
    static public Collection<ArrayList<String>> getFileFields(boolean firstRecord, Collection<String> results, Collection<ArrayList<String>> fields) {
    	
    	Collection<String> fieldList = new ArrayList<String>();
    	ResultSet getResults;
    	
		String fileName = new String();
		String fileLibrary = new String();
		String fieldName = new String();
		String fieldType = new String();
		int fieldSize = 0;
		int decimal = 0;
		int count = 0;
		for (String field : results) {
			count++;
			switch (count) {
				case 1:
					fileName = field.trim();
					break;
				case 2:
					fileLibrary = field.trim();
					break;
				case 3:
					fieldName = field.trim();
					break;
				case 4:
					fieldSize = Integer.parseInt(field);
					break;
				case 5:
					fieldType = field.trim();
					break;
				case 6:
					decimal = Integer.parseInt(field);
					break;
			}
		}
		
		fileName = fileName.toLowerCase();
		fieldName = fieldName.toLowerCase();
		fieldList.add(fileName);
		fieldList.add(fieldName);
		if (db.equals("oracle")) {
			if (fieldSize > 4000) {
				fieldList.add("long varchar");
			} else {
				fieldList.add("varchar2");
			}
		} else if (db.equals("mysql")) {
			if (fieldSize > 255) {
				fieldList.add("text");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("B")) {
			if (db.equals("oracle")) {
				fieldList.add("number");
			} else {
				if (db.equals("mssql")) {
					if (decimal == 0) fieldList.add("long");
					else fieldList.add("double");
				} else fieldList.add("numeric");
			}
		} else if (fieldType.equals("P")) {
			if (decimal == 0) fieldList.add("int");
			else fieldList.add("double");
		}
		if (fieldType.equals("S")) {
			if (db.equals("mssql")) {
				if (decimal == 0) fieldList.add("int");
				else fieldList.add("double");
			} else {
				fieldList.add("numeric");
			}
		} else if (fieldType.equals("A")) {
			fieldList.add("String");
		} else if (fieldType.equals("Z")) {
			fieldList.add("String");
		} else if (fieldType.equals("L")) {
			fieldList.add("String");
		} else if (fieldType.equals("O")) {
			fieldList.add("String");
		} else if (fieldType.equals("T")) {
			fieldList.add("String");
		} else if (fieldType.equals("F")) {
			fieldList.add("numeric");
		} else if (fieldType.equals("H")) {
			fieldList.add("String");
		} else if (fieldType.equals("int")) {
			fieldList.add("int");
		} else if (fieldType.equals("String")) {
			fieldList.add("String");
		} else if (fieldType.equals("double")) {
			fieldList.add("double");
		}
		fieldList.add(Integer.toString(fieldSize));
		fieldList.add(Integer.toString(decimal));
		try {
			int numberOfRecords = 0;
			String checkSql = "select count(*) as numberOfRecords from qdspfdacc "
					   		+ "Where aplib = ? And apfile = ? And apkeyf = ?";
			PreparedStatement checkStmt = connMSSQL.prepareStatement(checkSql);
			checkStmt.setString(1, fileLibrary);
			checkStmt.setString(2, fileName);
			checkStmt.setString(3, fieldName);
			getResults = checkStmt.executeQuery();
			if (getResults.next()) {
				numberOfRecords = getResults.getInt(1);
				if (numberOfRecords > 0) {
					fieldList.add("1");
					int numberOfKeyFields = ffc.getNumberOfKeyFields();
					ffc.setNumberOfKeyFields(numberOfKeyFields + 1);
				}
				else fieldList.add("0");
			}
			else fieldList.add("0");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		fieldList.add("set" + fieldName);
		fieldList.add("get" + fieldName);
		fields.add((ArrayList<String>) fieldList);
    	return fields;
    }
    
    static public void getPhysicalFiles(String fileName, String libraryName) {
    	
    	try {
			String selectSql = "Select apfile, aplib, apkeyf, apkseq from qdspfdacc " +
					   "Where aplib = ? And apfile = ? " +
					   "Order by aplib, apfile, apkeyn";
			PreparedStatement checkStmt = connMSSQL.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    checkStmt.setString(1, libraryName.trim());
		    checkStmt.setString(2, fileName.trim());	
		    ResultSet results = checkStmt.executeQuery();
		    if (results.first()) {
		    	String apfile = results.getString(1).toLowerCase();
		    	String aplib = results.getString(2).toLowerCase();
		    	String apkeyf = results.getString(3).toLowerCase();
		    	String apkseq = results.getString(4);
		    	if (apkeyf.isEmpty()) {
			    	allPhysicalKeyFieldNames.add(apkeyf.trim().toLowerCase());
			    	buildKeyFieldListMap(apfile, aplib, apkeyf, apkseq);
			    	while (results.next()) {
			    		apfile = results.getString(1).toLowerCase();
				    	aplib = results.getString(2).toLowerCase();
				    	apkeyf = results.getString(3).toLowerCase();
				    	apkseq = results.getString(4);
				    	allPhysicalKeyFieldNames.add(apkeyf.trim());
				    	buildKeyFieldListMap(apfile, aplib, apkeyf, apkseq);
			    	}
			    	keyFieldListMap.put(fileName, keyFldLstMap);
		    	}
		    	keyFldLstMap = new HashMap<>();
		    }

		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    static public void getLogicalFiles(String fileName, String libraryName) {
    	
    	Collection<String> keyFieldList = new ArrayList<String>();
    	Collection<String> logicalFieldList = new ArrayList<String>();  	
    	
    	try {
			String selectSql = "Select apfile, aplib, apkeyf, apkseq from qdspfdacc " +
					   "Where aplib = ? And apfile = ? " +
					   "Order by aplib, apfile";
			PreparedStatement checkStmt = connMSSQL.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    checkStmt.setString(1, libraryName.trim());
		    checkStmt.setString(2, fileName.trim());		    
		    ResultSet results = checkStmt.executeQuery();
		    if (results.first()) {
		    	String apfile = results.getString(1).toLowerCase();
		    	String aplib = results.getString(2).toLowerCase();
		    	String apkeyf = results.getString(3).toLowerCase();
		    	String apkseq = results.getString(4);
		    	keyFieldList.add(apkeyf.trim());
		    	allLogicalKeyFieldNames.add(apkeyf.trim());
		    	logicalFieldList.add(apfile);
		    	buildKeyFieldListMap(apfile, aplib, apkeyf, apkseq);
		    	while (results.next()) {
		    		apfile = results.getString(1).toLowerCase();
			    	aplib = results.getString(2).toLowerCase();
			    	apkeyf = results.getString(3).toLowerCase();
			    	apkseq = results.getString(4);
			    	keyFieldList.add(apkeyf.trim());
			    	allLogicalKeyFieldNames.add(apkeyf.trim());
			    	logicalFieldList.add(apkeyf.toLowerCase());
			    	buildKeyFieldListMap(apfile, aplib, apkeyf, apkseq);
		    	}
		    	logicalFiles.add(apfile);
		    	keyFieldListMap.put(apfile, keyFldLstMap);
		    	keyFldLstMap = new HashMap<>();
		    	allLogicalKeyFieldList.add((ArrayList<String>) logicalFieldList);
		    	logicalFileKeyField.add((ArrayList<String>) keyFieldList);
		    } else {
		    	
		    }

		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    static public void buildKeyFieldListMap(String fileName, String libraryName, String keyName, String direction) {

    	if (direction.equals("A")) {
    		keyFldLstMap.put(keyName.trim(), "asc");
    	} else {
    		keyFldLstMap.put(keyName, "desc");
    	}
    }
}