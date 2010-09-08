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
import org.Connect.Rules.ConnectBaseRule;
import org.Connect.Rules.RuleConverter;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class EventsEvaluator implements MessageListener{

	private String topic;
	private TopicConnection connection;
	private Topic connectionTopic;
	private TopicSubscriber tSub;
	private EventsBuffer buffer;
	private ConnectBaseRule listenerRule;
	private KnowledgeBase kbase;
	private StatefulKnowledgeSession ksession;
	protected static String RULEPATH = 					"org/Connect/Rules/FirstRule.drl";


	public EventsEvaluator(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn, ConnectBaseRule listenerRule, EventsBuffer buffer)
	{
		this.listenerRule = listenerRule;
		this.buffer = buffer;
		
		this.topic = settings.getProperty("probeTopic");
		
		try {
			System.out.print("EVENTSEVALUATOR: Creating connection object ");
			connection = connectionFact.createTopicConnection();
			System.out.println("	[ OK ]");
			
			System.out.print("EVENTSEVALUATOR: Creating subscribe object ");
			TopicSession subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("	[ OK ]");
			
			System.out.print("EVENTSEVALUATOR: Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(topic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
			System.out.println("	[ OK ]");
			
			tSub.setMessageListener(this);
			
			System.out.print("EVENTSEVALUATOR: Starting connection ");
			connection.start();
			System.out.println("		[ OK ]");
			System.out.println();
			
			System.out.print("EVENTSEVALUATOR: Creating rule for the engine ");
			String ruleConverted = RuleConverter.convert(listenerRule);
			System.out.println("	[ OK ]");
			
			System.out.print("EVENTSEVALUATOR: Reading knowledge base ");
			kbase = readKnowledgeBase(ruleConverted);
			ksession = kbase.newStatefulKnowledgeSession();
			System.out.println("				[ OK ]");
			
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
				System.out.println("EVENTSEVALUATOR: RICEVE " + msg.getText());
				buffer.add(evt);
				//ksession.insert(arg0)
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
		
	private KnowledgeBase readKnowledgeBase(String ruleConverted) {
		
		try
		{
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	
			//convertedRule must be provided to the engine.
			//kbuilder.add(ResourceFactory.newClassPathResource(ruleConverted, EventsEvaluator.class.getClassLoader()), ResourceType.DRL);
			kbuilder.add(ResourceFactory.newClassPathResource(RULEPATH, EventsEvaluator.class.getClassLoader()), ResourceType.DRL);
			KnowledgeBuilderErrors errors = kbuilder.getErrors();
			if (errors.size() > 0) {
				for (KnowledgeBuilderError error: errors) {
					System.err.println(error);
				}
				throw new IllegalArgumentException("Could not parse knowledge.");
			}
		
			KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
			kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
			return kbase;
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
