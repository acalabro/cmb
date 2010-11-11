package it.cnr.isti.labse.glimpse.enabler;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

public interface ConnectEnabler {
	abstract void setupConnection(TopicConnectionFactory connectionFact, InitialContext initConn);
}