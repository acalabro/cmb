package org.Connect.Consumer;


import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.Connect.Event.ConnectBaseEvent;
import org.Connect.Event.MyEvent;

public class MyConsumer implements MessageListener{

	public static MyConsumer myConsumerInstance = null;
	private String serviceTopic;
	private String request;
	private TopicConnection connection;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private Topic connectionTopic;
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private String consumerName;
	
	public static MyConsumer getInstance(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		if (myConsumerInstance == null)
		{
			return new MyConsumer(settings, connectionFact, initConn);
		}
		else
			return myConsumerInstance;	
	}
	
	public MyConsumer(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.request = settings.getProperty("request");
		this.serviceTopic = settings.getProperty("serviceTopic");
		this.consumerName = settings.getProperty("consumerName");

		try {
			System.out.print(consumerName + ": Creating connection object ");
			connection = connectionFact.createTopicConnection();
			System.out.println("	[ OK ]");
			
			System.out.print(consumerName + ": Creating public session object ");
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("[ OK ]");
			
			System.out.print(consumerName + ": Setting up destination topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			System.out.println("	[ OK ]");
			
			System.out.print(consumerName + ": Creating subscribe object ");
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("	[ OK ]");
			
			System.out.print(consumerName + ": Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			System.out.println("		[ OK ]");
			
			tSub.setMessageListener(this);
			
			System.out.print(consumerName + ": Starting connection ");
			connection.start();
			System.out.println("		[ OK ]");
			System.out.println();
			
			/*CUSTOMER SEND REQUEST ON THE SERVICETOPIC*/
			sendRequest(createMessage(request));
			
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0; 
		ConnectBaseEvent<String>  evt = new MyEvent();
		try {
			evt.setTimestamp(msg.getJMSTimestamp());
			evt.setData(msg.getText());
			System.out.println(consumerName + ": RICEVE " + msg.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private TextMessage createMessage(String msg)
	{
		try 
		{
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(msg);
			return sendMessage;
		} catch (JMSException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void sendRequest(TextMessage msg)
	{
		try {
			if (msg != null)
			{
				System.out.println(consumerName + ": INVIA " + msg.getText());
				tPub.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}
