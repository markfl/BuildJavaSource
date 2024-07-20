package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DBTestBuilder {
	
	private String company;
	private String dataBase;
	private String fileSource;
	private String fileName;
	private int numberOfKeyFields;
	private int recordsToBuild;
	private Collection<ArrayList<String>> fields;
	private DBClassBuilder dbcb;
	private StringBuilder text = new StringBuilder();
	private Connection connMSSQL = null;
	private MsSQL dbMSSQL = new MsSQL();
	private boolean sharedConnection;
	private boolean stringsExist;
	
	public DBTestBuilder(String company, String dataBase, String fileName) throws SQLException {
		setCompany(company);
		setDataBase(dataBase);
		setFileName(fileName);
		dbcb = new DBClassBuilder(company);
		setRecordsToBuild(1000);
		dbMSSQL.setCompanyName(company);
		setConnMSSQL(dbMSSQL.connect());
		dbcb.setConnMSSQL(connMSSQL);
		dbcb.setFileName(fileName);
		setSharedConnection(false);
		setStringsExist(false);
	}

	public DBTestBuilder(String company, String dataBase, String fileName, int recordsToCreate) throws SQLException {
		setCompany(company);
		setDataBase(dataBase);
		setFileName(fileName);
		dbcb = new DBClassBuilder(company);
		setRecordsToBuild(recordsToCreate);
		dbMSSQL.setCompanyName(company);
		setConnMSSQL(dbMSSQL.connect());
		dbcb.setConnMSSQL(connMSSQL);
		dbcb.setFileName(fileName);
		setSharedConnection(false);
		setStringsExist(false);
	}
		
	public DBTestBuilder(String company, String dataBase, String fileName, MsSQL dbMSSQL , Connection connMMSQL) throws SQLException {
		setCompany(company);
		setDataBase(dataBase);
		setFileName(fileName);
		dbcb = new DBClassBuilder(company, connMSSQL, dataBase, company);
		setRecordsToBuild(1000);
		setDbMSSQL(dbMSSQL);
		setConnMSSQL(connMMSQL);
		dbcb.setConnMSSQL(connMSSQL);
		dbcb.setFileName(fileName);
		setSharedConnection(true);
		setStringsExist(false);
	}

	public DBTestBuilder(String company, String dataBase, String fileSource, String libraryName, String fileName, MsSQL dbMSSQL , Connection connMMSQL) throws SQLException {
		setCompany(company);
		setDataBase(dataBase);
		setFileSource(fileSource);
		setFileName(fileName);
		dbcb = new DBClassBuilder(company, connMSSQL, dataBase, libraryName);
		setRecordsToBuild(1000);
		setDbMSSQL(dbMSSQL);
		setConnMSSQL(connMMSQL);
		dbcb.setConnMSSQL(connMSSQL);
		dbcb.setFileName(fileName);
		dbcb.setLongFileName(libraryName + "_" + fileName);
		setSharedConnection(true);
		setStringsExist(false);
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getDataBase() {
		return dataBase;
	}

	public void setDataBase(String dataBase) {
		this.dataBase = dataBase;
	}
	
	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public boolean isStringsExist() {
		return stringsExist;
	}

	public void setStringsExist(boolean stringsExist) {
		this.stringsExist = stringsExist;
	}

	public void closeConnection() {
		try {
			dbMSSQL.closeConnection(connMSSQL);
			connMSSQL.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Program completed normally. Test" + getFileName() + ".java created.");
	}
	
	public void testScript() {
		
		Collection<ArrayList<String>> fields = new ArrayList<ArrayList<String>>();
		
		dbcb.setCompanyName(getCompany());
		dbcb.setDataBase(getDataBase());
		dbcb.setFileName(getFileName());
		if (dbcb.readJSON()) {
			fields = dbcb.getAllFields();
			dbcb.setNumberOfFields(fields.size());
			setFields(fields);
			dbcb.getFileIndexString();
			Collection<ArrayList<String>> allKeyFields = dbcb.getAllKeyFields();
			dbcb.setNumberOfKeyFields(allKeyFields.size());
			
			String fieldType = new String();
			for (ArrayList<String> element : fields) {
				int counter1 = 0;
				for (String field : element) {
					counter1++;
					if (counter1 > 3) break;
					if (isStringsExist()) break;
					switch (counter1) {
						case 3:
							fieldType = field;
							if (fieldType.equals("String")) {
								setStringsExist(true);
							}
							break;
					}
				}
			}
	
			String line = "package com.test." + dataBase + ";\n\n";
			line += "import java.sql.SQLException;\n";
			line += "import java.util.ArrayList;\n";
			line += "import java.util.Collection;\n\n";
			if (dbcb.getLongFileName() != null) {
				line += "import com.database." + dataBase + "." + dbcb.getLongFileName() + ";\n\n";
			} else {
				line += "import com.database." + dataBase + "." + dbcb.getFileName() + ";\n\n";
			}
			line += "import model.MsSQL;\n\n";
			if (isStringsExist()) {
				line += "import de.svenjacobs.loremipsum.LoremIpsum;\n\n";
			}
			if (dbcb.getLongFileName() != null) {
				line += "public class test_" + dbcb.getLongFileName() + " {\n\n";
				line += "\tstatic " + dbcb.getLongFileName() + " db = new " + dbcb.getLongFileName() + "();\n\n";
				
			} else {
				line += "public class test_" + dbcb.getFileName() + " {\n\n";
				line += "\tstatic " + dbcb.getFileName() + " db = new " + dbcb.getFileName() + "();\n\n";
			}
			ArrayList<String> uniqueFields = new ArrayList<String>();
			if (dbcb.getNumberOfFields() > 0) {
				for(ArrayList<String> elements : dbcb.getAllKeyFields()) {
					int counter = 0;
					String fieldName = new String();
					fieldType = new String();
					int fieldDec = 0;
					for (String field : elements) {
						counter++;
						switch (counter) {
							case 2:
								fieldName = field;
								break;
							case 3:
								fieldType = field;
								break;
							case 5:
								if (fieldType.equals("int")) {
									fieldDec = 0;
								} else {
									fieldDec = Integer.parseInt(field);
								}
								break;
						}
					}
	
					if (!uniqueFields.contains(fieldName)) {
						uniqueFields.add(fieldName);
						if (fieldType.equals("String")) {
							line += "\tstatic String\tsaveKey" + fieldName + " = new String();\n";
						} else if (fieldType.equals("int")) {
							if (fieldDec == 0) {
								line += "\tstatic int\t\tsaveKey" + fieldName + " = 0;\n";
							} else {
								line += "\tstatic double\tsaveKey" + fieldName + " = 0.0;\n";
							}	
						} else if (fieldType.equals("long")) {
							if (fieldDec == 0) {
								line += "\tstatic long\tsaveKey" + fieldName + " = 0;\n";
							} else {
								line += "\tstatic double\tsaveKey" + fieldName + " = 0.0;\n";
							}
						} else if (fieldType.equals("double")) {
							line += "\tstatic double\tsaveKey" + fieldName + " = 0.0;\n";
						}
					}
				}
			}
			line += "\n";
			line += "\tpublic static void main(String[] args) {\n\n";
			line += "\t\tString company = \"" + company + "\";\n";
			line += "\t\tMsSQL dbMSSQL = new MsSQL(company);\n\n";
			
			line += "\t\tdb.setUpdateOK(true);\n\n";
			line += "\t\ttry {\n";
			line += "\t\t\tdb.setConn(dbMSSQL.connect());\n";
			line += "\t\t} catch (SQLException e) {\n";
			line += "\t\t\te.printStackTrace();\n";
			line += "\t\t}\n\n";
			
			line += "\t\tCollection<ArrayList<String>> getAllFields = db.getAllFields();\n";
			text.append(line);
			line = "\t\tfor (ArrayList<String> element : getAllFields) {\n";
			line += "\t\t\tint count = 0;\n";
			line += "\t\t\tString fieldName = new String();\n";
			line += "\t\t\tString fieldType = new String();\n";
			line += "\t\t\tString setter = new String();\n";
			line += "\t\t\tString getter = new String();\n";
			line += "\t\t\tint fieldSize = 0;\n";
			line += "\t\t\tint fieldDec = 0;\n";
			line += "\t\t\tfor (String field : element) {\n";
			line += "\t\t\t\tcount++;\n";
			line += "\t\t\t\tswitch (count) {\n";
			line += "\t\t\t\t\tcase 2:\n";
			line += "\t\t\t\t\t\tfieldName = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 3:\n";
			line += "\t\t\t\t\t\tfieldType = field;\n";
			line += "\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 4:\n";
			line += "\t\t\t\t\t\tfieldSize = Integer.parseInt(field);\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 5:\n";
			line += "\t\t\t\t\t\tfieldDec = Integer.parseInt(field);\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 7:\n";
			line += "\t\t\t\t\t\tsetter = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 8:\n";
			line += "\t\t\t\t\t\tgetter = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t}\n";
			line += "\t\t\t}\n";
			line += "\t\t\tSystem.out.println(fieldName.trim() + \" \" + fieldType + \" \" + fieldSize + \" \" + fieldDec + \" \" + setter + \" \" + getter);\n";
			line += "\t\t}\n\n";
	
			line += "\t\tCollection<ArrayList<String>> getAllKeyFields = db.getAllKeyFields();\n";
			line += "\t\tfor (ArrayList<String> element : getAllKeyFields) {\n";
			line += "\t\t\tint count = 0;\n";
			line += "\t\t\tString fieldName = new String();\n";
			line += "\t\t\tString fieldType = new String();\n";
			line += "\t\t\tString setter = new String();\n";
			line += "\t\t\tString getter = new String();\n";
			line += "\t\t\tint fieldSize = 0;\n";
			line += "\t\t\tint fieldDec = 0;\n";
			line += "\t\t\tfor (String field : element) {\n";
			line += "\t\t\t\tcount++;\n";
			line += "\t\t\t\tswitch (count) {\n";
			line += "\t\t\t\t\tcase 2:\n";
			line += "\t\t\t\t\t\tfieldName = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 3:\n";
			line += "\t\t\t\t\t\tfieldType = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 4:\n";
			line += "\t\t\t\t\t\tfieldSize = Integer.parseInt(field);\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 5:\n";
			line += "\t\t\t\t\t\tfieldDec = Integer.parseInt(field);\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 7:\n";
			line += "\t\t\t\t\t\tsetter = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t\tcase 8:\n";
			line += "\t\t\t\t\t\tgetter = field;\n";
			line += "\t\t\t\t\t\tbreak;\n";
			line += "\t\t\t\t}\n";
			line += "\t\t\t}\n";
			line += "\t\t\tSystem.out.println(fieldName.trim() + \" \" + fieldType + \" \" + fieldSize + \" \" + fieldDec + \" \" + setter + \" \" + getter);\n";
			line += "\t\t}\n\n";
			
			line += "\t\tArrayList<String> allKeyFiles = db.getAllKeyFiles();\n";
			line += "\t\tfor (String fileName : allKeyFiles) {\n";
			line += "\t\t\tSystem.out.println(fileName.trim());\n";
			line += "\t\t}\n\n";
			
			line += "\t\tArrayList<String> allPhysicalKeyFieldNames = db.getAllPhysicalKeyFieldNames();\n";
			line += "\t\tfor (String fieldName : allPhysicalKeyFieldNames) {\n";
			line += "\t\t\tSystem.out.println(fieldName.trim());\n";
			line += "\t\t}\n\n";
			
			line += "\t\tArrayList<String> allLogicalKeyFieldNames = db.getAllLogicalKeyFieldNames();\n";
			line += "\t\tfor (String fieldName : allLogicalKeyFieldNames) {\n";
			line += "\t\t\tSystem.out.println(fieldName.trim());\n";
			line += "\t\t}\n\n";
	
			line += "\t\tCollection<ArrayList<String>> allLogicalKeyFields = db.getallLogicalKeyFields();\n";
			line += "\t\tfor (ArrayList<String> fieldNames : allLogicalKeyFields) {\n";
			line += "\t\t\tSystem.out.println(fieldNames);\n";
			line += "\t\t}\n\n";
	
			// add readfirst & delete function
			line += "\t\tdeleteRecords();\n\n";
			
			// add function
			line += "\t\taddRecords();\n\n";
			
			if (dbcb.getNumberOfKeyFields() > 0) {
				for(ArrayList<String> elements : dbcb.getAllKeyFields()) {
					int counter = 0;
					String fieldName = new String();
					for (String field : elements) {
						counter++;
						switch (counter) {
							case 2:
								fieldName = field;
								line += "\t\tSystem.out.println(saveKey" + fieldName.trim() + ");\n";
								break;
						}
					}
				}
				line += "\n";
			}
			
			// read function
			line += "\t\treadRecords();\n\n";
			
			if (dbcb.isHasKeysInd()) {
				// read equal function
				line += "\t\treadEqualRecords();\n\n";
				if (dbcb.isHasMultipleKeysInd()) {
					for(String lglFile : dbcb.getLogicalFiles()) {
						line += "\t\treadEqualRecords" + lglFile + "();\n\n";
					}
				}
			}
			
			if (dbcb.isHasKeysInd()) {
				// read update function
				line += "\t\tUpdateRecords();\n\n";
			}
					
			line += "\t\tSystem.out.println(\"Test complete.\");\n";
			line += "\t}";
			text.append(line + "\n\n");
			
			// begin delete function
			line = "\tpublic static void deleteRecords() {\n\n";
			line += "\t\tint count = 0;\n";
			line += "\t\ttry {\n";
			line += "\t\t\tdb.readFirst();\n";
			line += "\t\t\tdb.delete();\n";
			line += "\t\t\tcount++;\n";
			line += "\t\t\twhile (db.readNext()) {\n";
			line += "\t\t\t\tdb.delete();\n";
			line += "\t\t\t\tcount++;\n";
			line += "\t\t\t}\n";
			line += "\t\t} catch (SQLException e1) {\n";
			line += "\t\t\te1.printStackTrace();\n";
			line += "\t\t}\n\n";
			line += "\t\tSystem.out.println(count + \" records deleted\");\n";
			line += "\t}\n\n";
			text.append(line);
			
			// begin add function
			line = "\tpublic static void addRecords() {\n\n";
	
			line += "\t\tint\t\tcount = 0;\n";
			// define file fields
			String fieldName = new String();
			fieldType = new String();
			int fieldSize = 0;
			int fieldDec = 0;
			for (ArrayList<String> element : fields) {
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
						case 4:
							fieldSize = Integer.parseInt(field);
							break;
						case 5:
							if (fieldType.equals("int")) {
								fieldDec = 0;
							} else {
								fieldDec = Integer.parseInt(field);
							}
							break;
					}
				}
	
				int randomNumber = (int) Math.ceil(Math.random() * fieldSize);
				if (fieldDec > 0) {
					switch (fieldDec) {
						case 1:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0." + randomNumber + ";\n";
							break;
						case 2:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0" + randomNumber + ";\n";
							break;
						case 3:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00" + randomNumber + ";\n";
							break;
						case 4:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000" + randomNumber + ";\n";
							break;
						case 5:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000" + randomNumber + ";\n";
							break;
						case 6:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000" + randomNumber + ";\n";
							break;
						case 7:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000" + randomNumber + ";\n";
							break;
						case 8:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000" + randomNumber + ";\n";
							break;
						case 9:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000000" + randomNumber + ";\n";
							break;
						case 10:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000" + randomNumber + ";\n";
							break;
						case 11:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000000" + randomNumber + ";\n";
							break;
						case 12:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000000000" + randomNumber + ";\n";
							break;
						case 13:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000000" + randomNumber + ";\n";
							break;
						case 14:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000000000" + randomNumber + ";\n";
							break;
						case 15:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000000000000" + randomNumber + ";\n";
							break;
						case 16:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000000000" + randomNumber + ";\n";
							break;
						case 17:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000000000000" + randomNumber + ";\n";
							break;
						case 18:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000000000000000" + randomNumber + ";\n";
							break;
						case 19:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000000000000" + randomNumber + ";\n";
							break;
						case 20:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000000000000000" + randomNumber + ";\n";
							break;
						case 21:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000000000000000000" + randomNumber + ";\n";
							break;
						case 22:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000000000000000" + randomNumber + ";\n";
							break;
						case 23:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000000000000000000" + randomNumber + ";\n";
							break;
						case 24:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.00000000000000000000000" + randomNumber + ";\n";
							break;
						case 25:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000000000000000000" + randomNumber + ";\n";
							break;
						case 26:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.0000000000000000000000000" + randomNumber + ";\n";
							break;
						case 31:
							line += "\t\tdouble\t" + fieldName + "counter\t= 0.000000000000000000000000000000" + randomNumber + ";\n";
							break;
					}
				} else
					if (!fieldType.equals("String")) {
						line += "\t\tint\t\t" + fieldName + "counter\t= (int) Math.ceil(Math.random() * " + fieldSize + ");\n";
					}
			}
			
			line += "\n\t\tdb.setUpdateOK(true);\n\n";
			if (stringsExist) {
				line += "\t\tLoremIpsum loremIpsum = new LoremIpsum();\n";
				line += "\t\tString liWords = loremIpsum.getWords();\n";
				line += "\t\tint ipsumCounter = (int) Math.ceil(Math.random());\n\n";
			}
			line += "\t\tfor (int i = 0; i < " + recordsToBuild + "; i ++) {\n";
			
			// define file fields
			fieldName = new String();
			fieldType = new String();
			fieldSize = 0;
			fieldDec = 0;
			int count = 0;
			for (ArrayList<String> element : fields) {
				int counter1 = 0;
				for (String field : element) {
					counter1++;
					if (counter1 > 4) break;
					switch (counter1) {
						case 2:
							fieldName = field;
							break;
						case 3:
							fieldType = field;
							break;
						case 4:
							fieldSize = Integer.parseInt(field);
							break;
					}
				}
				
				if (fieldType.equals("String")) {
					line += "\t\t\tliWords = loremIpsum.getWords(" + Integer.toString(fieldSize) + ", ipsumCounter);\n";
					line += "\t\t\tdb.set" + fieldName + "(liWords.substring(0," + Integer.toString(fieldSize) + "));\n";
					line += "\t\t\tipsumCounter += (int) Math.ceil(Math.random() * " + Integer.toString(fieldSize) + ");\n";
					line += "\t\t\tif (ipsumCounter >= 50) ipsumCounter = (int) Math.ceil(Math.random());\n";
				} else {
					line += "\t\t\tdb.set" + fieldName + "(" + fieldName + "counter);\n";
				}
			}
			line += "\n";
			
			if (dbcb.getNumberOfKeyFields() > 0) {
				line += "\t\t\tif (i == 500) {\n";
				for(ArrayList<String> elements : dbcb.getAllKeyFields()) {
					int counter2 = 0;
					for (String field : elements) {
						counter2++;
						switch (counter2) {
							case 2:
								fieldName = field;
								if (fieldName.length() > 4) {
									line += "\t\t\t\tsaveKey" + fieldName + "\t= db.get" + fieldName + "();\n";
								} else {
									line += "\t\t\t\tsaveKey" + fieldName + "\t\t= db.get" + fieldName + "();\n";
								}
								break;
						}
					}
				}
				line += "\t\t\t}\n\n";
			}
			
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
					}
				}
				
				if (!fieldType.equals("String")) {
					if (fieldDec == 0) {
						line += "\t\t\t" + fieldName + "counter++;\n";
					} else if (fieldType.equals("int")) {
						line += "\t\t\t" + fieldName + "counter++;\n";
					} else {
						switch (fieldDec) {
							case 1:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.1;\n";
								break;
							case 2:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.01;\n";
								break;
							case 3:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.001;\n";
								break;
							case 4:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.0001;\n";
								break;
							case 5:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.00001;\n";
								break;
							case 6:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.000001;\n";
								break;
							case 7:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.0000001;\n";
								break;
							case 8:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.00000001;\n";
								break;
							case 9:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.000000001;\n";
								break;
							case 10:
								line += "\t\t\t" + fieldName + "counter\t= " + fieldName + "counter + 0.0000000001;\n";
								break;
						}
					}
				}
			}
			line += "\n";
			
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
						case 2:
							fieldName = field;
							break;
						case 3:
							fieldType = field;
							break;
						case 4:
							fieldSize = Integer.parseInt(field);
					}
				}
				
				if (!fieldType.equals("String")) {
					if (fieldSize == 1) {
						line += "\t\t\tif (" + fieldName + "counter == 10) {\n";
						line += "\t\t\t\t" + fieldName + "counter = (int) Math.ceil(Math.random() * 2);\n";
						line += "\t\t\t}\n\n";
					}
					
					if (fieldSize == 2) {
						line += "\t\t\tif (" + fieldName + "counter == 100) {\n";
						line += "\t\t\t\t" + fieldName + "counter = (int) Math.ceil(Math.random() * 3);\n";
						line += "\t\t\t}\n\n";
					}
		
					if (fieldSize == 3) {
						line += "\t\t\tif (" + fieldName + "counter == 1000) {\n";
						line += "\t\t\t\t" + fieldName + "counter = (int) Math.ceil(Math.random() * 4);\n";
						line += "\t\t\t}\n\n";
					}
					
					if (fieldSize == 4) {
						line += "\t\t\tif (" + fieldName + "counter == 10000) {\n";
						line += "\t\t\t\t" + fieldName + "counter = (int) Math.ceil(Math.random() * 5);\n";
						line += "\t\t\t}\n\n";
					}
		
					if ((fieldSize == 5) || (fieldType.equals(doubleString))) {
						line += "\t\t\tif (" + fieldName + "counter == 100000) {\n";
						line += "\t\t\t\t" + fieldName + "counter = (int) Math.ceil(Math.random() * 6);\n";
						line += "\t\t\t}\n\n";
					}
				}
			}
			
			line += "\t\t\ttry {\n";
			line += "\t\t\t\tif (db.add()) {\n";
			line += "\t\t\t\t\tcount++;\n";
			line += "\t\t\t\t}\n";
			line += "\t\t\t} catch (SQLException e) {\n";
			line += "\t\t\t\tSystem.err.println(e.getMessage());\n";
			line += "\t\t\t\tSystem.out.println(db.toString());\n";
			line += "\t\t\t}\n";
			line += "\t\t}\n";
			line += "\t\tSystem.out.println(count + \" records added\");\n";
			line += "\t}\n";
			text.append(line + "\n");
	
			// begin add function
			line = "\tpublic static void readRecords() {\n\n";
			line += "\t\ttry {\n";
			line += "\t\t\tif (db.readFirst()) {\n";
			line += "\t\t\t\tSystem.out.println(\"Read first successful.\");\n";
			line += "\t\t\t\tif (db.readNext()) {\n";
			line += "\t\t\t\t\tSystem.out.println(\"Read next successful.\");\n";
			line += "\t\t\t\t}\n";
			line += "\t\t\t}\n";
			line += "\t\t} catch (SQLException e2) {\n";
			line += "\t\t\te2.printStackTrace();\n";
			line += "\t\t}\n\n";
	
			line += "\t\ttry {\n";
			line += "\t\t\tif (db.readLast()) {\n";
			line += "\t\t\t\tSystem.out.println(\"Read last successful.\");\n";
			line += "\t\t\t\tif (db.readPrevious()) {\n";
			line += "\t\t\t\t\tSystem.out.println(\"Read previous successful.\");\n";
			line += "\t\t\t\t}\n";
			line += "\t\t\t}\n";
			line += "\t\t} catch (SQLException e2) {\n";
			line += "\t\t\te2.printStackTrace();\n";
			line += "\t\t}\n";
			line += "\t}\n\n";
			text.append(line);
			
			if (dbcb.isHasKeysInd()) {
				BuildReadEqualForAllIndexes(dbcb.getAllPhysicalKeyFieldNames(), false, 0);
				if (dbcb.isHasMultipleKeysInd()) {
					count = 0;
					for(ArrayList<String> lglFileKeys : dbcb.getAllLogicalKeyFields()) {
						count++;
						BuildReadEqualForAllIndexes(lglFileKeys, true, count);
					}
				}
	
				// begin update function
				line = "\tstatic public void UpdateRecords() {\n\n";
				line += "\t\tdb.setKeyFields(" + formatKeyFields(dbcb.getAllPhysicalKeyFieldNames()) + ";\n";
				line += "\t\ttry {\n";
				line += "\t\t\tSystem.out.println(db.exists());\n";
				line += "\t\t\tSystem.out.println(\"Record found: \" + db.get());\n";
				line = line + setKeyGetters(fields, numberOfKeyFields) + "\n";
				line += "\t\t\tif (db.update()) {\n";
				line += "\t\t\t\tSystem.out.println(\"Update successful.\");\n";
				line += "\t\t\t}\n";
				line += "\t\t} catch (SQLException e1) {\n";
				line += "\t\t\te1.printStackTrace();\n";
				line += "\t\t}\n";
				line += "\t}\n";
				text.append(line);
				BuildGetters();
				// close class
				line = "}";
				text.append(line);
			} else {
				// close class
				line = "}";
				text.append(line);
			}
	
			if (dbcb.getLongFileName() != null) {
				try (FileOutputStream out = new FileOutputStream(new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompany() + "\\src\\com\\test\\" + getDataBase() + "\\test_" + dbcb.getLongFileName() + ".java"))) {
					out.write(text.toString().getBytes());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try (FileOutputStream out = new FileOutputStream(new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompany() + "\\src\\com\\test\\" + getDataBase() + "\\test_" + getFileName() + ".java"))) {
					out.write(text.toString().getBytes());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String formatKeyFields(ArrayList<String>  arrayList) {
		
		String fieldName = new String();
		String line = new String();
		int nkeyfld = arrayList.size();
		int counter = 0;
		for (String keyField : arrayList) {
			counter++;
			fieldName = keyField.trim();
			if (counter == 1) {
				line = "getSaveKey" + fieldName + "()";
				if (nkeyfld == 1) line += ")";
			} else {
				if (counter < nkeyfld) {
					line += ", getSaveKey" + fieldName + "()";
				} else {
					line += ", getSaveKey" + fieldName + "())";
				}
			}
		}
		
		return line;
	}
	
	private String formatLogicalKeyFields(String fileToProcess, Collection<ArrayList<String>> allFields) {
		
		String line = new String();
		for (ArrayList<String> element : allFields) {
			int counter1 = 0;
			String fileName = new String();
			String field = new String();
			for (String fileField : element) {
				counter1++;
				switch (counter1) {
					case 1:
						fileName = fileField;
						break;
					default:
						if (fileName.equals(fileToProcess)) {
							field = fileField;
						}
						break;
				}
				if (counter1 == 2) {
					line = "getSaveKey" + field.trim() + "()";
				} else if (counter1 > 2) {
					line += ", getSaveKey" + field.trim() + "()";
				}
			}
			if (fileName.equals(fileToProcess)) break;
		}
			
		return line;
		
	}
	
	public static String setKeyGetters(Collection<ArrayList<String>> allFields, int nkeyfld) {
		
		String fieldName = new String();
		String one = "1";
		String keyField = new String();
		String line = new String();
		int counter1 = 0;
		for (ArrayList<String> element : allFields) {
			int counter2 = 0;
			for (String field : element) {
				counter2++;
				switch (counter2) {
					case 2:
						fieldName = field;
						break;
					case 6:
						keyField = field;
						break;
				}
			}
			
			if (keyField.equals(one)) {
				counter1++;
				line += "\t\t\tdb.set" + fieldName + "(";
				if ((counter1 == 1) && (counter1 < nkeyfld)) {
					line = "getSaveKey" + fieldName + "());";
				} else {
					if (counter1 < nkeyfld) {
						line += "getSaveKey" + fieldName + "());";
					} else {
						line += "getSaveKey" + fieldName + "());";
					}
				}
				line += "\n";
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
		if (lglFileInd) line = "\tstatic public void readEqualRecords" + file + "() {\n\n";
		else line = "\tstatic public void readEqualRecords() {\n\n";
		line += "\t\ttry {\n";
		// define key fields
		if (lglFileInd) {
			// int count =0;
			for (String element : keys) {
				line += "\t\t\tdb.setUseKey" + element.trim() + "(false);\n";
			}
		}
		if (lglFileInd) line += "\t\t\tdb.setKeyFields" + file + "(" + formatLogicalKeyFields(file, dbcb.getAllLogicalKeyFieldList()) + ");\n";
		else line += "\t\t\tdb.setKeyFields(" + formatKeyFields(dbcb.getAllPhysicalKeyFieldNames()) + ";\n";
		if (lglFileInd) {
			int count =0;
			for (String element : keys) {
				count++;
				if (count == keys.size() && keys.size() > 1) {
					line += "\t\t\tdb.setUseKey" + element.trim() + "(false);\n";
				}
			}
		}
		if (lglFileInd) line += "\t\t\tif (db.readEqualFirst" + file + "()) {\n";
		else line += "\t\t\tif (db.readEqualFirst()) {\n";
		line += "\t\t\t\tSystem.out.println(\"Read equal for " + file + " first successful.\");\n";
		line += "\t\t\t\tSystem.out.println(db.toString());\n";
		line += "\t\t\t}\n";
		line += "\t\t\tif (db.readEqualNext()) {\n";
		line += "\t\t\t\tSystem.out.println(\"Read equal for " + file + " next successful.\");\n";
		line += "\t\t\t\tSystem.out.println(db.toString());\n";
		line += "\t\t\t}\n";
		// define key fields
		if (lglFileInd) line += "\t\t\tdb.setKeyFields" + file + "(" + formatLogicalKeyFields(file, dbcb.getAllLogicalKeyFieldList()) + ");\n";
		else line += "\t\t\tdb.setKeyFields(" + formatKeyFields(dbcb.getAllPhysicalKeyFieldNames()) + ";\n";
		if (lglFileInd) line += "\t\t\tif (db.readEqualLast" + file + "()) {\n";
		else line += "\t\t\tif (db.readEqualLast()) {\n";
		line += "\t\t\t\tSystem.out.println(\"Read equal for " + file + " last successful.\");\n";
		line += "\t\t\t\tSystem.out.println(db.toString());\n";
		line += "\t\t\t}\n";
		line += "\t\t\tif (db.readEqualPrevious()) {\n";
		line += "\t\t\t\tSystem.out.println(\"Read equal for " + file + " previous successful.\");\n";
		line += "\t\t\t\tSystem.out.println(db.toString());\n";
		line += "\t\t\t}\n";
		line += "\t\t} catch (SQLException e1) {\n";
		line += "\t\t\te1.printStackTrace();\n";
		line += "\t\t}\n";
		line += "\t}\n\n";
		text.append(line);
	}
	
	private void BuildGetters() {
		
		ArrayList<String> uniqueFields = new ArrayList<String>();
		for(ArrayList<String> elements : dbcb.getAllKeyFields()) {
			int counter = 0;
			String fieldName = new String();
			String fieldType = new String();
			for (String field : elements) {
				counter++;
				switch (counter) {
					case 2:
						fieldName = field;
						break;
					case 3:
						fieldType = field;
						break;
				}
			}
			
			if (!uniqueFields.contains(fieldName)) {
				uniqueFields.add(fieldName);
				text.append("\n");
				String line = new String();
				if (fieldType.equals("String")) {
					line = "\tprivate static String getSaveKey" + fieldName + "() {\n";
				}
				if (fieldType.equals("double")) {
					line = "\tprivate static double getSaveKey" + fieldName + "() {\n";
				}
				if (fieldType.equals("int")) {
					line = "\tprivate static int getSaveKey" + fieldName + "() {\n";
				}
				if (fieldType.equals("long")) {
					line = "\tprivate static long getSaveKey" + fieldName + "() {\n";
				}
				line += "\t\treturn saveKey" + fieldName + ";\n"; 
				line += "\t}\n";
				text.append(line);
			}
		}
	}
}