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
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.Connect.Event.ConnectBaseEvent;
import org.Connect.Event.MyEvent;

public class MyConsumer extends Thread implements MessageListener{

	public static MyConsumer myConsumerInstance = null;
	private String topic;
	private String request;
	private TopicConnection connection;
	private TopicSession subscribeSession;
	private Topic connectionTopic;
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
		this.topic = settings.getProperty("topic");
		this.consumerName = settings.getProperty("consumerName");

		try {
			System.out.print(consumerName + ": Creating connection object ");
			connection = connectionFact.createTopicConnection();
			System.out.println("	[ OK ]");
			
			System.out.print(consumerName + ": Creating subscribe object ");
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("	[ OK ]");
			
			System.out.print(consumerName + ": Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(topic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			System.out.println("		[ OK ]");
			
			tSub.setMessageListener(this);
			
			System.out.print(consumerName + ": Starting connection ");
			connection.start();
			System.out.println("		[ OK ]");
			System.out.println();
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
	
}
