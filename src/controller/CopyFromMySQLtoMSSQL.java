package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.MsSQL;
import model.MySQL;

public class CopyFromMySQLtoMSSQL {

	public static void main(String[] args) {
		
		Connection connMySql = null;
		MySQL dbMySql = new MySQL(args[0]);
		Connection connMSSQL = null;
		MsSQL dbMSSQL = new MsSQL(args[0]);
		String selectSql = "select * from dscfiles";
		String insertSql = "insert into dscfiles (filename) values(?)";
		
		try {
			connMySql = dbMySql.connect("root", "root");
			connMSSQL = dbMSSQL.connect();
			
			PreparedStatement checkStmtSelect = connMySql.prepareStatement(selectSql);
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	PreparedStatement checkStmtInsert = connMSSQL.prepareStatement(insertSql);
		    	String results = resultsSelect.getString(1);
		    	checkStmtInsert.setString(1, results);
		    	checkStmtInsert.executeUpdate();
		    }
		      
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			dbMySql.closeConnection(connMySql);
			dbMSSQL.closeConnection(connMSSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}