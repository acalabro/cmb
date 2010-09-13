package it.cnr.isti.labse.cmb.listener;

import it.cnr.isti.labse.cmb.buffer.EventsBuffer;
import it.cnr.isti.labse.cmb.event.ConnectBaseEvent;
import it.cnr.isti.labse.cmb.event.SimpleEvent;
import it.cnr.isti.labse.cmb.rules.ConnectBaseRule;
import it.cnr.isti.labse.cmb.rules.RuleConverter;
import it.cnr.isti.labse.cmb.settings.DebugMessages;

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

public class DroolsEventsEvaluator implements MessageListener, EventsEvaluator {

	private String topic;
	private TopicConnection connection;
	private Topic connectionTopic;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private ConnectBaseRule listenerRule;
	private KnowledgeBase kbase;
	private StatefulKnowledgeSession ksession;
	private WorkingMemoryEntryPoint eventStream;
	private String answerTopic;
	private ConnectBaseEvent<String> receivedEvent;
	protected static String RULEPATH = "it/cnr/isti/labse/cmb/rules/FirstRule.drl";

	public DroolsEventsEvaluator(Properties settings, EventsBuffer<SimpleEvent> buffer, ConnectBaseRule listenerRule, String answerTopic) {
		this.listenerRule = listenerRule;
		this.topic = settings.getProperty("probeTopic");
		this.answerTopic = answerTopic;
	}
	
	public void setupConnection(TopicConnectionFactory connectionFact, InitialContext initConn)
	{
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
			String ruleConverted = RuleConverter.convert(listenerRule);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Reading knowledge base ");
			kbase = readKnowledgeBase(ruleConverted);
			ksession = kbase.newStatefulKnowledgeSession();
			ksession.setGlobal("EVENTS EntryPoint", eventStream);
			eventStream = ksession.getWorkingMemoryEntryPoint("DEFAULT");
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

	private KnowledgeBase readKnowledgeBase(String ruleConverted) {

		try {
			KnowledgeBaseConfiguration config = KnowledgeBaseFactory
					.newKnowledgeBaseConfiguration();
			config.setOption(EventProcessingOption.STREAM);
			
			/*RuleBaseConfiguration conf = new RuleBaseConfiguration();
			conf.setSequential(true);

			RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );*/
			
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
					.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource(RULEPATH, this
					.getClass().getClassLoader()), ResourceType.DRL);
						KnowledgeBuilderErrors errors = kbuilder.getErrors();
			if (errors.size() > 0) {
				for (KnowledgeBuilderError error : errors) {
					System.err.println(error);
				}
				throw new IllegalArgumentException("Could not parse knowledge.");
			}

			KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
			kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

			/*PackageBuilder pb = new PackageBuilder();
			pb.addPackageFromDrl(ResourceFactory.newClassPathResource(RULEPATH, this
					.getClass().getClassLoader()));
			ruleBase.addPackage(pb.getPackage());*/
			
			return kbase;
		} catch (Exception e) {
			return null;
		}
	}

	public void start() {
		try {
			tSub.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		ksession.fireUntilHalt();
		DebugMessages.line();
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
