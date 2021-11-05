package controller;

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

import model.MsSQL;

public class BuildTestScript {
	
	private String company;
	private String FileName;
	private int numberOfKeyFields;
	private int recordsToBuild;
	private Collection<ArrayList<String>> fields;
	private DBClassBuilder dbcb = new DBClassBuilder();
	private StringBuilder text = new StringBuilder();
	private Connection connMSSQL = null;
	private MsSQL dbMSSQL = new MsSQL();
	private boolean sharedConnection;
	
	public BuildTestScript(String company, String fileName) throws SQLException {
		setCompany(company);
		setFileName(fileName);
		setRecordsToBuild(1000);
		dbMSSQL.setCompanyName(company);
		connMSSQL = dbMSSQL.connect();
		dbcb.setConn(connMSSQL);
		dbcb.setFileName(fileName);
		setSharedConnection(false);
	}

	public BuildTestScript(String company, String fileName, int recordsToCreate) throws SQLException {
		setCompany(company);
		setFileName(fileName);
		setRecordsToBuild(recordsToCreate);
		dbMSSQL.setCompanyName(company);
		connMSSQL = dbMSSQL.connect();
		dbcb.setConn(connMSSQL);
		dbcb.setFileName(fileName);
		setSharedConnection(false);
	}
	
	public BuildTestScript(String company, String fileName, MsSQL db , Connection conn) throws SQLException {
		setCompany(company);
		setFileName(fileName);
		setRecordsToBuild(1000);
		dbMSSQL = db;
		connMSSQL = conn;
		dbcb.setConn(connMSSQL);
		dbcb.setFileName(fileName);
		setSharedConnection(true);
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		this.FileName = fileName;
	}

	public int getNumberOfKeyFields() {
		return numberOfKeyFields;
	}

	public void setNumberOfKeyFields(int numberOfKeyFields) {
		this.numberOfKeyFields = numberOfKeyFields;
	}

	public int getRecordsToBuild() {
		return recordsToBuild;
	}

	public void setRecordsToBuild(int recordsToBuild) {
		this.recordsToBuild = recordsToBuild;
	}

	public DBClassBuilder getDbcb() {
		return dbcb;
	}

	public void setDbcb(DBClassBuilder dbcb) {
		this.dbcb = dbcb;
	}

	public Connection getConnMSSQL() {
		return connMSSQL;
	}

	public void setConnMSSQL(Connection connMSSQL) {
		this.connMSSQL = connMSSQL;
	}

	public MsSQL getDbMSSQL() {
		return dbMSSQL;
	}

	public void setDbMSSQL(MsSQL dbMSSQL) {
		this.dbMSSQL = dbMSSQL;
	}

	public Collection<ArrayList<String>> getFields() {
		return fields;
	}

	public void setFields(Collection<ArrayList<String>> fields) {
		this.fields = fields;
	}
	
	public boolean isSharedConnection() {
		return sharedConnection;
	}

	public void setSharedConnection(boolean sharedConnection) {
		this.sharedConnection = sharedConnection;
	}

