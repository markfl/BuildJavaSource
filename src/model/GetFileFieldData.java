package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import controller.CheckName;

public class GetFileFieldData {

	private static Connection connMSSQL;
	private static String companyName;
	private static String libraryName;
	private static String fileName;
	private static String db;
	private static CheckName cn;
	private static Boolean hasConcatField;
	private static Boolean hasSubStringField;
	
	public GetFileFieldData() {

		super();
		
	}
		
	public GetFileFieldData(String companyName, String libraryName, String fileName, String db, Connection connMSSQL) {

		super();
		
		setCompanyName(companyName);
		setLibraryName(libraryName);
		setFileName(fileName);
		setDb(db);
		setConnMSSQL(connMSSQL);
		
	}

	public GetFileFieldData(String companyName, String libraryName, String fileName, String db, CheckName cn, Connection connMSSQL) {

		super();
		
		setCompanyName(companyName);
		setLibraryName(libraryName);
		setFileName(fileName);
		setDb(db);
		setCn(cn);
		setConnMSSQL(connMSSQL);
		
	}

	public Collection<ArrayList<String>> getFieldData(Collection<ArrayList<String>> fields) {
		
		Collection<String> currentFields = new ArrayList<String>();
		String selectSql = "Select whfile, whlib, whflde, whfldb, whfldd, whfldt, whfldp, whftxt, whjref, concat, whfldi, whmap, whmaps, whmapl "
						 + "From qdbasedict Where whfile = ? And whlib = ? "
						 + "Order by whfobo";
		
		try {
			
			PreparedStatement checkStmt = getConnMSSQL().prepareStatement(selectSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    checkStmt.setString(1, getFileName());
		    checkStmt.setString(2, getLibraryName());
		    ResultSet resultsSelect = checkStmt.executeQuery();
		    boolean firstRecord = true;
		    while (resultsSelect.next()) {
		    	String currentFileName = resultsSelect.getString(1).trim().toLowerCase();
		    	String currentLibraryName = resultsSelect.getString(2).trim().toLowerCase();
		    	String fieldName = resultsSelect.getString(3).trim().toLowerCase();
				firstRecord = false;
				if (!fieldName.trim().equals("QZG0000031")) {
			    	setLibraryName(currentLibraryName);
			    	fieldName = cn.checkFieldName(fieldName);
			    	int fieldSizeAlpha = resultsSelect.getInt(4);
			    	int fieldSizeNumeric = resultsSelect.getInt(5);
					String fieldType = resultsSelect.getString(6);
					int decimal = resultsSelect.getInt(7);
					String fieldText = resultsSelect.getString(8);
					//int joinReference = resultsSelect.getInt(9);
					//String concat = resultsSelect.getString(10);
					String interalFieldName = resultsSelect.getString(11);
					interalFieldName = cn.checkFieldName(interalFieldName.trim().toLowerCase());
					//String map = resultsSelect.getString(12);
					//int substringStart = resultsSelect.getInt(13);
					//int substringLength = resultsSelect.getInt(14);
					currentFields.add(currentFileName.toLowerCase().trim());
					currentFields.add(fieldName);
					currentFields.add(fieldType);
					currentFields.add(Integer.toString(fieldSizeAlpha));
					currentFields.add(Integer.toString(fieldSizeNumeric));
					currentFields.add(Integer.toString(decimal));
					currentFields.add("set" + fieldName);
					currentFields.add("get" + fieldName);
					currentFields.add(fieldText.trim());
					/*currentFields.add(Integer.toString(joinReference));
					currentFields.add(concat.trim());
					currentFields.add(interalFieldName);
					currentFields.add(map.trim());
					currentFields.add(Integer.toString(substringStart));
					currentFields.add(Integer.toString(substringLength));*/
					//currentFields.add(currentLibraryName.toLowerCase().trim());
					fields = getFileFields(firstRecord, currentFields, fields);
					currentFields = new ArrayList<String>();
				}
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fields;
	}

	public static Connection getConnMSSQL() {
		return connMSSQL;
	}

	public static void setConnMSSQL(Connection connMSSQL) {
		GetFileFieldData.connMSSQL = connMSSQL;
	}

	public static String getCompanyName() {
		return companyName;
	}

	public static void setCompanyName(String companyName) {
		GetFileFieldData.companyName = companyName;
	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		GetFileFieldData.libraryName = libraryName;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		GetFileFieldData.fileName = fileName;
	}

	public static String getDb() {
		return db;
	}

	public static void setDb(String db) {
		GetFileFieldData.db = db;
	}
	
    public static Boolean getHasConcatField() {
		return hasConcatField;
	}

	public static void setHasConcatField(Boolean hasConcatField) {
		GetFileFieldData.hasConcatField = hasConcatField;
	}

	public static Boolean getHasSubStringField() {
		return hasSubStringField;
	}

	public static void setHasSubStringField(Boolean hasSubStringField) {
		GetFileFieldData.hasSubStringField = hasSubStringField;
	}

	public static CheckName getCn() {
		return cn;
	}

	public static void setCn(CheckName cn) {
		GetFileFieldData.cn = cn;
	}

	static public Collection<ArrayList<String>> getFileFields(boolean firstRecord, Collection<String> results, Collection<ArrayList<String>> fields) {
    	
    	Collection<String> fieldList = new ArrayList<String>();
    	
    	String fileName = new String();
		String fieldName = new String();
		String fieldType = new String();
		int fieldSizeAlpha = 0;
		int fieldSizeNumeric = 0;
		int decimal = 0;
		String setter = new String();
		String getter = new String();
		String fieldText = new String();
		int count = 0;
		for (String field : results) {
			count++;
			switch (count) {
				case 1:
					fileName = field.trim();
					break;
				case 2:
					fieldName = field.trim();
					break;
				case 3:
					fieldType = field.trim();
					break;
				case 4:
					fieldSizeAlpha = Integer.parseInt(field);
					break;
				case 5:
					fieldSizeNumeric = Integer.parseInt(field);
					break;
				case 6:
					decimal = Integer.parseInt(field);
					break;
				case 7:
					setter = field.trim();
					break;
				case 8:
					getter = field.trim();
					break;
				case 9:
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
				fieldList.add(Integer.toString(fieldSizeNumeric));
			} else {
				fieldList.add("varchar2");
				fieldList.add(Integer.toString(fieldSizeNumeric));
			}
		} else if (db.equals("mysql")) {
			if (fieldSizeAlpha > 255) {
				fieldList.add("text");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		}
		if (fieldType.equals("B")) {
			if (db.equals("oracle")) {
				fieldList.add("number");
			}	else if (db.equals("mssql")) {
					if (decimal == 0) {
						fieldList.add("int");
						fieldList.add(Integer.toString(fieldSizeNumeric));
					}
					else fieldList.add("double");
					fieldList.add(Integer.toString(fieldSizeNumeric));
			} else {
				fieldList.add("numeric");
				fieldList.add(Integer.toString(fieldSizeNumeric));
			}
		} else if (fieldType.equals("P")) {
			if (decimal == 0) fieldList.add("int");
			else fieldList.add("double");
			fieldList.add(Integer.toString(fieldSizeNumeric));
		} else if (fieldType.equals("S")) {
			if (db.equals("mssql")) {
				if (decimal == 0) {
					fieldList.add("int");
					fieldList.add(Integer.toString(fieldSizeNumeric));
				} else {
					fieldList.add("double");
					fieldList.add(Integer.toString(fieldSizeNumeric));
				}
			} else {
				fieldList.add("numeric");
				fieldList.add(Integer.toString(fieldSizeNumeric));
			}
		} else if (fieldType.equals("A")) {
			fieldList.add("String");
			fieldList.add(Integer.toString(fieldSizeAlpha));
		} else if (fieldType.equals("L")) {
			if (db.equals("mssql")) {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("Z")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("T")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
			}
		} else if (fieldType.equals("E")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("O")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("H")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("F")) {
			if (db.equals("mssql")) {
				fieldList.add("double");
				fieldList.add(Integer.toString(fieldSizeNumeric));
			} else {
				fieldList.add("float");
				fieldList.add(Integer.toString(fieldSizeNumeric));
			}
		} else if (fieldType.equals("G")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("1")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("3")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("5")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.equals("6")) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		} else if (fieldType.isEmpty()) {
			if (db.equals("mssql")) {
				fieldList.add("String");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			} else {
				fieldList.add("char");
				fieldList.add(Integer.toString(fieldSizeAlpha));
			}
		}
		
		fieldList.add(Integer.toString(decimal));
		fieldList.add("0");
		fieldList.add(setter);
		fieldList.add(getter);
		fieldList.add(fieldText);
		fields.add((ArrayList<String>) fieldList);
    	return fields;
    }
}