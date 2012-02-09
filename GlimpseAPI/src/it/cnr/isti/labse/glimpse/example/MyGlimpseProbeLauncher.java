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

import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.event.GlimpseBaseEventImpl;
import it.cnr.isti.labse.glimpse.utils.Manager;

/** 
 * 
 * This is a test class that execute the extended class {@link MyGlimpseProbe}<br />
 * that simply override the {@link MyGlimpseProbe#sendMessage(GlimpseBaseEvent, boolean)} <br />
 * firing {@link GlimpseBaseEvent} events to the ESB which parameters are defined<br />
 * in the constructor {@link MyGlimpseProbe#MyGlimpseProbe(java.util.Properties)} <br />
 * The settings parameters may be defined also using the method
 * contained into the abstract class GlimpseAbstractProbe called<br />
 * {@link Manager#createProbeSettingsPropertiesObject(String, String, String, String, String, String, boolean, String, String)}.
 * 
 * @author Antonello Calabr&ograve;
 * @version 3.2
 * 
 */

public class MyGlimpseProbeLauncher {
	
	public static void main(String[] args) {
		
		MyGlimpseProbe aGenericProbe = new MyGlimpseProbe(Manager.createProbeSettingsPropertiesObject("org.apache.activemq.jndi.ActiveMQInitialContextFactory","tcp://atlantis.isti.cnr.it:61616","system","manager","TopicCF","jms.probeTopic",false, "probeName", "probeTopic"));
		
		try {
			//to send a generic String GlimpseBaseEvent, the data field should be the label of the LTS
			for (int i = 0; i<3; i++) {
				GlimpseBaseEvent<String> message = new GlimpseBaseEventImpl<String>("anEvent", "connector1","connInstance", "conninstexec",123,122,System.currentTimeMillis(),"NS1", false);
				message.setData("Drone");
				aGenericProbe.sendMessage(message, false);
				Thread.sleep(1000);	
				GlimpseBaseEvent<String> messageB = new GlimpseBaseEventImpl<String>("anotherEvent", "connector1","connInstance", "conninstexec",123,122,System.currentTimeMillis(),"NS1", false);
				messageB.setData("afterDrone");
				aGenericProbe.sendMessage(messageB, false);
				Thread.sleep(1000);	
			}
			
			//sending a genericObject
			GlimpseBaseEvent<Object> message = new GlimpseBaseEventImpl<Object>("asd", "connector1","connInstance", "conninstexec",123,122,System.currentTimeMillis(),"NS1", false);
			message.setData("An Object");
			aGenericProbe.sendMessage(message, false);	
			
			//Throwing a ClassCastException
			String[] arr = new String[2];
			System.out.println(arr[4]);	
		}
		catch(IndexOutOfBoundsException asdG) {

			//HERE a simulation of a ExceptionEvent
			GlimpseBaseEvent<Exception> exceptionEvent = new GlimpseBaseEventImpl<Exception>("e", "connector1","connInstance", "conninstexec",123,122,System.currentTimeMillis(),"NS1", true);
				
			exceptionEvent.setIsException(true);
			exceptionEvent.setData(asdG);
			
			aGenericProbe.sendMessage(exceptionEvent, false);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
}
