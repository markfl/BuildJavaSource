package controller;

public class BuildACopyScript {
	
	public static void main(String[] args) {
		
		String company = args[0];
		String database = args[1];
		String libraryname = args[2];
		String filename = args[3];
		
		DBCopyBuilder dbCB = new DBCopyBuilder(company, database, libraryname, filename);
		dbCB.BuildCopyClass();
		dbCB.WriteClass();
		System.out.println("Program Completed. Class copy" + filename + " created.");
	}
}