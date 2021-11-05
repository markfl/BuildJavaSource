package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class BuildAllCopyScripts {
	
	static StringBuilder text = new StringBuilder();
	
	public static void main(String[] args) {
		
		String company = args[0];
		String database = args[1];
		String library = args[2];
		
		try (BufferedReader in = new BufferedReader(new 
				InputStreamReader(new FileInputStream("C:\\Users Shared Folders\\markfl\\Documents\\My Development\\My SQL Source\\" + company + "\\data\\" + library + "\\filestoread.txt"), "UTF-8"))) {
			String line;
			int count = 0;
			while ((line  = in.readLine()) != null ) {
				int a = line.indexOf(".");
				String fileName = line.substring(0, a);
		    	DBCopyBuilder dbCB = new DBCopyBuilder(company, database, library, fileName);
				dbCB.BuildCopyClass();
				dbCB.WriteClass();
				count++;
				String lineout = "java com." + company + ".copy." + database + "." + library + ".copy" + fileName;
				text.append(lineout + "\n");
				System.out.println("Class copy" + fileName + " created.");
		    }
			System.out.println("Program completed normally, " + count + " classes created.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (FileOutputStream out = new FileOutputStream(new File(
	    		"C:\\Users Shared Folders\\markfl\\Documents\\My Development\\Eclipse\\Java EE\\BuildJavaSource\\bin\\" + library + ".bat"))) {
			out.write(text.toString().getBytes());
			text.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}