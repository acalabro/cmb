package it.cnr.isti.labse.cmb.consumer;

import it.cnr.isti.labse.cmb.buffer.DroolsEventsBuffer;
import it.cnr.isti.labse.cmb.buffer.EventsBuffer;
import it.cnr.isti.labse.cmb.event.SimpleEvent;
import it.cnr.isti.labse.cmb.listener.DroolsEventsEvaluator;
import it.cnr.isti.labse.cmb.listener.EventsEvaluator;
import it.cnr.isti.labse.cmb.rules.ConnectBaseRule;
import it.cnr.isti.labse.cmb.rules.MyRule;
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
	private EventsBuffer buffer;
	
	public ConsumerManager(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn)
	{
		this.settings = settings;
		this.connectionFact = connectionFact;
		this.initConn = initConn;
		/*
		 * setting up serviceTopic
		 * */
		serviceTopic = settings.getProperty("serviceTopic");
		
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
			//connectionTopic = subscribeSession.createTopic(serviceTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			DebugMessages.ok();
						
			tSub.setMessageListener(this);
			
			DebugMessages.print(this.getClass().getSimpleName(), "Starting connection ");
			connection.start();
			DebugMessages.ok();
			DebugMessages.line();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0; 
		
		//SUPPOSING RECEIVED MESSAGE IS A DROOLS QUERY
		ConnectBaseRule rule = new MyRule();
		
		try {
			//TODO: CHANGE STRING WITH XML
			String[] splittedRule = new String[4];
			splittedRule = msg.getText().split(",");
			rule.setEngine(splittedRule[0]);
			rule.setLanguage(splittedRule[1]);
			rule.setWhen(splittedRule[2]);
			rule.setThen(splittedRule[3]);
			DebugMessages.line();
			System.out.println(this.getClass().getSimpleName() + ": receive " + rule.getEngine() + " " +
					rule.getLanguage() + " " + rule.getWhen() + " " +
					rule.getThen());
			DebugMessages.line();
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up new Buffer to store events.");
			EventsBuffer<SimpleEvent> buffer = new DroolsEventsBuffer<SimpleEvent>();
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(), "Launch eventsEvaluator setup with client request.");
			DebugMessages.ok();
			DebugMessages.line();
			EventsEvaluator listener = new DroolsEventsEvaluator(settings, connectionFact, initConn, rule, buffer);	
			
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
	
	private void sendMessage(TextMessage msg)
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