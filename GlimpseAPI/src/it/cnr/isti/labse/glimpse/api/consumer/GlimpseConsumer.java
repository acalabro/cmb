package it.cnr.isti.labse.glimpse.api.consumer;

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
 * It is possible to use a generic implementation of the consumer
 * using or extending the class {@link GlimpseAbstractConsumer}
 * 
 * However, implementing this interface the behaviour of the 
 * consumer must respect the following actions:<br /><br />
 * 
 * Send a TextMessage that contains the Xml generated 
 * following the ComplexEventRequest schema<br /><br />
 * 
 * The message must be sent on the jms://serviceTopic channel.<br />
 * The consumer must listen for the response of the monitoring infrastructure
 * that will send a jms textMessage containing the response channel where to connect
 * to listen for the evaluation results.<br /><br />
 * 
 *  To aid on the creation of the XML, you can use the class ComplexEventRuleActionListDocument.<br />
 *  Possible XML actions are: Insert Delete Start Stop Restart.<br /><br />
 *  When a well structured message (see exampleRule.xml) is received from the monitoring infrastructure<br />
 *  the monitoring will provide a responseMessage (TextMessage) containing the name<br />
 *  
 * ************************************************************************<br />
 * **************An usage example is available here {@link GlimpseAbstractConsumer}**************<br />
 * ************************************************************************<br /><br />
 * 
 * @author Antonello Calabr&ograve;
 * @version 0.4
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
	 * Send a textMessage to the monitoring infrastructure,<br />
	 * examples of setting up connection and behaviour of<br />
	 * a generic {@link GlimpseConsumer} are defined in <br />
	 * the {@link GlimpseAbstractConsumer} class.
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