package org.Connect.Probe;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MyProbe extends Thread
{
	public static MyProbe myProbeInstance = null;
	private String filter;
	private String topic;
	private String connectionFactory;
	private String eventFile;
	
	public static MyProbe getInstance(Properties settings)
	{
		if (myProbeInstance == null)
		{
			return new MyProbe(settings);
		}
		else
			return myProbeInstance;
				
	}
	
	public MyProbe(Properties settings)
	{
		this.filter = settings.getProperty("filter");
		this.topic = settings.getProperty("topic");
		this.connectionFactory = settings.getProperty("connectionFactory");
		this.eventFile = settings.getProperty("eventFile");
	}
	
	public void run()
	{
		while(true)
		{
			read(eventFile);
		}
	}
	
	private void read(String eventFileToRead)
	{
		File file = new File(eventFileToRead);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		try	{
			fis = new FileInputStream(file);
		
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			
			// dis.available() returns 0 if the file does not have more lines.
			String message = "";
			String event = "";
			int timeout = 0;
		 
			while (dis.available() != 0)
			{
	    	message = dis.readLine().trim();
	    	event = message.substring(0,message.indexOf(","));
	    	timeout = Integer.parseInt(message.substring(message.indexOf(",")+1,message.length()));
			Thread.sleep(timeout*10);

			//CONNECT TO THE SESSION AND SEND MESSAGES
			//publishsession.WriteMessage(event);	
			//**/
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
