package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.MsSQL;

public class BuildCreateSQL {
	
	static StringBuilder text = new StringBuilder();

	public static void main(String[] args) {

		String company = args[0];
		
		String line;
		String fileName = new String();
		String columnName = new String();
		String dataType = new String();
		String charLength = new String();
		String precision = new String();
		String scale = new String();
		String fileNameSave = new String();
		int length = 0;
		boolean isFirst = true;
		boolean isFirstRow = true;
		
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		String selectSql = "SELECT INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION, "
				+ "INFORMATION_SCHEMA.COLUMNS.DATA_TYPE, "
				+ "INFORMATION_SCHEMA.COLUMNS.CHARACTER_MAXIMUM_LENGTH, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_PRECISION, "
				+ "INFORMATION_SCHEMA.COLUMNS.NUMERIC_SCALE "
				+ "FROM INFORMATION_SCHEMA.COLUMNS "
				+ "ORDER BY INFORMATION_SCHEMA.COLUMNS.TABLE_NAME, "
				+ "INFORMATION_SCHEMA.COLUMNS.ORDINAL_POSITION";

		try {
			connMSSQL = dbMSSQL.connect();
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(1);
		    	columnName = resultsSelect.getString(2);
		    	dataType = resultsSelect.getString(4);
		    	charLength = resultsSelect.getString(5);
		    	precision = resultsSelect.getString(6);
		    	scale = resultsSelect.getString(7);
		    	if (isFirst) {
					line = "create table " + fileName;
					text.append(line + "\n");
					line = "(";
					text.append(line + "\n");
					isFirst = false;
				}
		    	
		    	if (!fileName.equals("ospreff")) {
		    		if (!fileNameSave.equals("") && !fileNameSave.equals(fileName)) {
			    		line = ");";
						text.append(line + "\n");
						line = "create table " + fileName;
						text.append(line + "\n(");
						line = "(\n";
						text.append(line);
						isFirstRow = true;
			    	}
			    	
			    	line = "";
			    	if (!isFirstRow) line = ",";
			    	
			    	if (dataType.equals("char")) {
			    		length = Integer.parseInt(charLength);
			    		if (length < 255) {
			    			line = line + columnName + " char (" + length + ")";
			    		} else {
			    			line = line + columnName + " text (" + length + ")";
			    		}
			    		
						text.append(line + "\n");
			    	}
			    	
			    	if (dataType.equals("int")) {
			    		line = line + columnName + " int";
						text.append(line + "\n");
			    	}
			    	
			    	if (dataType.equals("numeric")) {
			    		line = line + columnName + " numeric (" + precision + "," + scale + ")";
						text.append(line + "\n");
			    	}
			    	
		    		
		    	}
		    	
		    	isFirstRow = false;
		    	
				fileNameSave = fileName;
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (FileOutputStream out = new FileOutputStream(new File("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\createmynew.sql"))) {
			out.write(text.toString().getBytes());
			dbMSSQL.closeConnection(connMSSQL);
			System.out.println("Program completed normally.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
