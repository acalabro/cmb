package it.cnr.isti.labse.connectEnabler.core;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

public interface ConnectEnabler {
	abstract void setupConnection(TopicConnectionFactory connectionFact, InitialContext initConn);
	public String getEnablerName();
	public void setEnablerName(String enablerName);
	public String getAnwerTopic();
	public void setAnwerTopic(String answerTopic);
}