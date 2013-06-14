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
package it.cnr.isti.labse.glimpse.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.NamingException;

import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.event.GlimpseBaseEventChoreos;
import it.cnr.isti.labse.glimpse.probe.GlimpseAbstractProbe;
import it.cnr.isti.labse.glimpse.probe.GlimpseProbe;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;


/**
 * This class provides an example of how to send messages (events) to the Glimpse Monitoring Bus,
 * <br />there are three methods for sending event on which payload is <br /><br />
 * a String: {@link MyGlimpseProbe#generateAndSendExample_GlimpseBaseEvents_StringPayload(int)},<br />
 * an Object: {@link MyGlimpseProbe#generateAndSendExample_GlimpseBaseEvents_ObjectPayload(int)},<br />
 * an Exception: {@link MyGlimpseProbe#generateAndSendExample_GlimpseBaseEvents_ExceptionPayload(Exception)}<br /><br />
 * the sendEvent action behavior may be modified simply implementing the abstract method <br />
 * {@link GlimpseAbstractProbe#sendMessage(GlimpseBaseEvent, boolean)}.<br /<br />
 * In this class we used the classic sendEventMessage method provided by the abstract class {@link GlimpseAbstractProbe}<br />
 * <br />
 * You can directly implement your probe extending {@link GlimpseAbstractProbe} or directly implementing {@link GlimpseProbe}<br /><br />
 * @author Antonello Calabr&ograve;
 * @version 3.3.1
 *
 */

public class MyGlimpseProbe_Two extends GlimpseAbstractProbe {

	public static int sendingInterval = 90000;
	
	public MyGlimpseProbe_Two(Properties settings) {
		super(settings);
	}


