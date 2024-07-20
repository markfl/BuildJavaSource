package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CreateJoinFileVIews {
	
	static StringBuilder view = new StringBuilder();
	static StringBuilder dropview = new StringBuilder();
	
	static DBClassBuilder dbcb;
	private static Connection connMSSQL;
	private static Connection connLibrary;
	private static String fromLibrary;
	private static String company;
	private static String db;
	private static String includeLibrary;
	private static ArrayList<String> filesSoFarViews = new ArrayList<String>();
	private static ArrayList<String> joinedPhysicalFiles = new ArrayList<String>();
	private static Collection<ArrayList<String>> allJoinedFiles = new ArrayList<ArrayList<String>>();
	
	public static void main(String[] args) {
		
		String fromLibrary = args[0];
		String company = args[1];
		String db = args[2];
		if (args.length >= 4) {
			setIncludeLibrary(args[3]);
		}
		String libraryList = company + "liblist";
		
		setFromLibrary(fromLibrary);
		setCompany(company);
		setDb(db);
		
		int viewsCreated = 0;

		connMSSQL = null;
		connLibrary = null;
		MsSQL dbLibrary = new MsSQL("liblist");
		MsSQL dbMSSQL = new MsSQL(fromLibrary);
		try {
			connMSSQL = dbMSSQL.connect();
			connLibrary = dbLibrary.connect();
			String companySql = new String();
			if (includeLibrary.isEmpty())
				companySql = "Select * from " + libraryList
						   + " Order by sequence, library";
			else
				companySql = "Select * from " + libraryList
						   + " Where library = '" + includeLibrary
						   + "' Order by sequence, library";
			
			PreparedStatement checkStmt = connLibrary.prepareStatement(companySql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultsSelect = checkStmt.executeQuery();
			while (resultsSelect.next()) {
				String libraryName = resultsSelect.getString(2).toLowerCase().trim();
				viewsCreated += createJoinView(libraryName);
			}
			WriteCreateSQLView("Join", "DropJoin");
			System.out.println("Program completed normally, " + viewsCreated + " create view scripts created.");
			resultsSelect.close();
			checkStmt.close();
			dbLibrary.closeConnection(connLibrary);
			connLibrary.close();
			dbMSSQL.closeConnection(connMSSQL);
			connMSSQL.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static public int createJoinView(String selectLibrary) {
		
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		int sqlScriptsCreated = 0;

		String selectSql1 = "Select atfile, atlib from qdspfdbas "
				 + "Where atfila = '*LGL' "
				 + "And atlib = '" + selectLibrary + "' "
				 //+ "And atfile = 'FMPRSS2' "
				 + "Order by atlib, atfile";
		String selectSql2 = "Select whrfi from qadspdbr "
				 + "Where whrefi = ? And whreli = ?";
		String selectSql3 = "select count(*) as numberOfRecords from qdspfdjoin "
				 + "Where jnfile = ? And jnlib = ?";

		try {	
			PreparedStatement checkStmt1 = connMSSQL.prepareStatement(selectSql1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultsSelect1 = checkStmt1.executeQuery();
			Boolean firstRecord = true;
			while (resultsSelect1.next()) {
				String fileName = resultsSelect1.getString(1).trim();
				if (checkFileName(fileName.trim())) {
					if (!filesSoFarViews.contains(fileName.trim())) {
						String libraryName = resultsSelect1.getString(2).trim();
						fileName = checkFieldName(fileName.trim().toLowerCase());
			   			libraryName = libraryName.trim().toLowerCase();
			   			PreparedStatement checkStmt2 = connMSSQL.prepareStatement(selectSql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
						checkStmt2.setString(1, fileName);
						checkStmt2.setString(2, libraryName);
						ResultSet resultsSelect2 = checkStmt2.executeQuery();
						if (resultsSelect2.next()) {
							String physicalFileName = resultsSelect2.getString(1).trim().toLowerCase();
							// if (filesSoFarTables.contains(physicalFileName)) {	
								PreparedStatement checkStmt3;
								checkStmt3 = connMSSQL.prepareStatement(selectSql3);
								checkStmt3.setString(1, fileName);
								checkStmt3.setString(2, libraryName);
								ResultSet resultsSelect3 = checkStmt3.executeQuery();
								if (resultsSelect3.next()) {
									int numberOfRecords = resultsSelect3.getInt(1);
									if (numberOfRecords > 1) {
										fields = getJoinFieldData(fileName, libraryName, fields);
										if (buildSelectJoinView(physicalFileName, fileName, libraryName, fields, firstRecord)) {
											System.out.println("View SQL Script from library " + libraryName.trim() + " file " + fileName.trim() + " added.");
											firstRecord = false;
											sqlScriptsCreated++;
											filesSoFarViews.add(fileName.trim());
											fields = new ArrayList<ArrayList<String>>();
										} else {
											fields = new ArrayList<ArrayList<String>>();
										//}
									}
								}
							}
						}
					}
				}
			}
			if (sqlScriptsCreated > 0)
				System.out.println(sqlScriptsCreated + " View SQL script created.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return sqlScriptsCreated;
	}

	static public Boolean buildSelectJoinView(String physicalFileName, String fileName, String libraryName, Collection<ArrayList<String>> fields, Boolean firstFile) {
		
		String lineCreate = new String();
		String lineDrop = new String();
		
		if (firstFile) {
			if (db.equals("mssql")) {
				lineCreate += "USE [" + company + "]\ngo\n";
				lineDrop += "USE [" + company + "]\ngo\n";
			}
		}
		
		String selectSql
		= "Select sofile, solib, sofld, sorule, socomp, sonval, sovall, sovalu "
		+ "from qdspfdsel "
		+ "Where sofile = ? And solib = ? And sorfmt <> \' \' "
		+ "Order by solib, sofile";
		
		try {
			PreparedStatement checkStmt = connMSSQL.prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			checkStmt.setString(1, fileName);
			checkStmt.setString(2, libraryName);
			ResultSet resultsSelect = checkStmt.executeQuery();
			if (resultsSelect.first()) {
				lineCreate += "create view " + fileName.trim() + "\n";
				lineDrop += "drop view " + fileName.trim()+ "\n";
				
				if (db.equals("mssql")) {
					lineCreate += "with schemabinding as\n";
				} else {
					lineCreate += "as\n";
				}
				
				lineCreate += "Select\n";
				
				int count1 = 0;
				int numberOfFields = fields.size();
				for (ArrayList<String> element : fields) {
					String joinFileName = new String();
					String joinFieldName = new String();
					count1++;
					int count2 = 0;
					for (String field : element) {
						count2++;
						if (count2 > 2) break;
						switch (count2) {
							case 1:
								joinFileName = field.trim();
								break;
							case 2:
								joinFieldName = field.trim();
								break;
						}
					}
					if (count1 < numberOfFields) {
						int m = count1%2;
			 			if (m == 0) {
			 				lineCreate += joinFileName + "." + joinFieldName + ",\n";
			 			} else {
			 				lineCreate += " " + joinFileName + "." + joinFieldName + ",";
			 			}
					} else {
						lineCreate += " " + joinFileName + "." + joinFieldName + "\n";
					}
				}
		
				ArrayList<String> filesJoined = getJoinedPhysicalFiles();
				if (db.equals("mssql")) {
					lineCreate += "From dbo." + filesJoined.get(0);
					lineCreate += " left outer join dbo." + filesJoined.get(1);
				} else {
					lineCreate += "From left outer join " + filesJoined.get(1);
				}
				lineCreate += " On\n";
				Collection<ArrayList<String>>joinedFiles = getAllJoinedFiles();
				count1 = 0;
				String jnjfnm = new String();
				String jnjtnm = new String();
		    	String jnjfd1 = new String();
		    	String jnjfd2 = new String();
				for (ArrayList<String> element : joinedFiles) {
					count1++;
					int count2 = 0;
					for (String field : element) {
						count2++;
						switch (count2) {
							case 1:
								jnjfnm = field.trim();
								break;
							case 2:
								jnjtnm = field.trim();
								break;
							case 3:
								jnjfd1 = field.trim();
								break;
							case 4:
								jnjfd2 = field.trim();
								break;
						}
					}
					if (count1 == 1)
						lineCreate += "\t" + jnjfnm + "." +jnjfd1 + " = " + jnjtnm + "." +jnjfd2 + "\n";
					else
						lineCreate += " and" + jnjfnm + "." +jnjfd1 + " = " + jnjtnm + "." +jnjfd2 + "\n";
				}
				
				lineCreate += "where\n";
				String lastField = new String();
				Boolean needsParan = false;
				String compareField = new String();
				String ruleField = new String();
				lineCreate = setViewValues(resultsSelect, lineCreate, lastField, compareField, ruleField, false);
				lastField = resultsSelect.getString(3).trim().toLowerCase();
				if (!lastField.isEmpty()) lastField = checkFieldName(lastField);
				ruleField = resultsSelect.getString(4).trim(); 
				compareField = resultsSelect.getString(5).trim(); 
				if (compareField.equals("VA")) needsParan = true;
				while (resultsSelect.next()) {
					lineCreate = setViewValues(resultsSelect, lineCreate, lastField, compareField, ruleField, needsParan);
					lastField = resultsSelect.getString(3).trim().toLowerCase();
					if (!lastField.isEmpty()) {
						ruleField = resultsSelect.getString(4).trim(); 
						compareField = resultsSelect.getString(5).trim();
						if (compareField.equals("VA")) needsParan = true;
					}
				}
				lineCreate += "\n;\n";
				if (db.equals("mssql")) {
					lineCreate += "go";
					lineDrop += "go";
				}
				
				WriteJavaSourceLineView(lineCreate);
				WriteJavaSourceLineDropView(lineDrop);
			} else return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	private static String setViewValues(ResultSet resultsSelect, String lineCreate, String lastField, String lastComp, String lastRule, Boolean needsParam) {
			
		try {		
			String fieldName = resultsSelect.getString(3).trim().toLowerCase();
			String rule = resultsSelect.getString(4).trim();
			String compare = resultsSelect.getString(5).trim();
			String values = resultsSelect.getString(8).trim();
			if (!fieldName.isEmpty()) fieldName = checkFieldName(fieldName);
			if (fieldName.isEmpty() && (lastField.isEmpty())) {
			} else {
				if (!compare.equals("VA")) {
					if (lastComp.equals("VA") && needsParam) lineCreate += ")\n";  
					if (rule.equals("S") && (!fieldName.isEmpty())) {
						if (lastRule.equals("S")) lineCreate += " or " + fieldName;
						if (lastRule.equals("A")) lineCreate += "\n  or " + fieldName;
						if (lastRule.equals("O")) lineCreate += " and " + fieldName;
						if (lastRule.isEmpty())   lineCreate += "\t " + fieldName;
						if (compare.equals("NE")) lineCreate += " <> ";
						if (compare.equals("EQ")) lineCreate += " = ";
						if (compare.equals("GT")) lineCreate += " > ";
						if (compare.equals("LT")) lineCreate += " < ";
						if (compare.equals("GE")) lineCreate += " >= ";
						if (compare.equals("LE")) lineCreate += " <= ";
						lineCreate += values + "\n";
					}
					if (rule.equals("A")) {
						if (!lastRule.equals("A") && lastComp.equals("VA")) lineCreate += " and " + fieldName;
						else if (!lastRule.equals("O")) lineCreate += " and " + fieldName;
						else lineCreate += " and " + fieldName;
						if (compare.equals("NE")) lineCreate += " <> ";
						if (compare.equals("EQ")) lineCreate += " = ";
						if (compare.equals("GT")) lineCreate += " > ";
						if (compare.equals("LT")) lineCreate += " < ";
						if (compare.equals("GE")) lineCreate += " >= ";
						if (compare.equals("LE")) lineCreate += " <= ";
						lineCreate += values + "\n";
					}
					if (rule.equals("O") && (!fieldName.isEmpty())) {
						if (lastRule.equals("O")) lineCreate += "\n or " + fieldName;
						else if (lastRule.equals("A")) lineCreate += "\n or  " + fieldName;
						else if (lastRule.equals("S")) lineCreate += "\n and " + fieldName;
						if (lastRule.isEmpty()) lineCreate += "\t " + fieldName;
						if (compare.equals("NE")) lineCreate += " = ";
						if (compare.equals("EQ")) lineCreate += " <> ";
						if (compare.equals("GT")) lineCreate += " <= ";
						if (compare.equals("LT")) lineCreate += " >= ";
						if (compare.equals("GE")) lineCreate += " <  ";
						if (compare.equals("LE")) lineCreate += " >  ";
						if (values.substring(0, 1).equals("+")) {
							values = values.substring(1);
						}
						lineCreate += values;
					}
	
				} else {
					if (!fieldName.equals(lastField)) {
						if (rule.equals("S")) {
							if (lastField.isEmpty()) {
								lineCreate += "\t " + fieldName + " in (" + values;
							} else lineCreate += ")\n";
						} else if (rule.equals("O")) {
							lineCreate += "\t " + fieldName + " not in (" + values;
						}
						if (rule.equals("A")) {
							lineCreate += " and " + fieldName + " in (" + values;;
						}
					} else {
						lineCreate += "," + values;
					}
				}
				lastField = fieldName;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lineCreate;
	}

	private static void WriteJavaSourceLineView(String line) {
		view.append(line + "\n");
	}
	
	private static void WriteJavaSourceLineDropView(String line) {
		dropview.append(line + "\n");
	}

	private static void WriteCreateSQLView(String createString, String dropString) {
		
		try (FileOutputStream outCreate = new FileOutputStream(new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + getCompany() + "\\" + createString + ".sql"))) {
			outCreate.write(view.toString().getBytes());
			view.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (FileOutputStream outDrop = new FileOutputStream(new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + getCompany() + "\\" + dropString + ".sql"))) {
			outDrop.write(dropview.toString().getBytes());
			dropview.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static public Boolean checkFileName(String fileName) {
		if (fileName.contains(".")) return false;
		return true;
	}
	
	static public String checkFieldName(String fieldName) {
		
		String search = fieldName.substring(0, 1);
		Boolean i = search.equals("#");
		if (!i) i = search.equals("@");
		if (!i) i = search.equals("$");
		if (!i) i = search.equals("%");
		if (!i) i = search.equals("&");
		if (!i) i = search.equals("*");
		if (!i) i = search.equals("_");
		if (i) {
			fieldName = "a_" + fieldName.substring(1);
		}

		try {
			String checkSql = "select count(*) as numberOfRecords from qcrtsqlfld "
					+ "Where fieldnamel = ?";
			PreparedStatement checkStmt;
			checkStmt = connMSSQL.prepareStatement(checkSql);
			checkStmt.setString(1, fieldName);
			ResultSet results = checkStmt.executeQuery();
			if (results.next()) {
				int numberOfRecords = results.getInt(1);
				if (numberOfRecords > 0) {
					fieldName = fieldName + "_";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return fieldName;
	}
	
	static public Collection<ArrayList<String>> getJoinFieldData(String fileName, String libraryName, Collection<ArrayList<String>> fields) {
		
		Collection<String> currentFields = new ArrayList<String>();
		ArrayList<String> filesJoined = new ArrayList<String>();
		ArrayList<String> joinedFile = new ArrayList<String>();
		Collection<ArrayList<String>> joinedFiles = new ArrayList<ArrayList<String>>();

		String selectSql1 = "Select jndnam, jnjfnm, jnjtnm, jnjfd1, jnjfd2 " +
			  	"from qdspfdjoin Where jnfile = ? And jnlib = ?";
		String selectSql2 = "Select whfile, whlib, whflde, whfldb, whfldd, whfldt, whfldp, whftxt, whjref " +
			  	"from qdbasedict Where whfile = ? And whlib = ?";
		
		try {
			PreparedStatement checkStmt1 = connMSSQL.prepareStatement(selectSql1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    checkStmt1.setString(1, fileName);
		    checkStmt1.setString(2, libraryName);
		    ResultSet resultsSelect1 = checkStmt1.executeQuery();
		    while (resultsSelect1.next()) {
		    	String file = resultsSelect1.getString(1);
		    	file = checkFieldName(file.trim().toLowerCase());
		    	filesJoined.add(file);
		    	String jnjfnm = resultsSelect1.getString(2).trim().toLowerCase();
		    	if (!jnjfnm.isEmpty()) {
		    		String jnjtnm = resultsSelect1.getString(3).trim().toLowerCase();
			    	String jnjfd1 = resultsSelect1.getString(4).trim().toLowerCase();
			    	String jnjfd2 = resultsSelect1.getString(5).trim().toLowerCase();
			    	jnjfnm = checkFieldName(jnjfnm);
			    	jnjtnm = checkFieldName(jnjtnm);
			    	jnjfd1 = checkFieldName(jnjfd1);
			    	jnjfd2 = checkFieldName(jnjfd2);
			    	joinedFile.add(jnjtnm);
			    	joinedFile.add(jnjtnm);
			    	joinedFile.add(jnjfd1);
			    	joinedFile.add(jnjfd2);
			    	joinedFiles.add(joinedFile);
		    	}
		    }
		    setAllJoinedFiles(joinedFiles);
		    setJoinedPhysicalFiles(filesJoined);
			PreparedStatement checkStmt2 = connMSSQL.prepareStatement(selectSql2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    checkStmt2.setString(1, fileName);
		    checkStmt2.setString(2, libraryName);
		    ResultSet resultsSelect2 = checkStmt2.executeQuery();
		    boolean firstRecord = true;
		    while (resultsSelect2.next()) {
		    	fileName = resultsSelect2.getString(1);
		    	libraryName = resultsSelect2.getString(2);
		    	String fieldName = resultsSelect2.getString(3);
				firstRecord = false;
				if (!fieldName.trim().equals("QZG0000031")) {
			    	fieldName = checkFieldName(fieldName.trim().toLowerCase());
			    	int fieldSizeAlpha = resultsSelect2.getInt(4);
			    	int fieldSizeNumeric = resultsSelect2.getInt(5);
					String fieldType = resultsSelect2.getString(6);
					int decimal = resultsSelect2.getInt(7);
					String fieldText = resultsSelect2.getString(8);
					int joinReference = resultsSelect2.getInt(9);
					fileName = filesJoined.get(joinReference-1);
					currentFields.add(fileName);
					currentFields.add(libraryName);
					currentFields.add(fieldName);
					currentFields.add(Integer.toString(fieldSizeAlpha));
					currentFields.add(Integer.toString(fieldSizeNumeric));
					currentFields.add(fieldType);
					currentFields.add(Integer.toString(decimal));
					currentFields.add(fieldText);
					fields = getFileFields(firstRecord, currentFields, fields);
					currentFields = new ArrayList<String>();
				}
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fields;
	}
	
    static public Collection<ArrayList<String>> getFileFields(boolean firstRecord, Collection<String> results, Collection<ArrayList<String>> fields) {
    	
    	Collection<String> fieldList = new ArrayList<String>();
    	
		String fileName = new String();
		String fieldName = new String();
		String fieldType = new String();
		int fieldSizeAlpha = 0;
		int fieldSizeNumeric = 0;
		int decimal = 0;
		String fieldText = new String();
		int count = 0;
		for (String field : results) {
			count++;
			switch (count) {
				case 1:
					fileName = field.trim();
					break;
				case 2:
					//fileLibrary = field.trim();
					break;
				case 3:
					fieldName = field.trim();
					break;
				case 4:
					fieldSizeAlpha = Integer.parseInt(field);
					break;
				case 5:
					fieldSizeNumeric = Integer.parseInt(field);
					break;
				case 6:
					fieldType = field.trim();
					break;
				case 7:
					decimal = Integer.parseInt(field);
					break;
				case 8:
					fieldText = field.trim();
					break;
			}
		}
		
		fileName = fileName.toLowerCase();
		fieldName = fieldName.toLowerCase();
		fieldList.add(fileName);
		fieldList.add(fieldName);
		if (db.equals("oracle")) {
			if (fieldSizeNumeric > 4000) {
				fieldList.add("long varchar");
			} else {
				fieldList.add("varchar2");
			}
		} else if (db.equals("mysql")) {
			if (fieldSizeAlpha > 255) {
				fieldList.add("text");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("B")) {
			if (db.equals("oracle")) {
				fieldList.add("number");
			}	else if (db.equals("mssql")) {
					if (decimal == 0) fieldList.add("int");
					else fieldList.add("double");
			} else {
				fieldList.add("numeric");
			}
		}
		if (fieldType.equals("P")) {
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
		}
		if (fieldType.equals("A")) {
			fieldList.add("String");
		}
		if (fieldType.equals("L")) {
			if (db.equals("mssql")) {
				fieldList.add("char");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("Z")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("T")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("E")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("O")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("H")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("F")) {
			if (db.equals("mssql")) {
				fieldList.add("double");
			} else {
				fieldList.add("float");
			}
		}
		if (fieldType.equals("G")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("1")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		if (fieldType.equals("3")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}

		if (fieldType.equals("5")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}

		if (fieldType.equals("6")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		
		if (fieldType.isEmpty()) {
			if (db.equals("mssql")) {
				fieldList.add("String");
			} else {
				fieldList.add("char");
			}
		}
		fieldList.add(Integer.toString(fieldSizeAlpha));
		fieldList.add(Integer.toString(fieldSizeNumeric));
		fieldList.add(Integer.toString(decimal));
		fieldList.add(fieldText);
		fields.add((ArrayList<String>) fieldList);
    	return fields;
    }

	public static String getFromLibrary() {
		return fromLibrary;
	}

	public static void setFromLibrary(String fromLibrary) {
		CreateJoinFileVIews.fromLibrary = fromLibrary;
	}

	public static String getCompany() {
		return company;
	}

	public static void setCompany(String company) {
		CreateJoinFileVIews.company = company;
	}

	public static String getDb() {
		return db;
	}

	public static void setDb(String db) {
		CreateJoinFileVIews.db = db;
	}

	public static String getIncludeLibrary() {
		return includeLibrary;
	}

	public static void setIncludeLibrary(String includeLibrary) {
		CreateJoinFileVIews.includeLibrary = includeLibrary;
	}

	public static ArrayList<String> getJoinedPhysicalFiles() {
		return joinedPhysicalFiles;
	}

	public static void setJoinedPhysicalFiles(ArrayList<String> joinedPhysicalFiles) {
		CreateJoinFileVIews.joinedPhysicalFiles = joinedPhysicalFiles;
	}

	public static Collection<ArrayList<String>> getAllJoinedFiles() {
		return allJoinedFiles;
	}

	public static void setAllJoinedFiles(Collection<ArrayList<String>> allJoinedFiles) {
		CreateJoinFileVIews.allJoinedFiles = allJoinedFiles;
	}
}