	public void closeConnection() {
		try {
			dbMSSQL.closeConnection(connMSSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Program completed normally. Test" + getFileName() + ".java created.");
	}
	
	public void testScript() {
		
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		String fileName = getFileName();
		
		String selectSql = "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "WHERE INFORMATION_SCHEMA.COLUMNS.TABLE_NAME=? " 
				+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";
		
		try {
			boolean firstRecord = true;
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
			checkStmtSelect.setString(1, fileName);
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fields = dbcb.getFileFields(firstRecord, resultsSelect, fields);
		    	firstRecord = false;
		    }
		    setFields(fields);
		    dbcb.setAllFields(fields);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String line = "package com.concentricab.test;";
		text.append(line + "\n\n");
		line = "import java.sql.SQLException;";
    	text.append(line + "\n\n");
    	line = "import com." + company + ".database.mssql." + fileName + ";";
    	text.append(line + "\n");
		line = "import model.MsSQL;";
		text.append(line + "\n\n");
		line = "public class Test" + fileName + " {";
		text.append(line + "\n\n");
		line = "   static " + fileName + " db = new " + fileName + "();\n";
		text.append(line + "\n");
		line = "   public static void main(String[] args) {";
		text.append(line + "\n\n");
		line = "      String company = \"" + company + "\";";
		text.append(line + "\n");
		line = "      MsSQL dbMSSQL = new MsSQL(company);";
		text.append(line + "\n\n");
		
		line = "      db.setUpdateOK(true);";
		text.append(line + "\n\n");
		line = "      try {";
		text.append(line + "\n");
		line = "         db.setConn(dbMSSQL.connect());";
		text.append(line + "\n");
		line = "      } catch (SQLException e) {";
		text.append(line + "\n");
		line = "         e.printStackTrace();";
		text.append(line + "\n");
		line = "      }";
		text.append(line + "\n\n");
		
		// add readfirst & delete function
		line = "      deleteRecords();\n";
		text.append(line + "\n");
		
		// add function
		line = "      addRecords();\n";
		text.append(line + "\n");
		
		// read function
		line = "      readRecords();\n";
		text.append(line + "\n");
		
		if (dbcb.isHasKeysInd()) {
			// read equal function
			line = "      readEqualRecords();\n";
			text.append(line + "\n");
			if (dbcb.isHasMultipleKeysInd()) {
				for(String lglFile : dbcb.getLogicalFiles()) {
					line = "      readEqualRecords" + lglFile + "();\n";
					text.append(line + "\n");
				}
			}
		}
		
		if (dbcb.isHasKeysInd()) {
			// read update function
			line = "      UpdateRecords();\n";
			text.append(line + "\n");
		}
				
		line = "      System.out.println(\"Test complete.\");";
		text.append(line + "\n");
		
		// close main
		line = "   }\n";
		text.append(line + "\n");
		
		// begin delete function
		line = "   public static void deleteRecords() {";
		text.append(line + "\n");
		line = "      int count = 0;";
		text.append(line + "\n");
		line = "      try {";
		text.append(line + "\n");
		
		line = "         db.readFirst();";
		text.append(line + "\n");
		line = "         db.delete();";
		text.append(line + "\n");
		line = "         count++;";
		text.append(line + "\n");
		line = "         while (db.readNext()) {";
		text.append(line + "\n");
		line = "            db.delete();";
		text.append(line + "\n");
		line = "            count++;";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "      } catch (SQLException e1) {";
		text.append(line + "\n");
		line = "         e1.printStackTrace();";
		text.append(line + "\n");
		line = "      }\n";
		text.append(line + "\n");
		line = "      System.out.println(count + \" records deleted\");";
		text.append(line + "\n");
		line = "   }\n";
		text.append(line + "\n");
				
		// begin add function
		line = "   public static void addRecords() {";
		text.append(line + "\n");

		line = "      int count = 0;";
		text.append(line + "\n");
		// define file fields
		String fieldName = new String();
		String fieldType = new String();
		int fieldSize = 0;
		int fieldDec = 0;
		for (ArrayList<String> element : fields) {
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
					case 3:
						fieldSize = Integer.parseInt(field);
						break;
					case 4:
						fieldDec = Integer.parseInt(field);
						break;
				}
			}
			
			int randomNumber = (int) Math.ceil(Math.random() * fieldSize);
			if (fieldDec > 0) {
				switch (fieldDec) {
					case 1:
						line = "      double " + fieldName + "counter = 0." + randomNumber + ";";
						break;
					case 2:
						line = "      double " + fieldName + "counter = 0.0" + randomNumber + ";";
						break;
					case 3:
						line = "      double " + fieldName + "counter = 0.00" + randomNumber + ";";
						break;
					case 4:
						line = "      double " + fieldName + "counter = 0.000" + randomNumber + ";";
						break;
					case 5:
						line = "      double " + fieldName + "counter = 0.0000" + randomNumber + ";";
						break;
					case 6:
						line = "      double " + fieldName + "counter = 0.00000" + randomNumber + ";";
						break;
					case 7:
						line = "      double " + fieldName + "counter = 0.000000" + randomNumber + ";";
						break;
					case 8:
						line = "      double " + fieldName + "counter = 0.0000000" + randomNumber + ";";
						break;
				}
			} else
				line = "      int " + fieldName + "counter = (int) Math.ceil(Math.random() * " + fieldSize + ");";
			text.append(line + "\n");
		}
			
		text.append("\n");
		line = "      db.setUpdateOK(true);";
		text.append(line + "\n\n");
		line = "      String alphabetLower = \"abcdefghigklmnopqrstuvwxyz\";";
		text.append(line + "\n");
		line = "      String alphabetUpper = \"ABCDEFGHIGKLMNOPQRSTUVWXYZ\";";
		text.append(line + "\n\n");
		line = "      for (int i = 0; i < " + recordsToBuild + "; i ++) {";
		text.append(line + "\n");
		
		// define file fields
		fieldName = new String();
		fieldType = new String();
		fieldSize = 0;
		fieldDec = 0;
		int count = 0;
		for (ArrayList<String> element : fields) {
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
					case 3:
						fieldSize = Integer.parseInt(field);
						break;
				}
			}
			
