package it.cnr.isti.labse.glimpse.example;

import it.cnr.isti.labse.glimpse.api.consumer.GlimpseAbstractConsumer;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;


public class MyGlimpseConsumer extends GlimpseAbstractConsumer {

	public MyGlimpseConsumer(Properties settings,
			String plainTextRule) {
		super(settings, plainTextRule);
	}

	@Override
	public void messageReceived(Message arg0) throws JMSException {
		System.out.println(((TextMessage)arg0).getText());
	}
}