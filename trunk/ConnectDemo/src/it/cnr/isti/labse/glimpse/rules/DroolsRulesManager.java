package it.cnr.isti.labse.glimpse.rules;

import java.util.Collection;
import java.util.Iterator;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;
import org.w3c.dom.DOMException;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.exceptions.UnknownRuleException;
import it.cnr.isti.labse.glimpse.rules.RulesManager;
import it.cnr.isti.labse.glimpse.settings.DebugMessages;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;

public class DroolsRulesManager extends RulesManager {

	private KnowledgeBuilder kbuilder;
	private KnowledgeBase kbase;
	private KnowledgeBuilder newKnowledgeBuilder;
	
	public DroolsRulesManager(Object knowledgeBuilder, Object knowledgeBase) {
		super(knowledgeBuilder, knowledgeBase);
		kbuilder = (KnowledgeBuilder) knowledgeBuilder;
		kbase = (KnowledgeBase) knowledgeBase;
	}

	@Override
	void insertRule(final String rule, final String ruleName) throws IncorrectRuleFormatException {
		newKnowledgeBuilder.add(ResourceFactory.newByteArrayResource(rule.trim().getBytes()), ResourceType.DRL);
		if (newKnowledgeBuilder.getErrors().size() > 0)
				throw new IncorrectRuleFormatException();
	}

	@Override
	void deleteRule(final String ruleName) throws UnknownRuleException {
		//TODO REMOVE RULE
		/*final Collection<KnowledgePackage> pkg = kbase.getKnowledgePackages();
		final Object[] pkgArray = pkg.toArray();
		final KnowledgePackage pkgPd = (org.drools.definition.KnowledgePackage)pkgArray[0];
		kbase.removeRule(pkgPd.getName(), ruleName);*/
		DebugMessages.line();
		DebugMessages.print(this.getClass().getSimpleName(), "Rule " + ruleName + " successful removed.");
		DebugMessages.line();
	}

	@Override
	void startRule(final String ruleName) throws UnknownRuleException {
		
	}

	@Override
	void stopRule(final String ruleName) throws UnknownRuleException {
		
	}

	@Override
	void restartRule(final String ruleName) throws UnknownRuleException {
		
	}
	
	@Override
	public Object[] loadRules(final ComplexEventRuleActionType rules) throws IncorrectRuleFormatException {
		
		newKnowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		final ComplexEventRuleType[] insertRules = rules.getInsertArray();
		for(int i = 0; i < insertRules.length; i++)
		{
			try {
				insertRule(insertRules[i].getRuleBody(),insertRules[i].getRuleName());
			} catch (final DOMException e) {
				e.printStackTrace();
			}
		}
		
		final ComplexEventRuleType[] deleteRules = rules.getDeleteArray();
		for(int i = 0; i < deleteRules.length; i++)
		{
			try {
				deleteRule(deleteRules[i].getRuleName());
			} catch (final DOMException e) {
				e.printStackTrace();
			} catch (final UnknownRuleException e) {
				e.printStackTrace();
			}
		}		
		
		final ComplexEventRuleType[] startRules = rules.getStartArray();
		for(int i = 0; i < startRules.length; i++)
		{
			try {
				startRule(startRules[i].getRuleName());
			} catch (final DOMException e) {
				e.printStackTrace();
			} catch (final UnknownRuleException e) {
				e.printStackTrace();
			}
		}	
		
		final ComplexEventRuleType[] stopRules = rules.getStopArray();
		for(int i = 0; i < stopRules.length; i++)
		{
			try {
				stopRule(stopRules[i].getRuleName());
			} catch (final DOMException e) {
				e.printStackTrace();
			} catch (final UnknownRuleException e) {
				e.printStackTrace();
			}
		}	
		
		final ComplexEventRuleType[] restartRules = rules.getRestartArray();
		for(int i = 0; i < restartRules.length; i++)
		{
			try {
				restartRule(restartRules[i].getRuleName());
			} catch (final DOMException e) {
				e.printStackTrace();
			} catch (final UnknownRuleException e) {
				e.printStackTrace();
			}
		}
		kbase.addKnowledgePackages(newKnowledgeBuilder.getKnowledgePackages());
		return newKnowledgeBuilder.getKnowledgePackages().toArray();
	}
	
	public void getLoadedRulesInfo()
	{
		DebugMessages.line();
		Collection<KnowledgePackage> pkg = kbuilder.getKnowledgePackages();
		Object[] pkgArray = pkg.toArray();
		KnowledgePackage pkgPd = (org.drools.definition.KnowledgePackage)pkgArray[0];
		Collection<Rule> loadedRules = pkgPd.getRules();
		Object[] rlsArray = loadedRules.toArray();
		Rule rl;
		for(int i = 0; i<rlsArray.length; i++) {
			rl = (Rule) rlsArray[i];
			System.out.println("Package: " + pkgPd.getName() + " - RuleName: " + rl.getName());
		}
		DebugMessages.line();
	}
}
