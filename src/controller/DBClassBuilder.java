package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.MsSQL;

public class DBClassBuilder {
	
	static StringBuilder text = new StringBuilder();

	static String message2 = "Some fields were not updated properly.";
	static String message3 = "Record not retrieved.";
	static String message4 = " to long for field size";
	static String message5 = "Key fields must be set.";
	static String stringString = "String";
	static String doubleString = "double";
	static String intString = "int";
	static String longString = "long";
	static String bigintString = "BigInteger";
	static String tableString = "BASE TABLE";
	
	private Collection<ArrayList<String>> allFields = new ArrayList<ArrayList<String>>();
	private Map<String, Map<String, Object>> newFields = new HashMap<>();
	private ArrayList<String> allPhysicalKeyFields = new ArrayList<String>();
	private Collection<ArrayList<String>> allLogicalKeyFields = new ArrayList<ArrayList<String>>();
	private Map<String, Map<String, String>> keyFieldListMap = new HashMap<>();
	
	private ArrayList<String> logicalFiles = new ArrayList<String>();
	private Connection conn;
	private int numberOfFields;
	private int numberOfKeyFields;
	private String companyName;
	private String dataBase;
	private String FileName;
	private String tableType;
	private boolean hasKeysInd;
	private boolean hasMultipleKeysInd;

	public static void setText(StringBuilder text) {
		DBClassBuilder.text = text;
	}

	public Collection<ArrayList<String>> getAllFields() {
		return allFields;
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

	public ArrayList<String> getAllPhysicalKeyFields() {
		return allPhysicalKeyFields;
	}

	public void setAllPhysicalKeyFields(ArrayList<String> allPhysicalKeyFields) {
		this.allPhysicalKeyFields = allPhysicalKeyFields;
	}

	public Collection<ArrayList<String>> getAllLogicalKeyFields() {
		return allLogicalKeyFields;
	}

	public void setAllLogicalKeyFields(Collection<ArrayList<String>> allLogicalKeyFields) {
		this.allLogicalKeyFields = allLogicalKeyFields;
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

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
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

	public void executeAllfunctions() {
		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(companyName);
		String selectSql = "SELECT TABLE_TYPE FROM " + this.companyName + ".INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?";
		try {
			connMSSQL = dbMSSQL.connect();
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
			checkStmtSelect.setString(1, FileName);
			ResultSet resultsSelect = checkStmtSelect.executeQuery();
			resultsSelect.next();
			setTableType(resultsSelect.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    
		GetFileInfo();
		PrivateSection();
		Constructor();
		GetterSetter();
        if (getNumberOfKeyFields() > 0) {
        	SetKeyFields();
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
    		String line = "//***********File Read Equal Section******************************************//";
    		WriteJavaSourceLine(line);
    		WriteBlankLine();
        	ReadEqualFirst(getFileName(), getAllPhysicalKeyFields());
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
        	ReadEqualLast(getFileName(), getAllPhysicalKeyFields());
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
	}
	
	private void GetFileInfo() {
		
		String line = "package com." + getCompanyName() + ".database." + getDataBase() + ";\n";
		WriteJavaSourceLine(line);

        // write class heading
        line = "import java.sql.Connection;";
        WriteJavaSourceLine(line);
        line = "import java.sql.Statement;";
        WriteJavaSourceLine(line);
        if ((tableType.equals(tableString)) || (getNumberOfKeyFields() > 0)) {
        	line = "import java.sql.PreparedStatement;";
        	WriteJavaSourceLine(line);
        }
        line = "import java.sql.ResultSet;";
        WriteJavaSourceLine(line);
        line = "import java.sql.SQLException;";
        WriteJavaSourceLine(line);
        line = "import java.sql.SQLWarning;";
        WriteJavaSourceLine(line);
        WriteBlankLine();

        // write class heading
        line = "public class " + getFileName() + " {";
        WriteJavaSourceLine(line);
	}
	
	private void PrivateSection() {
		WriteBlankLine();
		
		String line = "//***********Field Definition Section*****************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

		// define file fields
		String fieldName = new String();
		String fieldType = new String();
		for (ArrayList<String> element : allFields) {
			line = "   private";
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
				}
			}
			line = line + " " + fieldType + " " + fieldName + ";";
			WriteJavaSourceLine(line);
		}
		
		WriteBlankLine();

		// define file save fields
		fieldName = new String();
		fieldType = new String();
		for (ArrayList<String> element : allFields) {
			line = "   private";
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
				}
			}
			line = line + " " + fieldType + " " + fieldName + "Sav;";
			WriteJavaSourceLine(line);
		}
		
		WriteBlankLine();
		
		// define key fields
		ArrayList<String> keysSoFar = new ArrayList<String>();
		fieldName = new String();
		String one = "1";
		for (ArrayList<String> element : allFields) {
			line = "   private";
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
				case 1:
					fieldName = field;
					break;
				case 2:
					fieldType = field;
					break;
				case 5:
					if (field.equals(one)) {
						line = line + " " + fieldType + " Key" + fieldName + ";";
						WriteJavaSourceLine(line);
						keysSoFar.add(fieldName);
					}
				}
			}
		}

		Collection<ArrayList<String>> keyFields = getAllLogicalKeyFields();	
		for (ArrayList<String> keys : keyFields) {
			for (String key : keys) {
				if (!keysSoFar.contains(key.trim())) {
					line = "   private " + getFieldType(key.trim()) + " Key" + key.trim() + ";";
					WriteJavaSourceLine(line);
					keysSoFar.add(key.trim());
				}
			}
		}
		
		WriteBlankLine();
		
		line = "   private Connection conn;";
		WriteJavaSourceLine(line);
		line = "   private ResultSet results;";
		WriteJavaSourceLine(line);
		if ((tableType.equals(tableString)) || (getNumberOfKeyFields() > 0)) {
			line = "   private PreparedStatement checkStmt;";
			WriteJavaSourceLine(line);
		}

		WriteBlankLine();
		
