package it.cnr.isti.labse.connectEnabler.core;


import it.cnr.isti.labse.connectEnabler.utils.DebugMessages;
import it.cnr.isti.labse.connectEnabler.utils.Manager;

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

import org.example.complexEventResponse.ComplexEventResponseListDocument;
import org.example.complexEventResponse.impl.ComplexEventResponseListDocumentImpl;

public class GenericConnectEnabler extends Thread implements MessageListener,
		ConnectEnabler {

	private String enablerName;
	private String serviceTopic;
	private String requestRulePath_0;
	private TopicConnection connection;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private Topic connectionTopic;
	private TopicPublisher tPub;
	private TopicSubscriber tSub;
	private String answerTopic;

	public GenericConnectEnabler(Properties settings,
			TopicConnectionFactory connectionFact, InitialContext initConn) {
		this.requestRulePath_0 = settings.getProperty("requestRulePath_0");
		this.serviceTopic = settings.getProperty("serviceTopic");
		this.setEnablerName(settings.getProperty("enablerName"));
		setupConnection(connectionFact, initConn);
	}

	public void setupConnection(TopicConnectionFactory connectionFact,
			InitialContext initConn) {
		try {
			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating connection object ");
			connection = connectionFact.createTopicConnection();
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating public session object ");
			publishSession = connection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(),
					"Setting up destination topic ");
			connectionTopic = (Topic) initConn.lookup(serviceTopic);
			tPub = publishSession.createPublisher(connectionTopic);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(),
					"Creating subscribe object ");
			subscribeSession = connection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(),
					"Setting up reading topic ");
			connectionTopic = (Topic) initConn.lookup(serviceTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic,
					"DESTINATION = '" + enablerName + "'", true);
			DebugMessages.ok();
			tSub.setMessageListener(this);

			DebugMessages
					.print(this.getClass().getSimpleName(),
							"Starting connection and wait 5 seconds for system startup");
			connection.start();
			DebugMessages.ok();
			DebugMessages.line();

		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private void sendRequest(TextMessage msg) {
		try {
			if (msg != null) {
				System.out.println(this.getClass().getSimpleName() + ": send "
						+ msg.getText());
				tPub.publish(msg);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		sendRequest(createMessage(Manager.ReadTextFromFile(requestRulePath_0)));
	}

	@Override
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0;
		try {
			if (msg.getText().startsWith("AnswerTopic == ")) {
				System.out.println(this.getClass().getSimpleName() + "#"
						+ this.hashCode() + ": receive " + msg.getText());
				setAnwerTopic(msg.getText()
						.substring(14, msg.getText().length()).trim());
				listenForAnswer(answerTopic);
				msg.acknowledge();
			} else
			{
				exploitIncomingResponse(arg0);
			}
				
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void exploitIncomingResponse(Message incomingResponseList)
	{
		ObjectMessage responseListObj = (ObjectMessage)incomingResponseList;
		try {
			ComplexEventResponseListDocument responseListDocument = (ComplexEventResponseListDocument)responseListObj.getObject();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODOTODOTODO
	}
	
	private void listenForAnswer(String answerTopic) {

		DebugMessages.print(this.getClass().getSimpleName(),
				"Creating subscribe object ");
		try {
			subscribeSession = connection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			DebugMessages.ok();

			DebugMessages.print(this.getClass().getSimpleName(),
					"Setting up answerTopic ");
			connectionTopic = subscribeSession.createTopic(answerTopic);
			tSub = subscribeSession.createSubscriber(connectionTopic, null,
					true);
			tSub.setMessageListener(this);
			DebugMessages.ok();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private TextMessage createMessage(String msg) {
		try {
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(msg);
			sendMessage.setStringProperty("SENDER", this.getEnablerName());
			sendMessage.setStringProperty("DESTINATION", "monitor");
			return sendMessage;

		} catch (JMSException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getEnablerName() {
		return this.enablerName;
	}

	public void setEnablerName(String enablerName) {
		this.enablerName = enablerName;
	}

	public String getAnwerTopic() {
		return this.answerTopic;
	}

	public void setAnwerTopic(String answerTopic) {
		this.answerTopic = answerTopic;
	}
}
