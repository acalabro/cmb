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

import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.rules.DroolsRulesManager;
import it.cnr.isti.labse.glimpse.rules.RulesManager;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class ComplexEventProcessorImpl extends ComplexEventProcessor implements MessageListener {

	private String topic;
	private TopicConnection connection;
	private Topic connectionTopic;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	@SuppressWarnings("unused")
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private KnowledgeBase kbase;
	private StatefulKnowledgeSession ksession;
	private WorkingMemoryEntryPoint eventStream;
	private KnowledgeBuilder kbuilder;
	
	public ComplexEventProcessorImpl(Properties settings, EventsBuffer<GlimpseBaseEvent<?>> buffer, TopicConnectionFactory connectionFact,
			InitialContext initConn) {
		this.topic = settings.getProperty("probeTopic");
		
		init(connectionFact,initConn);
	}

	public void init(TopicConnectionFactory connectionFact,
			InitialContext initConn) {
		try {
			DebugMessages.print(this.getClass().getSimpleName(), "Creating connection object ");
			connection = connectionFact.createTopicConnection();
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Creating public session object ");
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating subscribe object ");
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up reading topic ");
			connectionTopic = (Topic) initConn.lookup(topic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null,true);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up destination topic ");
			connectionTopic = publishSession.createTopic(this.topic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();	

			DebugMessages.print(this.getClass().getSimpleName(), "Reading knowledge base ");
			kbase = readKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			ksession.setGlobal("EVENTS EntryPoint", eventStream);
			eventStream = ksession.getWorkingMemoryEntryPoint("DEFAULT");
			cepRuleManager = new DroolsRulesManager(kbuilder, kbase, ksession);
			DebugMessages.ok();
			
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}		
	}
	
	public void run()
	{
		DebugMessages.print(this.getClass().getSimpleName(), "Starting connection ");
		try {
			connection.start();
			tSub.setMessageListener(this);
			DebugMessages.ok();
			DebugMessages.line();
			while (this.getState() == State.RUNNABLE) {
		        Thread.sleep(20);
		        ksession.fireAllRules();
		        } 
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(Message arg0) {
		ObjectMessage msg = (ObjectMessage) arg0;
		try {
			GlimpseBaseEvent<String> receivedEvent = (GlimpseBaseEvent<String>) msg.getObject();
			if (eventStream != null) {
					eventStream.insert(receivedEvent);
					System.out.println(this.getClass().getSimpleName() + ": receive: " + receivedEvent.getData() + " from: " + receivedEvent.getConnectorID() + " execution: " + receivedEvent.getConnectorInstanceID());	
					DebugMessages.line();
				}
		} catch (JMSException e) {
			e.printStackTrace();
		}
		catch(ClassCastException ex) {
		}
	}
	
	private KnowledgeBase readKnowledgeBase() {
		try
			{				
				KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
				config.setOption(EventProcessingOption.STREAM);

				kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
				kbuilder.add(ResourceFactory.newClassPathResource("startupRule.drl",getClass()), ResourceType.DRL);
				
				KnowledgeBuilderErrors errors = kbuilder.getErrors();
				if (errors.size() > 0)
				{
					for (KnowledgeBuilderError error : errors)
					{
						System.err.println(error);
					}
					throw new IllegalArgumentException("Could not parse knowledge.");
				}
				kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
				return kbase;
			}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setMetric() {		
	}

	public RulesManager getRuleManager() {
		return this.cepRuleManager;
	}
}
