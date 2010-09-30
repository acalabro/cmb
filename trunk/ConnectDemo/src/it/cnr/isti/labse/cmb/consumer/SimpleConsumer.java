package it.cnr.isti.labse.cmb.consumer;

import it.cnr.isti.labse.cmb.settings.DebugMessages;

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


public class SimpleConsumer extends Thread implements MessageListener{

	private String serviceTopic;
	private String request;
	private TopicConnection connection;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private Topic connectionTopic;
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private String answerTopic;
	
	public SimpleConsumer(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.request = settings.getProperty("request");
		this.serviceTopic = settings.getProperty("serviceTopic");
		//this.consumerName = settings.getProperty("consumerName");
		setupConnection(connectionFact, initConn);
	}
	
	private void setupConnection(TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		try {
			DebugMessages.print(this.getClass().getSimpleName(), "Creating connection object ");
			connection = connectionFact.createTopicConnection();
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating public session object ");
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up destination topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating subscribe object ");
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			DebugMessages.ok();
			
			tSub.setMessageListener(this);
			
			DebugMessages.print(this.getClass().getSimpleName(), "Starting connection and wait 5 seconds for system startup");
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
		/*CUSTOMER SEND REQUEST ON THE SERVICETOPIC*/
		request = "<rule-set name=\"cheese rules\" xmlns=\"http://drools.org/rules\" xmlns:java=\"http://drools.org/semantics/java\"" +
				" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:schemaLocation=\"http://drools.org/rules " +
				"rules.xsd http://drools.org/semantics/java java.xsd\">" +
				"<rule name=\"occurrencies A -> B\">" +
				"<parameter identifier=\"eventA\">" +
				"<class>it.cnr.isti.labse.cmb.event.SimpleEvent</class>" +
				"</parameter>" +
				"<java:condition>eventA.getData() == \"EventA\"</java:condition>" +
				"<java:consequence>" +
				"System.out.println( \"EventA regola XML\");" +
				"</java:consequence>" +
				"</rule>" +
				"</rule-set>";
		sendRequest(createMessage(request));
	}
	
	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0; 
		try {
			if (msg.getText().startsWith("AnswerTopic == "))
			{
				System.out.println(this.getClass().getSimpleName() + "#" + this.hashCode() + ": receive " + msg.getText());
				answerTopic = msg.getText().substring(0,14).trim();
				listenForAnswer(answerTopic);
				msg.acknowledge();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private void listenForAnswer(String answerTopic) {
		
		DebugMessages.print(this.getClass().getSimpleName(), "Creating subscribe object ");
		try {
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
		
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up reading topic ");
			connectionTopic = subscribeSession.createTopic(answerTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			
			DebugMessages.ok();
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
				System.out.println(this.getClass().getSimpleName() + ": send " + msg.getText());
				tPub.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
}