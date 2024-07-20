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

import model.BuildCopyScripts;
import model.CheckTime;
import model.DBClassBuilder;
import model.DBCopyBuilder;
import model.MsSQL;

public class BuildAllCopyScripts {
	
	static StringBuilder runSQL = new StringBuilder();	// runFileName .java
	static StringBuilder runJavaAll = new StringBuilder();
	static ArrayList<String> allFileName;
	
	public static void main(String[] args) {
		
		CheckTime ct = new CheckTime();
		
		String company = args[0];
		String database = args[1];
		String dataSource = args[2];
		ArrayList<String> allCopyFile = new ArrayList<String>();
		Connection connMSSQL = null;
		Connection connMSSQLLibList = null;
		MsSQL dbMSSQL = new MsSQL(company);
		MsSQL dbMSSQLLibList = new MsSQL("liblist");
		BuildCopyScripts bcs = new BuildCopyScripts();
		DBClassBuilder cb = new DBClassBuilder();
		bcs.setCompany(company);
		bcs.setDatabase(database);
		int countTotal = 0;
		
		try {
			connMSSQL = dbMSSQL.connect();
			connMSSQLLibList = dbMSSQLLibList.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try (BufferedReader dirin = new BufferedReader(new 
				InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\liblist"), "UTF-8"))) {
			String library;
			while ((library  = dirin.readLine()) != null ) {
				bcs.setLibrary(library);
				try (BufferedReader in = new BufferedReader(new 
						InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + library + "\\filestoread.txt"), "UTF-8"))) {
					String line;
					int count = 0;
					while ((line  = in.readLine()) != null ) {
						int a = line.indexOf(".");
						String fileName = line.substring(0, a);
						bcs.setFileName(fileName);
						String checkSql = "select count(*) as numberOfRecords from " + company + ".INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' And TABLE_NAME = '" + fileName + "'";
						String insertSql = "insert into " + company + "uploadfilelist (library, filename, recordcount) "
								 		 + "values (?, ?, ?)";
						PreparedStatement checkStmt = connMSSQL.prepareStatement(checkSql);;
						ResultSet resultsSelect = checkStmt.executeQuery();
						resultsSelect.next();
						int recordCount = resultsSelect.getInt(1);
						if (recordCount > 0) {
					    	DBCopyBuilder dbcb = new DBCopyBuilder(company, database, library, fileName, dataSource);
							dbcb.BuildCopyClass();
							// dbcb.WriteClass();
							count++;
							countTotal++;
							String className = "copy_" + library + "_" + fileName; 
							System.out.println("Class " + className + " created.");
							allCopyFile.add(fileName);
							bcs.BuildRun();
							PreparedStatement insertStmt = connMSSQLLibList.prepareStatement(insertSql);
							insertStmt.setString(1, library);
							insertStmt.setString(2, fileName);
							double counterTotal = cb.getRecordCount(company, library, fileName);
							insertStmt.setInt(3, (int) counterTotal);
							//insertStmt.executeUpdate();
							insertStmt.close();
						}
						resultsSelect.close();
						checkStmt.close();
				    }
					System.out.println("Program completed normally, " + count + " classes created.");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				bcs.setAllCopyFile(allCopyFile);
				bcs.createSQLCount(company);
				bcs.BuildRunAll(allCopyFile);
			}
			dbMSSQL.closeConnection(connMSSQL);
			dbMSSQLLibList.closeConnection(connMSSQLLibList);
			connMSSQL.close();
			connMSSQLLibList.close();
			
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String returnString = ct.calculateElapse("Build Copy");
		returnString = returnString + " " + countTotal + " classes created.";
		System.out.println(returnString);
	}
}