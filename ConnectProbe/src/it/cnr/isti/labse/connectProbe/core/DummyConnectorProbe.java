package it.cnr.isti.labse.connectProbe.core;

import it.cnr.isti.labse.connectProbe.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.event.ConnectBaseEvent;
import it.cnr.isti.labse.glimpse.event.SimpleEvent;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class DummyConnectorProbe extends Thread
{
	public static DummyConnectorProbe myConnectorInstance = null;
	private String filter;
	private String topic;
	private String eventFile;
	private String connectorName;
	private TopicConnection connection;
	private TopicSession publishSession;
	private TopicPublisher tPubb;
	private Topic connectionTopic;
	private int eventID = 0;
	private String sourceState = "startState";
	private String instance;
	private int executionValue = 0;
	private Random rand = new Random();
	
	public static DummyConnectorProbe getInstance(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		if (myConnectorInstance == null)
		{
			return new DummyConnectorProbe(settings, connectionFact, initConn);
		}
		else
			return myConnectorInstance;
				
	}
	
	public DummyConnectorProbe(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.filter = settings.getProperty("filter");
		this.topic = settings.getProperty("topic");
		this.eventFile = settings.getProperty("eventFile");
		this.connectorName = settings.getProperty("connectorName");
		this.instance = settings.getProperty("instance");

		try {
			DebugMessages.print(this.getClass().getSimpleName(), "Creating connection object");
			connection = connectionFact.createTopicConnection();
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating public session object ");
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up destination topic ");
			connectionTopic = (Topic)initConn.lookup(topic);
			tPubb = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Starting connection and wait 8 seconds for system startup");
			connection.start();
			DebugMessages.ok();
			DebugMessages.line();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
			System.out.println(this.getClass().getSimpleName() + ": Starts sending events");
			DebugMessages.line();
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
				System.out.println(this.getClass().getSimpleName() + " " + this.connectorName + " send: " + event + " instanceName = " + this.instance);
				sendMessage(prepareMessage(event), timeout);
			}

			// dispose all the resources after using them.
			fis.close();
			bis.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ObjectMessage prepareMessage(String msg)
	{
		//START FILTER
		if (filter.compareTo(msg) != 0)
		{
			try 
			{
				ObjectMessage messageToSend = publishSession.createObjectMessage();
				
				//Creo un simple event da spedire
				
				ConnectBaseEvent<String> message = new SimpleEvent(this.connectorName, this.instance, this.connectorName + this.instance + executionValue, eventID,  System.currentTimeMillis(), sourceState);
				eventID++;
				message.setData(msg);
				sourceState = msg;				
				messageToSend.setObject(message);
				return messageToSend;
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void sendMessage(ObjectMessage msg, int timeout)
	{
		try {
			if (msg != null)
			{
				Thread.sleep(timeout*rand.nextInt(60));
				tPubb.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
