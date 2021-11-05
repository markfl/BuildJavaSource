package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.MySQL;

public class CreateDeleteList {

	public static void main(String[] args) {
		
		Connection conn = null;
		MySQL db = new MySQL();
		StringBuilder text = new StringBuilder();
		
		try {
			conn = db.connect("root", "root");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String line;
		String inputFileName = "F:\\Temp\\fixjava\\delete" + args[0] + ".txt";
		String sql = "select count(*) as rowCount from dscfiles where filename = ?";
		
		try (BufferedReader in = new BufferedReader(new 
				InputStreamReader(new FileInputStream(inputFileName), "UTF-8"))) {
			while ((line  = in.readLine()) != null ) {
			
				int a = line.indexOf(args[0] + ".java");
				if (a >= 0) {
					String newFileName = line.substring(0, a);
					try {
						int rowCount;
						PreparedStatement checkStmt = conn.prepareStatement(sql);
					    checkStmt.setString(1, newFileName);
					    ResultSet results = checkStmt.executeQuery();
					    
					    if (results.next()) {
					    	rowCount = results.getInt("rowCount");
					    	if (rowCount == 0) {
								line = "del " + newFileName + args[0] + ".java";
								text.append(line + "\n");
								try (FileOutputStream out = new FileOutputStream(new File("F:\\Temp\\fixjava\\deletefiles" + args[0] + ".bat"))) {
									out.write(text.toString().getBytes());
								}
					    	}
					    }
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				db.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Program completed normally.");
	}
}
