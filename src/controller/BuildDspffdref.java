package controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.MsSQL;

public class BuildDspffdref {
	
	public static void main(String[] args) {
		
		String companyDir = args[0];
		String line;
		String inputFileName = "F:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + companyDir + "\\create.sql";
		MsSQL db = new MsSQL(companyDir);
		Connection connMsSQL = null;
		String insertSql = "insert into dspffdref (fileName, fieldName, fieldOrder, fieldSize, numberofdecimal, fieldType, keyfield, fileType) values(?,?,?,?,?,?,?,?)";
		String fileName = new String();
		String fieldName = new String();
		int fieldOrder = 0;
		int fieldSize = 0;
		int decimal = 0;
		String fieldType = new String();
		int count = 0;
		
		try {
			connMsSQL = db.connect();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try (BufferedReader in = new BufferedReader(new 
					InputStreamReader(new FileInputStream(inputFileName), "UTF-8"))) {
			while ((line  = in.readLine()) != null ) {
				int a = line.indexOf("create");
				fieldSize = 0;
				decimal = 0;
				fieldType = "";
				if (a >= 0) {
					fileName = line.substring(13);
					fieldOrder = 0;
				} else {
					a = line.indexOf("char(");
					if (a < 0) {
						int b= line.indexOf("numeric(");
						if (b >= 0) {
							fieldName = getFieldName(line, b);
							fieldSize = getFieldSize(line, true);
							decimal = getDecimal(line);
							if (decimal == 0) {
								fieldType = "int";
							} else {
								fieldType = "double";
							}
						}
					} else {
						fieldName = getFieldName(line, a);
						fieldSize = getFieldSize(line, false);
						fieldType = "String";
					}
				}

				if (fieldSize > 0) {
					PreparedStatement checkStmtInsert;
					try {
						count++;
						fieldOrder++;
						checkStmtInsert = connMsSQL.prepareStatement(insertSql);
						checkStmtInsert.setString(1, fileName);
				    	checkStmtInsert.setString(2, fieldName);
				    	checkStmtInsert.setInt(3, fieldOrder);
				    	checkStmtInsert.setInt(4, fieldSize);
				    	checkStmtInsert.setInt(5, decimal);
				    	checkStmtInsert.setString(6, fieldType);
				    	checkStmtInsert.setString(7, "0");
				    	checkStmtInsert.setString(8, "P");
				    	checkStmtInsert.executeUpdate();
				    	int m = count % 1000;
				    	if (m == 0) {
				    		System.out.println(count + " records processed.");
				    	}
					} catch (SQLException e) {
						e.printStackTrace();
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
	
	static private String getFieldName(String newLine, int start) {
		String comma = new String(",");
		
		String compareString = newLine.substring(0, 1);
		if (!compareString.equals(comma)) {
			return newLine.substring(0, start-1);
		} else {
			return newLine.substring(1, start-1);
		}
	}
	
	static private int getFieldSize(String newLine, boolean numeric) {
		
		int a = 0;
		int b = 0;
		
		a = newLine.indexOf("(");
		if (a >= 0) {
			if (numeric) {
				b = newLine.indexOf(",",  a);
			} else {
				b = newLine.indexOf(")",  a);
			}
		}
		return Integer.parseInt(newLine.substring(a+1, b));
	}
	
	static private int getDecimal(String newLine) {
		
		int a = 0;
		int b = 0;
		int c = 0;
		
		a = newLine.indexOf("(");
		if (a >= 0) {
			b = newLine.indexOf(",",  a);
			if (b >= 0) {
				c = newLine.indexOf(")",  b); 
			}
		}
		return Integer.parseInt(newLine.substring(b+1, c));
	}
}
