package it.cnr.isti.labse.glimpse.manager;

import it.cnr.isti.labse.glimpse.consumer.ConsumerProfile;

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

public class ResponseDispatcher {

	private static Topic connectionTopic;
	@SuppressWarnings("unused")
	private static InitialContext initConn;
	private static TopicSession publishSession;
	private static TopicPublisher tPub;
	private static HashMap<Object, ConsumerProfile> requestMap;
	private static TopicConnection connection;
	@SuppressWarnings("unused")
	private static TopicSession publicSession;
	
	public ResponseDispatcher(InitialContext initConn,
			TopicConnectionFactory connectionFact,
			HashMap<Object, ConsumerProfile> requestMap) {

		ResponseDispatcher.requestMap = requestMap;
		ResponseDispatcher.initConn = initConn;
		try {
			connection = connectionFact.createTopicConnection();
			publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
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
			tPub.publish(sendMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void NotifyMe(String ruleMatched, String enablerName, String value)
	{
		ConsumerProfile enablerMatched = (ConsumerProfile)requestMap.get(ruleMatched);
		
		/*ComplexEventResponseListDocument responseList = ComplexEventResponseListDocument.Factory.newInstance();

		ComplexEventResponse theResponse = responseList.addNewComplexEventResponseList();
		
		ComplexEventResponseType theResponseType = theResponse.addNewResponseType();
		theResponseType.setString(value.toString());*/
		
		ResponseDispatcher.sendResponse(value, enablerName, enablerMatched.getAnswerTopic());
		System.out.println(ResponseDispatcher.class.getSimpleName()
				+ ": ruleMatched: " + ruleMatched
				+ " - enablerName: " + enablerName
				+ " - evaluationResult: " + value.toString());
	}
}
