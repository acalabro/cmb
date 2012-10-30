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

public class MyGlimpseProbe_One extends GlimpseAbstractProbe {

	public MyGlimpseProbe_One(Properties settings) {
		super(settings);
	}


	private void generateAndSendExample_GlimpseBaseEvents_StringPayload(String data) throws UnknownHostException {
			GlimpseBaseEventChoreos<String> message = new GlimpseBaseEventChoreos<String>(data, 
				System.currentTimeMillis(), "TheEventName", false,
				"CH1", "service1", InetAddress.getLocalHost().toString());
		try {
			this.sendEventMessage(message, false);
		} catch (JMSException e1) {
			e1.printStackTrace();
		} catch (NamingException e1) {
			e1.printStackTrace();
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
		
		MyGlimpseProbe_One aGenericProbe = new
				MyGlimpseProbe_One(Manager.createProbeSettingsPropertiesObject(
								"org.apache.activemq.jndi.ActiveMQInitialContextFactory",
								"tcp://localhost:61616",
								"system",
								"manager",
								"TopicCF",
								"jms.probeTopic",
								false,
								"probeName",
								"probeTopic")
								);
		
			DebugMessages.println(MyGlimpseProbe_One.class.getSimpleName(), "Executing generateAndSendExample_GlimpseBaseEvents_StringPayload method");
			aGenericProbe.generateAndSendExample_GlimpseBaseEvents_StringPayload("SLA Violation");		
	}
}