package it.cnr.isti.labse.cmb.listener;

import javax.jms.Message;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

public interface EventsEvaluator {
	public abstract void onMessage(Message arg0);
	public abstract void start();
	public abstract void setupConnection(TopicConnectionFactory connectionFact,
			InitialContext initConn);
}