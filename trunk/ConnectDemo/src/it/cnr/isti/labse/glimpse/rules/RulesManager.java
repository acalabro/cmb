package it.cnr.isti.labse.glimpse.rules;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.exceptions.UnknownRuleException;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;

public abstract class RulesManager {
	public RulesManager(Object knowledgeBuilder, Object knowledgeBase) {
	}
	abstract void insertRule(String rule, String ruleName) throws IncorrectRuleFormatException;
	abstract void deleteRule(String ruleName) throws UnknownRuleException;
	abstract void startRule(String ruleName) throws UnknownRuleException;
	abstract void stopRule(String ruleName) throws UnknownRuleException;
	abstract void restartRule(String ruleName) throws UnknownRuleException;
	public abstract void loadRules(ComplexEventRuleActionType rules);
	public abstract void getLoadedRulesInfo();
}
