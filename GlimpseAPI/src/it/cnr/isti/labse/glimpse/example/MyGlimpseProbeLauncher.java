package it.cnr.isti.labse.glimpse.example;

import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.api.event.GlimpseBaseEventImpl;
import it.cnr.isti.labse.glimpse.api.probe.GlimpseAbstractProbe;

public class MyGlimpseProbeLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		MyGlimpseProbe asd = new MyGlimpseProbe(GlimpseAbstractProbe.createSettingsPropertiesObject("org.apache.activemq.jndi.ActiveMQInitialContextFactory","tcp://atlantis.isti.cnr.it:61616","system","manager","TopicCF","jms.probeTopic",false, "probeName", "probeTopic"));
		for(;;) {
			GlimpseBaseEvent<String> message = new GlimpseBaseEventImpl("asd", "connector1","connInstance", "conninstexec",123,122,System.currentTimeMillis(),"NS1");
			message.setData("aaahhahaha");
			asd.sendMessage(message, false);
		}
			
	}
}