			if (fieldType.equals("String")) {
				if (fieldSize == 1) {
					count++;
					int m = count%2;
					if (m == 0) {
						line = "         db.set" + fieldName + "(alphabetLower.substring(" + fieldName + "counter, " + fieldName + "counter+1));";
					} else {
						line = "         db.set" + fieldName + "(alphabetUpper.substring(" + fieldName + "counter, " + fieldName + "counter+1));";
					}
				} else {
					line = "         db.set" + fieldName + "(Integer.toString(" + fieldName + "counter));";
				}
			} else {
				line = "         db.set" + fieldName + "(" + fieldName + "counter);";
			}
			text.append(line + "\n");
		}
		
		text.append("\n");
		
		// define file fields
		fieldName = new String();
		fieldType = new String();
		fieldSize = 0;
		fieldDec = 0;
		for (ArrayList<String> element : fields) {
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
					case 3:
						fieldSize = Integer.parseInt(field);
						break;
					case 4:
						fieldDec = Integer.parseInt(field);
						break;
				}
			}
			if (fieldDec == 0) {
				line = "         " + fieldName + "counter++;";
			} else {
				switch (fieldDec) {
					case 1:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.1;";
						break;
					case 2:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.01;";
						break;
					case 3:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.001;";
						break;
					case 4:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.0001;";
						break;
					case 5:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.00001;";
						break;
					case 6:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.000001;";
						break;
					case 7:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.0000001;";
						break;
					case 8:
						line = "         " + fieldName + "counter = " + fieldName + "counter + 0.00000001;";
						break;
				}
			}
			
			text.append(line + "\n");
		}

		text.append("\n");
		
		// define file fields
		fieldName = new String();
		fieldType = new String();
		String doubleString = "double";
		fieldSize = 0;
		for (ArrayList<String> element : fields) {
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
					case 3:
						fieldSize = Integer.parseInt(field);
				}
			}
		
			if (fieldSize == 1) {
				line = "         if (" + fieldName + "counter == 10) {";
				text.append(line + "\n");
				line = "             " + fieldName + "counter = (int) Math.ceil(Math.random() * 2);";
				text.append(line + "\n");
				line = "         }";
				text.append(line + "\n\n");
			}
			
			if (fieldSize == 2) {
				line = "         if (" + fieldName + "counter == 100) {";
				text.append(line + "\n");
				line = "             " + fieldName + "counter = (int) Math.ceil(Math.random() * 3);";
				text.append(line + "\n");
				line = "         }";
				text.append(line + "\n\n");
			}

			if (fieldSize == 3) {
				line = "         if (" + fieldName + "counter == 1000) {";
				text.append(line + "\n");
				line = "             " + fieldName + "counter = (int) Math.ceil(Math.random() * 4);";
				text.append(line + "\n");
				line = "         }";
				text.append(line + "\n\n");
			}
			
			if (fieldSize == 4) {
				line = "         if (" + fieldName + "counter == 10000) {";
				text.append(line + "\n");
				line = "             " + fieldName + "counter = (int) Math.ceil(Math.random() * 5);";
				text.append(line + "\n");
				line = "         }";
				text.append(line + "\n\n");
			}

			if ((fieldSize == 5) || (fieldType.equals(doubleString))) {
				line = "         if (" + fieldName + "counter == 100000) {";
				text.append(line + "\n");
				line = "             " + fieldName + "counter = (int) Math.ceil(Math.random() * 6);";
				text.append(line + "\n");
				line = "         }";
				text.append(line + "\n\n");
			}
		}
		
		text.append("\n");
		
		line = "         try {";
		text.append(line + "\n");
		line = "            if (db.add()) {";
		text.append(line + "\n");
		line = "               count++;";
		text.append(line + "\n");
		line = "            }";
		text.append(line + "\n");
		line = "         } catch (SQLException e) {";
		text.append(line + "\n");
		line = "            System.err.println(e.getMessage());";
		text.append(line + "\n");
		line = "            System.out.println(db.toString());";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "      }";
		text.append(line + "\n");
		line = "      System.out.println(count + \" records added\");";
		text.append(line + "\n");
		line = "   }\n";
		text.append(line + "\n");

		// begin add function
		line = "   public static void readRecords() {";
		text.append(line + "\n");
		line = "      try {";
		text.append(line + "\n");
		line = "         if (db.readFirst()) {";
		text.append(line + "\n");
		line = "            System.out.println(\"Read first successful.\");";
		text.append(line + "\n");
		line = "            if (db.readNext()) {";
		text.append(line + "\n");
		line = "               System.out.println(\"Read next successful.\");";
		text.append(line + "\n");
		line = "            }";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "      } catch (SQLException e2) {";
		text.append(line + "\n");
		line = "         e2.printStackTrace();";
		text.append(line + "\n");
		line = "      }\n";
		text.append(line + "\n");

		line = "      try {";
		text.append(line + "\n");
		line = "         if (db.readLast()) {";
		text.append(line + "\n");
		line = "            System.out.println(\"Read last successful.\");";
		text.append(line + "\n");
		line = "            if (db.readPrevious()) {";
		text.append(line + "\n");
		line = "               System.out.println(\"Read previous successful.\");";
		text.append(line + "\n");
		line = "            }";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "      } catch (SQLException e2) {";
		text.append(line + "\n");
		line = "         e2.printStackTrace();";
		text.append(line + "\n");
		line = "      }";
		text.append(line + "\n");
		line = "   }\n";
		text.append(line + "\n");

		if (dbcb.isHasKeysInd()) {
			BuildReadEqualForAllIndexes(dbcb.getAllPhysicalKeyFields(), false, 0);
			if (dbcb.isHasMultipleKeysInd()) {
				count = 0;
				for(ArrayList<String> lglFileKeys : dbcb.getAllLogicalKeyFields()) {
					count++;
					BuildReadEqualForAllIndexes(lglFileKeys, true, count);
				}
			}

			// begin update function
			line = "   static public void UpdateRecords() {\n";
			text.append(line + "\n");
			line = "      db.setKeyFields(" + FormatKeyFields(dbcb.getAllPhysicalKeyFields());
			text.append(line + "\n");
			line = "      try {";
			text.append(line + "\n");
			line = "         System.out.println(db.exists());";
			text.append(line + "\n");
			line = "         System.out.println(\"Record found: \" + db.get());";
			text.append(line + "\n");
			/* line = "            db.setadjtyp(\"A\");";
			text.append(line + "\n");
			line = "            db.setdocpcs(99);";*/
			line = setKeySetters(fields, numberOfKeyFields);
			text.append(line + "\n");
			line = "         if (db.update()) {";
			text.append(line + "\n");
			line = "            System.out.println(\"Update successful.\");";
			text.append(line + "\n");
			line = "         }";
			text.append(line + "\n");
			line = "      } catch (SQLException e1) {";
			text.append(line + "\n");
			line = "         e1.printStackTrace();";
			text.append(line + "\n");
			line = "      }\n";
			text.append(line);
			line = "   }\n";
			text.append(line);
			// close class
			line = "}";
			text.append(line);
		}
		
		try (FileOutputStream out = new FileOutputStream(new File("F:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompany() + "\\src\\test\\Test" + getFileName() + ".java"))) {
			out.write(text.toString().getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String FormatKeyFields(ArrayList<String> allFields) {
		
		int nkeyfld = allFields.size();
		String fieldType = new String();
		String line = new String();
		int counter1 = 0;
		for (String keyField : allFields) {
			counter1++;
			fieldType = dbcb.getFieldType(keyField.trim());
			if ((counter1 == 1) && (counter1 < nkeyfld)) {
				if (fieldType.equals("String")) line = line + "\"A\",";
				if (fieldType.equals("double")) line = line + "0.0,";
				if (fieldType.equals("int")) line = line + "0,";
			} else {
				if (counter1 < nkeyfld) {
					if (fieldType.equals("String")) line = line + "\"A\",";
					if (fieldType.equals("double")) line = line + "0.0,";
					if (fieldType.equals("int")) line = line + "0,";
				} else {
					if (fieldType.equals("String")) line = line + "\"A\");";
					if (fieldType.equals("double")) line = line + "0.0);";
					if (fieldType.equals("int")) line = line + "0);";
					break;
				}
			}
		}
		
		return line;
	}
	
	public static String setKeySetters(Collection<ArrayList<String>> allFields, int nkeyfld) {
		
		String fieldName = new String();
		String fieldType = new String();
		String one = "1";
		String keyField = new String();
		String line = new String();
		int counter1 = 0;
		for (ArrayList<String> element : allFields) {
			int counter2 = 0;
			for (String field : element) {
				counter2++;
				switch (counter2) {
					case 1:
						fieldName = field;
						break;
					case 2:
						fieldType = field;
						break;
					case 5:
						keyField = field;
						break;
				}
			}
			
			if (keyField.equals(one)) {
				counter1++;
				line = line + "         db.set" + fieldName + "(";
				if ((counter1 == 1) && (counter1 < nkeyfld)) {
					if (fieldType.equals("String")) line = line + "\"A\"";
					if (fieldType.equals("double")) line = line + "0.0";
					if (fieldType.equals("int")) line = line + "0";
				} else {
					if (counter1 < nkeyfld) {
						if (fieldType.equals("String")) line = line + "\"A\"";
						if (fieldType.equals("double")) line = line + "0.0";
						if (fieldType.equals("int")) line = line + "0";
					} else {
						if (fieldType.equals("String")) line = line + "\"A\"";
						if (fieldType.equals("double")) line = line + "0.0";
						if (fieldType.equals("int")) line = line + "0";
					}
				}
				line = line + ");\n";
			}
		}
		return line;
	}
	
	private void BuildReadEqualForAllIndexes(ArrayList<String> keys, boolean lglFileInd,int fileCount) {
		
		String file = new String();
		if (fileCount == 0) file = dbcb.getFileName();
		else {
			int count = 0;
			for(String logicalFile : dbcb.getLogicalFiles() ) {
				count++;
				if (count == fileCount) {
					file = logicalFile;
				}
			}
		}
		
		String line = new String();
		// begin add function
		if (lglFileInd) line = "   static public void readEqualRecords" + file + "() {\n";
		else line = "   static public void readEqualRecords() {\n";
		text.append(line + "\n");
		line = "      try {";
		text.append(line + "\n");
		// define key fields
		if (lglFileInd) line = "         db.setKeyFields" + file + "(" + FormatKeyFields(keys);
		else line = "         db.setKeyFields(" + FormatKeyFields(keys);
		text.append(line + "\n");
		if (lglFileInd) line = "         if (db.readEqualFirst" + file + "()) {";
		else line = "         if (db.readEqualFirst()) {";
		text.append(line + "\n");
		line = "            System.out.println(\"Read equal first successful.\");";
		text.append(line + "\n");
		line = "            System.out.println(db.toString());";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "         if (db.readEqualNext()) {";
		text.append(line + "\n");
		line = "            System.out.println(\"Read equal next successful.\");";
		text.append(line + "\n");
		line = "            System.out.println(db.toString());";
		text.append(line + "\n");
		line = "         }\n";
		text.append(line + "\n");
		// define key fields
		if (lglFileInd) line = "         db.setKeyFields" + file + "(" + FormatKeyFields(keys);
		else line = "         db.setKeyFields(" + FormatKeyFields(keys);
		text.append(line + "\n");
		if (lglFileInd) line = "         if (db.readEqualLast" + file + "()) {";
		else line = "         if (db.readEqualLast()) {";
		text.append(line + "\n");
		line = "            System.out.println(\"Read equal last successful.\");";
		text.append(line + "\n");
		line = "            System.out.println(db.toString());";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "         if (db.readEqualPrevious()) {";
		text.append(line + "\n");
		line = "            System.out.println(\"Read equal previous successful.\");";
		text.append(line + "\n");
		line = "            System.out.println(db.toString());";
		text.append(line + "\n");
		line = "         }";
		text.append(line + "\n");
		line = "      } catch (SQLException e1) {";
		text.append(line + "\n");
		line = "         e1.printStackTrace();";
		text.append(line + "\n");
		line = "      }";
		text.append(line + "\n");
		line = "   }\n\n";
		text.append(line);
	}
}