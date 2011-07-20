package it.cnr.isti.labse.glimpse.api.consumer;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import it.cnr.isti.labse.glimpse.utils.Status;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;


/**
 * Implementing this interface to create
 * a communication with the monitoring Enabler.<br /><br />
 * To correctly use the monitoring, the connection parameters
 * must be configured using the following methods:<br />
 * {@link #initConnection(Properties, boolean)}<br />
 * {@link #createConnection(InitialContext, Properties, boolean)}<br /><br />
 * <br />
 * How the Monitoring Enabler works:<br />
 *  -> To request an evaluation to the monitoring enabler, a client<br />
 *  must be able to send a well formed request: a JMS Message which payload<br />
 *  is an xml containing a set of actions expressed using the <br />
 *  ComplexEventRule.xsd schema.<br />
 *  To aid on the creation of the XML, you can use the class ComplexEventRuleActionListDocument.<br />
 *  Possible XML actions are: Insert Delete Start Stop Restart.<br /><br />
 *  When a well structured message (see exampleRule.xml in the APITestProject example) is received from the monitoring infrastructure<br />
 *  the monitoring will provide a responseMessage (TextMessage) containing the name<br />
 *  of the private topic where all the messages related to the requested evaluation will be sent.<br />
 *  When the enabler receives this message, should subscribes the new channel.<br />
 *  For this action, it can be use the method {@link #connectToTheResponseChannel(TopicConnection, String, boolean)}<br /><br />
 *  
 * ************************************************************************<br />
 * **************An usage example is available here {@link GlimpseAbstractConsumer}**************<br />
 * ************************************************************************<br /><br />
 * 
 * 	@see #initConnection(Properties, boolean) initConnection method set InitialContext <br />
 * 	@see #createConnection(InitialContext, Properties, boolean) set TopicConnectionFactory <br /> <br />
 * 
 * @author Antonello Calabr&ograve;
 * @version 0.2
 * 
 */
public interface GlimpseConsumer extends MessageListener{

	/**
	 * @param publishSession the TopicSession where to send the message
	 * @param debug debug value
	 * @return a TextMessage that can be sent to the monitoring infrastructure<br />
	 * to obtain the Status of the monitoring enabler<br />
	 * that is defined using the enum {@link Status}
	 */
	TextMessage getMonitorStatusMessage(TopicSession publishSession, boolean debug);
	
	/**
	 * Send a ActionList of rules to the monitoring infrastructure requesting an evaluation.<br />
	 * If the request is accepted, the monitoring will answer with a responseChannel where to connect<br />
	 * The actionList parameter is a list of action to provide to the Monitoring<br />
	 * To create it, you can use ComplexEventRuleActionListDocument class.
	 * 
	 * @param connection
	 * @param initContext
	 * @param serviceChannel
	 * @param actionList
	 * @param debug debug value
	 * @throws JMSException
	 * @throws NamingException
	 */
	void sendActionListMessage(TopicConnection connection, InitialContext initContext, String serviceChannel, ComplexEventRuleActionListDocument actionList, boolean debug) throws JMSException, NamingException;

	/**
	 * Send a textMessage to the monitoring infrastructure,
	 * you can use it after setting up connection, see 
	 * {@link #createConnection(InitialContext, Properties, boolean)} method
	 * and {@link #initConnection(Properties, boolean)} method
	 * 
	 * @param connection
	 * @param initContext
	 * @param serviceChannel
	 * @param textToSend
	 * @param debug debug value
	 * @throws JMSException
	 * @throws NamingException
	 */
	void sendTextMessage(TopicConnection connection, InitialContext initContext, String serviceChannel, String textToSend, boolean debug) throws JMSException, NamingException;
}