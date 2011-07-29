package it.cnr.isti.labse.glimpse.api.consumer;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
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

import org.apache.xmlbeans.XmlException;

/**
 *
 * This class represent a generic implementation of the interface {@link GlimpseConsumer}.
 * It is possible to use or extends the method: <br />
 * {{@link #messageReceived(Message)}<br /> <br />
 * 
 * 
 * @author Antonello Calabr&ograve;
 * 
 */
public abstract class GlimpseAbstractConsumer implements GlimpseConsumer {

	protected static TopicConnection connection;
	protected static boolean firstMessage = true;
	protected static InitialContext initContext;
	protected Properties settings;
	
	/**
	 * This method setup the connection
	 * 
	 * @param settings, the settings properties for the connection to the Monitoring infrastructure.
	 * The Properties object may also be created using the method {@link #createSettingsPropertiesObject(String, String, String, String, String, String, boolean, String)}
	 */
	protected void init(Properties settings) {
		try {
			this.settings = settings;
			initContext = this.initConnection(settings, true);
			connection = this.createConnection(initContext, settings, true);
			TopicSubscriber tSub = this.createSubscriber(connection, initContext, "serviceTopic", true);
			tSub.setMessageListener(this);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public GlimpseAbstractConsumer(Properties settings, String plainTextRule) {
		init(settings);
		try {
			this.sendTextMessage(connection, initContext, "serviceTopic", plainTextRule, true);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	public GlimpseAbstractConsumer(Properties settings, ComplexEventRuleActionListDocument complexEventRuleXML) {
				
		try {
			init(settings);
			this.sendActionListMessage(connection, initContext, "serviceTopic", complexEventRuleXML, true);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
			
	@Override
	public void onMessage(Message arg0) {
		try {
			if (firstMessage) {
				TextMessage msg = (TextMessage)arg0;
				TopicSubscriber newTopic;
				try {
					newTopic = this.connectToTheResponseChannel(connection, this.getAnswerTopicFromTextMessage(msg) , true);
					newTopic.setMessageListener(this);
				} catch (IncorrectRuleFormatException e) {
					System.out.println("IncorrectRuleFromatException raised: INVALID RULE");
				}
				firstMessage = false;				
			}
			else
			/*the message response from monitoring will be ObjectMessage object, here an example using TextMessage*/
				messageReceived((TextMessage)arg0);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public abstract void messageReceived(Message arg0) throws JMSException;
	
	@Override
	public TextMessage getMonitorStatusMessage(TopicSession publishSession, boolean debug) {
		//TODO:Not yet implemented server side.
		return null;
	}

	protected InitialContext initConnection(Properties settings, boolean debug) throws NamingException {
		if (debug)
		DebugMessages.print(this.getClass().getSimpleName(),
				"Creating InitialContext with settings ");
		InitialContext initConn = new InitialContext(settings);
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		return initConn;
	}
	
	protected TopicConnection createConnection(InitialContext initConn, Properties settings, boolean debug) throws NamingException, JMSException
	{
		if (debug)
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating ConnectionFactory with settings ");
		TopicConnectionFactory connFact = (TopicConnectionFactory)initConn.lookup(settings.getProperty("connectionFactoryNames"));
		if (debug) {
			DebugMessages.ok();  
			DebugMessages.print(this.getClass().getSimpleName(),
						"Creating TopicConnection "); }
			TopicConnection connection = connFact.createTopicConnection();
			if (debug) {
			DebugMessages.ok();
			DebugMessages.line(); }
		return connection;
	}
	
	@Override
	public void sendActionListMessage(TopicConnection connection, InitialContext initContext, String serviceChannel, ComplexEventRuleActionListDocument actionList, boolean debug) throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Session "); }
		TopicSession publishSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Looking up for channel ");}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Publisher "); }
		TopicPublisher tPub = publishSession.createPublisher(connectionTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Message "); }
		ObjectMessage sendMessage = publishSession.createObjectMessage();
		sendMessage.setStringProperty("SENDER", settings.getProperty("consumerName"));
		sendMessage.setStringProperty("DESTINATION", "monitor");
		sendMessage.setObject((Serializable)actionList);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Publishing message  "); }
		tPub.publish(sendMessage);	
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
	}
	
	@Override
	public void sendTextMessage(TopicConnection connection, InitialContext initContext, String serviceChannel, String textToSend, boolean debug)
			throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Session "); }
		TopicSession publishSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Looking up for channel ");}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Publisher "); }
		TopicPublisher tPub = publishSession.createPublisher(connectionTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Message "); }
		TextMessage sendMessage = publishSession.createTextMessage();
		sendMessage.setStringProperty("SENDER", settings.getProperty("consumerName"));
		sendMessage.setStringProperty("DESTINATION", "monitor");
		sendMessage.setText(textToSend);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Publishing message  "); }
		tPub.publish(sendMessage);		
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
	}
	
	protected TopicSubscriber createSubscriber(TopicConnection connection, InitialContext initContext, String serviceChannel, boolean debug) throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Session "); }
		TopicSession subscribeSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Connecting to channel ");}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Create subscriber ");}
		TopicSubscriber tSub = subscribeSession.createSubscriber(connectionTopic,"DESTINATION = '" + settings.getProperty("consumerName") + "'", true);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Starting connection ");}
		connection.start();
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		return tSub;
	}
	
	protected ComplexEventRuleActionListDocument createComplexEventRuleActionDocumentFromXMLString(
			String xmlRule, boolean debug) throws XmlException, JMSException {
		if (debug)
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating ComplexEventRuleActionListDocument with provided XML ");
		ComplexEventRuleActionListDocument theDocument = ComplexEventRuleActionListDocument.Factory.parse(xmlRule);
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		return theDocument;
	}
	
	protected TopicSubscriber connectToTheResponseChannel(TopicConnection connection, String responseChannel, boolean debug) throws JMSException {
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Session "); }
		TopicSession subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Connecting to channel ");}
		Topic connectionTopic = subscribeSession.createTopic(responseChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Create subscriber ");}
		TopicSubscriber tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Starting connection ");}
		connection.start();
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.println(this.getClass().getSimpleName(), "Successfully connected to the responseChannel " + responseChannel);
			DebugMessages.line(); }
		 return tSub;
	}
	
	public static Properties createSettingsPropertiesObject(
				String javaNamingFactoryInitial, String javaNamingProviderUrl,
				String javaNamingSecurityPrincipal,
				String javaNamingSecurityCredential, String connectionFactoryNames,
				String topicServiceTopic, boolean debug, String consumerName) {
			if (debug)
				DebugMessages.print(GlimpseAbstractConsumer.class.getSimpleName(),
				"Creating Properties object ");
			Properties settings = new Properties();
			settings.setProperty("java.naming.factory.initial",javaNamingFactoryInitial);
			settings.setProperty("java.naming.provider.url", javaNamingProviderUrl);
			settings.setProperty("java.naming.security.principal", javaNamingSecurityPrincipal);
			settings.setProperty("java.naming.security.credential", javaNamingSecurityCredential);
			settings.setProperty("connectionFactoryNames", connectionFactoryNames);
			settings.setProperty("topic.serviceTopic", topicServiceTopic);
			settings.setProperty("consumerName", consumerName);
			if (debug) {
				DebugMessages.ok(); 
				DebugMessages.line(); }
			return settings;
		}

	protected String getAnswerTopicFromTextMessage(TextMessage msg) throws JMSException, IncorrectRuleFormatException {
		if (msg.getText().substring(0,14).compareTo("AnswerTopic ==") == 0)
			return msg.getText().substring(15,msg.getText().length());
		else
			throw (new IncorrectRuleFormatException());
	}
}
