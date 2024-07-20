package controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.CheckTime;
import model.MsSQL;

public class CheckRecordCount {

	public static void main(String[] args) {
		
		boolean diffOnly = false;
		CheckTime ct = new CheckTime();
		
		String company = args[0];
		if (args.length > 1) {
			String oneString = "1";
			if (args[1].equals(oneString)) {
				diffOnly = true;	
			}	
		}
		if (diffOnly) {
			System.out.println("Only differing counts will display.");
		} else {
			System.out.println("All counts will display.");
		}
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		try {
			connMSSQL = dbMSSQL.connect();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String line = new String();
		int count = 0;
		int errorCount = 0;
		int goodCount = 0;
		try (BufferedReader dirin = new BufferedReader(new 
				InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\liblist"), "UTF-8"))) {
			String library = new String();
			while ((library  = dirin.readLine()) != null ) {
				System.out.println("Comparing library " + library);
				try (BufferedReader in = new BufferedReader(new 
					InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + library + "\\filestoread.txt"), "UTF-8"))) {
					int recordCount = 0;
					while ((line  = in.readLine()) != null ) {
						count++;
						int a = line.indexOf(".");
						String fileName = line.substring(0, a);
						String selectSql = "select count(*) from " + fileName;
						try {
							PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
							ResultSet resultsSelect = checkStmtSelect.executeQuery();
							resultsSelect.next();
							recordCount = resultsSelect.getInt(1);
							if (recordCount > 0) {
								
							}
							resultsSelect.close();
							checkStmtSelect.close();
						} catch (SQLException e) {
							System.err.println("File " + fileName + " in library " + library + " doesn't exist.");
						}
						int inputRecordCount = 0;
						fileName = line;
						try (BufferedReader input = new BufferedReader(new
								InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + library + "\\" + fileName), "UTF-8"))) {
							while ((line  = input.readLine()) != null ) {
								inputRecordCount++;
							}
						}
						if (recordCount == inputRecordCount)
							goodCount++;
						else
							errorCount++;
						if (diffOnly) {
							if (recordCount != inputRecordCount) {
								System.err.println("File " + fileName + " in library " + library + " has " + recordCount + " records. " + inputRecordCount + " input records");
							}
						} else {
							if (recordCount != inputRecordCount) {
								System.err.println("File " + fileName + " in library " + library + " has " + recordCount + " records. " + inputRecordCount + " input records");
							} else {
								System.out.println("File " + fileName + " in library " + library + " has " + recordCount + " records. " + inputRecordCount + " input records");
							}
						}
					} 
				}
			}
			dbMSSQL.closeConnection(connMSSQL);
			connMSSQL.close();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ct.calculateElapse("Count");
		
		System.out.println("Program completed normally, " + count + " file(s) read. " + goodCount + " files match, " + errorCount + " don't match.");
	}
}