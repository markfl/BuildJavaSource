package controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.MsSQL;

public class BuildDspffdrefView {
	
	public static void main(String[] args) {
		
		String companyDir = args[0];
		String line;
		String inputFileName = "F:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + companyDir + "\\view.sql";
		MsSQL db = new MsSQL(companyDir);
		Connection connMsSQL = null;
		String insertSql = "insert into dspffdref (fileName, fieldName, fieldOrder, fieldSize, numberofdecimal, fieldType, keyfield, fileType) values(?,?,?,?,?,?,?,?)";
		String selectSql = "select * from dspffdref";
		String where = " where fileName = ? and fieldName = ?";
		String phyFileName = new String();
		String logFileName = new String();
		int count = 0;
		
		try {
			connMsSQL = db.connect();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		PreparedStatement checkStmtInsert;
		
		ArrayList<String> fieldNames = new ArrayList<String>();;
		boolean newFileFound = false;
		try (BufferedReader in = new BufferedReader(new 
					InputStreamReader(new FileInputStream(inputFileName), "UTF-8"))) {
			while ((line  = in.readLine()) != null ) {
				int a = line.indexOf("create");
				if (a >= 0) {
					logFileName = line.substring(12);
				} 
				a = line.indexOf(",");
				if (a >= 0) {
					fieldNames = getFieldName(line, fieldNames);
				}
				if (!newFileFound) {
					a = line.indexOf("From");
					if (a >= 0) {
						int b = line.indexOf(".");
						if (b >= 0) {
							phyFileName = line.substring(b+1);
							for (String field : fieldNames) {
								try {
									PreparedStatement checkStmtSelect = connMsSQL.prepareStatement(selectSql + where);
									checkStmtSelect.setString(1, phyFileName);
									checkStmtSelect.setString(2, field);
									ResultSet resultsSelect = checkStmtSelect.executeQuery();
								    while (resultsSelect.next()) {
								    	checkStmtInsert = connMsSQL.prepareStatement(insertSql);
								    	count++;
										checkStmtInsert.setString(1, logFileName);
								    	checkStmtInsert.setString(2, field);
								    	checkStmtInsert.setInt(3, resultsSelect.getInt(3));
								    	checkStmtInsert.setInt(4, resultsSelect.getInt(4));
								    	checkStmtInsert.setInt(5, resultsSelect.getInt(5));
								    	checkStmtInsert.setString(6, resultsSelect.getString(6));
								    	checkStmtInsert.setString(7, "0");
								    	checkStmtInsert.setString(8, "L");
								    	checkStmtInsert.executeUpdate();
								    	int m = count % 1000;
								    	if (m == 0) {
								    		System.out.println(count + " records processed.");
								    	}
								    }
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				} 
			}
			try {
				db.closeConnection(connMsSQL);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Program completed normally: " + count);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static private ArrayList<String> getFieldName(String newLine, ArrayList<String> fields) {
		String[] fieldNames = newLine.split(",");
		for (String field : fieldNames) {
			fields.add(field.trim());
		}
		return fields;
	}
}