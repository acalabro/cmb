package it.cnr.isti.labse.cmb.probe;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class TestProbe extends Thread
{
	public static TestProbe myProbeInstance = null;
	private String filter;
	private String topic;
	private String eventFile;
	private String probeName;
	private TopicConnection connection;
	
	private TopicSession publishSession;
	private TopicPublisher tPubb;
	
	private Topic connectionTopic;
	
	public static TestProbe getInstance(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		if (myProbeInstance == null)
		{
			return new TestProbe(settings, connectionFact, initConn);
		}
		else
			return myProbeInstance;
				
	}
	
	public TestProbe(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.filter = settings.getProperty("filter");
		this.topic = settings.getProperty("topic");
		this.eventFile = settings.getProperty("eventFile");
		this.probeName = settings.getProperty("probeName");

		try {
			System.out.print(probeName + ": Creating connection object ");
			connection = connectionFact.createTopicConnection();
			System.out.println("		[ OK ]");
			
			System.out.print(probeName + ": Creating public session object ");
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("		[ OK ]");
			
			System.out.print(probeName + ": Setting up destination topic ");
			connectionTopic = (Topic)initConn.lookup(topic);
			tPubb = publishSession.createPublisher(connectionTopic);
			System.out.println("		[ OK ]");
			
			System.out.print(probeName + ": Starting connection ");
			connection.start();
			System.out.println("			[ OK ]");
			System.out.println();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(true)
		{
			read(eventFile);
		}
	}
	
	@SuppressWarnings("deprecation")
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
			sendMessage(createMessage(event), timeout);
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private TextMessage createMessage(String msg)
	{
		//START FILTER
		if (filter.compareTo(msg) != 0)
		{
			try 
			{
				TextMessage sendMessage = publishSession.createTextMessage();
				sendMessage.setText(msg);
				return sendMessage;
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void sendMessage(TextMessage msg, int timeout)
	{
		try {
			if (msg != null)
			{
				Thread.sleep(timeout*50);
				System.out.println(probeName + ": INVIA " + msg.getText());
				tPubb.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
