package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class DBCopyBuilder {
	
	private static StringBuilder text = new StringBuilder();
	private String companyName;
	private String dataBase;
	private String libraryName;
	private String longLibraryName;
	private String fileName;
	private String longFileName;
	private String dataSource;
	private Boolean classBuilt;
	
	public DBCopyBuilder(String companyName, String dataBase, String libraryName, String fileName, String longLibraryName, String longFileName, String dataSource) {
		super();
		this.companyName = companyName;
		this.dataBase = dataBase;
		this.libraryName = libraryName;
		this.fileName = fileName;
		this.longLibraryName = longLibraryName;
		this.longFileName = longFileName;
		this.dataSource = dataSource;
		this.classBuilt = false;
	}


	public DBCopyBuilder(String companyName, String dataBase, String libraryName, String fileName, String dataSource) {
		super();
		this.companyName = companyName;
		this.dataBase = dataBase;
		this.libraryName = libraryName;
		this.fileName = fileName;
		this.longLibraryName = libraryName;
		this.longFileName = fileName;
		this.dataSource = dataSource;
		this.classBuilt = false;
	}

	public DBCopyBuilder(String companyName, String dataBase, String libraryName, String fileName) {
		super();
		this.companyName = companyName;
		this.dataBase = dataBase;
		this.libraryName = libraryName;
		this.fileName = fileName;
		this.longLibraryName = libraryName;
		this.longFileName = fileName;
		this.dataSource = new String();
		this.classBuilt = false;
	}

	public void BuildCopyClass() {
		GetFileInfo();
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

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLongLibraryName() {
		return longLibraryName;
	}

	public void setLongLibraryName(String longLibraryName) {
		this.longLibraryName = longLibraryName;
	}

	public String getLongFileName() {
		return longFileName;
	}

	public void setLongFileName(String longFileName) {
		this.longFileName = longFileName;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public Boolean getClassBuilt() {
		return classBuilt;
	}

	public void setClassBuilt(Boolean classBuilt) {
		this.classBuilt = classBuilt;
	}

	private void GetFileInfo() {
		
		DBClassBuilder dbcb;
		if (getLongFileName().isEmpty())
			dbcb = new DBClassBuilder(getCompanyName());
		else
			dbcb = new DBClassBuilder(getLongLibraryName());
		Collection<ArrayList<String>> fileFields = new ArrayList<ArrayList<String>>();
		String dBase = getDataBase();
		
		dbcb.setCompanyName(getCompanyName());
		dbcb.setDataBase(getDataBase());
		dbcb.setFileName(getFileName().trim());
		dbcb.setLongLibraryName(getLongLibraryName());
		dbcb.setLongFileName(getLongFileName());
		if (dbcb.readJSON()) {
			fileFields = dbcb.getAllFields();
			dbcb.closeConnection();
			
			if (!fileFields.isEmpty()) {
				String line = "package com.copy." + getDataBase() + ";\n\n";
		
		        // write class heading
				line += "import java.io.BufferedReader;\n";
				line += "import java.io.FileInputStream;\n";
				line += "import java.io.InputStreamReader;\n";
				line += "import java.io.FileNotFoundException;\n";
				line += "import java.io.IOException;\n";
				line += "import java.io.UnsupportedEncodingException;\n";
				line += "import java.sql.Connection;\n";
				line += "import java.sql.SQLException;\n\n";
		
				if (getLongFileName().isEmpty())
					line += "import com.database." + getDataBase() + "." + getFileName().trim() + ";\n\n";
				else
					line += "import com.database." + getDataBase() + "." + getLongFileName() + ";\n\n";
		        // write class heading
		        if (dBase.equals("mssql")) {
		        	line += "import model.MsSQL;\n\n";
		        } else if (dBase.equals("mysql")) {
		        	line += "import model.MySQL;\n\n";
		        } else if (dBase.equals("oracle")) {
		        	line += "import model.Oracle;\n\n";
		        }
		        line += "import model.CheckTime;\n\n";
		        if (getLongFileName().isEmpty())
		        	line += "public class copy_" + getLibraryName().trim() + "_" + getFileName().trim().trim() + " extends " + getFileName().trim().trim() + " {\n\n";
		        else
		        	line += "public class copy_" + getLongLibraryName().trim() + "_" + getFileName().trim().trim() + " extends " + getLongFileName().trim() + " {\n\n";
		        line += "\tpublic String run" + getFileName().trim() + "() {\n\n";
		        line += "\t\tString company = \"" + getCompanyName() + "\";\n";
		        line += "\t\tString returnString = new String();\n";
		        line += "\t\tCheckTime ct = new CheckTime();\n";
		        if (!getLongFileName().isEmpty()) {
		        	line += "\t\tsetLongLibraryName(\"" + getLongLibraryName().trim() + "\");\n";
		        	line += "\t\tsetLongFileName(\"" + getLongFileName().trim() + "\");\n";
		        }
	
		        if (getLongFileName().isEmpty()) {
			        if (dBase.equals("mssql")) {
			        	line += "\t\tConnection connMSSQL = null;\n";
						line += "\t\tMsSQL dbMSSQL = new MsSQL(company);\n";
			        } else if (dBase.equals("mysql")) {
			        	line += "\t\tConnection connMYSQL = null;\n";
						line += "\t\tMySQL dbMYSQL = new MySQL(company);\n";
			        } else if (dBase.equals("oracle")) {
			        	line += "\t\tConnection connORACLE = null;\n";
						line += "\t\tOracle dbORACLE = new Oracle(company);\n";
			        }
		        } else {
		        	if (dBase.equals("mssql")) {
			        	line += "\t\tConnection connMSSQL = null;\n";
						line += "\t\tMsSQL dbMSSQL = new MsSQL(\"" + getLongLibraryName().trim() +  "\");\n";
			        } else if (dBase.equals("mysql")) {
			        	line += "\t\tConnection connMYSQL = null;\n";
						line += "\t\tMySQL dbMYSQL = new MySQL(\"" + getLongLibraryName().trim() +  "\");\n";
			        } else if (dBase.equals("oracle")) {
			        	line += "\t\tConnection connORACLE = null;\n";
						line += "\t\tOracle dbORACLE = new Oracle(\"" + getLongLibraryName() +  "\");\n";
			        }
		        }
		        
				line += "\t\tsetCompanyName(company);\n";
				line += "\t\tsetDataBase(\"" + getDataBase() + "\");\n";
				line += "\t\tsetFileName(\"" + getFileName().trim() + "\");\n";
				line += "\t\treadJSON();\n";
				line += "\t\ttry {\n";
				if (dBase.equals("mssql")) {
					line += "\t\t\tconnMSSQL = dbMSSQL.connect();\n";
					line += "\t\t\tsetConn(connMSSQL);\n";
					line += "\t\t\tdbMSSQL.closeConnection(connMSSQL);\n";
		        } else if (dBase.equals("mysql")) {
		        	line += "\t\t\tconnMYSQL = dbMYSQL.connect();\n";
					line += "\t\t\tsetConn(connMYSQL);\n";
					line += "\t\t\tdbMYSQL.closeConnection(connMYSQL);\n";
		        } else if (dBase.equals("oracle")) {
		        	line += "\t\t\tconnORACLE = dbORACLE.connect();\n";
					line += "\t\t\tsetConn(connORACLE);\n";
					line += "\t\t\tdbORACLE.closeConnection(connORACLE);\n";
		        }
				
				line += "\t\t} catch (SQLException e1) {\n";
		        line += "\t\t\te1.printStackTrace();\n";
		        line += "\t\t}\n\n";
		        
		        line += "\t\tString fileInputStream = new String();\n";
		        line += "\t\tfileInputStream = \"C:\\\\Users Shared Folders\\\\markfl\\\\Documents\\\\My Development\\\\My SQL Source\\\\" + getCompanyName().trim() + "\\\\data\\\\" + getDataSource().trim() + "\\\\" + getFileName().trim() + ".csv\";\n";
		        line += "\t\tdouble counterTotal = getRecordCount(getCompanyName(), \"" + getDataSource().trim() + "\", getFileName().trim(), fileInputStream);\n";
				line += "\t\tSystem.out.println((int) counterTotal + \" record(s) to copy to " + getFileName().trim() +  ".\");\n\n";
				
		        line += "\t\tsetsupressErrorMsg(true);\n";
		        line += "\t\tdouble counter = 0.0;\n";
		        line += "\t\tint errorCounter = 0;\n";
		        line += "\t\ttry (BufferedReader in = new BufferedReader(new\n";
		        if (getDataSource().isEmpty()) {
			        line += "\t\t\tInputStreamReader(new FileInputStream(fileInputStream), \"UTF-8\"))) {\n";

		        } else {
			        line += "\t\t\tInputStreamReader(new FileInputStream(fileInputStream), \"UTF-8\"))) {\n";
		        }
		        line += "\t\t\tString line;\n";
		        line += "\t\t\tString splitBy = \"\\\\t\";\n";
		        if (dBase.equals("mssql")) {
		        	line += "\t\t\tsetConn(dbMSSQL.connect());\n";
		        } else if (dBase.equals("mysql")) {
		        	line += "\t\t\tsetConn(dbMYSQL.connect());\n";
		        } else if (dBase.equals("oracle")) {
		        	line += "\t\t\tsetConn(dbORACLE.connect());\n";
		        }
		        line += "\t\t\tsetUpdateOK(true);\n";
		        line += "\t\t\treadFirst();\n";
		        line += "\t\t\tSystem.out.println(\"Clearing " + getFileName().trim() + "\");\n";
		        line += "\t\t\tdelete();\n";
		        line += "\t\t\tSystem.out.println(\"Copying data to " + getFileName().trim() + "\");\n";
				line += "\t\t\twhile ((line  = in.readLine()) != null ) {\n";
		        line += "\t\t\t\tString records[] = line.split(splitBy);\n";
				int counter1 = 0;
				for (ArrayList<String> element : fileFields) {
					String setter = new String();
					String fldType = new String();
					int counter2 = 0;
					for (String field : element) {
						counter2++;
						if (counter2 == 9) break;
						switch (counter2) {
							case 3:
								fldType = field;
								break;
							case 7:
								setter = field;
								break;
						}
					}
					if ((fldType.equals("double")) || (fldType.equals("int")) 
					|| (fldType.equals("long"))) {
						line += "\t\t\t\ttry {\n";
					}
					int counter3 = counter1 + 1;
					if (fldType.equals("String")) {
						line += "\t\t\t\tif (records.length >= " + counter3 + ")\n";
					} else {
						line += "\t\t\t\t\tif (records.length >= " + counter3 + ")\n";
					}
					if (fldType.equals("String"))
						line += "\t\t\t\t\t" + setter + "(records[" + counter1 + "]);\n";
					else if (fldType.equals("double"))
						line += "\t\t\t\t\t\t" + setter + "(Double.parseDouble(records[" + counter1 + "]));\n";
					else if (fldType.equals("int"))
						line += "\t\t\t\t\t\t" + setter + "(Integer.parseInt(records[" + counter1 + "]));\n";
					else if (fldType.equals("long"))
						line += "\t\t\t\t\t\t" + setter + "(Long.parseLong(records[" + counter1 + "]));\n";
					if ((fldType.equals("double")) || (fldType.equals("int")) 
					|| (fldType.equals("long"))) {
						line += "\t\t\t\t} catch(NumberFormatException e) {\n";
						line += "\t\t\t\t}\n";
					}
					counter1++;
				}
		        line += "\t\t\t\ttry {\n";
		        line += "\t\t\t\t\tsetUpdateOK(true);\n";
		        line += "\t\t\t\t\tadd();\n";
		        line += "\t\t\t\t\tcounter++;\n";
		        line += "\t\t\t\t\tint m = (int) counter % 100000;\n";
		        line += "\t\t\t\t\tif (m == 0) {\n";
		        line += "\t\t\t\t\t\tdouble counterDiff = counter / counterTotal;\n";
		        line += "\t\t\t\t\t\tint counterPercent = (int) (counterDiff * 100);\n";
		        line += "\t\t\t\t\t\tint printCounterTotal = (int) counterTotal;";
		        line += "\t\t\t\t\t\tSystem.out.println((int) counter + \" records of \" + printCounterTotal + \" written to " + getFileName().trim() + ". \" + counterPercent + \"% complete.\");\n";
		        line += "\t\t\t\t\t}\n";
		        line += "\t\t\t\t} catch (SQLException e) {\n";
		        line += "\t\t\t\t\te.printStackTrace();\n";
		        line += "\t\t\t\t\terrorCounter++;\n";
		    	line += "\t\t\t\t}\n";
		    	line += "\t\t\t}\n";
		    	line += "\t\t\treturnString = ct.calculateElapse(\"Copy\", \"" + getFileName().trim() + "\", (int) counter);\n";
		    	line += "\t\t\treturnString = returnString.trim() + \" \" + (int) counter + \" records copied.\";\n";
		    	line += "\t\t\tdbMSSQL.closeConnection(connMSSQL);\n";
		        line += "\t\t} catch (UnsupportedEncodingException e) {\n";
		        line += "\t\t\te.printStackTrace();\n";
		        line += "\t\t\terrorCounter++;\n";
		        line += "\t\t} catch (FileNotFoundException e) {\n";
		        line += "\t\t\te.printStackTrace();\n";
		        line += "\t\t\terrorCounter++;\n";
		        line += "\t\t} catch (IOException e) {\n";
		        line += "\t\t\te.printStackTrace();\n";
		        line += "\t\t\terrorCounter++;\n";
				line += "\t\t} catch (SQLException e1) {\n";
				line += "\t\t\te1.printStackTrace();\n";
				line += "\t\t\terrorCounter++;\n";
				line += "\t\t}\n\n";
				line += "\t\tif (errorCounter == 0) {\n";
				line += "\t\t\treturn returnString;\n";
				line += "\t\t} else {\n";
				line += "\t\t\treturn returnString + errorCounter + \" errors occured.\";\n";
				line += "\t\t}\n";
		        line += "\t}\n";
		        line += "}";
		        WriteJavaSourceLine(line);
		        WriteClass();
			}
			this.classBuilt = true;
		}
	}
	
	private void WriteJavaSourceLine(String line) {
		text.append(line);
	}
	
	private void WriteClass() {
		String outputString = new String();
		if (getLongFileName().isEmpty())
			outputString = "C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\src\\com\\copy\\" + getDataBase().trim() + "\\copy_" + getLibraryName().trim() + "_" + getFileName().trim().trim() + ".java";
		else
			outputString = "C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\" + getCompanyName() + "\\src\\com\\copy\\" + getDataBase().trim() + "\\copy_" + getLongLibraryName().trim() + "_" + getFileName().trim().trim() + ".java";
	    try (FileOutputStream out = new FileOutputStream(new File(outputString))) {
			out.write(text.toString().getBytes());
			text.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}