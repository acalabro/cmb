package it.cnr.isti.labse.cmb.listener;

import it.cnr.isti.labse.cmb.buffer.EventsBuffer;
import it.cnr.isti.labse.cmb.event.ConnectBaseEvent;
import it.cnr.isti.labse.cmb.event.SimpleEvent;
import it.cnr.isti.labse.cmb.rules.ConnectBaseRule;
import it.cnr.isti.labse.cmb.rules.RuleConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class EventsEvaluator implements MessageListener{

	private String topic;
	private TopicConnection connection;
	private Topic connectionTopic;
	private TopicSubscriber tSub;
	private EventsBuffer buffer;
	private ConnectBaseRule listenerRule;
	private KnowledgeBase kbase;
	private StatefulKnowledgeSession ksession;
	private WorkingMemoryEntryPoint eventStream;
	private final List results = new ArrayList();
	protected static String RULEPATH = 					"it/cnr/isti/labse/cmb/rules/FirstRule.drl";


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
			ksession.setGlobal("EVENTS EntryPoint", eventStream);
			eventStream = ksession.getWorkingMemoryEntryPoint( "DEFAULT" );
			System.out.println("				[ OK ]");
			tSub.setMessageListener(this);
			
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
		
	public int i = 0;
		@Override
		public void onMessage(Message arg0) {
			TextMessage msg = (TextMessage) arg0; 
			ConnectBaseEvent<String>  evt = new SimpleEvent();
			try {
				evt.setTimestamp(msg.getJMSTimestamp());
				evt.setData(msg.getText());
				System.out.println("EVENTSEVALUATOR: RICEVE " + msg.getText());
				eventStream.insert(evt);
				ksession.fireAllRules();
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}
		
	private KnowledgeBase readKnowledgeBase(String ruleConverted) {
		
		try
		{
			KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
			config.setOption(EventProcessingOption.STREAM);
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource(RULEPATH, EventsEvaluator.class.getClassLoader()), ResourceType.DRL);
			KnowledgeBuilderErrors errors = kbuilder.getErrors();
			if (errors.size() > 0) {
				for (KnowledgeBuilderError error: errors) {
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
}
