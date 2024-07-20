package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileFieldCheck extends DBClassBuilder {
	
	private static Connection connLibList;
	
	public FileFieldCheck(String fromLibrary) {
		
		super();
		
		connLibList = null;
		connMSSQL = null;
		MsSQL dmMSSQL;
		dmMSSQL = new MsSQL("liblist");
		try {
			connLibList = dmMSSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dmMSSQL = new MsSQL(fromLibrary);
		try {
			connMSSQL = dmMSSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public FileFieldCheck(String fromLibrary, Connection connection) {
		
		super();
		
		connLibList = connection;
		connMSSQL = null;
		MsSQL dmMSSQL;
		dmMSSQL = new MsSQL(fromLibrary);
		try {
			connMSSQL = dmMSSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Boolean checkFileName(String fileName) {
		if (fileName.contains(".")) return false;
		return true;
	}
	
	public String checkFieldName(String fieldName) {
		
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

		fieldName = fieldName.replace("#", "_a");
		fieldName = fieldName.replace("@", "_b");
		fieldName = fieldName.replace("$", "_c");
		fieldName = fieldName.replace("%", "_d");
		fieldName = fieldName.replace("&", "_e");
		fieldName = fieldName.replace("*", "_f");
		
		try {
			String checkSql = "select count(*) as numberOfRecords from qcrtsqlfld "
					+ "Where fieldnamel = ?";
			PreparedStatement checkStmt;
			checkStmt = connLibList.prepareStatement(checkSql);
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
	public void setConn(String library) {
		MsSQL dbMSSQL = new MsSQL(library);
		try {
			connLibList = dbMSSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			connLibList.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}