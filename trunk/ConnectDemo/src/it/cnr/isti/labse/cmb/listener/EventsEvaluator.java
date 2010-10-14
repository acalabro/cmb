package it.cnr.isti.labse.cmb.listener;

import javax.jms.Message;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

public interface EventsEvaluator {
	public abstract void onMessage(Message arg0);
	public abstract void start();
	public abstract void setSink(String topicName);
	public abstract void setMetric();
	public abstract void run(TopicConnectionFactory connectionFact,
			InitialContext initConn);
}