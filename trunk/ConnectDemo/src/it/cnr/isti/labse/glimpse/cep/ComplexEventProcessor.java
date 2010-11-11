package it.cnr.isti.labse.glimpse.cep;

import it.cnr.isti.labse.glimpse.rules.RulesManager;

import javax.jms.Message;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

public abstract class ComplexEventProcessor extends Thread {
	public RulesManager cepRuleManager;
	public abstract RulesManager getRuleManager();
	
	public abstract void init(TopicConnectionFactory connectionFact, InitialContext initConn);
	public abstract void onMessage(Message arg0);	
	public abstract void setMetric();
}