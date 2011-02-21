package it.cnr.isti.labse.glimpse.rules;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.exceptions.UnknownRuleException;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;

public abstract class RulesManager {
	public RulesManager(Object knowledgeBuilder, Object knowledgeBase, Object knowledgeSession) {
	}
	abstract void insertRule(String rule, String ruleName) throws IncorrectRuleFormatException;
	abstract void deleteRule(String ruleName) throws UnknownRuleException;
	abstract void startRule(String ruleName) throws UnknownRuleException;
	abstract void stopRule(String ruleName) throws UnknownRuleException;
	abstract void restartRule(String ruleName) throws UnknownRuleException;
	public abstract Object[] loadRules(ComplexEventRuleActionType rules) throws IncorrectRuleFormatException; //it returns a vector contains the knowledge packages created from the request.
	public abstract void getLoadedRulesInfo();
	
}
