package it.cnr.isti.labse.glimpse.manager;

import it.cnr.isti.labse.glimpse.enabler.EnablerProfile;
import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.rules.RulesManager;
import it.cnr.isti.labse.glimpse.settings.DebugMessages;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;

import java.util.HashMap;
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

import org.apache.xmlbeans.XmlException;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.definition.rule.*;

public class ConnectEnablersManager extends Thread implements MessageListener {
	
	private TopicConnection connection;
	private static TopicSession publishSession;
	private TopicSession subscribeSession;
	private static Topic connectionTopic;
	private static TopicPublisher tPub;
	private TopicSubscriber tSub;
	private String serviceTopic;
	private String answerTopic;
	private RulesManager rulesManager;
	public static HashMap<Object, EnablerProfile> requestMap = new HashMap<Object, EnablerProfile>();
	
	public ConnectEnablersManager(Properties settings, TopicConnectionFactory connectionFact, InitialContext initConn, RulesManager rulesManager)
	{
		serviceTopic = settings.getProperty("serviceTopic");
		setupConnection(connectionFact, initConn);
		this.rulesManager = rulesManager; 
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
			
			DebugMessages.print(this.getClass().getSimpleName(), "Creating subscribe object");
			subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setting up destination topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(), "Setting up reading topic ");
			connectionTopic = (Topic)initConn.lookup(serviceTopic);
			
			//TODO ricordarsi che la destination va modificata
			tSub = subscribeSession.createSubscriber(connectionTopic, "DESTINATION = 'monitor'", true);
			tSub.setMessageListener(this);
			DebugMessages.ok();
						
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Message arg0) {
		
			TextMessage msg = null;
		try {
			msg = (TextMessage) arg0;
			DebugMessages.line();			
			System.out.println(this.getClass().getSimpleName() + ": receive " + msg.getText());
			DebugMessages.line();
			String XMLRule = msg.getText();
			String sender = msg.getStringProperty("SENDER");		
			
			ComplexEventRuleActionListDocument ruleDoc;			
			ruleDoc = ComplexEventRuleActionListDocument.Factory.parse(XMLRule);	
			ComplexEventRuleActionType rules = ruleDoc.getComplexEventRuleActionList();
			
			//the topic where the listener will give analysis results
			answerTopic =  "answerTopic" + "#" + this.getName() + "#" + System.nanoTime();

			DebugMessages.print(this.getClass().getSimpleName(),"Communicate the answerTopic to the enabler");
			sendMessage(createMessage("AnswerTopic == " + answerTopic, sender));
			DebugMessages.ok();	

			DebugMessages.print(this.getClass().getSimpleName(), "Create answerTopic");
			connectionTopic = publishSession.createTopic(answerTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();
			
			DebugMessages.print(this.getClass().getSimpleName(), "Setup ComplexEventProcessor with Enabler request.");
			try {
				Object[] loadedKnowledgePackage = rulesManager.loadRules(rules);
				//inserisco la coppia chiave valore dove la chiave è il KnowledgePackage
				//caricato, generato da DroolsRulesManager con la loadRules
				//e il valore è l'enabler che l'ha inviata
				//(il KnowledgePackage array dovrebbe avere sempre dimensione 1 essendo creato ad ogni loadrules)
				for (int i = 0; i<loadedKnowledgePackage.length;i++)
				{
					KnowledgePackageImp singleKnowlPack = (KnowledgePackageImp) loadedKnowledgePackage[i];
					Rule singleRuleContainer[] = new Rule[singleKnowlPack.getRules().size()];
					singleRuleContainer = singleKnowlPack.getRules().toArray(singleRuleContainer);
					
					for(int j = 0; j<singleRuleContainer.length;j++)
					{
						requestMap.put(singleRuleContainer[j].getName(), new EnablerProfile(sender, answerTopic));
					}
				}
			} catch (IncorrectRuleFormatException e) {
				sendMessage(createMessage("PROVIDED RULE CONTAINS ERRORS", msg.getStringProperty("SENDER")));
			}
			DebugMessages.ok();
			DebugMessages.line();
		} catch (XmlException e) {
				try {
					sendMessage(createMessage("PROVIDED XML CONTAINS ERRORS", msg.getStringProperty("SENDER")));
				} catch (JMSException e1) {
					e1.printStackTrace();
				}	
		} catch(JMSException ee) {
				ee.printStackTrace();
		}
	}

	public void run()
	{
		DebugMessages.print(this.getClass().getSimpleName(), "Starting connection ");
		try {
			connection.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		DebugMessages.ok();
		DebugMessages.line();
	}

	private TextMessage createMessage(String msg, String sender)
	{
		try {
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(msg);
			sendMessage.setStringProperty("DESTINATION", sender);
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
				//DebugMessages.line();
				tPub.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendResponse(String msg, String enablerName, String answerTopic)
	{
		try {
			connectionTopic = publishSession.createTopic(answerTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(msg);
			sendMessage.setStringProperty("DESTINATION", enablerName);
			System.out.println(ConnectEnablersManager.class.getSimpleName() + ": send " + sendMessage.getText() + " on channel: " + answerTopic);
			tPub.publish(sendMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void NotifyMe(String ruleMatched, String enablerName, Object value)
	{
		EnablerProfile enablerMatched = (EnablerProfile)requestMap.get(ruleMatched);
		
		ConnectEnablersManager.sendResponse("The evaluation for request: " + ruleMatched + " is: " + (String)value.toString(), enablerName, enablerMatched.getAnswerTopic());
		System.out.println(ConnectEnablersManager.class.getSimpleName()
				+ ": ruleMatched: " + ruleMatched
				+ " - enablerName: " + enablerName
				+ " - evaluationResult: " + value.toString());
	}
}