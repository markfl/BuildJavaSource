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

import model.MsSQL;

public class UpdateDspffdrefIndex {
	
	public static void main(String[] args) {
		
		String companyDir = args[0];
		String line;
		String inputFileName = "F:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + companyDir + "\\index.sql";
		MsSQL db = new MsSQL(companyDir);
		Connection connMsSQL = null;
		String selectSql = "select * from dspffdref";
		String updateSql = "update dspffdref set keyfield = ?";
		String where = " where fileName = ? and fieldName = ?";
		int count = 0;
		String fileName = new String();
		String fieldName = new String();

		try {
			connMsSQL = db.connect();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try (BufferedReader in = new BufferedReader(new 
					InputStreamReader(new FileInputStream(inputFileName), "UTF-8"))) {
			while ((line  = in.readLine()) != null ) {
				int a = line.indexOf("create");
				if (a >= 0) {
					a = line.indexOf("index");
					if (a >= 0) {
						fileName = line.substring(a+6);
					}
				} else {
					a = line.indexOf("Go");
					if (a < 0) {
						a = line.indexOf("on");
						if (a < 0) {
							a = line.indexOf(");");
							if (a < 0) {
								a = line.indexOf("USE");
								if (a < 0) {
									fieldName = getFieldName(line);
									try {
										PreparedStatement checkStmtSelect = connMsSQL.prepareStatement(selectSql + where);
										checkStmtSelect.setString(1, fileName);
										checkStmtSelect.setString(2, fieldName);
										ResultSet resultsSelect = checkStmtSelect.executeQuery();
									    while (resultsSelect.next()) {
									    	count++;
									    	PreparedStatement checkStmtUpdate = connMsSQL.prepareStatement(updateSql + where);
									    	checkStmtUpdate.setString(1, "1");
									    	checkStmtUpdate.setString(2, fileName);
									    	checkStmtUpdate.setString(3, fieldName);
									    	checkStmtUpdate.executeUpdate();
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
	
	static private String getFieldName(String newLine) {
		
		String comma = new String(",");
		String compareString = newLine.substring(0, 1);
		if (!compareString.equals(comma)) {
			return newLine.substring(0);
		} else {
			return newLine.substring(1);
		}
	}
}
