package it.cnr.isti.labse.glimpse.listener;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.exceptions.UnknownRuleException;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;

import javax.jms.Message;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;

public interface EventsEvaluator {
	public abstract void onMessage(Message arg0);
	public abstract void run(TopicConnectionFactory connectionFact, InitialContext initConn);
	public abstract void setSink(String topicName);
	public abstract void setMetric();
	public abstract void loadRules(ComplexEventRuleActionType rules);
	public abstract void insertRule(String rule, String ruleName) throws IncorrectRuleFormatException;
	public abstract void deleteRule(String ruleName) throws UnknownRuleException;
	public abstract void startRule(String ruleName) throws UnknownRuleException;
	public abstract void stopRule(String ruleName) throws UnknownRuleException;
	public abstract void restartRule(String ruleName) throws UnknownRuleException;
}