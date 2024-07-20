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

import model.BuildCopyScripts;
import model.MsSQL;

public class FixRunOption {

	public static void main(String[] args) {
		
		String company = args[0];
		String database = args[1];
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL("liblist");
		BuildCopyScripts bcs = new BuildCopyScripts();
		bcs.setCompany(company);
		bcs.setDatabase(database);
		
		try {
			connMSSQL = dbMSSQL.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (BufferedReader dirin = new BufferedReader(new 
				InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\liblist"), "UTF-8"))) {
			String library;
			while ((library  = dirin.readLine()) != null ) {
				String checkSql = "select count(*) as numberOfRecords from " + company + "liblistreseq WHERE library = '" + library + "'";
				PreparedStatement checkStmt = connMSSQL.prepareStatement(checkSql);
				ResultSet resultsSelect = checkStmt.executeQuery();
				resultsSelect.next();
				int recordCount = resultsSelect.getInt(1);
				if (recordCount > 0) {
					checkSql = "update " + company + "liblistreseq set runoption = 'y' where library = '" + library + "'";
					checkStmt = connMSSQL.prepareStatement(checkSql);
					int record = checkStmt.executeUpdate();
					if (record > 0) {
						System.out.println("Library record " + library + " updated.");
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}