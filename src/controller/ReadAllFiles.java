package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.DBClassBuilder;
import model.MsSQL;

public class ReadAllFiles {

	public static void main(String[] args) {
		
		DBClassBuilder dbcb;
		String company = args[0];
		String DB = args[1];
		
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
			dbcb = new DBClassBuilder(company);
			dbcb.setConnMSSQL(connMSSQL);
			dbcb.setCompanyName(company);
			dbcb.setDataBase(DB);
			String fileName = new String();
			PreparedStatement checkStmtSelect = connMSSQL.prepareStatement(selectSql);
		    ResultSet resultsSelect = checkStmtSelect.executeQuery();
		    while (resultsSelect.next()) {
		    	fileName = resultsSelect.getString(1);
		    	System.out.println(fileName);
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}