	private void generateAndSendExample_GlimpseBaseEvents_StringPayload(String data, int sendingInterval) throws UnknownHostException {
		DebugMessages.ok();
		DebugMessages.print(MyGlimpseProbe_Two.class.getName(),"Creating GlimpseBaseEventChoreos message");
		GlimpseBaseEventChoreos<String> message;
		DebugMessages.ok();
		DebugMessages.line();
		try {
			
			while(true) {
				message = new GlimpseBaseEventChoreos<String>(data, 
						System.currentTimeMillis(), "SLA Alert", false,
						"CH1", "service1", InetAddress.getLocalHost().toString());
				this.sendEventMessage(message, false);
				DebugMessages.println(MyGlimpseProbe_Two.class.getName(),"GlimpseBaseEventChoreos message sent\n"
				+ "timestamp: " + message.getTimeStamp() + "\n"
				+ "eventData: " + message.getEventData() + "\n"
				+ "machineIP: " + message.getMachineIP() + "\n"
				+ "choreographySource: " + message.getChoreographySource() + "\n" 
				+ "eventName: " + message.getEventName() + "\n"
				+ "eventData: " + message.getEventData());
				DebugMessages.line();
				Thread.sleep(sendingInterval);
			}
		} catch (JMSException e1) {
			e1.printStackTrace();
		} catch (NamingException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	

	/*
	 * To override the sendMessage, you can use the following code
	 * 
	 * @Override
	 * public void sendMessage(GlimpseBaseEvent<?> event, boolean debug) {
	 * 	//YOUR SENDING IMPLEMENTATION
	 * }
	 * 
	 * */
	
	@Override
	public void sendMessage(GlimpseBaseEvent<?> event, boolean debug) {		
	}

	public static void main(String[] args) throws UnknownHostException {

		DebugMessages.line();
		DebugMessages.println(MyGlimpseProbe_Two.class.getName(),"\nONE ALERT WILL BE SENT ON EACH 90 SECONDS\nTO SPECIFY A DIFFERENT RATE, PROVIDE AN ARG IN MILLISECONDS\nUSAGE: java -jar MyGlimpseProbe_Two.jar [amountOfMilliseconds]");
		DebugMessages.line();
		try {
			if (args.length > 0 && Integer.parseInt(args[0])>0) {
				sendingInterval = Integer.parseInt(args[0]);
			}	
		} catch (IndexOutOfBoundsException e) {
		}
		
		MyGlimpseProbe_Two aGenericProbe = new
				MyGlimpseProbe_Two(Manager.createProbeSettingsPropertiesObject(
								"org.apache.activemq.jndi.ActiveMQInitialContextFactory",
								"tcp://atlantis.isti.cnr.it:61616",
								"system",
								"manager",
								"TopicCF",
								"jms.probeTopic",
								false,
								"probeName",
								"probeTopic")
								);
		
		DebugMessages.println(MyGlimpseProbe_Two.class.getName(),"Starting infinite loop");	
		aGenericProbe.generateAndSendExample_GlimpseBaseEvents_StringPayload("<wscap:alert xmlns:admin=\"http://com.petalslink.easyesb/admin/model/datatype/1.0\" xmlns:bsoap11=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:cad=\"http://com.petalslink.common.alerting.definition/1.0\" xmlns:cadsoap=\"http://com.petalslink.common.alerting.definition.protocol/soap-notification/1.0\" xmlns:capnote=\"http://com.petalslink.common.alerting.protocol.note/1.0\" xmlns:exch=\"http://com.petalslink.easyesb/exchange/1.0\" xmlns:ns13=\"http://docs.oasis-open.org/wsdm/muws2-2.xsd\" xmlns:ns14=\"http://docs.oasis-open.org/wsdm/muws1-2.xsd\" xmlns:plnk=\"http://docs.oasis-open.org/wsbpel/2.0/plnktype\" xmlns:qml=\"http://upmc.fr/ns/ws-qml\" xmlns:rawreport=\"http://com.petalslink/esrawreport/1.0\" xmlns:soa=\"http://com.petalslink.easyesb/soa/model/datatype/1.0\" xmlns:vprop=\"http://docs.oasis-open.org/wsbpel/2.0/varprop\" xmlns:wsaddr=\"http://www.w3.org/2005/08/addressing\" xmlns:wsagr=\"http://schemas.ggf.org/graap/2007/03/ws-agreement\" xmlns:wscap=\"urn:oasis:names:tc:emergency:cap:1.2\" xmlns:wsdl11=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsdm=\"http://docs.oasis-open.org/wsdm/mows-2.xsd\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><wscap:identifier>58e5a3ae-9a67-45e8-8fd9-e85459b6813c</wscap:identifier><wscap:sent>2013-06-11T15:45:33.777+02:00</wscap:sent><wscap:msgType>Alert</wscap:msgType><wscap:note>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;capnote:alertNoteDefinition xmlns:admin=\"http://com.petalslink.easyesb/admin/model/datatype/1.0\" xmlns:bsoap11=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:cad=\"http://com.petalslink.common.alerting.definition/1.0\" xmlns:cadsoap=\"http://com.petalslink.common.alerting.definition.protocol/soap-notification/1.0\" xmlns:capnote=\"http://com.petalslink.common.alerting.protocol.note/1.0\" xmlns:exch=\"http://com.petalslink.easyesb/exchange/1.0\" xmlns:ns13=\"http://docs.oasis-open.org/wsdm/muws2-2.xsd\" xmlns:ns14=\"http://docs.oasis-open.org/wsdm/muws1-2.xsd\" xmlns:plnk=\"http://docs.oasis-open.org/wsbpel/2.0/plnktype\" xmlns:qml=\"http://upmc.fr/ns/ws-qml\" xmlns:rawreport=\"http://com.petalslink/esrawreport/1.0\" xmlns:soa=\"http://com.petalslink.easyesb/soa/model/datatype/1.0\" xmlns:vprop=\"http://docs.oasis-open.org/wsbpel/2.0/varprop\" xmlns:wsaddr=\"http://www.w3.org/2005/08/addressing\" xmlns:wsagr=\"http://schemas.ggf.org/graap/2007/03/ws-agreement\" xmlns:wscap=\"urn:oasis:names:tc:emergency:cap:1.2\" xmlns:wsdl11=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsdm=\"http://docs.oasis-open.org/wsdm/mows-2.xsd\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"&gt;  &lt;capnote:message&gt;latency violation: current latency(3075) is not &amp;lt; to 2000 (Confirmed)&lt;/capnote:message&gt;  &lt;capnote:responderIdentifier&gt;    &lt;capnote:endpoint&gt;      &lt;capnote:namespaceURI&gt;http://securitycompany.services.scn2.wp6.thalesgroup.choreos.ow2.org/&lt;/capnote:namespaceURI&gt;      &lt;capnote:serviceName&gt;SecurityCompanyService&lt;/capnote:serviceName&gt;      &lt;capnote:endpointName&gt;SecurityCompanyPort&lt;/capnote:endpointName&gt;    &lt;/capnote:endpoint&gt;    &lt;capnote:originalURL&gt;http://localhost:8187/securitycompany&lt;/capnote:originalURL&gt;  &lt;/capnote:responderIdentifier&gt;  &lt;capnote:initiatorIdentifier&gt;    &lt;capnote:endpoint&gt;      &lt;capnote:namespaceURI&gt;http://securitycompany.services.scn2.wp6.thalesgroup.choreos.ow2.org/&lt;/capnote:namespaceURI&gt;      &lt;capnote:endpointName&gt;SecurityCompanyPortClientProxyEndpoint&lt;/capnote:endpointName&gt;    &lt;/capnote:endpoint&gt;    &lt;capnote:originalURL&gt;http://localhost:8180/services/SecurityCompanyPortClientProxyEndpoint&lt;/capnote:originalURL&gt;  &lt;/capnote:initiatorIdentifier&gt;&lt;/capnote:alertNoteDefinition&gt;</wscap:note><wscap:info><wscap:category>Infra</wscap:category><wscap:event>latency violation: current latency(3075) is not &lt; to 2000 (Confirmed)</wscap:event><wscap:urgency>Unknown</wscap:urgency><wscap:severity>Severe</wscap:severity><wscap:certainty>Observed</wscap:certainty></wscap:info></wscap:alert>", sendingInterval);
	}
}
