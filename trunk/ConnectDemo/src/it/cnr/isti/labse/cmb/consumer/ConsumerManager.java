package it.cnr.isti.labse.cmb.consumer;

import it.cnr.isti.labse.cmb.buffer.DroolsEventsBuffer;
import it.cnr.isti.labse.cmb.buffer.EventsBuffer;
import it.cnr.isti.labse.cmb.event.SimpleEvent;
import it.cnr.isti.labse.cmb.listener.DroolsEventsEvaluator;
import it.cnr.isti.labse.cmb.listener.EventsEvaluator;
import it.cnr.isti.labse.cmb.rules.Engines;
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

public class ConsumerManager extends Thread implements MessageListener {
	
	/*
	 * ConsumerManager setup serviceTopic.
	 * The serviceTopic is the topic where the Consumers will send
	 * requests.
	 * The ConsumerManager will respond to the consumers with a
	 * topic name where the consumer starts listen for the answer
	 * to the sent request.
	 * */
	
	private TopicConnection connection;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private Topic connectionTopic;
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private String serviceTopic;
	private Properties settings;
	private TopicConnectionFactory connectionFact;
	private InitialContext initConn;
	private String answerTopic;
	
	public ConsumerManager(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.settings = settings;
		serviceTopic = settings.getProperty("serviceTopic");
		setupConnection(connectionFact, initConn);
	}
	
	public void setupConnection(TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.connectionFact = connectionFact;
		this.initConn = initConn;
	
		try {
			DebugMessages.print(this.getClass().getSimpleName(), "Creating connection object ");
			connection = connectionFact.createTopicConnection();
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating public session object ");
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating subscribe object");
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up destination topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			DebugMessages.ok();
						
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0; 
				
		try {
			DebugMessages.line();
			
			System.out.println(this.getClass().getSimpleName() + ": receive " + msg.getText());
			DebugMessages.line();
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up new Buffer to store events.");
			
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(), "Launch eventsEvaluator setup with client request.");
			DebugMessages.ok();
			DebugMessages.line();
			
			//the topic where the listener will give analysis results
			answerTopic =  "answerTopic" + "#" + this.getName() + "#" + System.nanoTime();
			
			//communicate the answerTopic to the consumer
			sendMessage(createMessage("AnswerTopic == " + answerTopic));

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			startListener(Engines.drools, msg.getText(), createBuffer(Engines.drools), answerTopic);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}	
	}

	public void run()
	{
		DebugMessages.print(this.getClass().getSimpleName(), "Starting connection ");
		try {
			tSub.setMessageListener(this);
			connection.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		DebugMessages.ok();
		DebugMessages.line();
	}
	
	private void startListener(Engines engine, String rule, EventsBuffer<SimpleEvent> buffer, String answerTopic)
	{
		switch(engine)
		{
			case drools:
			{		
				EventsEvaluator listener = new DroolsEventsEvaluator(settings, buffer, rule, answerTopic);
				
				listener.setupConnection(connectionFact, initConn);
				listener.start();
			}
			case sql:
			{
				
			}
		}
	}
	
	public void answersFromEvaluationEngine(String answer)
	{
		
	}

	private EventsBuffer<SimpleEvent> createBuffer(Engines engine)
	{
		switch(engine)
		{
			case drools:
			{
				EventsBuffer<SimpleEvent> buffer = new DroolsEventsBuffer<SimpleEvent>();
				return buffer;
			}
			case sql:
			{
				return null;
			}
		}
		return null;
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
	
	private void sendMessage(TextMessage msg)
	{
		try {
			if (msg != null)
			{
				System.out.println(this.getClass().getSimpleName() + ": send " + msg.getText());
				DebugMessages.line();
				tPub.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}