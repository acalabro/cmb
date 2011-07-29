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

import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEventImpl;
import it.cnr.isti.labse.glimpse.api.probe.GlimpseAbstractProbe;

/* 
 *
 * This is a test class that execute the extended class {@link MyGlimpseProbe}<br />
 * that simply override the {@link MyGlimpseProbe#sendMessage(GlimpseBaseEvent, boolean)} <br />
 * firing {@link GlimpseBaseEvent} events to the ESB which parameters are defined<br />
 * in the constructor {@link MyGlimpseProbe#MyGlimpseProbe(java.util.Properties)} <br />
 * using the method defined into the abstract class {@link GlimpseAbstractProbe} called<br />
 * {@link GlimpseAbstractProbe#createSettingsPropertiesObject(String, String, String, String, String, String, boolean, String, String)}.
 * 
 * @author acalabro
 */

public class MyGlimpseProbeLauncher {
	
	public static void main(String[] args) {
		
		MyGlimpseProbe asd = new MyGlimpseProbe(GlimpseAbstractProbe.createSettingsPropertiesObject("org.apache.activemq.jndi.ActiveMQInitialContextFactory","tcp://atlantis.isti.cnr.it:61616","system","manager","TopicCF","jms.probeTopic",false, "probeName", "probeTopic"));
		for(;;) {
			GlimpseBaseEvent<String> message = new GlimpseBaseEventImpl("asd", "connector1","connInstance", "conninstexec",123,122,System.currentTimeMillis(),"NS1");
			message.setData("aaahhahaha");
			asd.sendMessage(message, false);
		}
			
	}
}
