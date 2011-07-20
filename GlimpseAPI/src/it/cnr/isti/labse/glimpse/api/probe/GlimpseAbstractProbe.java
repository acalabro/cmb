package it.cnr.isti.labse.glimpse.api.probe;

import java.util.Properties;

import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.api.probe.GlimpseProbe;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;

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

public abstract class GlimpseAbstractProbe implements GlimpseProbe {
	
	protected static InitialContext initContext;
	protected static TopicSession publishSession;
	protected static TopicPublisher tPub;
	protected static TopicConnection connection;
	protected static Topic connectionTopic;
	
	public GlimpseAbstractProbe(Properties settings) {
		
		try {	
			initContext = this.initConnection(settings, true);
			this.createConnection(initContext,settings.getProperty("probeChannel"), settings, true);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}
	
	protected TopicPublisher createConnection(InitialContext initConn, String probeChannel, Properties settings, boolean debug) throws NamingException, JMSException
	{
		if (debug)
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating ConnectionFactory with settings ");
		TopicConnectionFactory connFact = (TopicConnectionFactory)initConn.lookup(settings.getProperty("connectionFactoryNames"));
		if (debug) {
			DebugMessages.ok();  
			DebugMessages.print(this.getClass().getSimpleName(),
						"Creating TopicConnection "); }
			connection = connFact.createTopicConnection();
			if (debug) {
			DebugMessages.ok();
			DebugMessages.line(); }
			if (debug) {
				DebugMessages.print(this.getClass().getSimpleName(),
						"Creating Session "); }
			publishSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			if (debug) {
				DebugMessages.ok();
				DebugMessages.print(this.getClass().getSimpleName(),
						"Looking up for channel ");}
			connectionTopic = (Topic) initContext.lookup(probeChannel);
			if (debug) {
				DebugMessages.ok();
				DebugMessages.print(this.getClass().getSimpleName(),
						"Creating Publisher "); }
			return tPub = publishSession.createPublisher(connectionTopic);
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
	
	public abstract void sendMessage(GlimpseBaseEvent<String> event, boolean debug);
	
	protected void sendEventMessage(GlimpseBaseEvent<?> event, boolean debug) throws JMSException,
			NamingException {
		if (debug) {
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating Message "); }
		try 
		{
			ObjectMessage messageToSend = publishSession.createObjectMessage();			
			messageToSend.setObject(event);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(this.getClass().getSimpleName(),
					"Publishing message  "); }
		tPub.publish(messageToSend);	
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static Properties createSettingsPropertiesObject(
			String javaNamingFactoryInitial, String javaNamingProviderUrl,
			String javaNamingSecurityPrincipal,
			String javaNamingSecurityCredential, String connectionFactoryNames,
			String topicProbeTopic, boolean debug,
			String probeName, String probeChannel) {
		if (debug)
			DebugMessages.print(GlimpseAbstractProbe.class.getSimpleName(),
			"Creating Properties object ");
		Properties settings = new Properties();
		settings.setProperty("java.naming.factory.initial",javaNamingFactoryInitial);
		settings.setProperty("java.naming.provider.url", javaNamingProviderUrl);
		settings.setProperty("java.naming.security.principal", javaNamingSecurityPrincipal);
		settings.setProperty("java.naming.security.credential", javaNamingSecurityCredential);
		settings.setProperty("connectionFactoryNames", connectionFactoryNames);
		settings.setProperty("topic.probeTopic", topicProbeTopic);
		settings.setProperty("probeName", probeName);
		settings.setProperty("probeChannel", probeChannel);
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		return settings;
	}
}
