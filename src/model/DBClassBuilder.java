package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DBClassBuilder {
	
	static StringBuilder text = new StringBuilder();

	private static String message2 = "Some fields were not updated properly.";
	private static String message3 = "Record not retrieved.";
	private static String message4 = " to long for field size";
	private static String message5 = "Key fields must be set.";
	private static String stringString = "String";
	private static String doubleString = "double";
	private static String intString = "int";
	private static String longString = "long";
	private static String bigintString = "BigInteger";
	private static String tableString = "BASE TABLE";
	
	private Collection<ArrayList<String>> allFields = new ArrayList<ArrayList<String>>();
	private ArrayList<String> allKeyFiles = new ArrayList<String>();
	
	private ArrayList<String> allPhysicalKeyFieldNames = new ArrayList<String>();
	private Collection<ArrayList<String>> allLogicalKeyFields = new ArrayList<ArrayList<String>>();
	private ArrayList<String> allLogicalKeyFieldNames = new ArrayList<String>();
	private Collection<ArrayList<String>> allLogicalKeyFieldList = new ArrayList<ArrayList<String>>();
	private ArrayList<String> allLogicalFieldList = new ArrayList<String>();
	private Collection<ArrayList<String>> allKeyFields = new ArrayList<ArrayList<String>>();
	
	private Map<String, Map<String, Object>> newFields = new HashMap<>();
	private Map<String, Map<String, String>> keyFieldListMap = new HashMap<>();
	
	private ArrayList<String> logicalFiles = new ArrayList<String>();
	protected Connection connMSSQL;
	private int numberOfFields;
	private int numberOfKeyFields;
	private String companyName;
	private String shortLibraryName;
	private String longLibraryName;
	private String dataBase;
	private String FileName;
	private String longFileName;
	private String tableType;
	private boolean hasKeysInd;
	private boolean hasMultipleKeysInd;
	private static FileFieldCheck ffc;
	
	public DBClassBuilder() {
		
		super();
		
		String company = getCompanyName();
		if (company == null) {
		} else {
			ffc.closeConnection();
			ffc = new FileFieldCheck(company.trim());
		}
		
	}

	public DBClassBuilder(String company) {
		
		super();
		
		setCompanyName(company);
		if (ffc != null) ffc.closeConnection();
		ffc = new FileFieldCheck(company);
		
	}
	
	public DBClassBuilder(String company, Connection connMSSQL) {
		
		super();
		
		setCompanyName(company);
		setConnMSSQL(connMSSQL);
		if (ffc != null) ffc.closeConnection();
		ffc = new FileFieldCheck(company);
		
	}
	
	public DBClassBuilder(String company, Connection connMSSQL, String DB) {
		
		super();
		
		setCompanyName(company);
		setConnMSSQL(connMSSQL);
		setDataBase(DB);
		if (ffc != null) ffc.closeConnection();
		ffc = new FileFieldCheck(company);
		
	}
	
	public DBClassBuilder(String company, Connection connMSSQL, String DB, String library) {
		
		super();
		
		setCompanyName(company);
		setConnMSSQL(connMSSQL);
		setDataBase(DB);
		setShortLibraryName(library);
		setLongLibraryName(library);
		if (ffc != null) ffc.closeConnection();
		ffc = new FileFieldCheck(getLongLibraryName());
		
	}
	
	public DBClassBuilder(String company, Connection connMSSQL, String DB, String longLibrary, String shortLibrary) {
		
		super();
		
		setCompanyName(company);
		setConnMSSQL(connMSSQL);
		setDataBase(DB);
		setShortLibraryName(shortLibrary);
		setLongLibraryName(longLibrary);
		if (ffc != null) ffc.closeConnection();
		ffc = new FileFieldCheck(getLongLibraryName());
		
	}
	
	public void closeConnection() {
		if (connMSSQL != null) {
			try {
				connMSSQL.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void setText(StringBuilder text) {
		DBClassBuilder.text = text;
	}

	public Collection<ArrayList<String>> getAllFields() {
		return allFields;
	}

	public void setAllKeyFiles(ArrayList<String> allKeyFiles) {
		this.allKeyFiles = allKeyFiles;
	}

	public ArrayList<String> getAllKeyFiles() {
		return allKeyFiles;
	}

	public void setAllFields(Collection<ArrayList<String>> allFields) {
		this.allFields = allFields;
	}

	public Map<String, Map<String, Object>> getNewFields() {
		return newFields;
	}

	public void setNewFields(Map<String, Map<String, Object>> newFields) {
		this.newFields = newFields;
	}

	public ArrayList<String> getAllPhysicalKeyFieldNames() {
		return allPhysicalKeyFieldNames;
	}
	
	public void setAllPhysicalKeyFieldNames(ArrayList<String> allPhysicalKeyFieldNames) {
		this.allPhysicalKeyFieldNames = allPhysicalKeyFieldNames;
	}

	public Collection<ArrayList<String>> getAllLogicalKeyFields() {
		return allLogicalKeyFields;
	}

	public void setAllLogicalKeyFields(Collection<ArrayList<String>> allLogicalKeyFields) {
		this.allLogicalKeyFields = allLogicalKeyFields;
	}

	public ArrayList<String> getAllLogicalKeyFieldNames() {
		return allLogicalKeyFieldNames;
	}

	public void setAllLogicalKeyFieldNames(ArrayList<String> allLogicalKeyFieldNames) {
		this.allLogicalKeyFieldNames = allLogicalKeyFieldNames;
	}

	public Collection<ArrayList<String>> getAllLogicalKeyFieldList() {
		return allLogicalKeyFieldList;
	}

	public void setAllLogicalKeyFieldList(Collection<ArrayList<String>> allLogicalKeyFieldList) {
		this.allLogicalKeyFieldList = allLogicalKeyFieldList;
	}

	public void setAllLogicalFieldList(ArrayList<String> allLogicalFieldList) {
		this.allLogicalFieldList = allLogicalFieldList;
	}

	public ArrayList<String> getAllLogicalFieldList() {
		return allLogicalFieldList;
	}
	
	public Map<String, Map<String, String>> getKeyFieldListMap() {
		return keyFieldListMap;
	}

	public void setKeyFieldListMap(Map<String, Map<String, String>> keyFieldListMap) {
		this.keyFieldListMap = keyFieldListMap;
	}

	public ArrayList<String> getLogicalFiles() {
		return logicalFiles;
	}

	public void setLogicalFiles(ArrayList<String> logicalFiles) {
		this.logicalFiles = logicalFiles;
	}

	public Connection getConnMSSQL() {
		return connMSSQL;
	}

	public void setConnMSSQL(Connection connMSSQL) {
		this.connMSSQL = connMSSQL;
	}

	public int getNumberOfFields() {
		return numberOfFields;
	}

	public void setNumberOfFields(int numberOfFields) {
		this.numberOfFields = numberOfFields;
	}

	public int getNumberOfKeyFields() {
		return numberOfKeyFields;
	}

	public void setNumberOfKeyFields(int numberOfKeyFields) {
		this.numberOfKeyFields = numberOfKeyFields;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDataBase() {
		return dataBase;
	}

	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getLongFileName() {
		return longFileName;
	}

	public void setLongFileName(String fileName) {
		longFileName = fileName;
	}

	public String getShortLibraryName() {
		return shortLibraryName;
	}

	public void setShortLibraryName(String libraryName) {
		this.shortLibraryName = libraryName;
	}

	public String getLongLibraryName() {
		return longLibraryName;
	}

	public void setLongLibraryName(String libraryName) {
		this.longLibraryName = libraryName;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public boolean isHasKeysInd() {
		return hasKeysInd;
	}

	public void setHasKeysInd(boolean hasKeysInd) {
		this.hasKeysInd = hasKeysInd;
	}

	public boolean isHasMultipleKeysInd() {
		return hasMultipleKeysInd;
	}

	public void setHasMultipleKeysInd(boolean hasMultipleKeysInd) {
		this.hasMultipleKeysInd = hasMultipleKeysInd;
	}

	public void executeAllfunctions(Boolean getTableType) {
		
		if (getTableType) {
			try {
				if (this.connMSSQL == null) {
					MsSQL dbMSSQL = new MsSQL(companyName);
					connMSSQL = dbMSSQL.connect();
				}
				String selectSql = new String();
				if (getLongLibraryName() != null) {
					selectSql = "SELECT TABLE_TYPE FROM " + getLongLibraryName() + ".INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
				} else
					selectSql = "SELECT TABLE_TYPE FROM " + this.companyName + ".INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
				PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
				checkStmtSelect.setString(1, FileName);
				ResultSet resultsSelect = checkStmtSelect.executeQuery();
				resultsSelect.next();
				setTableType(resultsSelect.getString(1));
				checkStmtSelect.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			setTableType("BASE TABLE");
		}
		
		GetFileInfo();
		BuildAllKeyFields();
		PrivateSection();
		Constructor();
		GetterSetter();
		SetKeyFields();
        if (getNumberOfKeyFields() > 0) {
        	GetRecord();
        	Exists();
        }
        if (tableType.equals(tableString)) {
        	AddRecord();
        	if (getNumberOfKeyFields() > 0) {
        		UpdateRecord();
        	}
        	DeleteRecord();
        }
        ReadFirst();
        ReadNext();
        ReadLast();
        ReadPrevious();
        if (getNumberOfKeyFields() > 0) {		
    		String line = "//***********File Read Equal Section******************************************//\n";
    		WriteJavaSourceLine(line);
        	ReadEqualFirst(getFileName(), getAllPhysicalKeyFieldNames());
        	int count1 = 0;
        	int count2 = 0;
        	for (String lglFile : logicalFiles) {
        		count1++;
        		count2 = 0;
        		for (ArrayList<String> keys : allLogicalKeyFields) {
        			count2++;
        			if (count1 == count2) {
        				ReadEqualFirst(lglFile, keys);
        			}
        		}
        	}
        	ReadEqualNext();
        	ReadEqualLast(getFileName(), getAllPhysicalKeyFieldNames());
        	count1 = 0;
        	count2 = 0;
        	for (String lglFile : logicalFiles) {
        		count1++;
        		count2 = 0;
        		for (ArrayList<String> keys : allLogicalKeyFields) {
        			count2++;
        			if (count1 == count2) {
        				ReadEqualLast(lglFile, keys);
        			}
        		}
        	}
        	ReadEqualPrevious();
        }
        UpdateAllFromResults();
        ToString();
        if (getNumberOfKeyFields() > 0) {
        	ToStringKeys();
        }
        
        if (hasMultipleKeysInd) {
        	int counter = 0;
        	for(ArrayList<String> keyflds : allLogicalKeyFields) {
        		counter++;
        		KeyBuildForLogicalFiles(counter, keyflds);
        	}
        }
        
        CheckFields();
        CloseClass();
        buildJSON();
        readJSON();
        // ffc.closeConnection();
	}
	
	private void GetFileInfo() {
		
		String line = "package com.database." + getDataBase() + ";\n\n";

        // write class heading
        line += "import java.sql.Connection;\n";
        line += "import java.sql.Statement;\n";
        line += "import java.util.ArrayList;\n";
        line += "import java.util.Collection;\n\n";
        line += "import model.DBClassBuilder;\n\n";
        if ((tableType.equals(tableString)) || (getNumberOfKeyFields() > 0)) {
        	line += "import java.sql.PreparedStatement;\n";
        }
        line += "import java.sql.ResultSet;\n";
        line += "import java.sql.SQLException;\n";
        line += "import java.sql.SQLWarning;\n\n";

        // write class heading
        if (getLongFileName() != null) {
        	line += "public class " + getLongFileName() + " extends DBClassBuilder {";
        } else {
        	line += "public class " + getFileName() + " extends DBClassBuilder {";
        }
        
        WriteJavaSourceLine(line);
	}
	
	private void PrivateSection() {
		WriteBlankLine();
		
		String line = "//***********Field Definition Section*****************************************//\n\n";

		// define file fields
		String fieldName = new String();
		String fieldType = new String();
		String saveLine = new String();
		String description = new String();
		for (ArrayList<String> element : allFields) {
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 2:
						fieldName = field;
						break;
					case 3:
						fieldType = field;
						break;
					case 9:
						description = field;
						break;
				}
			}
			if (fieldType.equals(intString)) {
				line += "\tprivate " + fieldType + "\t\t" + fieldName + "; // " + description + "\n";
				saveLine += "\tprivate " + fieldType + "\t\t" + fieldName + "Sav; // " + description + "\n";
				
			} else {
				line += "\tprivate " + fieldType + "\t" + fieldName + "; // " + description + "\n";
				saveLine += "\tprivate " + fieldType + "\t" + fieldName + "Sav; // " + description + "\n";
			}
		}
		line += "\n" + saveLine + "\n";
		// define key fields
		ArrayList<String> keysSoFar = new ArrayList<String>();
		fieldName = new String();
		String one = "1";
		for (ArrayList<String> element : allFields) {
			int counter = 0;
			for (String field : element) {
				counter++;
				if (counter > 6) break;
				switch (counter) {
				case 2:
					fieldName = field;
					break;
				case 3:
					fieldType = field;
					break;
				case 6:
					if (field.equals(one)) {
						if (fieldType.equals(intString)) {
							line += "\tprivate " + fieldType + "\t\tKey" + fieldName + ";\n";
						} else {
							line += "\tprivate " + fieldType + "\tKey" + fieldName + ";\n";
						}
						keysSoFar.add(fieldName);
					}
				}
			}
		}
		line += "\n";
		Collection<ArrayList<String>> keyFields = getAllLogicalKeyFields();
		if (keyFields.size() > 0) {
			for (ArrayList<String> keys : keyFields) {
				for (String key : keys) {
					if (!keysSoFar.contains(key.trim())) {
						String returnString = getFieldType(key.trim());
						if (!returnString.isEmpty()) {
							if (returnString.equals(intString)) {
								line += "\tprivate " + getFieldType(key.trim()) + "\t\tKey" + key.trim() + ";\n";
							} else {
								line += "\tprivate " + getFieldType(key.trim()) + "\tKey" + key.trim() + ";\n";
							}
							keysSoFar.add(key.trim());
						}
					}
				}
			}
		}
		line += "\n";
		for (String key : keysSoFar) {
			line += "\tprivate boolean\tuseKey" + key.trim() + ";\n";
		}
		
		line += "\n\tprivate Collection<ArrayList<String>> allFields = new ArrayList<ArrayList<String>>();\n";
		line += "\tprivate ArrayList<String> allKeyFiles = new ArrayList<String>();\n";
		line += "\tprivate Collection<ArrayList<String>> allKeyFields = new ArrayList<ArrayList<String>>();\n";
		line += "\tprivate ArrayList<String> allPhysicalKeyFieldNames = new ArrayList<String>();\n";
		line += "\tprivate ArrayList<String> allLogicalKeyFieldNames = new ArrayList<String>();\n";
		line += "\tprivate Collection<ArrayList<String>> allLogicalKeyFields = new ArrayList<ArrayList<String>>();\n";
		line += "\tprivate ArrayList<String> allLogicalFieldList = new ArrayList<String>();\n";
		line += "\tprivate Collection<ArrayList<String>> allLogicalKeyFieldList = new ArrayList<ArrayList<String>>();\n\n";
		
		line += "\tprivate Connection connection;\n";
		line += "\tprivate ResultSet results;\n";
		if ((tableType.equals(tableString)) || (getNumberOfKeyFields() > 0)) {
			line += "\tprivate PreparedStatement checkStmt;\n";
		}
		
		line += "\n\tprivate boolean recordFound;\n";
		line += "\tprivate boolean updateOK;\n";
		line += "\tprivate boolean readeOK;\n";
		line += "\tprivate boolean supressErrorMsg;\n\n";
		WriteJavaSourceLine(line);
	}
	
	private void Constructor() {
		
		String line = "\n//***********Constructor Section**********************************************//\n\n";
		
		// write class heading
		if (getLongFileName() != null)
			line += "\tpublic " + getLongFileName() + "() {\n\n";
		else
			line += "\tpublic " + getFileName() + "() {\n\n";

        line += "\t\tsuper();\n\n";

        line += "\t\tsetsupressErrorMsg(true);\n\n";
        		
		line += "\t\tsetDataBase(\"" + getDataBase() + "\");\n";
		if (getLongFileName() != null)
			line += "\t\tsetFileName(\"" + getLongFileName() + "\");\n";
		else	
			line += "\t\tsetFileName(\"" + getFileName() + "\");\n";
		line += "\t\tsetCompanyName(\"" + getCompanyName() + "\");\n\n";
        
		// setters for all file fields
		String fieldType = new String();
		String setter = new String();
		String saveLine = new String();
		for (ArrayList<String> element : allFields) {
			int counter = 0;
			for (String field : element) {
				counter++;
				if (counter > 7) break;
				switch (counter) {
					case 3:
						fieldType = field;
						break;
					case 7:
						setter = field;
						break;
				}
			}
			if (fieldType.equals(stringString)) {
				line += "\t\t" + setter + "(\"\");\n";
			} else if (fieldType.equals(doubleString)) {
				line += "\t\t" + setter + "(0.0);\n";
			} else {
				line += "\t\t" + setter + "(0);\n";
			}
			saveLine += "\t\t" + setter + "Sav();\n";
		}
		
		// setters for all save fields
		line += "\n" + saveLine + "\n";
				
		// setters for all file key fields & use indicators
		String fieldName = new String();
		ArrayList<String> keysSoFar = new ArrayList<String>();
		saveLine = new String();
		for (ArrayList<String> element : allKeyFields) {
			int counter = 0;
			for (String field : element) {
				counter++;
				if (counter >= 8) break;
				switch (counter) {
					case 2:
						fieldName = field;
						break;
					case 3:
						fieldType = field;
						break;
					case 7:
						setter = field;
						break;
				}
			}
			if (!keysSoFar.contains(fieldName.trim())) {
				line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(get" + fieldName + "());\n";
				saveLine = saveLine + "\t\t" + "setUseKey" + fieldName + "(false);\n";
				keysSoFar.add(fieldName);
			}
		}
		
		// set all Use key indicators off
		line += "\n" +saveLine + "\n";

		line += "\t\tif (!getFields()) {\n";
		line += "\t\t\tSystem.out.println(\"Fields could not be retrieved.\");\n";
		line += "\t\t}\n\n";
		
        line += "\t\tsetsupressErrorMsg(false);";
        WriteJavaSourceLine(line);
		
		SetOffAllInd(2);

        // Close bracket
        line = "\t}\n";
        WriteJavaSourceLine(line);
        
        // 255 java parameter limit
        if ((getNumberOfFields() * 2) <= 255) {
			// write class heading
    		if (getLongFileName() != null)
    			line = "\tpublic " + getLongFileName() + "(";
    		else
    			line = "\tpublic " + getFileName() + "(";
	        fieldType = new String();
	  
	        int count1 = 0;
	        for (ArrayList<String> element : allFields) {
	        	count1++;
				int count2 = 0;
				for (String field : element) {
					count2++;
					if (count2 > 3) break;
					switch (count2) {
						case 2:
							fieldName = field;
							break;
						case 3:
							fieldType = field;
							break;
					}
				}
				if (count1 == 1) {
					line += fieldType + " " + fieldName;
				} else {
					line += ", " + fieldType + " " + fieldName;
				}
				int m = count1%4;
	 			if (count1 == numberOfFields) {
						line += ") {\n\n";
				} else {
					if (m == 0) {
						line += "\n\t\t\t\t";
	 				}
	 			}
			}

	        line += "\t\tsuper();\n\n";
	        line += "\t\tsetsupressErrorMsg(true);\n\n";
			line += "\t\tsetDataBase(\"" + getDataBase() + "\");\n";
			if (getLongFileName() != null)
				line += "\t\tsetFileName(\"" + getLongFileName() + "\");\n";
			else
				line += "\t\tsetFileName(\"" + getFileName() + "\");\n";
			line += "\t\tsetCompanyName(\"" + getCompanyName() + "\");\n\n";
	        
	        // setters for all file fields
	        count1 = 0;
	        setter = new String();
	        saveLine = new String();
	        for (ArrayList<String> element : allFields) {
	        	count1++;
	        	
				int count2 = 0;
				for (String field : element) {
					count2++;
					switch (count2) {
						case 2:
							fieldName = field;
							break;
						case 7:
							setter = field;
							break;
					}
				}
				line += "\t\t" + setter + "(" + fieldName + ");\n";
				saveLine = saveLine + "\t\t" + setter + "Sav();\n";
			}
	        line += "\n" + saveLine + "\n";
			
			// setters for all file key fields & use indicators
			fieldName = new String();
			keysSoFar = new ArrayList<String>();
			saveLine = new String();
			if (allKeyFields.size() > 0) {
				for (ArrayList<String> element : allKeyFields) {
					int counter = 0;
					for (String field : element) {
						counter++;
						if (counter >= 8) break;
						switch (counter) {
							case 2:
								fieldName = field;
								break;
							case 3:
								fieldType = field;
								break;
							case 7:
								setter = field;
								break;
						}
					}
					if (!keysSoFar.contains(fieldName.trim())) {
						line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(get" + fieldName + "());\n";
						saveLine = saveLine + "\t\t" + "setUseKey" + fieldName + "(false);\n";
						keysSoFar.add(fieldName);
					}
				}
			
				// set all Use key indicators off
				line += "\n" + saveLine + "\n";
			}

			line += "\t\tif (!getFields()) {\n";
			line += "\t\t\tSystem.out.println(\"Fields could not be retrieved.\");\n";
			line += "\t\t}\n";

	        WriteJavaSourceLine(line);
	        
			SetOffAllInd(2);

			// Close bracket
			line = "\t}\n";
			WriteJavaSourceLine(line);
        }
	}
	
	private void GetterSetter() {
		
		String line = "//***********Getter Setter Section********************************************//\n";
		WriteJavaSourceLine(line);

        // write setters/getters for file fields
        String fieldName = new String();
        String fieldType = new String();
        String setter = new String();
        String getter = new String();
        int fieldSize = 0;
        int fieldDec = 0;
        for (ArrayList<String> element : allFields) {
			int count = 0;
			for (String field : element) {
				count++;
				switch (count) {
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
					case 7:
						setter = field;
						break;
					case 8:
						getter = field;
						break;
				}
			}
			Getter("public", fieldName, fieldType, getter);
			Setter("public", fieldName, fieldType, setter, fieldSize, fieldDec, false);
		}
        
        // write setters/getters for file save fields
        fieldSize = 0;
        fieldDec = 0;
        for (ArrayList<String> element : allFields) {
			int count2 = 0;
			for (String field : element) {
				count2++;
				switch (count2) {
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
					case 7:
						setter = field;
						break;
					case 8:
						getter = field;
						break;
				}
			}
			
			Getter("public", fieldName + "Sav", fieldType, getter + "Sav");
			SetterSav(fieldName + "Sav", fieldType, setter + "Sav", getter);
		}
        
        // write setters/getters for key file fields
        ArrayList<String> keysSoFar = new ArrayList<String>();
        fieldName = new String();
        fieldType = new String();
        setter = new String();
        getter = new String();
        fieldSize = 0;
        fieldDec = 0;
        for (ArrayList<String> element : allKeyFields) {
			int count2 = 0;
			for (String field : element) {
				count2++;
				switch (count2) {
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
					case 7:
						setter = field;
						break;
					case 8:
						getter = field;
						break;
				}
			}
			if (!keysSoFar.contains(fieldName.trim())) {
				Getter("public", "Key" + fieldName, fieldType, getter.substring(0, 3) + "Key" + getter.substring(3));
				Setter("public", "Key" + fieldName, fieldType, setter.substring(0, 3) + "Key" + setter.substring(3), fieldSize, fieldDec, false);
				keysSoFar.add(fieldName);
			}
		}
        
        keysSoFar = new ArrayList<String>();
        fieldName = new String();
        for (ArrayList<String> element : allKeyFields) {
			int count2 = 0;
			for (String field : element) {
				count2++;
				if (count2 >+ 3) break;
				switch (count2) {
					case 2:
						fieldName = field;
						break;
				}
			}
			if (!keysSoFar.contains(fieldName.trim())) {
				Getter("public", "useKey" + fieldName, "boolean", "isUseKey" + fieldName);
				Setter("public", "useKey" + fieldName, "boolean", "setUseKey" + fieldName, 0, 0, false);
				keysSoFar.add(fieldName);
			}
		}
        
        Getter("public", "connection", "Connection", "getConn");
		Setter("public", "connection", "Connection", "setConn", 0, 0, false);
        
        Getter("private", "recordFound", "boolean", "getRecordFound");
        Setter("private", "recordFound", "boolean", "setRecordFound", 0, 0, false);

        Getter("public", "updateOK", "boolean", "getUpdateOK");
        Setter("public", "updateOK", "boolean", "setUpdateOK", 0, 0, false);

        Getter("public", "readeOK", "boolean", "getReadeOK");
        Setter("public", "readeOK", "boolean", "setReadeOK", 0, 0, false);

        Setter("public", "supressErrorMsg", "boolean", "setsupressErrorMsg", 0, 0, false);
	}
	
	private void Getter(String access, String field, String type, String getterName) {
		
		// write getter
		String line = "\t" + access + " " + type + " " + getterName + "() {\n";
		line += "\t\treturn this." + field + ";\n";
		line += "\t}\n";
		WriteJavaSourceLine(line);
	}
	
	private void Setter(String access, String field, String type, String setterName, long length, int dec, boolean setind) {
		
        // Check for set off read ind.
		if (setind) SetOffAllInd(2);

		// write getter
		String line = "\t" + access + " void " + setterName + "(" + type + " " + field + ") {\n";
		
		String booleanString = "boolean";
		String connectionString = "Connection";
		if (field.startsWith("Key")) {
			line += "\t\t" + "setUse" + field + "(false);\n";
		}
		if (type.equals(doubleString)) {
			line += "\t\tthis." + field + " = 0.0;\n";
            line += "\t\tif (" + field + " != 0) {\n";
            line += "\t\t\tint fldLength = " + length + ";\n";
            line += "\t\t\tint decimal = " + dec + ";\n";
            line += "\t\t\tif (checkSizeDouble(" + field + ", fldLength, decimal)) {\n";
            line += "\t\t\t\tString doubleString = String.format";
            line += "(\"%." + dec + "f\", " + field + ");\n";;
            line += "\t\t\t\t" + "this." + field
                  + " = Double.parseDouble(doubleString);\n";
            if (field.startsWith("Key")) {
    			line += "\t\t\t\t" + "setUse" + field + "(true);\n"; 
    		}
            line += "\t\t\t} else {\n";
            line += "\t\t\t\tif (!supressErrorMsg) {\n";
            line += "\t\t\t\t\tSystem.err.println(\"Field " + field;
            line += ": not updated properly.";
            line += " " + field + " = \" +";
            line += " " + field + ");\n";
            line += "\t\t\t\t}\n";
            line += "\t\t\t\tsetUpdateOK(false);\n";
            line += "\t\t\t}\n";
            line += "\t\t}\n";
		} else if (type.equals(intString)) {
			line += "\t\tthis." + field + " = 0;\n";
            line += "\t\tif (" + field + " != 0) {\n";
            line += "\t\t\tint fldlength = " + length + ";\n";
            line += "\t\t\tif (checkSizeInt(\"" + field + "\", " + field + ", fldlength))\n";
            line += "\t\t\t\tthis." + field + " = " + field + ";\n";
            if (field.startsWith("Key")) {
    			line += "\t\t\t\t" + "setUse" + field + "(true);\n"; 
    		}
            line += "\t\t\t} else {\n";
            line += "\t\t\t\tif (!supressErrorMsg) {\n";
            line += "\t\t\t\t\tSystem.err.println(\"Field " + field;
            line += ": not updated properly in file.";
            line += " " + field + " = \" +";
            line += " " + field + ");\n";
            line += "\t\t\t\t}\n";
            line += "\t\t\t\tsetUpdateOK(false);\n";
            line += "\t\t\t}\n";
		} else if (type.equals(longString)) {
			line += "\t\tthis." + field + " = 0;\n";
            line += "\t\tif (" + field + " != 0) {\n";
            line += "\t\t\tint fldlength = " + length + ";\n";
            line += "\t\t\tif (checkSizeLong(\"" + field + "\", " + field + ", fldlength))\n";
            line += "\t\t\t\tthis." + field + " = " + field + ";\n";
            if (field.startsWith("Key")) {
    			line += "\t\t\t\t" + "setUse" + field + "(true);\n"; 
    		}
            line += "\t\t\t} else {\n";
            line += "\t\t\t\tif (!supressErrorMsg) {\n";
            line += "\t\t\t\t\tSystem.err.println(\"Field " + field;
            line += ": not updated properly in file.";
            line += " " + field + " = \" +";
            line += " " + field + ");\n";
            line += "\t\t\t}\t\n";
            line += "\t\t\t\t\tsetUpdateOK(false);\n";
            line += "\t\t\t\t}\n";
		} else if (type.equals(bigintString)) {
			line += "\t\tthis." + field + " = 0;\n";
            line += "\t\tif (" + field + " != 0) {\n";
            line += "\t\t\tint fldlength = " + length + ";\n";
            line += "\t\t\tif (checkSizeBigInt(" + field + ", fldlength))\n";
            line += "\t\t\t\tthis." + field + " = " + field + ";\n";
            if (field.startsWith("Key")) {
    			line += "\t\t\t\t" + "setUse" + field + "(true);\n"; 
    		}
            line += "\t\t\t} else {\n";
            line += "\t\t\t\tif (!supressErrorMsg) {\n";
            line += "\t\t\t\t\tSystem.err.println(\"Field " + field;
            line += ": not updated properly in file.";
            line += " " + field + " = \" +";
            line += " " + field + ");\n";
            line += "\t\t\t\t}\n";
            line += "\t\t\t\tsetUpdateOK(false);\n";
            line += "\t\t}\t\n";
		} else if (type.equals(stringString)) {
			line += "\t\tthis." + field + " = \"\";\n";
            line += "\t\tif (!" + field + ".isEmpty()) {\n";
            line += "\t\t\tint fldlength = " + length + ";\n";
            line += "\t\t\tif (checkSizeString(\"" + field + "\", " + field + ", fldlength)) {\n";
            line += "\t\t\t\tthis." + field + " = " + field + ";\n";
            if (field.startsWith("Key")) {
    			line += "\t\t\t\t" + "setUse" + field + "(true);\n"; 
    		}
            line += "\t\t\t} else {\n";
            line += "\t\t\t\tif (!supressErrorMsg) {\n";
            line += "\t\t\t\t\tSystem.err.println(\"Field " + field;
            line += ": not updated properly.";
            line += " " + field + " = \" +";
            line += " " + field + ");\n";
            line += "\t\t\t\t}\n";
            line += "\t\t\t\tsetUpdateOK(false);\n";
            line += "\t\t\t}\n";
            line += "\t\t}\n";
		} else if (type.equals(booleanString)) {
			line += "\t\tthis." + field + " = " + field + ";\n";
		} else if (type.equals(connectionString)) {
			line += "\t\tthis." + field + " = " + field + ";\n";
		}
		line += "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void SetterSav(String field, String type, String setterName, String getterName) {
		
		// write setter
		String line = "\tprivate void " + setterName + "() {\n";
		line += "\t\tthis." + field + " = " + getterName + "();\n";
		line += "\t}\n";
		WriteJavaSourceLine(line);
	}
	
	private void WriteBlankLine() {
		text.append("\n");
	}
	
	public void BuildAllKeyFields() {
		
		Collection<ArrayList<String>> allKeyFields = new ArrayList<ArrayList<String>>();

	    // read through array list to create all key fields
 		for (String fieldNames : allPhysicalKeyFieldNames) {
 			for (ArrayList<String> keyFields : allFields) {
 				int count = 0;
 				for (String field : keyFields) {
					count++;
					if (count > 2) break;
					switch (count) {
						case 2:							
							if (field.equals(fieldNames.trim())) {
								allKeyFields.add(keyFields);
								break;
							}
							break;
					}
				}
 			}
 		}
	
	    // read through array list to create all key fields
 		for (String fieldNames : allLogicalKeyFieldNames) {
 			for (ArrayList<String> keyFields : allFields) {
 				int count = 0;
 				for (String field : keyFields) {
					count++;
					if (count > 2) break;
					switch (count) {
						case 2:							
							if (field.equals(fieldNames.trim())) {
								allKeyFields.add(keyFields);
								break;
							}
							break;
					}
				}
 			}
 		}
 		
	    setAllKeyFields(allKeyFields);	
	}

	public void SetKeyFields() {
		
		Collection<ArrayList<String>> allKeyFields = new ArrayList<ArrayList<String>>();
		allKeyFields = getAllKeyFields();
		int size = allKeyFields.size();
		if (size > 0) {
			
			String line = "//***********Set Key FieldS Section*******************************************//\n\n";
	
		    // write set key
		    line += "\tpublic void setKeyFields() {\n";
		 		
		    // read through array list to create all key fields
		    ArrayList<String> keysSoFar = new ArrayList<String>();
			for (ArrayList<String> element : allKeyFields) {
				String fieldName = new String();
				String setter = new String();
				String getter = new String();
				int count = 0;
				for (String field : element) {
					count++;
					switch (count) {
						case 2:
							fieldName = field;
							break;
						case 7:
							setter = field;
							break;
						case 8:
							getter = field;
							break;
					}	
				}
				if (!keysSoFar.contains(fieldName.trim())) {
					line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(" + getter + "());\n";
				    keysSoFar.add(fieldName);
				}
			}
		
		    line += "\t}\n";
		    WriteJavaSourceLine(line);
		    
		    for (int i = 0; i < numberOfKeyFields; i++) {
			    // write select
			    line = "\tpublic void setKeyFields(";
			    // read through array list to create all key fields
			    int count1 = 0;
			    boolean keysFilled = false;
		 		for (ArrayList<String> element : allKeyFields) {
		 			if (keysFilled) break;
					String fieldName = new String();
					String fieldType = new String();
					String one = "1";
			     	boolean isKeyField = false;
					int count = 0;
					for (String field : element) {
						count++;
						if (count > 6)break;
						switch (count) {
							case 2:
								fieldName = field;
								break;
							case 3:
								fieldType = field;
								break;
							case 6:
								if(field.equals(one)) {
									isKeyField = true;
								}
								break;
						}	
					}
					if (isKeyField) {
						count1++;
						if (count1 == i+1) keysFilled = true;
						if (count1 == 1) {
							line += fieldType + " Key" + fieldName;
						} else {
							line += ", " + fieldType + " Key" + fieldName;
						}
					}
				}
		 		line += ") {\n";
			    count1 = 0;
			    keysFilled = false;
				boolean errorSupressInd = false;
			    // read through array list to create all key fields
		 		for (ArrayList<String> element : allKeyFields) {
		 			if (keysFilled) break;
		 			String fieldName = new String();
		 			String fieldType = new String();
		 			String setter = new String();
		 			String one = "1";
		 	     	boolean isKeyField = false;
		 			int count = 0;
		 			for (String field : element) {
		 				count++;
		 				if (count > 7) break;
		 				switch (count) {
		 					case 2:
		 						fieldName = field;
		 						break;
		 					case 3:
		 						fieldType = field;
		 						break;
		 					case 6:
		 						if(field.equals(one)) {
		 							isKeyField = true;
		 						}
		 						break;
		 					case 7:
		 						setter = field;
		 						break;
		 				}	
		 			}
		 			if (isKeyField) {
		 				count1++;
		 				if (keysFilled) {
		 					if (fieldType.equals(stringString)) {
		 						line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(\"\");\n";
		 					} else if (fieldType.equals(doubleString)) {
		 						line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(0.0);\n";
		 					} else {
		 						line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(0);\n";
		 					}
		 				} else {
		 					line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(Key" + fieldName + ");\n";
		 				}
		 			    if (count1 == i+1) {
		 			    	keysFilled = true;
		 			    	if (count1 < numberOfKeyFields) {
		 			    		line += "\t\tsetsupressErrorMsg(true);\n";
		 			    		errorSupressInd = true;
		 			    	}
		 			    }
		 			}
		 		}
		 	
		 		if (errorSupressInd) {
		 			line += "\t\tsetsupressErrorMsg(false);\n";
		 		}
		 		
			    line += "\t}\n";
			    WriteJavaSourceLine(line);
		    }
	    }
	}
	
	private void KeyBuildForLogicalFiles(int count, ArrayList<String> keyFields) {
		
		int nbrOfKeys = keyFields.size();
		for (int i = 0; i < nbrOfKeys; i++) {
			int counter = 0;
			for(String element : logicalFiles) {
				boolean keysFilled = false;
				
				counter++;
				if (counter == count) {
					String line = "\tpublic void setKeyFields" + element + "(";
					int count1 = 0;
					for (String key : keyFields) {
						if (keysFilled) continue;
						count1++;
						String type = getFieldType(key.trim());
						if (count1 == 1) {
							line += type + " " + key.trim();
							if (count1 == i+1) {
			 			    	keysFilled = true;
			 			    }
						} else {
							if (count1 == i+1) {
			 			    	keysFilled = true;
			 			    }
							int m = count1%4;
							if (m == 0) {
								if (keysFilled) line += ", " + type + " " + key.trim();

								else line += ", " + type + " " + key.trim();
							} else if (m == 1) {
								line += "\n\t\t\t\t\t\t\t\t\t,"  + type + " "+ key.trim();
							} else {
								line += ", " + type + " " + key.trim();
							}
						}
						
					}
					
					line += ") {\n";
					keysFilled = false;
					count1 = 0;
					for (String key : keyFields) {
						count1++;
						if (!keysFilled) {
							String setter = "set" + key.trim();
							line += "\t\t" + setter.substring(0, 3) + "Key" + setter.substring(3) + "(" + key.trim() + ");\n";
						}
					    if (count1 == i+1) keysFilled = true;
					}
					line += "\t}\n";
					WriteJavaSourceLine(line);
				}
			}
		}
	}
	
	private void GetRecord() {
		String line = "//***********Get A Record Section*********************************************//\n\n";

	    // write add
	    line += "\tpublic boolean get() throws SQLException {\n";
	    WriteJavaSourceLine(line);
	
	    SetOffAllInd(2);
	
	    WriteBlankLine();
	
	    line = "\t\t" + "if (connection == null) return false;\n";
	    WriteJavaSourceLine(line);
	
	    SetCheckKeyFields();
	
	    // build select statement
	    line = "\t\tString checkSql = \"Select * from " + getFileName() + " \";\n\n";
        
        line += "\t\tint counter = 1;\n";
        WriteJavaSourceLine(line);

	    SetWhereFields();

	    // write select
	    line = "\n\t\tcheckStmt = connection.prepareStatement(checkSql);\n";
	    WriteJavaSourceLine(line);

	    SetSelectStatementKeys(0);
	
	    // write select
	    line = "\n\t\tresults = checkStmt.executeQuery();\n";
	    // write select
	    line += "\t\tSQLWarning warning = results.getWarnings();\n";
	    // write select
	    line += "\t\tprintSQLWarnings(warning);\n";
	    // write select
	    line += "\t\twarning = checkStmt.getWarnings();\n";
	    // write select
	    line += "\t\tprintSQLWarnings(warning);\n\n";
	    // write read results
	    line += "\t\tif (results.next()) {\n";
	    // write select
	    line += "\t\t\tupdateAllFromResults();\n";
	
		 if (checkForKeyFields()) {
			 line += "\t\t\tsetKeyFields();\n";
		 }
	
	    line += "\t\t\tsetRecordFound(true);\n";
	    line += "\t\t\tsetUpdateOK(true);\n";
	    line += "\t\t\treturn true;\n";
	    line += "\t\t} else {";
	    WriteJavaSourceLine(line);
	
	    SetOffAllInd(3);
	
	    line = "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
	    line += "\t\t\treturn false;\n";
	    line += "\t\t}\n";
	    line += "\t}\n";
	    WriteJavaSourceLine(line);
	}
	
	private void Exists() {
		
		String line = "//***********Check Existence of a Record Section******************************//\n\n";
	    // write add
	    line += "\tpublic boolean exists() throws SQLException {\n\n";
	    // write select
	    line += "\t\tint numberOfRecords;";
	    WriteJavaSourceLine(line);
	
	    SetOffAllInd(2);
	   
	    line = "\n\t\tif (connection == null) return false;\n";
	    WriteJavaSourceLine(line);
	
	    SetCheckKeyFields();
	
	    // write select
	    line = "\t\tString checkSql = \"select count(*) as numberOfRecords from " + getFileName() + " \";";
	    WriteJavaSourceLine(line);
	
	    SetWhereFields();
	
	    // write select
	    line = "\n\t\t" + "checkStmt =";
	    line += "connection.prepareStatement(checkSql);\n";
        line += "\t\tint counter = 1;\n";
        WriteJavaSourceLine(line);

	    SetSelectStatementKeys(0);
	
	    // write select
	    line = "\n\t\tresults = checkStmt.executeQuery();\n\n";

	    // write select
	    line += "\t\tSQLWarning warning = results.getWarnings();\n";
	
	    // write select
	    line += "\t\tprintSQLWarnings(warning);\n";

	    // write select
	    line += "\t\twarning = checkStmt.getWarnings();\n";

	    // write select
	    line += "\t\tprintSQLWarnings(warning);\n\n";

	    // write select get
	    line += "\t\tresults.next();\n";
	    line += "\t\tnumberOfRecords = results.getInt(1);\n\n";
	    line += "\t\tif (numberOfRecords > 0) return true;\n";
	    line += "\t\telse return false;\n";
	    line += "\t}\n";
	    WriteJavaSourceLine(line);
	}
	
	private void AddRecord() {
		
		String line = "//***********File Updating Section********************************************//\n\n";
		
	    // write add
	    line += "\tpublic boolean add() throws SQLException {\n\n";
	
	    line += "\t\tsetReadeOK(false);\n\n";
	
	    // write check for record found
	    line += "\t\tif (!getUpdateOK()) {\n";
	    line += "\t\t\tSystem.err.println(\"" + message2 + "\");\n";
	    line += "\t\t\treturn false;\n";
	    line += "\t\t}\n\n";

	    // write insert string
	    line += "\t\tString checkSql = \"insert into " + getFileName() + " (";
	    
	    // get all fields for insert string
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			String fieldName = new String();
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 2:
 						fieldName = field;
 						break;
 				}
 			}
 			
 			count1++;
 			if (count1 == 1) {
 				line += fieldName;
 			} else {
 				if (line.isEmpty()) {
 					line = "                        					\", " + fieldName;
 				} else {
 					line += ", " + fieldName;
 				}
 			}
 			
 			int m = count1%5;
 			if (count1 == numberOfFields) {
					line += ") \" +";
	 				WriteJavaSourceLine(line);
	 				line = "";
			} else {
				if (m == 0) {
 					line += "\" +";
 	 				WriteJavaSourceLine(line);
 	 				line = "";
 				}
 			}
 		}
 		
 		// setup where clause
        line = "\t\t\t\t\t\t\"values (";
 		for (int i = 0; i < count1; i++) {
 			if (i == 0) line += "?";
 			else line += ", ?";
 			if (i > 0) {
 				int m = i % 10;
 	 			if (m == 0) line += " \" + \n\t\t\t\t\t\t\t\"";
 			}
 		}
 		line += ");\";\n\n";
	    
	    // write check statement
        line += "\t\tcheckStmt = connection.prepareStatement(checkSql);\n\n";
        
        // get all fields for insert string
        count1 = 0;
  		for (ArrayList<String> element : allFields) {
  			String fieldName = new String();
  			String fieldType = new String();
  			int count = 0;
  			for (String field : element) {
  				count++;
  				switch (count) {
  					case 2:
  						fieldName = field;
  						break;
  					case 3:
  						fieldType = field;
  						break;
  				}
  			}
  			
  			count1++;
  			if (fieldType.equals(stringString)) {
				line += "\t\tcheckStmt.setString(" + count1 + ", " + fieldName + ");\n";
			} else if (fieldType.equals(doubleString)) {
				line += "\t\tcheckStmt.setDouble(" + count1 + ", " + fieldName + ");\n";
			} else if (fieldType.equals(intString)) {
				line += "\t\tcheckStmt.setInt(" + count1 + ", " + fieldName + ");\n";
			} else if (fieldType.equals(longString)) {
				line += "\t\tcheckStmt.setLong(" + count1 + ", " + fieldName + ");\n";
			} else if (fieldType.equals(bigintString)) {
				line += "\t\tcheckStmt.setLong(" + count1 + ", " + fieldName + ");\n";
			}
  		}
  		
        line += "\n\t\tint record = checkStmt.executeUpdate();\n\n";

        // write select
        line += "\t\tSQLWarning warning = checkStmt.getWarnings();\n";

        // write select
        line += "\t\tprintSQLWarnings(warning);\n\n";

        // write select
        line += "\t\tif (record > 0) return true;\n";
        line += "\t\telse return false;\n";

        line += "\t}\n";
        WriteJavaSourceLine(line);	}
	
	private void UpdateRecord() {

	    // write update
		String line = "\tpublic boolean update() throws SQLException {\n\n";
	    if (numberOfFields > 1) {
	    	line += "\t\tboolean fieldBefore = false;\n";
	    }
	    line += "\t\tsetReadeOK(false);\n\n";
	    // write check for record found
	    line += "\t\tif (!getUpdateOK()) {\n";
	    line += "\t\t\tSystem.err.println(\"" + message2 + "\");\n";
	    line += "\t\t\treturn false;\n";
	    line += "\t\t}\n\n";

	    // write check for record found
	    line += "\t\tif (!getRecordFound()) {\n";
	    line += "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
	    line += "\t\t\treturn false;\n";
	    line += "\t\t}\n\n";
	
	    // write select
	    line += "\t\tString checkSql = \"update " + getFileName() + " \";\n\n";
	    
	    line += "\t\tint counter = 1;\n\n";
	    
	    line += "\t\tcheckSql = checkSql.concat(\" set \");\n\n";

	    // get all fields for insert string
	    String fieldName = new String();
	    String fieldType = new String();
		boolean notFirst = false;
  		for (ArrayList<String> element : allFields) {
  			int count2 = 0;
  			for (String field : element) {
  				count2++;
  				switch (count2) {
  					case 2:
  						fieldName = field;
  						break;
  					case 3:
  						fieldType = field;
  						break;
  				}
  			}
  			
  			// Check for field change
  			line += "\t\tif (" + fieldName + " != " + fieldName + "Sav) {\n";
  			if (notFirst) {
  				line += "\t\t\tif (fieldBefore) checkSql = checkSql.concat(\",\");\n";
  			}
  			notFirst = true;
  			line += "\t\t\tcheckSql = checkSql.concat(\" " + fieldName + " = ?\");\n";
		    if (numberOfFields > 1) {
		    	line += "\t\t\tfieldBefore = true;\n";
		    }
		    line += "\t\t}\n";

  		}
  		WriteJavaSourceLine(line);
	    
	    SetWhereFields();

	    // write select
	    line = "\n\t\tcheckStmt = connection.prepareStatement(checkSql);\n\n";
	    
	    // get all fields for insert string
	    fieldName = "";
	    fieldType = "";
  		for (ArrayList<String> element : allFields) {
  			int count2 = 0;
  			for (String field : element) {
  				count2++;
  				switch (count2) {
  					case 2:
  						fieldName = field;
  						break;
  					case 3:
  						fieldType = field;
  						break;
  				}
  			}
  			
  			// Check for field change
  			line += "\t\tif (" + fieldName + " != " + fieldName + "Sav) {\n";
		    if (fieldType.equals(stringString)) {
		    	line += "\t\t\tcheckStmt.setString(counter++, " + fieldName + ");\n";
			} else if (fieldType.equals(doubleString)) {
				line += "\t\t\tcheckStmt.setDouble(counter++, " + fieldName + ");\n";
			} else if (fieldType.equals(intString)) {
				line += "\t\t\tcheckStmt.setInt(counter++, " + fieldName + ");\n";
			} else if (fieldType.equals(longString)) {
				line += "\t\t\tcheckStmt.setLong(counter++, " + fieldName + ");\n";
			} else if (fieldType.equals(bigintString)) {
				line += "\t\t\tcheckStmt.setLong(counter++, " + fieldName + ");\n";
			}
		    line += "\t\t}\n";
  		}
  		
  		line += "\n\t\tint record = 0;\n\n";
  		line += "\t\tif (counter > 1) {\n";
		WriteJavaSourceLine(line);
  		
	    SetSelectStatementKeys(1);
	    
	    // write select
	    line = "\n\t\t\trecord = checkStmt.executeUpdate();\n\n";	
	    // write select
	    line += "\t\t\tSQLWarning warning = checkStmt.getWarnings();\n";
	    // write select
	    line += "\t\t\tprintSQLWarnings(warning);\n\n";
  		line += "\t\t}\n";
	    // write select
	    line += "\t\tif (record > 0) return true;\n";
	    line += "\t\telse return false;\n";
	    line += "\t}\n";
	    WriteJavaSourceLine(line);
	}
	
	private void DeleteRecord() {
		
        // write delete
        String line = "\tpublic boolean delete() throws SQLException {\n\n";

        line += "\t\tsetReadeOK(false);\n\n";

        // write check for record found
        line += "\t\tif (!getRecordFound()) {\n";
        line += "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
        line += "\t\t\treturn false;\n";
        line += "\t\t}\n\n";
        // write select
        line += "\t\tString checkSql = \"delete from " + getFileName() + " \";\n";
        WriteJavaSourceLine(line);

        if (numberOfKeyFields > 0) {
        	SetWhereFields();
        	WriteBlankLine();
        }

        // write select
        line = "\t\tcheckStmt = connection.prepareStatement(checkSql);\n";
        WriteJavaSourceLine(line);
        
        if (numberOfKeyFields > 0) {
        	line = "\t\tint counter = 1;\n";
        	WriteJavaSourceLine(line);
        	SetSelectStatementKeys(0);
        }

        // write select
        line = "\n\t\tint record = checkStmt.executeUpdate();\n\n";

        // write select
        line += "\t\tSQLWarning warning = checkStmt.getWarnings();\n";

        // write select
        line += "\t\tprintSQLWarnings(warning);\n\n";
        // write select
        line += "\t\tif (record > 0) return true;\n";
        line += "\t\telse return false;\n";
        line += "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadFirst() {
		
		String line = "//***********File Read Random Section*****************************************//\n\n";
		
		// write read first
        line += "\tpublic boolean readFirst() throws SQLException {\n";
        WriteJavaSourceLine(line);

        SetOffAllInd(2);

        // write select
        line = "\n\t\tString checkSql = \"Select * from " + getFileName() + "\";\n\n";

        // write select
        line += "\t\tStatement Stmt = connection.createStatement(" +
        	   "ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);\n\n";

        // write select
        line += "\t\tresults = Stmt.executeQuery(checkSql);\n\n";

        // write select
        line += "\t\tSQLWarning warning = results.getWarnings();\n";

        // write select
        line += "\t\tprintSQLWarnings(warning);\n";

        // write select
        line += "\t\twarning = Stmt.getWarnings();\n";

        // write select
        line += "\t\tprintSQLWarnings(warning);\n\n";

        // write read results
        line += "\t\tif (results.first()) {";
        WriteJavaSourceLine(line);
        
        SetResultLines(false);

        line = "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadNext() {
		
		String line = "\tpublic boolean readNext() throws SQLException {\n\n";

        line += "\t\tsetReadeOK(false);\n\n";

        // write check for record found
        line += "\t\tif (!getRecordFound()) {\n";
        line += "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
        line += "\t\t\treturn false;\n";
        line += "\t\t}\n\n";

        // write check for record found
        line += "\t\tif (results == null) return false;\n\n";
        
        // write read results
        line += "\t\tif (results.next()) {";
        WriteJavaSourceLine(line);
        
        SetResultLines(false);

        line = "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadLast() {
		
		// write readlast
        String line = "\tpublic boolean readLast() throws SQLException {\n";
        WriteJavaSourceLine(line);

        SetOffAllInd(2);
        WriteBlankLine();

        // write select
        line = "\t\tString checkSql = \"Select * from " + getFileName() + "\";\n\n";
        // write select
        line += "\t\tStatement Stmt = connection.createStatement(" +
         	   "ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);\n\n";
        // write select
        line += "\t\tresults = Stmt.executeQuery(checkSql);\n\n";
        // write select
        line += "\t\tSQLWarning warning = results.getWarnings();\n";
        // write select
        line += "\t\tprintSQLWarnings(warning);\n";
        // write select
        line += "\t\twarning = Stmt.getWarnings();\n";
        // write select
        line += "\t\tprintSQLWarnings(warning);\n\n";

        // write read results
        line += "\t\tif (results.last()) {";
        WriteJavaSourceLine(line);
        
        SetResultLines(false);

        line = "\t}\t\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadPrevious() {
		
		// write delete
        String line = "\tpublic boolean readPrevious() throws SQLException {\n\n";

        line += "\t\tsetReadeOK(false);\n\n";

        // write check for record found
        line += "\t\tif (!getRecordFound()) {\n";
        line += "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
        line += "\t\t\treturn false;\n";
        line += "\t\t}\n\n";

        // write check for record found
        line += "\t\tif (results == null) return false;\n\n";

        // write read results
        line += "\t\tif (results.previous()) {";
        WriteJavaSourceLine(line);

        SetResultLines(false);

        line = "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadEqualFirst(String ReadFile, ArrayList<String> keyFields) {
		
		String line = new String();
		
		// write read equal first header
		if (getFileName().equals(ReadFile)) {
			line = "\tpublic boolean readEqualFirst() throws SQLException {\n";
		} else {
			line = "\tpublic boolean readEqualFirst" + ReadFile + " () throws SQLException {\n";
		}
	    WriteJavaSourceLine(line);

	    SetOffAllInd(2);
	    WriteBlankLine();
	
	    SetCheckKeyFields(keyFields, 1);
	
	    // write select
	    line = "\t\tString checkSql = \"Select * from " + getFileName() + "\";";
	    WriteJavaSourceLine(line);
	
	    SetWhereFields(keyFields);
	    
	    SetOrderBy(ReadFile, keyFields);
	    WriteBlankLine();
	    
	    // write select
	    line = "\t\tcheckStmt = connection.prepareStatement(checkSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);";
	    WriteJavaSourceLine(line);
	    
	    SetSelectKeyFields(keyFields);
	    WriteBlankLine();
	
	    // write select
	    line = "\t\tresults = checkStmt.executeQuery();\n\n";
	
	    // write select
	    line += "\t\tSQLWarning warning = results.getWarnings();\n";
	
	    // write select
	    line += "\t\tprintSQLWarnings(warning);\n";
	
	    // write select
	    line += "\t\twarning = checkStmt.getWarnings();\n";
	
	    // write select
	    line += "\t\tprintSQLWarnings(warning);\n\n";
	
	    // write read results
	    line += "\t\tif (results.first()) {";
	    WriteJavaSourceLine(line);
	    
	    SetResultLines(true);

	    line = "\t}\n";
	    WriteJavaSourceLine(line);
	}
	
	private void ReadEqualNext() {
		
		// write readequalnext header
        String line = "\tpublic boolean readEqualNext() throws SQLException {\n\n";

        // write check for record found
        line += "\t\tif (!getReadeOK()) {\n";
        line += "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
        line += "\t\t\treturn false;\n";
        line += "\t\t" + "}\n\n";
        // write check for record found
        line += "\t\tif (results == null) return false;\n\n";
        
        // write read results
        line += "\t\tif (results.next()) {";
        WriteJavaSourceLine(line);

        SetResultLines(true);

        line = "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadEqualLast(String ReadFile, ArrayList<String> keyFields) {
		
		String line = new String();
		
		// write read equal last header
		if (getFileName().equals(ReadFile)) {
			line = "\tpublic boolean readEqualLast() throws SQLException {\n";
		} else {
			line = "\tpublic boolean readEqualLast" + ReadFile + " () throws SQLException {\n";
		}
		WriteJavaSourceLine(line);

        SetOffAllInd(2);
        WriteBlankLine();

        SetCheckKeyFields(keyFields, 1);

        // write select
        line = "\t\tString checkSql = \"Select * from " + getFileName() + "\";";
        WriteJavaSourceLine(line);

        SetWhereFields(keyFields);
	    
	    SetOrderBy(ReadFile, keyFields);

        // write select
        line = "\n\t\tcheckStmt = connection.prepareStatement(checkSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);\n";
        WriteJavaSourceLine(line);

        SetSelectKeyFields(keyFields);

        // write select
        line = "\n\t\tresults = checkStmt.executeQuery();\n\n";

        // write select
        line += "\t\tSQLWarning warning = results.getWarnings();\n";

        // write select
        line += "\t\tprintSQLWarnings(warning);\n";

        // write select
        line += "\t\twarning = checkStmt.getWarnings();\n";

        // write select
        line += "\t\tprintSQLWarnings(warning);\n\n";

        // write read results
        line += "\t\tif (results.last()) {";
        WriteJavaSourceLine(line);

        SetResultLines(true);
        
        line = "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void ReadEqualPrevious() {
		
		// write read equal previous header
		String line = "\tpublic boolean readEqualPrevious() throws SQLException {\n\n";

        // write check for record found
        line += "\t\tif (!getReadeOK()) {\n";
        line += "\t\t\tSystem.err.println(\"" + message3 + "\");\n";
        line += "\t\t\treturn false;\n";
        line += "\t\t}\n\n";

        // write check for record found
        line += "\t\tif (results == null) return false;\n\n";

        // write read results
        line += "\t\tif (results.previous()) {";
        WriteJavaSourceLine(line);
        
        SetResultLines(true);

        line = "\t}\n";
        WriteJavaSourceLine(line);
	}
	
	private void SetWhereFields() {
	
		String line = new String();
		
		// get all fields for set string
		String fieldName = new String();
		String fieldType = new String();
		String one = "1";
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			
 			boolean isKeyField = false;
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 2:
 						fieldName = field;
 						break;
 					case 3:
 						fieldType = field;
 						break;
 					case 6:
 						if(field.equals(one)) {
 							isKeyField = true;
 						}
 				}
 			}
 			if (isKeyField) {
				count1++;
				if (fieldType.equals(stringString)) {
					line = "\t\tif (Key" + fieldName + " != \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = "\t\tif (Key" + fieldName + " != 0.0) {";
				} else {
					line = "\t\tif (Key" + fieldName + " != 0) {";
				}
				WriteJavaSourceLine(line);
				if (count1 == 1) {
					line = "\t\t\tcheckSql = checkSql + \" Where " + fieldName + "=?\";";
				} else {
					line = "\t\t\tcheckSql = checkSql + \" And " + fieldName + "=?\";";
				}
				WriteJavaSourceLine(line);
			}
 		}
 		
 		line = "";
 		for (int i = 0; i < count1; i++) {
 			if (i == 0) line += "\t\t}";
 			else line += "}";
 		}
	    WriteJavaSourceLine(line);
	}
	
	private void SetWhereFields(ArrayList<String> keyFields) {
	
		String line = new String();
		
		// get all fields for set string
	    String fieldName = new String();
		String fieldType = new String();
		int count1 = 0;
		for (String key : keyFields) {
			fieldName = key.trim();
 			fieldType = getFieldType(fieldName);
			count1++;
			if (fieldType == null) {
			} else {
				if (count1 > 1) {
					line = "\t\tif (isUseKey" + fieldName + "())";
					WriteJavaSourceLine(line);
				}
				if (count1 == 1) {
					line = "\t\tcheckSql = checkSql + \" Where " + fieldName + "=?\";";
				} else {
					line = "\t\t\tcheckSql = checkSql + \" And " + fieldName + "=?\";";
				}
				WriteJavaSourceLine(line);
			}
 		}
	}
	
	private void SetSelectKeyFields(ArrayList<String> keyFields) {

		String line = new String();
		
		// get all fields for set string
		line = "\t\tint counter = 1;";
		WriteJavaSourceLine(line);
		String fieldName = new String();
		String fieldType = new String();
		int counter = 0;
 		for (String key : keyFields) {
 			int a = key.indexOf(",");
    		if (a > 0) {
    			key = key.substring(0,a);
    		} else {
    			a = key.indexOf("(");
    			if (a > 0) {
    				key = key.substring(0,a);
    			}
    		}
 			fieldName = key.trim();
 			fieldType = getFieldType(fieldName);
			counter++;
			if (fieldType.equals(stringString)) {
				line = "\t\tif (Key" + fieldName + " != \"\") {";
			} else if (fieldType.equals(doubleString)) {
				line = "\t\tif (Key" + fieldName + " != 0.0) {";
			} else {
				line = "\t\tif (Key" + fieldName + " != 0) {";
			}
			if (counter > 1) {
				line = "\t\tif (isUseKey" + fieldName + "())";
				WriteJavaSourceLine(line);
			}
			
			line = "";
			if (fieldType.equals(stringString)) {
				line = "\t\t\tcheckStmt.setString(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(doubleString)) {
				line = "\t\t\tcheckStmt.setDouble(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(intString)) {
				line = "\t\t\tcheckStmt.setInt(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(longString)) {
				line = "\t\t\tcheckStmt.setLong(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(bigintString)) {
				line = "\t\t\tcheckStmt.setLong(counter++, Key" + fieldName + ");";
			}
			WriteJavaSourceLine(line);
			line = "";
 		}
	}

	private void SetSelectStatementKeys(int indent) {
		
		String toTab = new String();
		switch (indent) {
			case 0:
				toTab = "\t\t";
				break;
			case 1:
				toTab = "\t\t\t";
				break;
			case 2:
				toTab = "\t\t\t\t";
				break;
		}

		String line = new String();
		
		// get all fields for set string
		String fieldName = new String();
		String fieldType = new String();
		String one = "1";
 		for (ArrayList<String> element : allFields) {
 			int count2 = 0;
 			boolean isKeyField = false;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 2:
 						fieldName = field;
 						break;
 					case 3:
 						fieldType = field;
 						break;
 					case 6:
						if(field.equals(one)) {
							isKeyField = true;
						}
						break;
 				}
 			}
 			if (isKeyField) {
 				if (fieldType.equals(stringString)) {
					line = toTab + "if (Key" + fieldName + " != \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = toTab + "if (Key" + fieldName + " != 0.00) {";
				} else {
					line = toTab + "if (Key" + fieldName + " != 0) {";
				}
 				WriteJavaSourceLine(line);
	 			if (fieldType.equals(stringString)) {
					line = toTab + "\tcheckStmt.setString(";
				} else if (fieldType.equals(doubleString)) {
					line = toTab + "\tcheckStmt.setDouble(";
				} else if (fieldType.equals(intString)) {
					line = toTab + "\tcheckStmt.setInt(";
				} else if (fieldType.equals(longString)) {
					line = toTab + "\tcheckStmt.setLong(";
				} else if (fieldType.equals(bigintString)) {
					line = toTab + "\ttcheckStmt.setLong(";
				}
	 			line += "counter++, Key" + fieldName + ");";
				WriteJavaSourceLine(line);
				line = toTab + "}";
				WriteJavaSourceLine(line);
 			}
 		}
	}
	
	private void SetOrderBy(String flName, ArrayList<String> keyFields) {
		
		String line = new String();
		String dir = "desc";
		// get all fields for set string
	    String fieldName = new String();
		line = "Order By ";
		int count = 0;
		for (String key : keyFields) {
			fieldName = key.trim();
			count++;
			String direction = getKeyFieldDirection(flName, fieldName);
			if (count == 1) {
				line += fieldName;
			} else {
				line += ", " + fieldName;
			}
			if (direction != null) {
				if (direction.equals(dir)) line += " " + direction;
			}
 		}
 		line = "\t\tcheckSql = checkSql + \" " + line;
 		line += "\";";
 		WriteJavaSourceLine(line);
	}
	
	private String getKeyFieldDirection(String fileName, String fieldName) {
		String direction = new String();
		Map<String, String> keyString = keyFieldListMap.get(fileName);
		if (keyString != null) {
			direction = keyString.get(fieldName);
		} else {
			direction  =	"asc"; 
		}
		return direction;
	}
	
	private void SetOffAllInd(int level) {
		
		String line = new String();
		switch (level) {
			case 1:
				line += "\tsetReadeOK(false);\n";
	            line += "\tsetUpdateOK(false);\n";
	            line += "\tsetRecordFound(false);";
				break;
				
			case 2:
				line += "\t\tsetReadeOK(false);\n";
	            line += "\t\tsetUpdateOK(false);\n";
	            line += "\t\tsetRecordFound(false);";
				break;
				
			case 3:
				line += "\t\t\tsetReadeOK(false);\n";
	            line += "\t\t\tsetUpdateOK(false);\n";
	            line += "\t\t\tsetRecordFound(false);";
				break;
				
			case 4:
				line += "\t\t\t\tsetReadeOK(false);\n";
	            line += "\t\t\t\tsetUpdateOK(false);\n";
	            line += "\t\t\t\tsetRecordFound(false);";
				break;
		}
		WriteJavaSourceLine(line);
	}
	
	private void WriteJavaSourceLine(String line) {
		text.append(line + "\n");
	}
	
	private void CheckFields() {
		
		String line = "//***********Utility Section**************************************************//\n\n";
		
		String message = message4;
		
		line += "\tpublic boolean checkSizeDouble(double field, ";
		line += "int length, int decimal) {\n\n";
		line += "\t\tint leftInt = 0;\n";
		line += "\t\tint leftIntSize;\n";
		line += "\t\tString doubleString, leftString;\n";
		line += "\t\tchar checkString = \'.\';\n\n";
		line += "\t\tdoubleString = String.valueOf(field);\n";
		line += "\t\tint stringLength = doubleString.length();\n";
		line += "\t\tfor (int i = 0; i < stringLength; i++) {\n";
		line += "\t\t\tchar newString = doubleString.charAt(i);\n";
		line += "\t\t\tif (newString == checkString) {\n";
		line += "\t\t\t\tleftInt = i;\n";
		line += "\t\t\t\tbreak;\n";
		line += "\t\t\t}\n";
		line += "\t\t}\n";
		line += "\t\tleftString = doubleString.substring(0, leftInt);\n";
		line += "\t\tleftIntSize = leftString.length();\n\n";
		line += "\t\tif (leftIntSize > leftInt)\n";
		line += "\t\t\treturn false;\n\n";
		line += "\t\t" + "return true;\n";
		line += "\t}\n\n";

		line += "\tpublic boolean checkSizeInt(String fieldName, int field, ";
		line += " int length) {\n\n";
		line += "\t\tdouble testInt;\n";
		line += "\t\tString stringInt = \"\";\n\n";
		line += "\t\tfor (int i = 0; i < length; i++) {\n";
		line += "\t\t\tstringInt = stringInt.concat(\"9\");\n";
		line += "\t\t}\n\n";
		line += "\t\ttestInt = Double.parseDouble(stringInt);\n";
		line += "\t\tif (field > testInt) {\n";
		line += "\t\t\tSystem.err.println(\"In File " + getFileName() + " number field" + message +  " for field \" + fieldName + \" size \" + length + \" value \" + field +\".\");\n";
		line += "\t\t\treturn false;\n";
		line += "\t\t} else return true;\n";
		line += "\t}\n\n";
		
		line += "\tpublic boolean checkSizeLong(String fieldName, Long field,";
		line += " int length) {\n\n";
		line += "\t\tdouble testInt;\n";
		line += "\t\tString stringLong = \"\";\n\n";
		line += "\t\tfor (int i = 0; i < length; i++) {\n";
		line += "\t\t\tstringLong = stringLong.concat(\"9\");\n";
		line += "\t\t}\n\n";
		line += "\t\ttestInt = Double.parseDouble(stringLong);\n";
		line += "\t\tif (field > testInt) {\n";
		line += "\t\t\tSystem.err.println(\"In File " + getFileName() + " number field" + message + " for field \" + fieldName + \" size \" + length + \" value \" + field +\".\");\n";
		line += "\t\t\treturn false;\n";
		line += "\t\t} else return true;\n";
		line += "\t}\n\n";
		
		line += "\tpublic boolean checkSizeString(String fieldName, String field,";
		line += " int length) {\n\n";
		line += "\t\t" + "String overflow;\n\n";
		line += "\t\t" + "if (field.length() < length) return true;\n";
		line += "\t\t" + "overflow = field.substring(length);\n";
		line += "\t\t" + "String trim = overflow.trim();\n";
		line += "\t\t" + "if (!trim.isEmpty()) {\n";
		line += "\t\t\tSystem.err.println(\"In File " + getFileName() + " string field" + message +  " for field \" + fieldName + \" size \" + length + \" value \" + field +\".\");\n";
		line += "\t\t\t" + "return false;\n";
		line += "\t\t} else return true;\n";
		line += "\t}\n\n";

		line += "\tprivate void printSQLWarnings(SQLWarning warning) {\n\n";
		line += "\t\t" + "while (warning != null) {\n\n";
		line += "\t\t\t" + "System.out.println(warning);\n";
		line += "\t\t\t" + "String message = warning.getMessage();\n";
		line += "\t\t\t" + "String sqlState = warning.getSQLState();\n";
		line += "\t\t\tint errorCode = warning.getErrorCode();\n";
		line += "\t\t\tSystem.err.println(message + sqlState";
		line += " + errorCode);\n";
		line += "\t\t\twarning = warning.getNextWarning();\n";
		line += "\t\t" + "}\n";
		line += "\t}\n\n";
		
		line += "\tpublic Boolean getFields() {\n\n";
	    line += "\t\tif (readJSON()) {\n";
	    line += "\t\t\tsetAllFields(getAllFields());\n";
		line += "\t\t\tsetAllKeyFiles(getAllKeyFiles());\n";
		line += "\t\t\tsetAllKeyFields(getAllKeyFields());\n";
		line += "\t\t\tsetAllPhysicalKeyFieldNames(getAllPhysicalKeyFieldNames());\n";
		line += "\t\t\tsetAllLogicalKeyFieldNames(getAllLogicalKeyFieldNames());\n";
		line += "\t\t\tsetAllLogicalKeyFields(getAllLogicalKeyFields());\n";
		line += "\t\t\tsetAllLogicalKeyFieldList(getAllLogicalKeyFieldList());\n";
		line += "\t\t\treturn true;\n";
		line += "\t\t} else {\n";
		line += "\t\t\treturn false;\n";
		line += "\t\t}\n";
	    line += "\t}\n\n";
	    
	    line += "\tpublic Collection<ArrayList<String>> getAllFields() {\n";
		line += "\t\treturn allFields;\n";
		line += "\t}\n\n";

		line += "\tpublic void setAllFields(Collection<ArrayList<String>> allFields) {\n";
		line += "\t\tthis.allFields = allFields;\n";
		line += "\t}\n\n";
		
		line += "\tpublic ArrayList<String> getAllKeyFiles() {\n";
		line += "\t\treturn allKeyFiles;\n";
		line += "\t}\n\n";

		line += "\tpublic void setAllKeyFiles(ArrayList<String> allKeyFiles) {\n";
		line += "\t\tthis.allKeyFiles = allKeyFiles;\n";
		line += "\t}\n\n";
		
		line += "\tpublic Collection<ArrayList<String>> getAllKeyFields() {\n";
		line += "\t\treturn allKeyFields;\n";
		line += "\t}\n\n";;

		line += "\tpublic void setAllKeyFields(Collection<ArrayList<String>> allKeyFields) {\n";
		line += "\t\tthis.allKeyFields = allKeyFields;\n";
		line += "\t}\n";
		
		line += "\tpublic ArrayList<String> getAllLogicalKeyFieldNames() {\n";
		line += "\t\treturn allLogicalKeyFieldNames;\n";
		line += "\t}\n\n";

		line += "\tpublic void setAllLogicalKeyFieldNames(ArrayList<String> allLogicalKeyFieldNames) {\n";
		line += "\t\tthis.allLogicalKeyFieldNames = allLogicalKeyFieldNames;\n";
		line += "\t}\n\n";

		line += "\tpublic Collection<ArrayList<String>> getallLogicalKeyFields() {\n";
		line += "\t\treturn allLogicalKeyFields;\n";
		line += "\t}\n\n";

		line += "\tpublic void setAllLogicalKeyFields(Collection<ArrayList<String>> allLogicalKeyFields) {\n";
		line += "\t\tthis.allLogicalKeyFields = allLogicalKeyFields;\n";
		line += "\t}\n\n";
		
		line += "\tpublic ArrayList<String> getAllPhysicalKeyFieldNames() {\n";
		line += "\t\treturn allPhysicalKeyFieldNames;\n";
		line += "\t}\n\n";

		line += "\tpublic void setAllPhysicalKeyFieldNames(ArrayList<String> allPhysicalKeyFieldNames) {\n";
		line += "\t\tthis.allPhysicalKeyFieldNames = allPhysicalKeyFieldNames;\n";
		line += "\t}\n\n";
		
		line += "\tpublic ArrayList<String> getAllLogicalFieldList() {\n";
		line += "\t\treturn allLogicalFieldList;\n";
		line += "\t}\n\n";

		line += "\tpublic void setAllLogicalFieldList(ArrayList<String> allLogicalFieldList) {\n";
		line += "\t\tthis.allLogicalFieldList = allLogicalFieldList;\n";
		line += "\t}\n\n";
		
		line += "\tpublic Collection<ArrayList<String>> getallLogicalKeyFieldList() {\n";
		line += "\t\treturn allLogicalKeyFieldList;\n";
		line += "\t}\n\n";
		
		line += "\tpublic void setAllLogicalKeyFieldList(Collection<ArrayList<String>> allLogicalKeyFieldList) {\n";
		line += "\t\tthis.allLogicalKeyFieldList = allLogicalKeyFieldList;\n";
		line += "\t}";
		WriteJavaSourceLine(line);
	}	

	private void SetCheckKeyFields() {
		
		String message = message5;
		
		// get all fields for select
		String one = "1";
		for (ArrayList<String> element : allFields) {
			String fieldName = new String();
			String fieldType = new String();
        	boolean isKeyField = false;
			int count = 0;
			for (String field : element) {
				count++;
				switch (count) {
					case 2:
						fieldName = field;
						break;
					case 3:
						fieldType = field;
						break;
					case 6:
						if(field.equals(one)) {
							isKeyField = true;
						}
						break;
				}	
			}
			if (isKeyField) {
				String line = new String();
				if (fieldType == null) {
					System.out.println(getFileName() + " " + allFields);
				}
				if (fieldType.equals(stringString)) {
					line = "\t\tif (Key" + fieldName + " == \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = "\t\tif (Key" + fieldName + " == 0.0) {";
				} else {
					line = "\t\tif (Key" + fieldName + " == 0) {";
				}
			    WriteJavaSourceLine(line);
			    line = "\t\t\tSystem.out.println(\"" + message + "\");";
			    WriteJavaSourceLine(line);
			    line = "\t\t\treturn false;";
			    WriteJavaSourceLine(line);
			    line = "\t\t}";
			    WriteJavaSourceLine(line);
			    WriteBlankLine();
			}
		}
	}
	
	private void SetCheckKeyFields(ArrayList<String> keyFields, int numberOfField) {
		
		// get all fields for select
		int count = 0;
		for (String key : keyFields) {
			count++;
			if (count == 1) {
				String line = new String();
				String fieldType = getFieldType(key.trim());
				if (fieldType == null) {
				} else {
					line = new String();
					if (fieldType.equals(stringString)) {
						line = "\t\tif (Key" + key.trim() + " == \"\") {\n";
					} else if (fieldType.equals(doubleString)) {
						line = "\t\tif (Key" + key.trim() + " == 0.0) {\n";
					} else 
						line = "\t\tif (Key" + key.trim() + " == 0) {\n";
					line += "\t\t\tSystem.err.println(\"" + message5 + "\");\n";
					line += "\t\t\treturn false;\n";
					line += "\t\t}\n\n";
					WriteJavaSourceLine(line);
				}
			}
		}
	}
	
	private void UpdateAllFromResults() {
		
		String line = "//***********Record Set Results Section***************************************//\n\n";

		// write delete
		line += "\tprivate void updateAllFromResults() throws SQLException {\n\n";
		
		// write check for record found
		line += "\t\tif (results == null) return;\n\n";

	
		// get all fields for set string
	    boolean checkForKeyFields = false;
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			String fieldType = new String();
 			String setter = new String();
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 3:
 						fieldType = field;
 						break;
 					case 7:
						setter = field;
 				}
 			}
 			count1++;
			line += "\t\t" + setter + "(";
			if (fieldType.equals(stringString)) {
				line += "results.getString(";
			} else if (fieldType.equals(doubleString)) {
				line += "results.getDouble(";
			} else if (fieldType.equals(intString)) {
				line += "results.getInt(";
			} else if (fieldType.equals(longString)) {
				line += "results.getLong(";
			} else if (fieldType.equals(bigintString)) {
				line += "results.getLong(";
			}
			line += count1 + "));\n";
 		}
	
		if (checkForKeyFields) {
			line += "\t\tsetKeyFields();\n";
		}
		
		// get all save fields for set string
		line += "\n";
 		for (ArrayList<String> element : allFields) {
 			String setter = new String();
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 8:
						setter = field;
 				}
 			}
			line += "\t\t" + setter + "Sav();\n";
 		}
		line += "\t}\n";
		WriteJavaSourceLine(line);
	}

	private void ToString() {
		
		String line = "//***********Print to String Section*******************************************//\n\n";
	    // write to string header
	    line += "\tpublic String toString() {\n";
	    // write to string
	    line += "\t\treturn \"" + getFileName()  + " [";
	
	    // get all fields for set string
	    boolean first = false;
	    boolean newLine = false; 
		String fieldName = new String();
		String fieldNameArray = new String();
		String fieldType = new String();
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 2:
 						fieldName = field;
 						break;
 					case 3:
 						fieldType = field;
 						break;
 				}
 			}
			if (!first) {
				if (fieldType.equals("String")) {
					fieldNameArray = line + fieldName + "=\" + " + fieldName + ".trim()";
				} else {
					fieldNameArray = line + fieldName + "=\" + " + fieldName;
				}
				
				first = true;
			} else {
				if (newLine) {
					newLine = false;
					if (fieldType.equals("String")) {
						fieldNameArray = "\t\t\t\t\t+\", " + fieldName + "=\" + " + fieldName + ".trim()";
					} else {
						fieldNameArray = "\t\t\t\t\t+\", " + fieldName + "=\" + " + fieldName;
					}
					
				} else {
					if (fieldType.equals("String")) {
						fieldNameArray = fieldNameArray + " + \", " + fieldName + "=\" + " + fieldName + ".trim()";
					} else {
						fieldNameArray = fieldNameArray + " + \", " + fieldName + "=\" + " + fieldName;
					}
				}
			}
			count1++;
			if (count1 != numberOfFields) {
				int m = count1%2;
	 			if (m == 0) {
	 				WriteJavaSourceLine(fieldNameArray);
	 				fieldNameArray = new String();
	 				newLine = true;
	 			}
 			}
 		}
 		
 		line = fieldNameArray + " + \"]\";\n";

	    line += "\t}\n";
	    WriteJavaSourceLine(line);
	}

    private void ToStringKeys() {

	    // write to string 
	    String line = "\tpublic String toStringKey() {\n";
	    // write to string
	    line += "\t\treturn \"" + getFileName()  + " [";
		
	    // get all fields for set string
	    String one = "1";
	    boolean first = false;
	    boolean newLine = false; 
		String fieldName = new String();
		String fieldNameArray = new String();
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			boolean isKeyField = false;
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 2:
 						fieldName = field;
 						break;
 					case 6:
 						if(field.equals(one)) {
 							isKeyField = true;
 						}
 				}
 			}
 			if (isKeyField) {
				if (!first) {
					fieldNameArray = line + fieldName + "=\" + " + fieldName;
					first = true;
				} else {
					if (newLine) {
						newLine = false;
						fieldNameArray = "                   + \", " + fieldName + "=\" + " + fieldName;
					} else {
						fieldNameArray = fieldNameArray + " + \", " + fieldName + "=\" + " + fieldName;
					}
				}
				count1++;
				if (count1 != numberOfKeyFields) {
					int m = count1%2;
		 			if (m == 0) {
		 				WriteJavaSourceLine(fieldNameArray);
		 				fieldNameArray = "";
		 				newLine = true;
		 			}
				}
 			}
 		}
 			
 		line = fieldNameArray + " + \"]\";\n";
	
	    line += "\t}\n";
	    WriteJavaSourceLine(line);
    }
    
    public boolean checkForKeyFields() {
		
		// get all fields for set string
    	Collection<ArrayList<String>> fields = getAllFields();
	    boolean isKeyField = false;
		String one = "1";
 		for (ArrayList<String> element : fields) {
 			int count = 0;
 			for (String field : element) {
 				count++;
 				switch (count) {
 					case 6:
 						if(field.equals(one)) {
 							isKeyField = true;
 					}
 				}
 			}
 		}
 		return isKeyField;
	}
    
    private void CloseClass() {

	    String line = "}";
	    WriteJavaSourceLine(line);

	    WriteClass();
	}
    
    public Collection<ArrayList<String>> getFileFields(boolean firstRecord, ResultSet resultsSelect, Collection<ArrayList<String>> field) {
		
    	Collection<String> keyFields = new ArrayList<String>();
    	
    	int nbrOfFields = 0;
		int nbrOfKeyFields = 0;
		String charType = "char";
		String varCharType = "varchar";
		String numericType = "numeric";
		String intType = "int";
		String bigintType = "bigint";
		
		try {
			String fieldName = resultsSelect.getString(2).toLowerCase();
			fieldName = ffc.checkFieldName(fieldName);
	    	int fieldOrder = resultsSelect.getInt(3);
	    	String fieldTypeTest = resultsSelect.getString(4);
			int fieldSize = resultsSelect.getInt(5);
			int decimal = resultsSelect.getInt(6);
			String fieldType = new String();
	    	if (fieldTypeTest.equals(charType) || fieldTypeTest.equals(varCharType)) {
	    		fieldType = "String";
	    		fieldSize = resultsSelect.getInt(5);
	    	} else if (fieldTypeTest.equals(numericType)) {
	    		fieldSize = resultsSelect.getInt(6);
				decimal = resultsSelect.getInt(7);
	    		if (decimal == 0) {
	    			if (fieldSize < 10)
	    				fieldType = "int";
	    			else
	    				fieldType = "long";
	    		} else {
	    			fieldType = "double";
	    		}
	    	} else if (fieldTypeTest.equals(intType)) {
	    		fieldType = "int";
	    	} else if (fieldTypeTest.equals(bigintType)) {
	    		fieldType = "long";
	    	}
	    	// check for first record read
			if (firstRecord) {
				getFileIndexString();
				firstRecord = false;
			}
			String fieldText = new String();	
			keyFields = getAllPhysicalKeyFieldNames();
			Collection<String> fieldList = new ArrayList<String>();
			fieldList.add(FileName);
			fieldList.add(fieldName);
			fieldList.add(fieldType);
			fieldList.add(Integer.toString(fieldSize));
			fieldList.add(Integer.toString(decimal));
			boolean keyFieldInd = false;
			if (keyFields != null) {
				for(String key : keyFields) {
					String newKey = key.trim();
					if (newKey.equals(fieldName)) {
						keyFieldInd = true;
						break;
					}
				}
			}
			String keyField = "0";
			if (keyFieldInd) {
				keyField = "1";
			}
			if (keyField.equals("1")) {
				nbrOfKeyFields = getNumberOfKeyFields() + 1;
				setNumberOfKeyFields(nbrOfKeyFields);
			}
			fieldList.add(keyField);
			fieldList.add("set" + fieldName);
			fieldList.add("get" + fieldName);
			fieldList.add(fieldText);
			field.add((ArrayList<String>) fieldList);
			nbrOfFields = fieldOrder;
			setNumberOfFields(nbrOfFields);
			
			Map<String, Object> currentfield = new HashMap<>();
			currentfield.put("FieldType", fieldType);
			currentfield.put("FieldSize", fieldSize);
			currentfield.put("Decimal", decimal);
			currentfield.put("isKey", keyField);
			if (keyField.equals("1")) {
				Map<String, String> keyDirection = this.keyFieldListMap.get(resultsSelect.getString(1));
				String keyDir = keyDirection.get(fieldName);
				if (keyDir != null) currentfield.put("Direction", keyDir);
				else currentfield.put("Direction", null);
			} else currentfield.put("Direction", null);
			currentfield.put("Setter", "set" + fieldName);
			currentfield.put("Getter", "get" + fieldName);
			newFields.put(fieldName, currentfield);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return field;
	}
    
    public Map<String, Object> getCurrentField(String fldName) {
    	return newFields.get(fldName);
    }
	
	public void getFileIndexString() {

		ArrayList<String> physicalFileKeyField = new ArrayList<String>();
		Collection<ArrayList<String>> logicalFileKeyField = new ArrayList<ArrayList<String>>();
		Collection<String> logicalFieldList = new ArrayList<String>();
		Collection<ArrayList<String>> allLogicalKeyFieldList = new ArrayList<ArrayList<String>>();

		CallableStatement statement;
		try {
			statement = connMSSQL.prepareCall("{call sp_helpindex (?)}");
			statement.setString(1, FileName);
			boolean hadResults = statement.execute();
			if (hadResults) {
				ArrayList<String> keyFieldList = new ArrayList<String>();
				ArrayList<String> allKeyFieldList = new ArrayList<String>();
				ArrayList<String> allPhysicalKeyFieldNames = new ArrayList<String>();
				ArrayList<String> allLogicalKeyFieldNames = new ArrayList<String>();
				ResultSet resultSet = statement.getResultSet();
				Collection<ArrayList<String>> newKeyFieldList = new ArrayList<ArrayList<String>> ();
			    // process result set
			    while (resultSet.next()) {
			    	Map<String, String> keyFldLstMap = new HashMap<>();
			    	String fileName = resultSet.getString(1);
			    	String fields = resultSet.getString(3);
			    	String[] keyFields = fields.split(",");
		    		ArrayList<String> newKeyFileList = new ArrayList<String> ();
		    		newKeyFileList.add(fileName);
			    	for(String kfld : keyFields) {
			    		newKeyFileList.add(kfld);
			    		int a = kfld.indexOf("(");
			    		if (a > 0) {
			    			kfld = kfld.substring(0,a);
			    			keyFieldList.add(kfld);
			    			keyFldLstMap.put(kfld, "desc");
			    		} else {
			    			keyFieldList.add(kfld.trim());
			    			allKeyFieldList.add(kfld.trim());
			    			keyFldLstMap.put(kfld.trim(), "asc");
			    		}
			    		if (FileName.equals(fileName)) {
			    			allPhysicalKeyFieldNames.add(kfld);
			    		} else {
			    			allLogicalKeyFieldNames.add(kfld.trim());
			    		}
			    		keyFieldListMap.put(fileName, keyFldLstMap);
		    		}
			    	newKeyFieldList.add(newKeyFileList);
			    	if (FileName.equals(fileName)) {
			    		setHasKeysInd(true);
				    	setAllPhysicalKeyFieldNames(allPhysicalKeyFieldNames);
				    	physicalFileKeyField.addAll((ArrayList<String>) keyFieldList);
				    	keyFieldList = new ArrayList<String>();
				    	allKeyFieldList = new ArrayList<String>();
			    	} else {
			    		logicalFiles.add(fileName);
			    		hasMultipleKeysInd = true;
			    		setAllLogicalKeyFieldNames(allLogicalKeyFieldNames);
			    		logicalFieldList.add(fileName);
			    		for (String addField : keyFieldList) {
			    			logicalFieldList.add(addField);
			    		}
			    		allLogicalKeyFieldList.add((ArrayList<String>) logicalFieldList);
			    		logicalFileKeyField.add((ArrayList<String>) keyFieldList);
			    		logicalFieldList = new ArrayList<String>();
			    		keyFieldList = new ArrayList<String>();
			    		
			    	}
			    }
			    
			    setAllLogicalKeyFields(logicalFileKeyField);
			    setAllLogicalKeyFieldList(allLogicalKeyFieldList);
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getFieldType(String fieldName) {
		
		Collection<ArrayList<String>>  fields = getAllFields();
		
		String emptyString = new String();
		
		for (ArrayList<String> element : fields) {
			String fldName = new String();
			String fldType = new String();
			int counter = 0;
			for (String field : element) {
				counter++;
				if (counter > 3) break;
				switch (counter) {
					case 2:
						fldName = field;
						break;
					case 3:
						fldType = field;
						break;
				}	
			}
			if (fieldName.equals(fldName)) {
				return fldType;
			}
		}
		return emptyString;
	}
	
	private void WriteClass() {
		String stringOut = new String();
		if (getLongFileName() != null) {
			stringOut = "C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\src\\com\\database\\" + getDataBase() + "\\" + getLongFileName() + ".java"; 
		} else {
			stringOut = "C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\src\\com\\database\\" + getDataBase() + "\\" + getFileName() + ".java"; 			
		}
		try (FileOutputStream out = new FileOutputStream(new File(stringOut))) {

			out.write(text.toString().getBytes());
			text.setLength(0);
			setNumberOfFields(0);
			setNumberOfKeyFields(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Collection<ArrayList<String>> getAllKeyFields() {
		return allKeyFields;
	}

	public void setAllKeyFields(Collection<ArrayList<String>> allKeyFields) {
		this.allKeyFields = allKeyFields;
	}
	
	@SuppressWarnings("unchecked")
	public void buildJSON() {
		
		Collection<ArrayList<String>> allFields = new ArrayList<ArrayList<String>>();
		Collection<ArrayList<String>> allKeyFields = new ArrayList<ArrayList<String>>();
		Collection<ArrayList<String>> allLogicalKeyFieldList = new ArrayList<ArrayList<String>>();
		ArrayList<String> allLogicalKeyFieldNames = new ArrayList<String>();
		Collection<ArrayList<String>> allLogicalKeyFields = new ArrayList<ArrayList<String>>();
		ArrayList<String> allPhysicalKeyFieldNames = new ArrayList<String>();
		
		allFields = getAllFields();
		allKeyFields = getAllKeyFields();
		allLogicalKeyFieldList = getAllLogicalKeyFieldList();
		allLogicalKeyFieldNames = getAllLogicalKeyFieldNames();
		allLogicalKeyFields = getAllLogicalKeyFields();
		allPhysicalKeyFieldNames = getAllPhysicalKeyFieldNames();

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		
		//***********all Fields Section allFields********************************************/
		json.put("FileName", getFileName());

		for (ArrayList<String> elements : allFields) {
			String fileName = new String();
			String fieldName = new String();
			String fieldType = new String();
			int fieldSize = 0;
			int fieldDec = 0;
			Boolean fieldIsKey = false;
			String getter = new String();
			String setter = new String();
			String description = new String();
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
						if (Integer.parseInt(field) == 1) {
							fieldIsKey = true;
						} else fieldIsKey = false;
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
						
				}
			}
			
			JSONObject item = new JSONObject();
			item.put("FileName", fileName);
			item.put("FieldName", fieldName);
			item.put("FieldType", fieldType);
			item.put("FieldSize", fieldSize);
			item.put("FieldDecimal", fieldDec);
			item.put("FieldIsKey", fieldIsKey);
			item.put("FieldSetter", setter);
			item.put("FieldGetter", getter);
			item.put("FieldText", description);
			item.put("LongLibraryName", getLongLibraryName().trim());
			item.put("ShortLibraryName",getShortLibraryName().trim());
			array.add(item);
		}

		json.put("allFields", array);
		
		//***********all Key Fields Section allKeyFields********************************************/
		array = new JSONArray();
		for (ArrayList<String> elements : allKeyFields) {
			String fileName = new String();
			String fieldName = new String();
			String fieldType = new String();
			int fieldSize = 0;
			int fieldDec = 0;
			Boolean fieldIsKey = false;
			String getter = new String();
			String setter = new String();
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
				}
			}
			
			JSONObject item = new JSONObject();
			item.put("FileName", fileName);
			item.put("FieldName", fieldName);
			item.put("FieldType", fieldType);
			item.put("FieldSize", fieldSize);
			item.put("FieldDecimal", fieldDec);
			item.put("FieldIsKey", fieldIsKey);
			item.put("FieldSetter", setter);
			item.put("FieldGetter", getter);
			array.add(item);
		}

		json.put("allKeyFields", array);
		
		//***********all Logical Key Field List Section allKeyFieldList********************************************/
		JSONObject jsonKeys = new JSONObject();
		JSONArray allKeys = new JSONArray();
		JSONArray arrayKeys = new JSONArray();
		for (ArrayList<String> elements : allLogicalKeyFieldList) {
			int count = 0;
			for (String field : elements) {
				count++;
				switch (count) {
					case 1:
						jsonKeys.put("KeyFileName", field);
						break;
					default:
						arrayKeys.add(field);
						break;
				}
			}
			jsonKeys.put("KeyFields", arrayKeys);
			allKeys.add(jsonKeys);
			jsonKeys = new JSONObject();
			arrayKeys = new JSONArray();
		}
		
		json.put("allLogicalKeyFieldList", allKeys);
		
		//***********all logical Key Field Names Section alllogicalkeyfieldnames********************************************/
		arrayKeys = new JSONArray();
		for (String field : allLogicalKeyFieldNames) {
			arrayKeys.add(field);
		}
		json.put("allLogicalKeyFieldNames", arrayKeys);
		
		//***********all logical key fields alllogicalkeyfields********************************************/
		arrayKeys = new JSONArray();
		for (ArrayList<String> field : allLogicalKeyFields) {
			arrayKeys.add(field);
		}
		json.put("allLogicalKeyFields", arrayKeys);
		
		//***********all physical key field names allphysicalkeyfieldnames********************************************
		arrayKeys = new JSONArray();
		for (String field : allPhysicalKeyFieldNames) {
			arrayKeys.add(field);
		}
		json.put("allPhysicalKeyFieldNames", arrayKeys);
		
		String outString = new String();
		if (getLongFileName() != null)
			outString ="C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\json\\" + getDataBase() + "\\" + getLongFileName() + ".json"; 
		else
			outString ="C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\json\\" + getDataBase() + "\\" + getFileName() + ".json"; 
		try (FileOutputStream out = new FileOutputStream(new File(outString))) {
			out.write(json.toString().getBytes());
			text.setLength(0);
			setNumberOfFields(0);
			setNumberOfKeyFields(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean readJSON() {
		
		Collection<ArrayList<String>> allFields = new ArrayList<ArrayList<String>>();
		ArrayList<String> allKeyFiles = new ArrayList<String>();
		Collection<ArrayList<String>> allKeyFields = new ArrayList<ArrayList<String>>();
		ArrayList<String> allPhysicalKeyFieldNames = new ArrayList<String>();
		ArrayList<String> allLogicalKeyFieldNames = new ArrayList<String>();
		Collection<ArrayList<String>> allLogicalKeyFields = new ArrayList<ArrayList<String>>();
		Collection<ArrayList<String>> allLogicalKeyFieldList = new ArrayList<ArrayList<String>>();
		ArrayList<String> allLogicalFieldList = new ArrayList<String>();

		String outString = new String();
		if ((getLongFileName() != null) && (!getLongFileName().isEmpty()))
			outString = "C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\json\\" + getDataBase() + "\\" + getLongFileName().trim() + ".json";
		else
			outString = "C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\json\\" + getDataBase() + "\\" + getFileName().trim() + ".json"; 
		try {
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(outString);
			Object jsonObj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) jsonObj;
			JSONArray jsonArray = new JSONArray();
			jsonArray = (JSONArray) jsonObject.get("allFields");
			int count = jsonArray.size();
			for (int i = 0; i < count; i++) {
				JSONObject obj = new JSONObject();
				obj = (JSONObject) jsonArray.get(i);
				String fileName = (String) obj.get("FileName");
				String fieldName = (String) obj.get("FieldName");
				String fieldType = (String) obj.get("FieldType");
				Long fieldSize = (Long) obj.get("FieldSize");
				Long fieldDecimal = (Long) obj.get("FieldDecimal");
				String fieldGetter = (String) obj.get("FieldGetter");
				Boolean fieldIsKey = (Boolean) obj.get("FieldIsKey");
				String fieldSetter = (String) obj.get("FieldSetter");
				String description = (String) obj.get("FieldText");
				String longLibraryName = (String) obj.get("LongLibraryName");
				String shortLibraryName = (String) obj.get("ShortLibraryName");
				Collection<String> fieldList = new ArrayList<String>();
				fieldList.add(fileName);
				fieldList.add(fieldName);
				fieldList.add(fieldType);
				fieldList.add(Long.toString(fieldSize));
				fieldList.add(Long.toString(fieldDecimal));
				fieldList.add(Boolean.toString(fieldIsKey));
				fieldList.add(fieldSetter);
				fieldList.add(fieldGetter);
				fieldList.add(description);
				fieldList.add(longLibraryName);
				fieldList.add(shortLibraryName);
				allFields.add((ArrayList<String>) fieldList);
			}

			jsonObject = (JSONObject) jsonObj;
			jsonArray = new JSONArray();
			jsonArray = (JSONArray) jsonObject.get("allKeyFields");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = new JSONObject();
				obj = (JSONObject) jsonArray.get(i);
				String fileName = (String) obj.get("FileName");
				String fieldName = (String) obj.get("FieldName");
				String fieldType = (String) obj.get("FieldType");
				Long fieldSize = (Long) obj.get("FieldSize");
				Long fieldDecimal = (Long) obj.get("FieldDecimal");
				String fieldGetter = (String) obj.get("FieldGetter");
				Boolean fieldIsKey = (Boolean) obj.get("FieldIsKey");
				String fieldSetter = (String) obj.get("FieldSetter");
				Collection<String> fieldList = new ArrayList<String>();
				fieldList.add(fileName);
				fieldList.add(fieldName);
				fieldList.add(fieldType);
				fieldList.add(Long.toString(fieldSize));
				fieldList.add(Long.toString(fieldDecimal));
				fieldList.add(Boolean.toString(fieldIsKey));
				fieldList.add(fieldSetter);
				fieldList.add(fieldGetter);
				allKeyFields.add((ArrayList<String>) fieldList);
			}
			
			jsonArray = (JSONArray) jsonObject.get("allLogicalKeyFieldList");
			count = jsonArray.size();
			for (int i = 0; i < count; i++) {
				ArrayList<String> logicalFieldList = new ArrayList<String>();
				JSONObject obj = new JSONObject();
				obj = (JSONObject) jsonArray.get(i);
				String fileName = (String) obj.get("KeyFileName");
				Collection<String> fieldList = new ArrayList<String>();
				fieldList = (Collection<String>) obj.get("KeyFields");
				allKeyFiles.add(fileName);
				allLogicalFieldList.add(fileName);
				logicalFieldList.add(fileName);
				for (String addField : fieldList) {
					allLogicalFieldList.add(addField);
					logicalFieldList.add(addField);
				}
				allLogicalKeyFieldList.add((ArrayList<String>) logicalFieldList);
			}
			
			jsonArray = (JSONArray) jsonObject.get("allPhysicalKeyFieldNames");
			count = jsonArray.size();
			for (int i = 0; i < count; i++) {
				allPhysicalKeyFieldNames.add((String) jsonArray.get(i));
			}

			jsonArray = (JSONArray) jsonObject.get("allLogicalKeyFields");
			count = jsonArray.size();
			for (int i = 0; i < count; i++) {
				ArrayList<String> obj = new JSONArray();
				obj = (ArrayList<String>) jsonArray.get(i);
				allLogicalKeyFields.add(obj);
			}
			
			jsonArray = (JSONArray) jsonObject.get("allLogicalKeyFieldNames");
			count = jsonArray.size();
			for (int i = 0; i < count; i++) {
				allLogicalKeyFieldNames.add((String) jsonArray.get(i));
			}
		} catch (IOException | ParseException e) {
			if ((getLongFileName() != null) && !getLongFileName().isEmpty()) {
				System.out.println("Class not created for file " + getLongFileName());
				return false;
			} else {
				System.out.println("Class not created for file " + getFileName());
				return false;
			}
		}
		
		setAllFields(allFields);
		setAllKeyFiles(allKeyFiles);
		setAllKeyFields(allKeyFields);
		setAllPhysicalKeyFieldNames(allPhysicalKeyFieldNames);
		setAllLogicalKeyFieldNames(allLogicalKeyFieldNames);
		setAllLogicalKeyFields(allLogicalKeyFields);
		setAllLogicalFieldList(allLogicalFieldList);
		setAllLogicalKeyFieldList(allLogicalKeyFieldList);
		return true;
	}
	
	private void SetResultLines(boolean readEqual) {

        // write select
		String line = "\t\t\tupdateAllFromResults();\n";
        line += "\t\t\tsetRecordFound(true);\n";
        line += "\t\t\tsetUpdateOK(true);\n";
        if (readEqual) {
        	line += "\t\t\tsetKeyFields();\n";
        	line += "\t\t\tsetReadeOK(true);\n";
        }
        line += "\t\t\treturn true;\n";
        line += "\t\t} else {";
        WriteJavaSourceLine(line);
        
        SetOffAllInd(3);

        line = "\t\t\treturn false;\n";
        line += "\t\t}";
        WriteJavaSourceLine(line);
	}
	
	@SuppressWarnings("unused")
	public int getRecordCount(String company, String data, String file, String fileInputStream) {
		
		int counterTotal = 0;
		try (BufferedReader in = new BufferedReader(new
		//  InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + data + "\\" + file + ".csv"), "UTF-8"))) {
			InputStreamReader(new FileInputStream(fileInputStream), "UTF-8"))) {
			String line = new String();
			while ((line  = in.readLine()) != null ) {
				counterTotal += 1;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return counterTotal;
	}
}