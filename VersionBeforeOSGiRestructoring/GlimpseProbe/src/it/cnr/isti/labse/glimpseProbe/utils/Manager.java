package it.cnr.isti.labse.glimpseProbe.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Manager
{
	@SuppressWarnings("deprecation")
	public static Properties Read(String fileName)
	{
		Properties readedProps = new Properties();
		
		File file = new File(fileName);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		try	{
			fis = new FileInputStream(file);
		
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			// dis.available() returns 0 if the file does not have more lines.
			String property = "";
			String key = "";
			String value = "";
		 
			while (dis.available() != 0) {
			  // this statement reads the line from the file and print it to
				 // the console.
	    	property = dis.readLine().trim();
	    	if (property.length() > 0)
	    	{
		    	key = property.substring(0,property.indexOf("="));
		    	value = property.substring(property.indexOf("=")+1,property.length());
				
		    	readedProps.put(key.trim(), value.trim());
	    	}
	    	}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readedProps;
	}
	
	@SuppressWarnings("deprecation")
	public static String ReadTextFromFile(String filePath)
	{
		File file = new File(filePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		StringBuilder strB = new StringBuilder();
				
		try	{
			fis = new FileInputStream(file);
		
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
				 
			while (dis.available() != 0) {
			  // this statement reads the line from the file and print it to
				 // the console.
	    	strB.append(dis.readLine());
	    	
	    	}
			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strB.toString();
	}
	
	public static boolean Write(Properties toWrite, File whereToWrite)
	{
		//if (writeSuccessfully)
		return true;
	}
}