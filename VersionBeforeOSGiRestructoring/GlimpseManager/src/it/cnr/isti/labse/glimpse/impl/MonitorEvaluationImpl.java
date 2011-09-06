 /*
  * GLIMPSE: A generic and flexible monitoring infrastructure.
  * For further information: http://labsewiki.isti.cnr.it/labse/tools/glimpse/public/main
  * 
  * Copyright (C) 2011  Software Engineering Laboratory - ISTI CNR - Pisa - Italy
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
*/
package it.cnr.isti.labse.glimpse.impl;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.JMSException;
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

import it.cnr.isti.labse.glimpse.consumer.MonitorConsumer;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

/**
 * This class is a generic implementation of the MonitorEvaluation interface.<br />
 * Could be used to contact the monitoring enabler.<br />
 * The constructor needs only the parameter enablerName.
 *
 * @author acalabro
 * @version 0.2
 */

public class MonitorEvaluationImpl implements MonitorConsumer {

	private String enablerName;

	public MonitorEvaluationImpl(String enablerName)
	{
		this.enablerName = enablerName;
	}
	
	@Override
	public TextMessage getMonitorStatusMessage(TopicSession publishSession, boolean debug) {
		//TODO:Not yet implemented server side.
		return null;
	}

	@Override
	public Properties createSettingsPropertiesObject(
			String javaNamingFactoryInitial, String javaNamingProviderUrl,
			String javaNamingSecurityPrincipal,
			String javaNamingSecurityCredential, String connectionFactoryNames,
			String topicServiceTopic, boolean debug) {
		if (debug)
			DebugMessages.print(this.getClass().getSimpleName(),
			"Creating Properties object ");
		Properties settings = new Properties();
		settings.setProperty("java.naming.factory.initial",javaNamingFactoryInitial);
		settings.setProperty("java.naming.provider.url", javaNamingProviderUrl);
		settings.setProperty("java.naming.security.principal", javaNamingSecurityPrincipal);
		settings.setProperty("java.naming.security.credential", javaNamingSecurityCredential);
		settings.setProperty("connectionFactoryNames", connectionFactoryNames);
		settings.setProperty("topic.serviceTopic", topicServiceTopic);
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		return settings;
	}

	@Override
	public InitialContext initConnection(Properties settings, boolean debug) throws NamingException {
		if (debug)
		DebugMessages.print(this.getClass().getSimpleName(),
				"Creating InitialContext with settings ");
		InitialContext initConn = new InitialContext(settings);
		if (debug) {
			DebugMessages.ok(); 
			DebugMessages.line(); }
		return initConn;
	}
	
	@Override
	public TopicConnection createConnection(InitialContext initConn, Properties settings, boolean debug) throws NamingException, JMSException
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
		sendMessage.setStringProperty("SENDER", this.enablerName);
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
		sendMessage.setStringProperty("SENDER", this.enablerName);
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
	
	@Override
	public TopicSubscriber createSubscriber(TopicConnection connection, InitialContext initContext, String serviceChannel, boolean debug) throws JMSException, NamingException {
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
		TopicSubscriber tSub = subscribeSession.createSubscriber(connectionTopic,"DESTINATION = '" + this.enablerName + "'", true);
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
	
	@Override
	public ComplexEventRuleActionListDocument createComplexEventRuleActionDocumentFromXMLString(
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
	
	@Override
	public TopicSubscriber connectToTheResponseChannel(TopicConnection connection, String responseChannel, boolean debug) throws JMSException {
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

	@Override
	public String getAnswerTopicFromTextMessage(TextMessage msg) throws JMSException {
		return msg.getText().substring(15,msg.getText().length());
	}
}
