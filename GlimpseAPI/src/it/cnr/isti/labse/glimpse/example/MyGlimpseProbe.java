package it.cnr.isti.labse.glimpse.example;

import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.NamingException;

import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.api.probe.GlimpseAbstractProbe;


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
