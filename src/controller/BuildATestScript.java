package controller;

import java.sql.SQLException;

public class BuildATestScript {

	public static void main(String[] args) {
		String company = args[0];
		String fileName = args[1];
		int recordsToBuild = 1000;
		if (args.length == 3) {
			recordsToBuild = Integer.parseInt(args[2]);
		}
		
		try {
			BuildTestScript bts = new BuildTestScript(company, fileName, recordsToBuild);
			bts.testScript();
			bts.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}