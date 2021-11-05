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

import model.MsSQL;

public class CheckRecordCount {

	public static void main(String[] args) {
		
		boolean diffOnly = false;
		
		String company = args[0];
		String library = args[1];
		if (args.length > 2) {
			String oneString = "1";
			if (args[2].equals(oneString)) {
				diffOnly = true;
			}
		}
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(company);
		try {
			connMSSQL = dbMSSQL.connect();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try (BufferedReader in = new BufferedReader(new 
				InputStreamReader(new FileInputStream("F:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + library + "\\filestoread.txt"), "UTF-8"))) {
			String line;
			int count = 0;
			int errorCount = 0;
			int goodCount = 0;
			while ((line  = in.readLine()) != null ) {
				int a = line.indexOf(".");
				String fileName = line.substring(0, a);
				String selectSql = "select count(*) from " + fileName;
				PreparedStatement checkStmtSelect;
				try {
					checkStmtSelect = connMSSQL.prepareStatement(selectSql);
					ResultSet resultsSelect = checkStmtSelect.executeQuery();
					resultsSelect.next();
					int recordCount = resultsSelect.getInt(1);
					if (recordCount > 0) {
						count++;
						int inputRecordCount = 0;
						try (BufferedReader input = new BufferedReader(new
								InputStreamReader(new FileInputStream("F:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + library + "\\" + line), "UTF-8"))) {
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
								System.out.println("File " + fileName + " has " + recordCount + " records. " + inputRecordCount + " input records");
							}
						} else
							System.out.println("File " + fileName + " has " + recordCount + " records. " + inputRecordCount + " input records");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
			System.out.println("Program completed normally, " + count + " file(s) read. " + goodCount + " files match, " + errorCount + " don't match.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}