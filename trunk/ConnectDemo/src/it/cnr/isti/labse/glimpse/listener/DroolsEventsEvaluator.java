package it.cnr.isti.labse.glimpse.listener;

import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.consumer.ConsumerManager;
import it.cnr.isti.labse.glimpse.event.ConnectBaseEvent;
import it.cnr.isti.labse.glimpse.event.SimpleEvent;
import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.exceptions.UnknownRuleException;
import it.cnr.isti.labse.glimpse.settings.DebugMessages;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;

import java.util.HashMap;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
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
import org.w3c.dom.DOMException;

public class DroolsEventsEvaluator extends Thread implements MessageListener, EventsEvaluator, Runnable {

	private String topic;
	private TopicConnection connection;
	private Topic connectionTopic;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private KnowledgeBase kbase;
	private StatefulKnowledgeSession ksession;
	private WorkingMemoryEntryPoint eventStream;
	private String answerTopic;
	private ConnectBaseEvent<String> receivedEvent;
	private ConsumerManager consumerManager;
	//protected static String RULEPATH = "it/cnr/isti/labse/glimpse/rules/FirstRule.drl";
	private KnowledgeBuilder kbuilder;
	private HashMap<String, String> loadedRuleMap;
	private ComplexEventRuleActionType rules;

	public DroolsEventsEvaluator(Properties settings, EventsBuffer<SimpleEvent> buffer, ComplexEventRuleActionType rules, String answerTopic, ConsumerManager consumerManager) {
		this.rules = rules;
		this.topic = settings.getProperty("probeTopic");
		this.answerTopic = answerTopic;
		this.consumerManager = consumerManager;
	}
	
	public void start()
	{
		run();
	}
	
	public void run(TopicConnectionFactory connectionFact, InitialContext initConn) {
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

			DebugMessages.print(this.getClass().getSimpleName(), "Setting up destination topic ");
			connectionTopic = publishSession.createTopic(answerTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up reading topic ");
			connectionTopic = (Topic) initConn.lookup(topic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null,true);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Starting connection ");
			connection.start();
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Creating rule for the engine ");
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Reading knowledge base ");
			kbase = readKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			ksession.setGlobal("EVENTS EntryPoint", eventStream);
			eventStream = ksession.getWorkingMemoryEntryPoint("DEFAULT");		
			tSub.setMessageListener(this);
			ksession.fireUntilHalt();
			DebugMessages.ok();	
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message arg0) {
		ObjectMessage msg = (ObjectMessage) arg0;
		
		try {
			receivedEvent = (ConnectBaseEvent<String>) msg.getObject();
			System.out.println(this.getClass().getSimpleName() + ": receive "
					+ receivedEvent.getID() + " payload: " + receivedEvent.getData());
			DebugMessages.line();
			eventStream.insert(receivedEvent);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		catch(ClassCastException ex)
		{
			
		}
	}
	
	private KnowledgeBase readKnowledgeBase() {
		try
			{				
				KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
				config.setOption(EventProcessingOption.STREAM);

				kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
				
				loadRules(rules);
				
				//kbuilder.add(ResourceFactory.newClassPathResource(RULEPATH, this.getClass().getClassLoader()), ResourceType.DRL);
				KnowledgeBuilderErrors errors = kbuilder.getErrors();
				if (errors.size() > 0)
				{
					for (KnowledgeBuilderError error : errors)
					{
						System.err.println(error);
					}
					throw new IllegalArgumentException("Could not parse knowledge.");
				}

				KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
						
				return kbase;
			}
		catch (Exception e)
		{
			return null;
		}
	}	
	
	private TextMessage createMessage(String msg) {
		try 
		{
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(msg);
			return sendMessage;
		}
		catch (JMSException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private void sendMessage(TextMessage msg) {
		try {
			if (msg != null)
			{
				System.out.println(this.getClass().getSimpleName() + ": send on " + tPub.getTopic().getTopicName() + " " + msg.getText());
				tPub.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void setSink(String topicName) {
		
	}

	public void setMetric() {		
	}

	@Override
	public void insertRule(String rule, String ruleName) throws IncorrectRuleFormatException {
		kbuilder.add(ResourceFactory.newByteArrayResource(rule.trim().getBytes()),ResourceType.DRL);
		//loadedRuleMap.put("", "");
		
	}

	@Override
	public void deleteRule(String ruleName) throws UnknownRuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRule(String ruleName) throws UnknownRuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRule(String ruleName) throws UnknownRuleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restartRule(String ruleName) throws UnknownRuleException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void loadRules(ComplexEventRuleActionType rules) {
		
		ComplexEventRuleType[] insertRules = rules.getInsertArray();
		for(int i = 0; i < insertRules.length; i++)
		{
			try {
				insertRule(insertRules[i].getRuleBody().getDomNode().getFirstChild().getNodeValue(),insertRules[i].getRuleName());
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (IncorrectRuleFormatException e) {
				e.printStackTrace();
			}
		}
		
		ComplexEventRuleType[] deleteRules = rules.getDeleteArray();
		for(int i = 0; i < deleteRules.length; i++)
		{
			try {
				deleteRule(deleteRules[i].getRuleName());
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (UnknownRuleException e) {
				e.printStackTrace();
			}
		}		
		
		ComplexEventRuleType[] startRules = rules.getStartArray();
		for(int i = 0; i < startRules.length; i++)
		{
			try {
				startRule(startRules[i].getRuleName());
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (UnknownRuleException e) {
				e.printStackTrace();
			}
		}	
		
		ComplexEventRuleType[] stopRules = rules.getStopArray();
		for(int i = 0; i < stopRules.length; i++)
		{
			try {
				stopRule(stopRules[i].getRuleName());
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (UnknownRuleException e) {
				e.printStackTrace();
			}
		}	
		
		ComplexEventRuleType[] restartRules = rules.getRestartArray();
		for(int i = 0; i < restartRules.length; i++)
		{
			try {
				restartRule(restartRules[i].getRuleName());
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (UnknownRuleException e) {
				e.printStackTrace();
			}
		}
	}
}
