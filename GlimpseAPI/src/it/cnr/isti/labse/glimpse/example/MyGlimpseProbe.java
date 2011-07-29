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

import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.NamingException;

import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.api.probe.GlimpseAbstractProbe;
import it.cnr.isti.labse.glimpse.api.probe.GlimpseProbe;


/**
 * This class is an example of how to extend the {@link GlimpseAbstractProbe} class, <br />
 * simply implementing the abstract method {@link MyGlimpseProbe#sendMessage(GlimpseBaseEvent, boolean)}.
 * <br /><br />
 * Obviously the behaviour of the class is defined in the {@link GlimpseAbstractProbe} class and<br />
 * can be modified implementing the interface {@link GlimpseProbe}.<br /><br />
 * 
 * Extending the {@link GlimpseAbstractProbe} class, it is possible to improve or modify the behaviour of theProbe.
 * 
 * @author acalabro
 *
 */

public class MyGlimpseProbe extends GlimpseAbstractProbe {

	public MyGlimpseProbe(Properties settings) {
		super(settings);
	}

	@Override
	public void sendMessage(GlimpseBaseEvent<String> event, boolean debug) {
		try {
			sendEventMessage(event, debug);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}		
	}

}
