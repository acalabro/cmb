package it.cnr.isti.labse.glimpse.manager;

import it.cnr.isti.labse.glimpse.enabler.EnablerProfile;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ResponseDispatcher {

	private static Topic connectionTopic;
	private static InitialContext initConn;
	private static TopicSession publishSession;
	private static TopicPublisher tPub;
	private static HashMap<Object, EnablerProfile> requestMap;
	private static TopicConnection connection;
	private static TopicSession publicSession;
	
	public ResponseDispatcher(InitialContext initConn,
			TopicConnectionFactory connectionFact,
			HashMap<Object, EnablerProfile> requestMap) {

		ResponseDispatcher.requestMap = requestMap;
		ResponseDispatcher.initConn = initConn;
		try {
			connection = connectionFact.createTopicConnection();
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public static void sendResponse(String msg, String enablerName, String answerTopic)
	{
		try {
			publicSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
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
		
		ResponseDispatcher.sendResponse("The evaluation for request: " + ruleMatched + " is: " + (String)value.toString(), enablerName, enablerMatched.getAnswerTopic());
		System.out.println(ResponseDispatcher.class.getSimpleName()
				+ ": ruleMatched: " + ruleMatched
				+ " - enablerName: " + enablerName
				+ " - evaluationResult: " + value.toString());
	}
}
