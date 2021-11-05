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
import model.MySQL;
import model.Oracle;

public class DBCopyBuilder {
	
	static StringBuilder text = new StringBuilder();
	private Connection conn;
	private String companyName;
	private String dataBase;
	private String libraryName;
	private String fileName;
	
	public DBCopyBuilder(String companyName, String dataBase, String libraryName, String fileName) {
		super();
		this.companyName = companyName;
		this.dataBase = dataBase;
		this.libraryName = libraryName;
		this.fileName = fileName;
	}

	public void BuildCopyClass() {
		GetFileInfo();
	}
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
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

	private void GetFileInfo() {
		
		MsSQL dbMsSQL = null;
		
		DBClassBuilder dbcb = new DBClassBuilder();
		Collection<ArrayList<String>> fileFields = new ArrayList<ArrayList<String>>();
		Connection connMsSQL = null;
		dbMsSQL = new MsSQL(this.companyName);
		String dBase = getDataBase();
		
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
			connMsSQL = dbMsSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbcb.setConn(connMsSQL);
		boolean firstRecord = true;
		PreparedStatement checkStmtSelect = null;
		try {
			checkStmtSelect = connMsSQL.prepareStatement(selectSql);
			checkStmtSelect.setString(1, getFileName());
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	dbcb.setFileName(getFileName());
		    	fileFields = dbcb.getFileFields(firstRecord, resultsSelect, fileFields);
			    firstRecord = false;
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (!fileFields.isEmpty()) {
			String line = "package com." + getCompanyName() + ".copy."+ getDataBase() + "." + getLibraryName() + ";\n";
			WriteJavaSourceLine(line);
	
	        // write class heading
			line = "import java.io.BufferedReader;";
			WriteJavaSourceLine(line);
			line = "import java.io.FileInputStream;";
			WriteJavaSourceLine(line);
			line = "import java.io.InputStreamReader;";
			WriteJavaSourceLine(line);
			line = "import java.io.FileNotFoundException;";
			WriteJavaSourceLine(line);
			line = "import java.io.IOException;";
			WriteJavaSourceLine(line);
			line = "import java.io.UnsupportedEncodingException;";
			WriteJavaSourceLine(line);
			line = "import java.sql.Connection;";
			WriteJavaSourceLine(line);
			line = "import java.sql.PreparedStatement;";
			WriteJavaSourceLine(line);
			line = "import java.sql.ResultSet;";
			WriteJavaSourceLine(line);
			line = "import java.sql.SQLException;";
			WriteJavaSourceLine(line);
			line = "import java.util.ArrayList;";
			WriteJavaSourceLine(line);
			line = "import java.util.Collection;";
			WriteJavaSourceLine(line);
	        WriteBlankLine();
	
	        line = "import com." + getCompanyName() + ".database." + getDataBase() + "." + getFileName() + ";";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        
	        // write class heading
	        line = "import controller.DBClassBuilder;";
	        WriteJavaSourceLine(line);
	        if (dBase.equals("mssql")) {
	        	line = "import model.MsSQL;";
	        } else if (dBase.equals("mysql")) {
	        	line = "import model.MySQL;";
	        }
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        line = "public class copy" + getFileName() + " {";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        line = "\tstatic " + getFileName() + " db = new " + getFileName() + "();";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        
	        line = "\tpublic static void main(String[] args) {";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        line = "\t\tDBClassBuilder dbcb = new DBClassBuilder();";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        line = "\t\tString company = \"" + getCompanyName() + "\";";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();

	        if (dBase.equals("mssql")) {
	        	line = "\t\tConnection connMSSQL = null;";
	        	WriteJavaSourceLine(line);
				line = "\t\tMsSQL dbMSSQL = new MsSQL(company);";
				WriteJavaSourceLine(line);
	        } else if (dBase.equals("mysql")) {
	        	line = "\t\tConnection connMYSQL = null;";
	        	WriteJavaSourceLine(line);
				line = "\t\tMySQL dbMYSQL = new MySQL(company);";
				WriteJavaSourceLine(line);
	        }	        
	        line = "\t\tString selectSql = \"SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"FROM INFORMATION_SCHEMA.COLUMNS \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"where INFORMATION_SCHEMA.COLUMNS.TABLE_NAME = '" + getFileName() + "'\"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, \"";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t+ \"INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION\";";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        
	        line = "\t\tPreparedStatement checkStmtSelect;";
	        WriteJavaSourceLine(line);
			line = "\t\tResultSet resultsSelect;";
			WriteJavaSourceLine(line);
			line = "\t\tCollection<ArrayList<String>> fileFields = new ArrayList<ArrayList<String>>();";
			WriteJavaSourceLine(line);
			line = "\t\ttry {";
			WriteJavaSourceLine(line);
			if (dBase.equals("mssql")) {
				line = "\t\t\tconnMSSQL = dbMSSQL.connect();";
				WriteJavaSourceLine(line);
				line = "\t\t\tdbcb.setConn(connMSSQL);";
				WriteJavaSourceLine(line);
				line = "\t\t\tcheckStmtSelect = connMSSQL.prepareStatement(selectSql);";
	        } else if (dBase.equals("mysql")) {
	        	line = "\t\t\tconnMYSQL = dbMYSQL.connect();";
				WriteJavaSourceLine(line);
				line = "\t\t\tdbcb.setConn(connMYSQL);";
				WriteJavaSourceLine(line);
				line = "\t\t\tcheckStmtSelect = connMYSQL.prepareStatement(selectSql);";
	        }
			WriteJavaSourceLine(line);
			line = "\t\t\tboolean firstRecord = true;";
			WriteJavaSourceLine(line);
			line = "\t\t\tresultsSelect = checkStmtSelect.executeQuery();";
			WriteJavaSourceLine(line);
			line = "\t\t\twhile (resultsSelect.next()) {";
			WriteJavaSourceLine(line);
			line = "\t\t\t\tdbcb.setFileName(\"" + getFileName() + "\");";
			WriteJavaSourceLine(line);
	    	line = "\t\t\t\tfileFields = dbcb.getFileFields(firstRecord, resultsSelect, fileFields);";
	    	WriteJavaSourceLine(line);
	    	line = "\t\t\t\tfirstRecord = false;";
			WriteJavaSourceLine(line);
			line = "\t\t\t}";
	        WriteJavaSourceLine(line);
	        if (dBase.equals("mssql")) {
	        	line = "\t\t\tdbMSSQL.closeConnection(connMSSQL);";
	        } else if (dBase.equals("mysql")) {
	        	line = "\t\t\tdbMYSQL.closeConnection(connMYSQL);";
	        }
			WriteJavaSourceLine(line);
			line = "\t\t} catch (SQLException e1) {";
			WriteJavaSourceLine(line);
	        line = "\t\t\te1.printStackTrace();";
	        WriteJavaSourceLine(line);
	        line = "\t\t}";
	        WriteJavaSourceLine(line);
	        WriteBlankLine();
	        
	        line = "\t\tdb.setsupressErrorMsg(true);";
	        WriteJavaSourceLine(line);
	        line = "\t\tint counter = 0;";
	        WriteJavaSourceLine(line);
	        line = "\t\ttry (BufferedReader in = new BufferedReader(new";
	        WriteJavaSourceLine(line);
	        line = "\t\t\tInputStreamReader(new FileInputStream(\"C:\\\\Users Shared Folders\\\\markfl\\\\Documents\\\\My Development\\\\My SQL Source\\\\" + getCompanyName() + "\\\\data\\\\" + getLibraryName() + "\\\\" + getFileName() + ".csv\"), \"UTF-8\"))) {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\tString line;";
	        WriteJavaSourceLine(line);
	        line = "\t\t\tString splitBy = \"\\\\t\";";
	        WriteJavaSourceLine(line);
	        if (dBase.equals("mssql")) {
	        	line = "\t\t\tdb.setConn(dbMSSQL.connect());";
	        } else if (dBase.equals("mysql")) {
	        	line = "\t\t\tdb.setConn(dbMYSQL.connect());";
	        }
	        WriteJavaSourceLine(line);
	        line = "\t\t\tdb.setUpdateOK(true);";
	        WriteJavaSourceLine(line);
	        line = "\t\t\tdb.readFirst();";
	        WriteJavaSourceLine(line);
	        line = "\t\t\tdb.delete();";
	        WriteJavaSourceLine(line);
			line = "\t\t\twhile ((line  = in.readLine()) != null ) {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\tString records[] = line.split(splitBy);";
	        WriteJavaSourceLine(line);
			int counter1 = 0;
			for (ArrayList<String> element : fileFields) {
				String setter = new String();
				String fldType = new String();
				int counter2 = 0;
				for (String field : element) {
					switch (counter2) {
						case 1:
							fldType = field;
							break;
						case 5:
							setter = field;
							break;
					}
					counter2++;
				}
				if ((fldType.equals("double")) || (fldType.equals("int")) 
				|| (fldType.equals("long"))) {
					line = "\t\t\t\ttry {";
					WriteJavaSourceLine(line);
				}
				int counter3 = counter1 + 1;
				if (fldType.equals("String")) {
					line = "\t\t\t\tif (records.length >= " + counter3 + ")";
				} else {
					line = "\t\t\t\t\tif (records.length >= " + counter3 + ")";
				}
				WriteJavaSourceLine(line);
				if (fldType.equals("String"))
					line = "\t\t\t\t\tdb." + setter + "(records[" + counter1 + "]);";
				else if (fldType.equals("double"))
					line = "\t\t\t\t\t\tdb." + setter + "(Double.parseDouble(records[" + counter1 + "]));";
				else if (fldType.equals("int"))
					line = "\t\t\t\t\t\tdb." + setter + "(Integer.parseInt(records[" + counter1 + "]));";
				else if (fldType.equals("long"))
					line = "\t\t\t\t\t\tdb." + setter + "(Long.parseLong(records[" + counter1 + "]));";
				WriteJavaSourceLine(line);
				if ((fldType.equals("double")) || (fldType.equals("int")) 
				|| (fldType.equals("long"))) {
					line = "\t\t\t\t} catch(NumberFormatException e) {";
					WriteJavaSourceLine(line);
					line = "\t\t\t\t}";
					WriteJavaSourceLine(line);
				}
				counter1++;
			}
	        line = "\t\t\t\ttry {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\tdb.setUpdateOK(true);";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\tdb.add();";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\tcounter++;";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\tint m = counter % 1000;";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\tif (m == 0)";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\t\tSystem.out.println(counter + \" records written\");";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t} catch (SQLException e) {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\t\t\te.printStackTrace();";
	        WriteJavaSourceLine(line);
	    	line = "\t\t\t\t}";
	    	WriteJavaSourceLine(line);
	    	line = "\t\t\t}";
	    	WriteJavaSourceLine(line);
	    	line = "\t\t\tSystem.out.println(counter + \" records written to " + getFileName() + "\");";
	    	WriteJavaSourceLine(line);
	        line = "\t\t} catch (UnsupportedEncodingException e) {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\te.printStackTrace();";
	        WriteJavaSourceLine(line);
	        line = "\t\t} catch (FileNotFoundException e) {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\te.printStackTrace();";
	        WriteJavaSourceLine(line);
	        line = "\t\t} catch (IOException e) {";
	        WriteJavaSourceLine(line);
	        line = "\t\t\te.printStackTrace();";
	        WriteJavaSourceLine(line);
			line = "\t\t} catch (SQLException e1) {";
			WriteJavaSourceLine(line);
			line = "\t\t\te1.printStackTrace();";
			WriteJavaSourceLine(line);
			line = "\t\t}";
	        WriteJavaSourceLine(line);
	        line = "\t}";
	        WriteJavaSourceLine(line);
	        line = "}";
	        WriteJavaSourceLine(line);
		}
	}
	
	private void WriteJavaSourceLine(String line) {
		text.append(line + "\n");
	}
	
	private void WriteBlankLine() {
		text.append("\n");
	}
	
	public void WriteClass() {
	    try (FileOutputStream out = new FileOutputStream(new File(
	    		"C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\BuildJavaSource\\src\\com\\" + getCompanyName() + "\\copy\\" + getDataBase() + "\\" + getLibraryName() + "\\copy" + getFileName() + ".java"))) {
			out.write(text.toString().getBytes());
			text.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}