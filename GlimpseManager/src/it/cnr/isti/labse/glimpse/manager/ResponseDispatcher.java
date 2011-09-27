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
package it.cnr.isti.labse.glimpse.manager;

import it.cnr.isti.labse.glimpse.consumer.ConsumerProfile;
import it.cnr.isti.labse.glimpse.xml.complexEventResponse.ComplexEventResponse;
import it.cnr.isti.labse.glimpse.xml.complexEventResponse.ComplexEventResponseListDocument;

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

	public static void sendResponse(String ruleMatched, String key, String value, String enablerName, String answerTopic)
	{
		try {
			publicSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			connectionTopic = publishSession.createTopic(answerTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			ComplexEventResponseListDocument rsp;
			rsp = ComplexEventResponseListDocument.Factory.newInstance();
			ComplexEventResponse response = rsp.addNewComplexEventResponseList();
			response.setRuleName(ruleMatched);
			response.setResponseKey(key);
			response.setResponseValue(value);
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(rsp.xmlText());
			sendMessage.setStringProperty("DESTINATION", enablerName);
			tPub.publish(sendMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void NotifyMe(String ruleMatched, String enablerName, String key, String value)
	{
		ConsumerProfile enablerMatched = (ConsumerProfile)requestMap.get(ruleMatched);
		
		/*ComplexEventResponseListDocument responseList = ComplexEventResponseListDocument.Factory.newInstance();

		ComplexEventResponse theResponse = responseList.addNewComplexEventResponseList();
		
		ComplexEventResponseType theResponseType = theResponse.addNewResponseType();
		theResponseType.setString(value.toString());*/
		
		ResponseDispatcher.sendResponse(ruleMatched, key, value, enablerName, enablerMatched.getAnswerTopic());
		System.out.println(ResponseDispatcher.class.getSimpleName()
				+ ": ruleMatched: " + ruleMatched
				+ " - enablerName: " + enablerName
				+ " - evaluationResult: " + value.toString());
	}
}
