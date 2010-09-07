package org.Connect.Listener;

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

import org.Connect.Buffer.EventsBuffer;
import org.Connect.Event.ConnectBaseEvent;
import org.Connect.Event.MyEvent;

public class DroolsListener extends Thread implements MessageListener{

	private String topic;
	private TopicConnection connection;
	private Topic connectionTopic;
	private TopicSubscriber tSub;
	private EventsBuffer buffer;

	public DroolsListener(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn, EventsBuffer buffer)
	{
		this.topic = settings.getProperty("topic");
		this.buffer = buffer;

		try {
			System.out.print("DROOLSLISTENER: Creating connection object ");
			connection = connectionFact.createTopicConnection();
			System.out.println("	[ OK ]");
			
			System.out.print("DROOLSLISTENER: Creating subscribe object ");
			TopicSession subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("	[ OK ]");
			
			System.out.print("DROOLSLISTENER: Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(topic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			System.out.println("	[ OK ]");
			
			tSub.setMessageListener(this);
			
			System.out.print("DROOLSLISTENER: Starting connection ");
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
				System.out.println("DROOLSLISTENER: RICEVE " + msg.getText());
				buffer.add(evt);
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
}
