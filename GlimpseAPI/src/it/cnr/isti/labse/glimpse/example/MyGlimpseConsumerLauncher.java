package it.cnr.isti.labse.glimpse.example;

import it.cnr.isti.labse.glimpse.api.consumer.GlimpseAbstractConsumer;
import it.cnr.isti.labse.glimpse.utils.Manager;

public class MyGlimpseConsumerLauncher {

	public static void main(String[] args) {
		
		new MyGlimpseConsumer(GlimpseAbstractConsumer.createSettingsPropertiesObject("org.apache.activemq.jndi.ActiveMQInitialContextFactory","tcp://atlantis.isti.cnr.it:61616","system","manager","TopicCF","jms.serviceTopic",false, "consumerTest"),
				Manager.ReadTextFromFile(System.getProperty("user.dir") + "/bin/it/cnr/isti/labse/glimpse/example/exampleRule.xml"));
	}
}
