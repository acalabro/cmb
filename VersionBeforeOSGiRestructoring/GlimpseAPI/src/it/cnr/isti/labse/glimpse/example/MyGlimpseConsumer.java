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

import it.cnr.isti.labse.glimpse.consumer.GlimpseAbstractConsumer;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;


/**
 * This class is an example of how to extend the {@link GlimpseAbstractConsumer} class, <br />
 * simply implementing the abstract method {@link MyGlimpseConsumer#messageReceived(Message)}.
 * <br /><br />
 * Obviously the behaviour of the class is defined in the {@link GlimpseAbstractConsumer} class and<br />
 * can be modified implementing the interface {@link GlimpseConsumer}.<br /><br />
 * 
 * Extending the {@link GlimpseAbstractConsumer} class, it is possible to improve the behaviour of the consumer.
 * 
 * @author acalabro
 *
 */
public class MyGlimpseConsumer extends GlimpseAbstractConsumer {

	/**
	 * @param settings can be generated automatically using {@link Manager#createConsumerSettingsPropertiesObject(String, String, String, String, String, String, boolean, String)}
	 * @param plainTextRule a plain text rule is a String containing the drools<br />
	 * (or other cep engine implemented) rule that can be generated structured<br />
	 * using the {@link ComplexEventRuleActionListDocument} classes.<br />
	 * For a rule example see the exampleRule.xml file. 
	 * 
	 */
	public MyGlimpseConsumer(Properties settings,
			String plainTextRule) {
		super(settings, plainTextRule);
	}

	@Override
	public void messageReceived(Message arg0) throws JMSException {
		System.out.println(((TextMessage)arg0).getText());
	}
}