		line = "   private boolean recordFound;";
		WriteJavaSourceLine(line);
		line = "   private boolean updateOK;";
		WriteJavaSourceLine(line);
		line = "   private boolean readeOK;";
		WriteJavaSourceLine(line);
		line = "   private boolean supressErrorMsg;";
		WriteJavaSourceLine(line);
	}
	
	private void Constructor() {
		
		WriteBlankLine();
		
		String line = "//***********Constructor Section**********************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		
		// write class heading
        line = "   public " + getFileName() + "() {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      super();";
        WriteJavaSourceLine(line);
		WriteBlankLine();

        line = "      setsupressErrorMsg(true);";
        WriteJavaSourceLine(line);
        WriteBlankLine();
        
		// setters for all file fields
		String fieldType = new String();
		String setter = new String();
		for (ArrayList<String> element : allFields) {
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 2:
						fieldType = field;
						break;
					case 6:
						setter = field;
						break;
				}
			}
			if (fieldType.equals(stringString)) {
				line = "      " + setter + "(\"\");";
			} else if (fieldType.equals(doubleString)) {
				line = "      " + setter + "(0.0);";
			} else {
				line = "      " + setter + "(0);";
			}
			WriteJavaSourceLine(line);	
		}

		WriteBlankLine();
		
		// setters for all file save fields
		for (ArrayList<String> element : allFields) {
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 2:
						fieldType = field;
						break;
					case 6:
						setter = field;
						break;
				}
			}
			line = "\t\t" + setter + "Sav();";
			WriteJavaSourceLine(line);
		}
		
		WriteBlankLine();
		
		// setters for all file key fields
		String fieldName = new String();
		String one = "1";
		for (ArrayList<String> element : allFields) {
			int counter = 0;
			boolean isKeyField = false;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
					case 5:
						if (field.equals(one)) {
							isKeyField = true;
						}
						break;
					case 6:
						setter = field;
						break;
				}
			}
			if (isKeyField) {
				line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(get" + fieldName + "());";
				WriteJavaSourceLine(line);
			}
		}

		WriteBlankLine();
		
        line = "      setsupressErrorMsg(false);";
        WriteJavaSourceLine(line);
		
		SetOffAllInd(2);

        // Close bracket
        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
        
        // 255 java parameter limit
        if ((getNumberOfFields() * 2) <= 255) {
			// write class heading
	        line = "   public " + getFileName() + "(";
	        fieldType = new String();
	  
	        int count1 = 0;
	        for (ArrayList<String> element : allFields) {
	        	count1++;
				int count2 = 0;
				for (String field : element) {
					count2++;
					switch (count2) {
						case 1:
							fieldName = field;
							break;
						case 2:
							fieldType = field;
							break;
					}
				}
				if (count1 == 1) {
					line = line + fieldType + " " + fieldName;
				} else {
					line = line + ", " + fieldType + " " + fieldName;
				}
				int m = count1%4;
	 			if (count1 == numberOfFields) {
						line = line + ") {";
		 				WriteJavaSourceLine(line);
		 				line = "";
				} else {
					if (m == 0) {
	 	 				WriteJavaSourceLine(line);
	 	 				line = "                 ";
	 				}
	 			}
			}
	        
	        WriteBlankLine();
	
	        // setters for all file fields
	        count1 = 0;
	        setter = new String();
	        for (ArrayList<String> element : allFields) {
	        	count1++;
	        	
				int count2 = 0;
				for (String field : element) {
					count2++;
					switch (count2) {
						case 1:
							fieldName = field;
							break;
						case 6:
							setter = field;
							break;
					}
				}
				line = "      " + setter + "(" + fieldName + ");";
				WriteJavaSourceLine(line);
			}
	        
	        WriteBlankLine();
	        
	        // write setters for save fields
	        count1 = 0;
	        for (ArrayList<String> element : allFields) {
	        	count1++;
				int count2 = 0;
				for (String field : element) {
					count2++;
					switch (count2) {
						case 1:
							fieldName = field;
							break;
						case 2:
							fieldType = field;
							break;
						case 6:
							line = "      " + field + "Sav();";
							break;
					}
				}
				WriteJavaSourceLine(line);
			}
			
	        WriteBlankLine();
	        
	        // setters for all file key fields
	 		fieldName = new String();
	 		one = "1";
	 		for (ArrayList<String> element : allFields) {
	 			int counter = 0;
	 			boolean isKeyField = false;
	 			for (String field : element) {
	 				counter++;
	 				switch (counter) {
						case 1:
							fieldName = field;
							break;
						case 2:
							fieldType = field;
							break;
						case 5:
							if (field.equals(one)) {
								isKeyField = true;
							}
							break;
						case 6:
							setter = field;
							break;
	 				}
	 			}
	 			if (isKeyField) {
	 				line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(get" + fieldName + "());";
	 				WriteJavaSourceLine(line);
				}	 			
	 		}
	     		
	     	WriteBlankLine();
	        
			SetOffAllInd(2);

			// Close bracket
			line = "   }";
			WriteJavaSourceLine(line);
        }
	}
	
	private void GetterSetter() {
		
		WriteBlankLine();
		
		String line = "//***********Getter Setter Section********************************************//";
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
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
					case 3:
						fieldSize = Integer.parseInt(field);
						break;
					case 4:
						fieldDec = Integer.parseInt(field);
						break;
					case 6:
						setter = field;
						break;
					case 7:
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
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
					case 3:
						fieldSize = Integer.parseInt(field);
						break;
					case 4:
						fieldDec = Integer.parseInt(field);
						break;
					case 6:
						setter = field;
						break;
					case 7:
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
        String one = "1";
        for (ArrayList<String> element : allFields) {
        	boolean isKeyField = false;
			int count2 = 0;
			for (String field : element) {
				count2++;
				switch (count2) {
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
					case 3:
						fieldSize = Integer.parseInt(field);
						break;
					case 4:
						fieldDec = Integer.parseInt(field);
						break;
					case 5:
						if(field.equals(one)) {
							isKeyField = true;
						}
						break;
					case 6:
						setter = field;
						break;
					case 7:
						getter = field;
						break;
				}
			}
			
			if (isKeyField) {
				Getter("public", "Key" + fieldName, fieldType, getter.substring(0, 3) + "Key" + getter.substring(3));
				Setter("public", "Key" + fieldName, fieldType, setter.substring(0, 3) + "Key" + setter.substring(3), fieldSize, fieldDec, false);
				keysSoFar.add(fieldName);
			}
			
			
		}
        
        Collection<ArrayList<String>> keyFields = getAllLogicalKeyFields();	
		for (ArrayList<String> keys : keyFields) {
			for (String key : keys) {
				if (!keysSoFar.contains(key.trim())) {
					fieldName = key.trim();
					fieldType = getFieldType(key.trim());
					String getterName = "getKey" + fieldName;
					String setterName = "setKey" + fieldName;
					Getter("public", "Key" + fieldName, fieldType, getterName);
					Setter("public", "Key" + fieldName, fieldType, setterName, fieldSize, fieldDec, false);
					keysSoFar.add(key.trim());
				}
			}
		}
		
        Getter("public", "conn", "Connection", "getConn");
		Setter("public", "conn", "Connection", "setConn", 0, 0, false);
        
        Getter("private", "recordFound", "boolean", "getRecordFound");
        Setter("private", "recordFound", "boolean", "setRecordFound", 0, 0, false);

        Getter("public", "updateOK", "boolean", "getUpdateOK");
        Setter("public", "updateOK", "boolean", "setUpdateOK", 0, 0, false);

        Getter("public", "readeOK", "boolean", "getReadeOK");
        Setter("public", "readeOK", "boolean", "setReadeOK", 0, 0, false);

        Setter("public", "supressErrorMsg", "boolean", "setsupressErrorMsg", 0, 0, false);

        WriteBlankLine();
	}
	
	private void Getter(String access, String field, String type, String getterName) {
		
		WriteBlankLine();
		// write getter
		String line = "   " + access + " " + type + " " + getterName + "() {";
		WriteJavaSourceLine(line);

		line = "      return this." + field + ";";
		WriteJavaSourceLine(line);

		line = "   }";
		WriteJavaSourceLine(line);
	}
	
	private void Setter(String access, String field, String type, String setterName, long length, int dec, boolean setind) {
		
		WriteBlankLine();
        // Check for set off read ind.
		if (setind) {
           SetOffAllInd(2);
		}

		// write getter
		String line = "   " + access + " void " + setterName + "(" + type + " " + field + ") {";
		WriteJavaSourceLine(line);
		
		String booleanString = "boolean";
		String connectionString = "Connection";
		if (type.equals(doubleString)) {
			line = "      this." + field + " = 0.0;";
            WriteJavaSourceLine(line);
            line = "      if (" + field + " != 0) {";
            WriteJavaSourceLine(line);
            line = "         int fldLength = " + length + ";";
            WriteJavaSourceLine(line);
            line = "         int decimal = " + dec + ";";
            WriteJavaSourceLine(line);
            line = "         if (checkSizeDouble(" + field + ", fldLength, decimal)) {";
            WriteJavaSourceLine(line);
            line = "            String doubleString = String.format";
            line = line + "(\"%." + dec + "f\",";
            line = line + " " + field + ");";
            WriteJavaSourceLine(line);
            line = "            " + field;
            line = "            " + "this." + field
                  + " = Double.parseDouble(doubleString);";
            WriteJavaSourceLine(line);
            line = "         } else {";
            WriteJavaSourceLine(line);
            line = "            if (!supressErrorMsg) {";
            WriteJavaSourceLine(line);
            line = "               System.out.println(\"Field " + field;
            line = line + ": not updated properly.";
            line = line + " " + field + " = \" +";
            line = line + " " + field + ");";
            WriteJavaSourceLine(line);
            line = "            }";
            WriteJavaSourceLine(line);
            line = "            setUpdateOK(false);";
            WriteJavaSourceLine(line);
            line = "         }";
            WriteJavaSourceLine(line);
            line = "\t\t}";
            WriteJavaSourceLine(line);
		} else if (type.equals(intString)) {
			line = "\tthis." + field + " = 0;";
            WriteJavaSourceLine(line);
            line = "\tif (" + field + " != 0) {";
            WriteJavaSourceLine(line);
            line = "\t\tint fldlength = " + length + ";";
            WriteJavaSourceLine(line);
            line = "\t\tif (checkSizeInt(\"" + field + "\", " + field + ", fldlength))";
            WriteJavaSourceLine(line);
            line = "\t\t\tthis." + field + " = " + field + ";";
            WriteJavaSourceLine(line);
            line = "\t\t} else {";
            WriteJavaSourceLine(line);
            line = "\t\t\tif (!supressErrorMsg) {";
            WriteJavaSourceLine(line);
            line = "\t\t\t\tSystem.out.println(\"Field " + field;
            line = line + ": not updated properly in file.";
            line = line + " " + field + " = \" +";
            line = line + " " + field + ");";
            WriteJavaSourceLine(line);
            line = "\t\t\t}";
            WriteJavaSourceLine(line);
            line = "\t\t\tsetUpdateOK(false);";
            WriteJavaSourceLine(line);
            line = "\t\t}";
            WriteJavaSourceLine(line);
		} else if (type.equals(longString)) {
			line = "\tthis." + field + " = 0;";
            WriteJavaSourceLine(line);
            line = "\tif (" + field + " != 0) {";
            WriteJavaSourceLine(line);
            line = "\t\tint fldlength = " + length + ";";
            WriteJavaSourceLine(line);
            line = "\t\tif (checkSizeLong(\"" + field + "\", " + field + ", fldlength))";
            WriteJavaSourceLine(line);
            line = "\t\t\tthis." + field + " = " + field + ";";
            WriteJavaSourceLine(line);
            line = "\t\t} else {";
            WriteJavaSourceLine(line);
            line = "\t\t\tif (!supressErrorMsg) {";
            WriteJavaSourceLine(line);
            line = "\t\t\t\tSystem.out.println(\"Field " + field;
            line = line + ": not updated properly in file.";
            line = line + " " + field + " = \" +";
            line = line + " " + field + ");";
            WriteJavaSourceLine(line);
            line = "\t\t\t}";
            WriteJavaSourceLine(line);
            line = "\t\t\t\tsetUpdateOK(false);";
            WriteJavaSourceLine(line);
            line = "\t\t\t}";
            WriteJavaSourceLine(line);
            
		} else if (type.equals(bigintString)) {
    			line = "   this." + field + " = 0;";
                WriteJavaSourceLine(line);
                line = "   if (" + field + " != 0) {";
                WriteJavaSourceLine(line);
                line = "      int fldlength = " + length + ";";
                WriteJavaSourceLine(line);
                line = "      if (checkSizeBigInt(" + field + ", fldlength))";
                WriteJavaSourceLine(line);
                line = "         this." + field + " = " + field + ";";
                WriteJavaSourceLine(line);
                line = "      } else {";
                WriteJavaSourceLine(line);
                line = "         if (!supressErrorMsg) {";
                WriteJavaSourceLine(line);
                line = "            System.out.println(\"Field " + field;
                line = line + ": not updated properly in file.";
                line = line + " " + field + " = \" +";
                line = line + " " + field + ");";
                WriteJavaSourceLine(line);
                line = "         }";
                WriteJavaSourceLine(line);
                line = "         setUpdateOK(false);";
                WriteJavaSourceLine(line);
                line = "      }";
                WriteJavaSourceLine(line);
		} else if (type.equals(stringString)) {
			line = "\tthis." + field + " = \"\";";
            WriteJavaSourceLine(line);
            line = "\tif (" + field + " != \"\") {";
            WriteJavaSourceLine(line);
            line = "\t\tint fldlength = " + length + ";";
            WriteJavaSourceLine(line);
            line = "\t\tif (checkSizeString(\"" + field + "\", " + field + ", fldlength))";
            WriteJavaSourceLine(line);
            line = "\t\t\tthis." + field + " = " + field + ";";
            WriteJavaSourceLine(line);
            line = "\t\t} else {";
            WriteJavaSourceLine(line);
            line = "\t\t\tif (!supressErrorMsg) {";
            WriteJavaSourceLine(line);
            line = "\t\t\t\tSystem.out.println(\"Field " + field;
            line = line + ": not updated properly.";
            line = line + " " + field + " = \" +";
            line = line + " " + field + ");";
            WriteJavaSourceLine(line);
            line = "\t\t\t}";
            WriteJavaSourceLine(line);
            line = "\t\t\tsetUpdateOK(false);";
            WriteJavaSourceLine(line);
            line = "\t\t}";
            WriteJavaSourceLine(line);
		} else if (type.equals(booleanString)) {
			line = "      this." + field + " = " + field + ";";
			WriteJavaSourceLine(line);
		}	else if (type.equals(connectionString)) {
			line = "      this." + field + " = " + field + ";";
			WriteJavaSourceLine(line);
		}
		line = "   }";
        WriteJavaSourceLine(line);
	}
	
	private void SetterSav(String field, String type, String setterName, String getterName) {
		
		WriteBlankLine();
		// write setter
		String line = "   private void " + setterName + "() {";
		WriteJavaSourceLine(line);

		line = "      this." + field + " = " + getterName + "();";
		WriteJavaSourceLine(line);

		line = "   }";
		WriteJavaSourceLine(line);
	}
	
	private void WriteBlankLine() {
		text.append("\n");
	}

	private void SetKeyFields() {
		
		String line = "//***********Set Key FieldS Section*******************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

		line = new String();
	    // write set key
	    line = "   private void setKeyFields() {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // read through array list to create all key fields
		for (ArrayList<String> element : allFields) {
			String setter = new String();
			String getter = new String();
			String one = "1";
	     	boolean isKeyField = false;
			int count = 0;
			for (String field : element) {
				count++;
				switch (count) {
					case 5:
						if(field.equals(one)) {
							isKeyField = true;
							break;
						}
					case 6:
						setter = field;
						break;
					case 7:
						getter = field;
						break;
				}	
			}
			if (isKeyField) {
				line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(" + getter + "());";
			    WriteJavaSourceLine(line);
			}
		}
	
	    line = "   }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	    
	    for (int i = 0; i < numberOfKeyFields; i++) {
		    // write select
		    line = "   public void setKeyFields(";
		    // read through array list to create all key fields
		    int count1 = 0;
		    boolean keysFilled = false;
	 		for (ArrayList<String> element : allFields) {
	 			if (keysFilled) continue;
				String fieldName = new String();
				String fieldType = new String();
				String one = "1";
		     	boolean isKeyField = false;
				int count = 0;
				for (String field : element) {
					count++;
					switch (count) {
						case 1:
							fieldName = field;
							break;
						case 2:
							fieldType = field;
							break;
						case 5:
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
						line = line + fieldType + " Key" + fieldName;
					} else {
						line = line + ", " + fieldType + " Key" + fieldName;
					}
				}
			}
	 		line = line + ") {";
	 		WriteJavaSourceLine(line);
	
		    WriteBlankLine();
		    count1 = 0;
		    keysFilled = false;
			boolean errorSupressInd = false;
		    // read through array list to create all key fields
	 		for (ArrayList<String> element : allFields) {
	 			String fieldName = new String();
	 			String fieldType = new String();
	 			String setter = new String();
	 			String one = "1";
	 	     	boolean isKeyField = false;
	 			int count = 0;
	 			for (String field : element) {
	 				count++;
	 				switch (count) {
	 					case 1:
	 						fieldName = field;
	 						break;
	 					case 2:
	 						fieldType = field;
	 						break;
	 					case 5:
	 						if(field.equals(one)) {
	 							isKeyField = true;
	 						}
	 						break;
	 					case 6:
	 						setter = field;
	 						break;
	 				}	
	 			}
	 			if (isKeyField) {
	 				count1++;
	 				if (keysFilled) {
	 					if (fieldType.equals(stringString)) {
	 						line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(\"\");";
	 					} else if (fieldType.equals(doubleString)) {
	 						line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(0.0);";
	 					} else {
	 						line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(0);";
	 					}
	 				} else {
	 					line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(Key" + fieldName + ");";
	 				}
	 			    WriteJavaSourceLine(line);
	 			    if (count1 == i+1) {
	 			    	keysFilled = true;
	 			    	if (count1 < numberOfKeyFields) {
	 			    		line = "      setsupressErrorMsg(true);";
	 			    		WriteJavaSourceLine(line);
	 			    		errorSupressInd = true;
	 			    	}
	 			    }
	 			}
	 		}
	 	
	 		if (errorSupressInd) {
	 			line = "      setsupressErrorMsg(false);";
		    		WriteJavaSourceLine(line);
	 		}
	 		
		    line = "   }";
		    WriteJavaSourceLine(line);
		    WriteBlankLine();
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
					String line = "   public void setKeyFields" + element + "(";
					int count1 = 0;
					for (String key : keyFields) {
						if (keysFilled) continue;
						count1++;
						String type = getFieldType(key.trim());
						if (count1 == 1) {
							line = line + type + " " + key.trim();
							if (count1 == i+1) {
			 			    	keysFilled = true;
			 			    }
						} else {
							if (count1 == i+1) {
			 			    	keysFilled = true;
			 			    }
							int m = count1%4;
							if (m == 0) {
								if (keysFilled) line = line + ", " + type + " " + key.trim() + ") {";
								else line = line + ", " + type + " " + key.trim();
								WriteJavaSourceLine(line);
								line = "";
							} else if (m == 1) {
								line = "                                  , "  + type + " "+ key.trim();
							} else {
								line = line + ", " + type + " " + key.trim();
							}
						}
						
					}
					
					if (!line.isEmpty()) line = line + ") {";
					WriteJavaSourceLine(line);
					keysFilled = false;
					count1 = 0;
					for (String key : keyFields) {
						count1++;
						if (!keysFilled) {
							String setter = "set" + key.trim();
							line = "      " + setter.substring(0, 3) + "Key" + setter.substring(3) + "(" + key.trim() + ");";
						    WriteJavaSourceLine(line);
						}
					    if (count1 == i+1) keysFilled = true;
					}
					line = "   }";
					WriteJavaSourceLine(line);
					WriteBlankLine();
				}
			}
		}
	}
	
	private void GetRecord() {
		
		String line = "//***********Get A Record Section*********************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

	    // write add
	    line = "   public boolean get() throws SQLException {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    SetOffAllInd(2);
	
	    WriteBlankLine();
	
	    line = "      " + "if (conn == null) return false;";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    SetCheckKeyFields();
	
	    // build select statement
	    line = "      String checkSql = \"select * from " + getFileName() + " \";";
	    WriteJavaSourceLine(line);
	    
	    WriteBlankLine();
        
        line = "      int counter = 1;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

	    SetWhereFields();
	    
		WriteBlankLine();

	    // write select
	    line = "      " + "checkStmt =";
	    line = line + " conn.prepareStatement(checkSql);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();

	    SetSelectStatementKeys(0);
	    
	    WriteBlankLine();
	
	    // write select
	    line = "      " + "results = checkStmt.executeQuery();";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select
	    line = "      " + "SQLWarning warning = results.getWarnings();";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      " + "printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      " + "warning = checkStmt.getWarnings();";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      " + "printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write read results
	    line = "      " + "if (results.next()) {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select
	    line = "         " + "updateAllFromResults();";
	    WriteJavaSourceLine(line);
	
		 if (checkForKeyFields()) {
			 line = "         " + "setKeyFields();";
			 WriteJavaSourceLine(line);
		 }
	
	    line = "         " + "setRecordFound(true);";
	    WriteJavaSourceLine(line);
	
	    line = "         " + "setUpdateOK(true);";
	    WriteJavaSourceLine(line);
	
	    line = "         " + "return true;";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    line = "      " +  "} else {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    SetOffAllInd(3);
	
	    line = "         System.out.println(\"" + message3 + "\");";
	    WriteJavaSourceLine(line);
	
	    line = "         " + "return false;";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    line = "      " +  "}";
	    WriteJavaSourceLine(line);
	
	    line = "   " +  "}";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();

	}
	
	private void Exists() {
		
		String line = "//***********Check Existence of a Record Section******************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

	    // write add
	    line = "   public boolean exists() throws SQLException {";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      int numberOfRecords;";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    SetOffAllInd(2);
	
	    WriteBlankLine();
	
	    line = "      if (conn == null) return false;";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    SetCheckKeyFields();
	
	    // write select
	    line = "      String checkSql = \"select count(*) as numberOfRecords from " + getFileName() + " \";";
	    WriteJavaSourceLine(line);
	
	    SetWhereFields();
	
	    WriteBlankLine();
	
	    // write select
	    line = "      " + "checkStmt =";
	    line = line + " conn.prepareStatement(checkSql);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
        
        line = "      int counter = 1;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

	    SetSelectStatementKeys(0);
	
	    WriteBlankLine();
	
	    // write select
	    line = "      " + "results = checkStmt.executeQuery();";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select
	    line = "      " + "SQLWarning warning = results.getWarnings();";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      " + "printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      " + "warning = checkStmt.getWarnings();";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      " + "printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select get
	    line = "      " + "results.next();";
	    WriteJavaSourceLine(line);
	
	    line = "      " + "numberOfRecords = results.getInt(1);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    line = "      " + "if (numberOfRecords > 0) return true;";
	    WriteJavaSourceLine(line);
	    line = "      " + "else return false;";
	    WriteJavaSourceLine(line);
	    
	    line = "   }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	}
	
	private void AddRecord() {
		
		String line = "//***********File Updating Section********************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

	    // write add
	    line = "   " + "public boolean add() throws SQLException {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    line = "      " + "setReadeOK(false);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write check for record found
	    line = "      if (!getUpdateOK()) {";
	    WriteJavaSourceLine(line);
	    line = "         System.out.println(\"" + message2 + "\");";
	    WriteJavaSourceLine(line);
	    line = "         return false;";
	    WriteJavaSourceLine(line);
	    line = "      }";
	    WriteJavaSourceLine(line);

	    WriteBlankLine();

	    // write insert string
	    line = "      String checkSql = \"insert into " + getFileName() + " (";
	    
	    // get all fields for insert string
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			String fieldName = new String();
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 1:
 						fieldName = field;
 						break;
 				}
 			}
 			
 			count1++;
 			if (count1 == 1) {
 				line = line + fieldName;
 			} else {
 				if (line.isEmpty()) {
 					line = "                        					\", " + fieldName;
 				} else {
 					line = line + ", " + fieldName;
 				}
 			}
 			
 			int m = count1%5;
 			if (count1 == numberOfFields) {
					line = line + ") \" +";
	 				WriteJavaSourceLine(line);
	 				line = "";
			} else {
				if (m == 0) {
 					line = line + "\" +";
 	 				WriteJavaSourceLine(line);
 	 				line = "";
 				}
 			}
 		}
 		
 		// setup where clause
        line = "					    \"values (";
 		for (int i = 0; i < count1; i++) {
 			if (i == 0) line = line + "?";
 			else line = line + ", ?";
 		}
 		line = line + ");\";";
        WriteJavaSourceLine(line);
        
	    WriteBlankLine();
	    
	    // write check statement
        line = "      checkStmt = conn.prepareStatement(checkSql);";
        WriteJavaSourceLine(line);

        WriteBlankLine();
        
        // get all fields for insert string
        count1 = 0;
  		for (ArrayList<String> element : allFields) {
  			String fieldName = new String();
  			String fieldType = new String();
  			int count = 0;
  			for (String field : element) {
  				count++;
  				switch (count) {
  					case 1:
  						fieldName = field;
  						break;
  					case 2:
  						fieldType = field;
  						break;
  				}
  			}
  			
  			count1++;
  			if (fieldType.equals(stringString)) {
				line = "      checkStmt.setString(" + count1 + ", " + fieldName + ");";
			} else if (fieldType.equals(doubleString)) {
				line = "      checkStmt.setDouble(" + count1 + ", " + fieldName + ");";
			} else if (fieldType.equals(intString)) {
				line = "      checkStmt.setInt(" + count1 + ", " + fieldName + ");";
			} else if (fieldType.equals(longString)) {
				line = "      checkStmt.setLong(" + count1 + ", " + fieldName + ");";
			} else if (fieldType.equals(bigintString)) {
				line = "      checkStmt.setLong(" + count1 + ", " + fieldName + ");";
			}
  			WriteJavaSourceLine(line);
  		}
	
	    WriteBlankLine();
	    
        line = "      int record = checkStmt.executeUpdate();";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      SQLWarning warning = checkStmt.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      if (record > 0) return true;";
        WriteJavaSourceLine(line);
        line = "      else return false;";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);
	}
	
	private void UpdateRecord() {

	    WriteBlankLine();

	    // write update
		String line = "   public boolean update() throws SQLException {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    if (numberOfFields > 1) {
	    	line = "      boolean fieldBefore = false;";
	    	WriteJavaSourceLine(line);
	    }
	
	    line = "      setReadeOK(false);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write check for record found
	    line = "      if (!getUpdateOK()) {";
	    WriteJavaSourceLine(line);
	    line = "         System.out.println(\"" + message2 + "\");";
	    WriteJavaSourceLine(line);
	    line = "         return false;";
	    WriteJavaSourceLine(line);
	    line = "      }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();

	    // write check for record found
	    line = "      if (!getRecordFound()) {";
	    WriteJavaSourceLine(line);
	    line = "         System.out.println(\"" + message3 + "\");";
	    WriteJavaSourceLine(line);
	    line = "         return false;";
	    WriteJavaSourceLine(line);
	    line = "      }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select
	    line = "      String checkSql = \"update " + getFileName() + " \";";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	    
	    line = "      int counter = 1;";
	    WriteJavaSourceLine(line);
	    
	    WriteBlankLine();
	    
	    line = "      checkSql = checkSql.concat(\" set \");";
	    WriteJavaSourceLine(line);
	    WriteBlankLine();

	    // get all fields for insert string
	    String fieldName = new String();
	    String fieldType = new String();
		boolean notFirst = false;
  		for (ArrayList<String> element : allFields) {
  			int count2 = 0;
  			for (String field : element) {
  				count2++;
  				switch (count2) {
  					case 1:
  						fieldName = field;
  						break;
  					case 2:
  						fieldType = field;
  						break;
  				}
  			}
  			
  			// Check for field change
  			line = "      if (" + fieldName + " != " + fieldName + "Sav) {";
  			WriteJavaSourceLine(line);
  			if (notFirst) {
  				line = "         if (fieldBefore) checkSql = checkSql.concat(\",\");";
  				WriteJavaSourceLine(line);
  			}
  			notFirst = true;
  			line = "         checkSql = checkSql.concat(\" " + fieldName + " = ?\");";
		    WriteJavaSourceLine(line);
		    if (numberOfFields > 1) {
		    	line = "         fieldBefore = true;";
		    	WriteJavaSourceLine(line);
		    }
		    line = "      }";
		    WriteJavaSourceLine(line);   
  		}
  		
	    WriteBlankLine();
	
	    SetWhereFields();

	    WriteBlankLine();

	    // write select
	    line = "         checkStmt = conn.prepareStatement(checkSql);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	    
	    // get all fields for insert string
	    fieldName = "";
	    fieldType = "";
  		for (ArrayList<String> element : allFields) {
  			int count2 = 0;
  			for (String field : element) {
  				count2++;
  				switch (count2) {
  					case 1:
  						fieldName = field;
  						break;
  					case 2:
  						fieldType = field;
  						break;
  				}
  			}
  			
  			// Check for field change
  			line = "      if (" + fieldName + " != " + fieldName + "Sav) {";
  			WriteJavaSourceLine(line);
		    if (fieldType.equals(stringString)) {
		    	line = "         checkStmt.setString(counter++, " + fieldName + ");";
			} else if (fieldType.equals(doubleString)) {
				line = "         checkStmt.setDouble(counter++, " + fieldName + ");";
			} else if (fieldType.equals(intString)) {
				line = "         checkStmt.setInt(counter++, " + fieldName + ");";
			} else if (fieldType.equals(longString)) {
				line = "         checkStmt.setLong(counter++, " + fieldName + ");";
			} else if (fieldType.equals(bigintString)) {
				line = "         checkStmt.setLong(counter++, " + fieldName + ");";
			}
		    WriteJavaSourceLine(line);
		    line = "      }";
		    WriteJavaSourceLine(line);   
  		}
  		WriteBlankLine();
  		line = "      int record = 0;";
	    WriteJavaSourceLine(line);
	    
  		WriteBlankLine();
  		line = "      if (counter > 1) {";
		WriteJavaSourceLine(line);
  		
        WriteBlankLine();
	    SetSelectStatementKeys(0);
	
	    WriteBlankLine();
	    
	    // write select
	    line = "         record = checkStmt.executeUpdate();";
	    WriteJavaSourceLine(line);
	    WriteBlankLine();
	
	    // write select
	    line = "         SQLWarning warning = checkStmt.getWarnings();";
	    WriteJavaSourceLine(line);

	    // write select
	    line = "         printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	    
  		line = "      }";
		WriteJavaSourceLine(line);

	    // write select
	    line = "      if (record > 0) return true;";
	    WriteJavaSourceLine(line);
	    line = "      else return false;";
	    WriteJavaSourceLine(line);
	    
	    line = "   }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	}
	
	private void DeleteRecord() {

        WriteBlankLine();
		
        // write delete
        String line = "\tpublic boolean delete() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "\t\tsetReadeOK(false);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "\t\tif (!getRecordFound()) {";
        WriteJavaSourceLine(line);
        line = "\t\t\tSystem.out.println(\"" + message3 + "\");";
        WriteJavaSourceLine(line);
        line = "\t\t\treturn false;";
        WriteJavaSourceLine(line);
        line = "\t\t}";
        WriteJavaSourceLine(line);

        WriteBlankLine();
     
        // write select
        line = "\t\tString checkSql = \"delete from " + getFileName() + " \";";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        if (numberOfKeyFields > 0) {
        	SetWhereFields();
        	WriteBlankLine();
        }

        // write select
        line = "\t\tcheckStmt = conn.prepareStatement(checkSql);";
        WriteJavaSourceLine(line);

        WriteBlankLine();
        
        if (numberOfKeyFields > 0) {
        	line = "\t\tint counter = 1;";
        	WriteJavaSourceLine(line);
        	WriteBlankLine();
        	SetSelectStatementKeys(0);
            WriteBlankLine();
        }

        // write select
        line = "\t\tint record = checkStmt.executeUpdate();";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "\t\tSQLWarning warning = checkStmt.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "\t\tprintSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "\t\tif (record > 0) return true;";
        WriteJavaSourceLine(line);
        line = "\t\telse return false;";
        WriteJavaSourceLine(line);

        line = "\t}";
        WriteJavaSourceLine(line);
        WriteBlankLine();
	}
	
	private void ReadFirst() {
		
		String line = "//***********File Read Random Section*****************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		
		// write read first
        line = "   public boolean readFirst() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(2);

        WriteBlankLine();

        // write select
        line = "      String checkSql = \"select * from " + getFileName() + "\";";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      Statement Stmt = conn.createStatement(" +
        	   "ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      results = Stmt.executeQuery(checkSql);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      SQLWarning warning = results.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        // write select
        line = "      warning = Stmt.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.first()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);
        
        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
	}
	
	private void ReadNext() {
		
		String line = "   public boolean readNext() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      setReadeOK(false);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (!getRecordFound()) {";
        WriteJavaSourceLine(line);
        line = "         System.out.println(\"" + message3 + "\");";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);
        line = "      }";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (results == null)";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.next()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);

        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
	}
	
	private void ReadLast() {
		
		// write delete
        String line = "   public boolean readLast() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(2);

        WriteBlankLine();

        // write select
        line = "      String checkSql = \"select * from " + getFileName() + "\";";
        WriteJavaSourceLine(line);
        WriteBlankLine();

        // write select
        line = "      Statement Stmt = conn.createStatement(" +
         	   "ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);";

        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      results = Stmt.executeQuery(checkSql);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      SQLWarning warning = results.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        // write select
        line = "      warning = Stmt.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.last()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);

        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
	}
	
	private void ReadPrevious() {
		
		// write delete
        String line = "   public boolean readPrevious() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      setReadeOK(false);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (!getRecordFound()) {";
        WriteJavaSourceLine(line);
        line = "         System.out.println(\"" + message3 + "\");";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);
        line = "      }";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (results == null)";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.previous()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);
        
        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
	}
	
	private void ReadEqualFirst(String ReadFile, ArrayList<String> keyFields) {
		
		String line = new String();
		
		// write read equal first header
		if (getFileName().equals(ReadFile)) {
			line = "   public boolean readEqualFirst() throws SQLException {";
		} else {
			line = "   public boolean readEqualFirst" + ReadFile + " () throws SQLException {";
		}
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();

	    SetOffAllInd(2);
	
	    WriteBlankLine();
	
	    SetCheckKeyFields(keyFields, 1);
	
	    // write select
	    line = "      String checkSql = \"select * from " + getFileName() + "\";";
	    WriteJavaSourceLine(line);
	
	    SetWhereFields(keyFields);
	
	    WriteBlankLine();
	    
	    SetOrderBy(ReadFile, keyFields);

	    WriteBlankLine();
	    
	    // write select
	    line = "      checkStmt = conn.prepareStatement(checkSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);";
	    WriteJavaSourceLine(line);
	    
	    SetSelectKeyFields(keyFields);
	
	    WriteBlankLine();
	
	    // write select
	    line = "      results = checkStmt.executeQuery();";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select
	    line = "      SQLWarning warning = results.getWarnings();";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      warning = checkStmt.getWarnings();";
	    WriteJavaSourceLine(line);
	
	    // write select
	    line = "      printSQLWarnings(warning);";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write read results
	    line = "      if (results.first()) {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write select
	    line = "         updateAllFromResults();";
	    WriteJavaSourceLine(line);
	    
	    line = "         setKeyFields();";
       	WriteJavaSourceLine(line);

	    line = "         setRecordFound(true);";
	    WriteJavaSourceLine(line);
	
	    line = "         setUpdateOK(true);";
	    WriteJavaSourceLine(line);
	
	    line = "         setReadeOK(true);";
	    WriteJavaSourceLine(line);

	    line = "         return true;";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    line = "      } else {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    SetOffAllInd(3);
	
	    line = "         return false;";
	    WriteJavaSourceLine(line);
	
	    line = "      }";
	    WriteJavaSourceLine(line);
	
	    line = "   }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	}
	
	private void ReadEqualNext() {
		
		// write readequalnext header
        String line = "   public boolean readEqualNext() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (!getReadeOK()) {";
        WriteJavaSourceLine(line);
        line = "         System.out.println(\"" + message3 + "\");";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);
        line = "      " + "}";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (results == null)";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.next()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);
        
 		line = "         setKeyFields();";
        WriteJavaSourceLine(line);
        
        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         setReadeOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
	}
	
	private void ReadEqualLast(String ReadFile, ArrayList<String> keyFields) {
		
		String line = new String();
		
		// write read equal last header
		if (getFileName().equals(ReadFile)) {
			line = "   public boolean readEqualLast() throws SQLException {";
		} else {
			line = "   public boolean readEqualLast" + ReadFile + " () throws SQLException {";
		}
		WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(2);

        WriteBlankLine();

        SetCheckKeyFields(keyFields, 1);

        // write select
        line = "      String checkSql = \"select * from " + getFileName() + "\";";
        WriteJavaSourceLine(line);

        SetWhereFields(keyFields);

        WriteBlankLine();
	    
	    SetOrderBy(ReadFile, keyFields);

	    WriteBlankLine();

        // write select
        line = "      checkStmt = conn.prepareStatement(checkSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetSelectKeyFields(keyFields);

        WriteBlankLine();

        // write select
        line = "      results = checkStmt.executeQuery();";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "      SQLWarning warning = results.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        // write select
        line = "      warning = checkStmt.getWarnings();";
        WriteJavaSourceLine(line);

        // write select
        line = "      printSQLWarnings(warning);";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.last()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);
        
		line = "         setKeyFields();";
        WriteJavaSourceLine(line);

        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         setReadeOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);
        
        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
	}
	
	private void ReadEqualPrevious() {
		
		// write read equal previous header
		String line = "   public boolean readEqualPrevious() throws SQLException {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (!getReadeOK()) {";
        WriteJavaSourceLine(line);
        line = "         System.out.println(\"" + message3 + "\");";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);
        line = "      }";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write check for record found
        line = "      if (results == null)";
        WriteJavaSourceLine(line);
        line = "         return false;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write read results
        line = "      if (results.previous()) {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        // write select
        line = "         updateAllFromResults();";
        WriteJavaSourceLine(line);
        
		line = "         setKeyFields();";
		WriteJavaSourceLine(line);

        line = "         setRecordFound(true);";
        WriteJavaSourceLine(line);

        line = "         setUpdateOK(true);";
        WriteJavaSourceLine(line);

        line = "         setReadeOK(true);";
        WriteJavaSourceLine(line);

        line = "         return true;";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        line = "      } else {";
        WriteJavaSourceLine(line);

        WriteBlankLine();

        SetOffAllInd(3);

        line = "         return false;";
        WriteJavaSourceLine(line);

        line = "      }";
        WriteJavaSourceLine(line);

        line = "   }";
        WriteJavaSourceLine(line);

        WriteBlankLine();
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
 					case 1:
 						fieldName = field;
 						break;
 					case 2:
 						fieldType = field;
 						break;
 					case 5:
 						if(field.equals(one)) {
 							isKeyField = true;
 						}
 				}
 			}
 			if (isKeyField) {
				count1++;
				if (fieldType.equals(stringString)) {
					line = "      if (Key" + fieldName + " != \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = "      if (Key" + fieldName + " != 0.0) {";
				} else {
					line = "      if (Key" + fieldName + " != 0) {";
				}
				WriteJavaSourceLine(line);
				if (count1 == 1) {
					line = "         checkSql = checkSql + \" where " + fieldName + "=?\";";
				} else {
					line = "         checkSql = checkSql + \" and " + fieldName + "=?\";";
				}
				WriteJavaSourceLine(line);
			}
 		}
 		
 		line = "";
 		for (int i = 0; i < count1; i++) {
 			if (i == 0) line = line + "      }";
 			else line = line + "}";
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
			if (fieldType.equals(stringString)) {
				line = "      if (Key" + fieldName + " != \"\") {";
			} else if (fieldType.equals(doubleString)) {
				line = "      if (Key" + fieldName + " != 0.0) {";
			} else {
				line = "      if (Key" + fieldName + " != 0) {";
			}
			WriteJavaSourceLine(line);
			if (count1 == 1) {
				line = "         checkSql = checkSql + \" where " + fieldName + "=?\";";
			} else {
				line = "         checkSql = checkSql + \" and " + fieldName + "=?\";";
			}
			WriteJavaSourceLine(line);
 		}
 		
 		line = "";
 		for (int i = 0; i < count1; i++) {
 			if (i == 0) line = line + "      }";
 			else line = line + "}";
 		}
	    WriteJavaSourceLine(line);
	}
	
	private void SetSelectKeyFields(ArrayList<String> keyFields) {

		String line = new String();
		
		// get all fields for set string
		line = "      int counter = 1;";
		WriteJavaSourceLine(line);
		String fieldName = new String();
		String fieldType = new String();
		int count1 = 0;
 		for (String key : keyFields) {
 			fieldName = key.trim();
 			fieldType = getFieldType(fieldName);
			count1++;
			if (fieldType.equals(stringString)) {
				line = "      if (Key" + fieldName + " != \"\") {";
			} else if (fieldType.equals(doubleString)) {
				line = "      if (Key" + fieldName + " != 0.0) {";
			} else {
				line = "      if (Key" + fieldName + " != 0) {";
			}
			WriteJavaSourceLine(line);
			line = "";
			if (fieldType.equals(stringString)) {
				line = "         checkStmt.setString(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(doubleString)) {
				line = "         checkStmt.setDouble(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(intString)) {
				line = "         checkStmt.setInt(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(longString)) {
				line = "         checkStmt.setLong(counter++, Key" + fieldName + ");";
			} else if (fieldType.equals(bigintString)) {
				line = "         checkStmt.setLong(counter++, Key" + fieldName + ");";
			}
			WriteJavaSourceLine(line);
			line = "";
 		}
 		
 		line = "";
 		for (int i = 0; i < count1; i++) {
 			if (i == 0) line = line + "      }";
 			else line = line + "}";
 		}
	    WriteJavaSourceLine(line);
	}

	private void SetSelectStatementKeys(int startCount) {

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
 					case 1:
 						fieldName = field;
 						break;
 					case 2:
 						fieldType = field;
 						break;
 					case 5:
						if(field.equals(one)) {
							isKeyField = true;
						}
						break;
 				}
 			}
 			if (isKeyField) {
 				if (fieldType.equals(stringString)) {
					line = "\t\tif (Key" + fieldName + " != \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = "\t\tif (Key" + fieldName + " != 0.00) {";
				} else {
					line = "\t\tif (Key" + fieldName + " != 0) {";
				}
 				WriteJavaSourceLine(line);
	 			if (fieldType.equals(stringString)) {
					line = "\t\t\tcheckStmt.setString(";
				} else if (fieldType.equals(doubleString)) {
					line = "\t\t\tcheckStmt.setDouble(";
				} else if (fieldType.equals(intString)) {
					line = "\t\t\tcheckStmt.setInt(";
				} else if (fieldType.equals(longString)) {
					line = "\t\t\tcheckStmt.setLong(";
				} else if (fieldType.equals(bigintString)) {
					line = "\t\t\tcheckStmt.setLong(";
				}
	 			line = line + "counter++, Key" + fieldName + ");";
				WriteJavaSourceLine(line);
				line = "\t\t}";
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
				line = line + fieldName;
			} else {
				line = line + ", " + fieldName;
			}
			if (direction != null) {
				if (direction.equals(dir)) line = line + " " + direction;
			}
 		}
 		line = "      checkSql = checkSql + \" " + line;
 		line = line + "\";";
 		WriteJavaSourceLine(line);
	}
	
	private String getKeyFieldDirection(String fileName, String fieldName) {
		Map<String, String> keyString = keyFieldListMap.get(fileName);
		String direction = keyString.get(fieldName);
		return direction;
	}
	
	private void SetOffAllInd(int level) {
		
		switch (level) {
			case 1:
				String line = "   setReadeOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "   setUpdateOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "   setRecordFound(false);";
	            WriteJavaSourceLine(line);
				break;
				
			case 2:
				line = "      setReadeOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "      setUpdateOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "      setRecordFound(false);";
	            WriteJavaSourceLine(line);
				break;
				
			case 3:
				line = "         setReadeOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "         setUpdateOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "         setRecordFound(false);";
	            WriteJavaSourceLine(line);
				break;
				
			case 4:
				line = "            setReadeOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "            setUpdateOK(false);";
	            WriteJavaSourceLine(line);
	
	            line = "            setRecordFound(false);";
	            WriteJavaSourceLine(line);
				break;
		}
	}
	
	private void WriteJavaSourceLine(String line) {
		text.append(line + "\n");
	}
	
	private void CheckFields() {
		
		String line = "//***********Utility Section**************************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

		String message = message4;
		
		line = "\tpublic boolean checkSizeDouble(double field,";
		line = line + " int length, int decimal) {";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "      int leftInt = 0;";
		WriteJavaSourceLine(line);
		line = "      int leftIntSize;";
		WriteJavaSourceLine(line);
		line = "      String doubleString, leftString;";
		WriteJavaSourceLine(line);
		line = "      char checkString = \'.\';";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "      doubleString = String.valueOf(field);";
		WriteJavaSourceLine(line);
		line = "      int stringLength = doubleString.length();";
		WriteJavaSourceLine(line);
		line = "      for (int i = 0; i < stringLength; i++) {";
		WriteJavaSourceLine(line);
		line = "         char newString = doubleString.charAt(i);";
		WriteJavaSourceLine(line);
		line = "         if (newString == checkString) {";
		WriteJavaSourceLine(line);
		line = "            leftInt = i;";
		WriteJavaSourceLine(line);
		line = "            break;";
		WriteJavaSourceLine(line);
		line = "         }";
		WriteJavaSourceLine(line);
		line = "      }";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "      leftString = doubleString.substring(0, leftInt);";
		WriteJavaSourceLine(line);
		line = "      leftIntSize = leftString.length();";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "      if (leftIntSize > leftInt)";
		WriteJavaSourceLine(line);

		line = "         return false;";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "      " + "return true;";
		WriteJavaSourceLine(line);

		line = "   " +  "}";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\tpublic boolean checkSizeInt(String fieldName, int field, ";
		line = line + " int length) {";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\t\tdouble testInt;";
		WriteJavaSourceLine(line);

		line = "\t\tString stringInt = \"\";";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\t\tfor (int i = 0; i < length; i++) {";
		WriteJavaSourceLine(line);

		line = "\t\t\tstringInt = stringInt.concat(\"9\");";
		WriteJavaSourceLine(line);

		line = "\t\t}";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\t\ttestInt = Double.parseDouble(stringInt);";
		WriteJavaSourceLine(line);

		line = "\t\tif (field > testInt) {";
		WriteJavaSourceLine(line);

		line = "\t\t\tSystem.out.println(\"Number field" + message +  " for field \" + fieldName + \" size \" + length + \" value \" + field +\".\");";
		WriteJavaSourceLine(line);

		line = "\t\t\treturn false;";
		WriteJavaSourceLine(line);

		line = "\t\t} else return true;";
		WriteJavaSourceLine(line);

		line = "\t}";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\tpublic boolean checkSizeLong(String fieldName, Long field,";
		line = line + " int length) {";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\t\tdouble testInt;";
		WriteJavaSourceLine(line);

		line = "\t\tString stringLong = \"\";";
		WriteJavaSourceLine(line);

		WriteBlankLine();

		line = "\t\tfor (int i = 0; i < length; i++) {";
		WriteJavaSourceLine(line);

		line = "\t\t\tstringLong = stringLong.concat(\"9\");";
		WriteJavaSourceLine(line);
		line = "\t\t}";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		
		line = "\t\ttestInt = Double.parseDouble(stringLong);";
		WriteJavaSourceLine(line);
		line = "\t\tif (field > testInt) {";
		WriteJavaSourceLine(line);
		line = "\t\t\tSystem.out.println(\"Number field" + message +  " for field \" + fieldName + \" size \" + length + \" value \" + field +\".\");";
		WriteJavaSourceLine(line);
		line = "\t\t\treturn false;";
		WriteJavaSourceLine(line);
		line = "\t\t} else return true;";
		WriteJavaSourceLine(line);
		line = "\t}";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		line = "\tpublic boolean checkSizeString(String fieldName, String field,";
		line = line + " int length) {";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		line = "\t\t" + "String overflow;";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		line = "\t\t" + "if (field.length() < length) return true;";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		line = "\t\t" + "overflow = field.substring(length);";
		WriteJavaSourceLine(line);
		line = "\t\t" + "String trim = overflow.trim();";
		WriteJavaSourceLine(line);
		line = "\t\t" + "if (!trim.isEmpty()) {";
		WriteJavaSourceLine(line);
		line = "\t\t\tSystem.out.println(\"String field" + message +  " for field \" + fieldName + \" size \" + length + \" value \" + field +\".\");";
		WriteJavaSourceLine(line);
		line = "\t\t\t" + "return false;";
		WriteJavaSourceLine(line);
		line = "\t\t} else return true;";
		WriteJavaSourceLine(line);
		line = "\t}";
		WriteJavaSourceLine(line);
		WriteBlankLine();

		line = "\t" + "private void printSQLWarnings(SQLWarning warning) {";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		line = "\t\t" + "while (warning != null) {";
		WriteJavaSourceLine(line);
		WriteBlankLine();
		line = "\t\t\t" + "System.out.println(warning);";
		WriteJavaSourceLine(line);
		line = "\t\t\t" + "String message = warning.getMessage();";
		WriteJavaSourceLine(line);
		line = "\t\t\t" + "String sqlState = warning.getSQLState();";
		WriteJavaSourceLine(line);
		line = "\t\t\tint errorCode = warning.getErrorCode();";
		WriteJavaSourceLine(line);
		line = "\t\t\tSystem.out.println(message + sqlState";
		line = line + " + errorCode);";
		WriteJavaSourceLine(line);
		line = "\t\t\twarning = warning.getNextWarning();";
		WriteJavaSourceLine(line);
		WriteBlankLine();

		line = "\t\t" + "}";
		WriteJavaSourceLine(line);

		line = "\t}";
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
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
					case 5:
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
					line = "      if (Key" + fieldName + " == \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = "      if (Key" + fieldName + " == 0.0) {";
				} else {
					line = "      if (Key" + fieldName + " == 0) {";
				}
			    WriteJavaSourceLine(line);
			    line = "         System.out.println(\"" + message + "\");";
			    WriteJavaSourceLine(line);
			    line = "         return false;";
			    WriteJavaSourceLine(line);
			    line = "      }";
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
				line = new String();
				if (fieldType.equals(stringString)) {
					line = "      if (Key" + key.trim() + " == \"\") {";
				} else if (fieldType.equals(doubleString)) {
					line = "      if (Key" + key.trim() + " == 0.0) {";
				} else 
					line = "      if (Key" + key.trim() + " == 0) {";
			    WriteJavaSourceLine(line);
			    line = "         System.out.println(\"" + message5 + "\");";
			    WriteJavaSourceLine(line);
			    line = "         return false;";
			    WriteJavaSourceLine(line);
			    line = "      }";
			    WriteJavaSourceLine(line);
			    WriteBlankLine();
			}
		}
	}
	
	private void UpdateAllFromResults() {
		
		String line = "//***********Record Set Results Section***************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

		// write delete
		line = "   private void updateAllFromResults() throws SQLException {";
		WriteJavaSourceLine(line);
	
		WriteBlankLine();
	
		// write check for record found
		line = "      if (results == null) return;";
		WriteJavaSourceLine(line);
		
		WriteBlankLine();
	
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
 					case 2:
 						fieldType = field;
 						break;
 					case 6:
						setter = field;
 				}
 			}
 			count1++;
			line = "      " + setter + "(";
			if (fieldType.equals(stringString)) {
				line = line + "results.getString(";
			} else if (fieldType.equals(doubleString)) {
				line = line + "results.getDouble(";
			} else if (fieldType.equals(intString)) {
				line = line + "results.getInt(";
			} else if (fieldType.equals(longString)) {
				line = line + "results.getLong(";
			} else if (fieldType.equals(bigintString)) {
				line = line + "results.getLong(";
			}
			line = line + count1 + "));";
			WriteJavaSourceLine(line);
 		}
	
		WriteBlankLine();
		if (checkForKeyFields) {
			line = "      setKeyFields();";
			WriteJavaSourceLine(line);
		}
		
		// get all save fields for set string
 		for (ArrayList<String> element : allFields) {
 			String setter = new String();
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 6:
						setter = field;
 				}
 			}
			line = "      " + setter + "Sav();";
			WriteJavaSourceLine(line);
 		}
 		WriteBlankLine();
		line = "   " +  "}";
		WriteJavaSourceLine(line);
	
		WriteBlankLine();
		
	}

	private void ToString() {
		
		String line = "//***********Print to String Section*******************************************//";
		WriteJavaSourceLine(line);
		WriteBlankLine();

	    // write to string header
	    line = "   public String toString() {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write to string
	    line = "      return \"" + getFileName()  + " [";
	
	    // get all fields for set string
	    boolean first = false;
	    boolean newLine = false; 
		String fieldName = new String();
		String fieldNameArray = new String();
		int count1 = 0;
 		for (ArrayList<String> element : allFields) {
 			int count2 = 0;
 			for (String field : element) {
 				count2++;
 				switch (count2) {
 					case 1:
 						fieldName = field;
 						break;
 				}
 			}
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
			if (count1 != numberOfFields) {
				int m = count1%2;
	 			if (m == 0) {
	 				WriteJavaSourceLine(fieldNameArray);
	 				fieldNameArray = "";
	 				newLine = true;
	 			}
 			}
 		}
 		
 		line = fieldNameArray + " + \"]\";";
 		WriteJavaSourceLine(line);

	    line = "   }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	}

    private void ToStringKeys() {

	    // write to string 
	    String line = "   public String toStringKey() {";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
	
	    // write to string
	    line = "      return \"" + getFileName()  + " [";
		
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
 					case 1:
 						fieldName = field;
 						break;
 					case 5:
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
 			
 		line = fieldNameArray + " + \"]\";";
 		WriteJavaSourceLine(line);
	
	    line = "   }";
	    WriteJavaSourceLine(line);
	
	    WriteBlankLine();
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
 					case 5:
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

		String fieldName;
		try {
			fieldName = resultsSelect.getString(2);
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
			keyFields = getAllPhysicalKeyFields();
			Collection<String> fieldList = new ArrayList<String>();
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
			currentfield.put("Getter", "get" + fieldName);
			currentfield.put("Setter", "set" + fieldName);
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

		Collection<ArrayList<String>> logicalFileKeyField = new ArrayList<ArrayList<String>>();
		

		CallableStatement statement;
		try {
			statement = conn.prepareCall("{call sp_helpindex (?)}");
			statement.setString(1, FileName);
			boolean hadResults = statement.execute();
			if (hadResults) {
				ResultSet resultSet = statement.getResultSet();
			    // process result set
			    while (resultSet.next()) {
			    	ArrayList<String> keyFieldList = new ArrayList<String>();
			    	Map<String, String> keyFldLstMap = new HashMap<>();
			    	String fileName = resultSet.getString(1);
			    	String fields = resultSet.getString(3);
			    	String[] keyFields = fields.split(",");
			    	for(String kfld : keyFields) {
			    		int a = kfld.indexOf("(-)");
			    		if (a > 0) {
			    			String kfldSubstring = kfld.substring(0,a);
			    			keyFieldList.add(kfldSubstring.trim());
			    			keyFldLstMap.put(kfldSubstring.trim(), "desc");
			    		} else {
			    			keyFieldList.add(kfld.trim());
			    			keyFldLstMap.put(kfld.trim(), "asc");
			    		}
			    		keyFieldListMap.put(fileName, keyFldLstMap);
		    		}
			    	if (FileName.equals(fileName)) {
				    	setHasKeysInd(true);
				    	setAllPhysicalKeyFields(keyFieldList);
			    	} else {
			    		logicalFiles.add(fileName);
			    		hasMultipleKeysInd = true;
			    		logicalFileKeyField.add((ArrayList<String>) keyFieldList);
			    	}
			    }
			    /* for( fields : ) {
			    	
			    }*/
			    setAllLogicalKeyFields(logicalFileKeyField);
			}
		} catch (SQLException e) {
			// e.printStackTrace();
		}
	}
	
	public String getFieldType(String fieldName) {
		
		Collection<ArrayList<String>>  fields = getAllFields();
		
		for (ArrayList<String> element : fields) {
			String fldName = new String();
			String fldType = new String();
			int counter = 0;
			for (String field : element) {
				counter++;
				switch (counter) {
					case 1:
						fldName = field;
						break;
					case 2:
						fldType = field;
						break;
				}	
			}
			if (fieldName.equals(fldName)) {
				return fldType;
			}
		}
		return null;
	}
	
	private void WriteClass() {
	    try (FileOutputStream out = new FileOutputStream(new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\BuildJavaSource\\src\\com\\" + getCompanyName() + "\\database\\" + getDataBase() + "\\" + getFileName() + ".java"))) {
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
}