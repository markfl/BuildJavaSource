package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BuildCopyScripts {
	
	private String company;
	private String library;
	private String database;
	private String fileName;
	private ArrayList<String> allCopyFile;
	private Collection<ArrayList<String>> allCopyFiles;
	private Boolean multiLibraryCaller;
	
	public BuildCopyScripts() {
		
		super();
		
		setMultiLibraryCaller(false);
		
	}
	
	public BuildCopyScripts(Boolean multiLibraryCaller) {
		
		super();
		
		setMultiLibraryCaller(multiLibraryCaller);
		
	}
	
	public void createSQLCount(String company) {
		
		StringBuilder sql = new StringBuilder();
		String lineout = new String();
		
		if (!getMultiLibraryCaller()) {
			if (getDatabase().equals("mssql")) {
				lineout = "use [" + company + "]\n";
				sql.append(lineout);
				lineout = "GO\n";
				sql.append(lineout);
			}
			for(String fileName : getAllCopyFile()) {
				lineout = "Select count(*) From " + fileName  + ";";
				sql.append(lineout + "\n");
			}
			
		} else {
		
			int classCount = 0;
			for(ArrayList<String> allcopyfile : getAllCopyFiles()) {
				classCount++;
				
				int count = 0;
				String library = new String();
				String fileName = new String();
				for(String field : allcopyfile) {
					count++;
					switch (count) {
						case 1:
							library = field;
							break;
						case 2:
							fileName = field;
							break;
					}
				}
				if (classCount == 1) {
					if (getDatabase().equals("mssql")) {
						lineout = "use [" + company  + "_" + library + "]\n";
						sql.append(lineout);
						lineout = "GO\n";
						sql.append(lineout);
					}
				}
				lineout = "Select count(*) From " + fileName  + ";";
				sql.append(lineout + "\n");
			}
		}
		
		try (FileOutputStream out = new FileOutputStream(new File(
				"C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + getCompany() + "\\count" + getLibrary() + "_" + getDatabase() + ".sql"))) {
			out.write(sql.toString().getBytes());
			sql.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void BuildRunAll(ArrayList<String> allCopyFile) {
		
		StringBuilder runJava = new StringBuilder();
		
		runJava.append("import java.util.ArrayList;\n\n");
		for(String copyFiles : allCopyFile) {
			runJava.append("import com.copy." + getDatabase() + ".copy_" + getLibrary() + "_" + copyFiles + ";\n");
    	}
		runJava.append("\n");
		runJava.append("import model.CheckTime;\n\n");
		runJava.append("public class runAllCopy" + getDatabase() + "_" + getLibrary() +" {\n");
		runJava.append("\n");
		runJava.append("\tpublic static void main(String[] args) {\n");
		runJava.append("\n");
		runJava.append("\t\tCheckTime ct = new CheckTime();\n");
		runJava.append("\t\tString returnString = new String();\n");
		runJava.append("\t\tArrayList<String> allReturnStrings = new ArrayList<String>();\n\n");
		int count = 0;
		for(String copyFiles : allCopyFile) {
			count++;
			if (copyFiles.length() <= 7) {
				runJava.append("\t\tcopy_" + getLibrary() + "_" + copyFiles + " runcopy_" + getLibrary() + "_" + copyFiles + " = new copy_" + getLibrary() + "_" + copyFiles + "();\n");
			} else {
				runJava.append("\t\tcopy_" + getLibrary() + "_" + copyFiles + " runcopy_" + getLibrary() + "_" + copyFiles + " = new copy_" + getLibrary() + "_" + copyFiles + "();\n");
			}			
			runJava.append("\t\treturnString = runcopy_" + getLibrary() + "_" + copyFiles + ".run" + copyFiles + "();\n");
			runJava.append("\t\tSystem.out.println(returnString);\n");
			runJava.append("\t\tallReturnStrings.add(returnString);\n");
			runJava.append("\t\tSystem.out.println(" + count + " + \" of \" + " + allCopyFile.size() + " + \" completed.\" + " + (allCopyFile.size() - count) + " + \" to go.\");\n\n");			
    	}
		runJava.append("\t\tfor(String returnAllString : allReturnStrings) {\n");
		runJava.append("\t\t\tSystem.out.println(returnAllString);\n");
		runJava.append("\t\t}\n\n");
		runJava.append("\t\treturnString = ct.calculateElapse(\"Copy\");\n");
		runJava.append("\t\tSystem.out.println(returnString);\n");
		runJava.append("\t}\n");
		runJava.append("}");
		try (FileOutputStream out = new FileOutputStream(new File(
			"C:\\Users Shared Folders\\markfl\\Documents\\My Development\\eclipse\\Java EE\\" + getCompany() + "\\src\\runAllCopy" + getDatabase() + "_" + getLibrary() + ".java"))) {
			out.write(runJava.toString().getBytes());
			runJava.setLength(0);
			allCopyFile.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void BuildRunAll(Collection<ArrayList<String>> allCopyFile, String dataSource) {
		
		StringBuilder runJava = new StringBuilder();
		
		runJava.append("import java.util.ArrayList;\n\n");
		String library = new String();
		String fileName = new String();
		for(ArrayList<String> copyFile : allCopyFile) {
			int count = 0;
			for(String field : copyFile) {
				count++;
				switch (count) {
					case 1:
						library = field;
						break;
					case 2:
						fileName = field;
						break;
				}
			}
			runJava.append("import com.copy." + getDatabase() + ".copy_" + getCompany() + "_" + library + "_" + fileName + ";\n");
		}
		
		runJava.append("\n");
		runJava.append("import model.CheckTime;\n\n");
		runJava.append("public class runAllCopy" + getDatabase() + "_" + getCompany() + "_" + dataSource +" {\n");
		runJava.append("\n");
		runJava.append("\tpublic static void main(String[] args) {\n");
		runJava.append("\n");
		runJava.append("\t\tCheckTime ct = new CheckTime();\n");
		runJava.append("\t\tString returnString = new String();\n");
		runJava.append("\t\tArrayList<String> allReturnStrings = new ArrayList<String>();\n\n");
		int count = 0;
		for(ArrayList<String> copyFile : allCopyFile) {
			int counter = 0;
			for(String field : copyFile) {
				counter++;
				switch (counter) {
					case 1:
						library = field;
						break;
					case 2:
						fileName = field;
						break;
				}
			}
			if (fileName.length() <= 7) {
				runJava.append("\t\tcopy_" + getLibrary() + "_" + fileName + " runcopy_" + library + "_" + fileName + " = new copy_" + getLibrary() + "_" + fileName + "();\n");
			} else {
				runJava.append("\t\tcopy_" + getLibrary() + "_" + fileName + " runcopy_" + library + "_" + fileName + " = new copy_" + getLibrary() + "_" + fileName + "();\n");
			}	
			count++;
			runJava.append("\t\treturnString = runcopy_" + library + "_" + fileName + ".run" + fileName + "();\n");
			runJava.append("\t\tSystem.out.println(returnString);\n");
			runJava.append("\t\tallReturnStrings.add(returnString);\n");
			runJava.append("\t\tSystem.out.println(" + count + " + \" of \" + " + allCopyFile.size() + " + \" completed. \" + " + (allCopyFile.size() - count) + " + \" to go.\");\n\n");			
    	}
		runJava.append("\t\tfor(String returnAllString : allReturnStrings) {\n");
		runJava.append("\t\t\tSystem.out.println(returnAllString);\n");
		runJava.append("\t\t}\n\n");
		runJava.append("\t\treturnString = ct.calculateElapse(\"Copy\");\n");
		runJava.append("\t\tSystem.out.println(returnString);\n");
		runJava.append("\t}\n");
		runJava.append("}");
		try (FileOutputStream out = new FileOutputStream(new File(
			"C:\\Users Shared Folders\\markfl\\Documents\\My Development\\eclipse\\Java EE\\" + getCompany() + "\\src\\runAllCopy" + getDatabase() + "_" + getCompany() + "_" + dataSource + ".java"))) {
			out.write(runJava.toString().getBytes());
			runJava.setLength(0);
			allCopyFiles.clear();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void BuildRun() {
		
		StringBuilder runSQL = new StringBuilder();
		
		runSQL.append("package com.copy." + getDatabase() + ";\n\n");
		runSQL.append("public class run_" + getLibrary() + "_Copy_" + getDatabase() + "_" + getFileName() +" {\n\n");
		runSQL.append("\tpublic static void main(String[] args) {\n\n");
		runSQL.append("\t\tString returnString = new String();\n");
		runSQL.append("\t\tcopy_" + getLibrary() + "_" + getFileName() + " runcopy_" + getLibrary() + "_" + getFileName() + " = new copy_" + getLibrary() + "_" + getFileName() + "();\n");
		runSQL.append("\t\treturnString = runcopy_" + getLibrary() + "_" + getFileName() + ".run" + getFileName() + "();\n");
		runSQL.append("\t\tSystem.out.println(returnString);\n");
		runSQL.append("\t}\n");
		runSQL.append("}");
		try (FileOutputStream out = new FileOutputStream(new File(
			"C:\\Users Shared Folders\\markfl\\Documents\\My Development\\eclipse\\Java EE\\" + getCompany() + "\\src\\com\\copy\\" + getDatabase() + "\\run_" + getLibrary() + "_Copy_" + getDatabase() + "_" + getFileName() + ".java"))) {
			out.write(runSQL.toString().getBytes());
			runSQL.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public ArrayList<String> getAllCopyFile() {
		return allCopyFile;
	}

	public void setAllCopyFile(ArrayList<String> allCopyFile) {
		this.allCopyFile = allCopyFile;
	}
	
	public Collection<ArrayList<String>> getAllCopyFiles() {
		return allCopyFiles;
	}

	public void setAllCopyFiles(Collection<ArrayList<String>> allCopyFiles) {
		this.allCopyFiles = allCopyFiles;
	}

	public Boolean getMultiLibraryCaller() {
		return multiLibraryCaller;
	}

	public void setMultiLibraryCaller(Boolean multiLibraryCaller) {
		this.multiLibraryCaller = multiLibraryCaller;
	}
}