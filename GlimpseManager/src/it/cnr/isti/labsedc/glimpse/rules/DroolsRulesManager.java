 /*
  * GLIMPSE: A generic and flexible monitoring infrastructure.
  * For further information: http://labsewiki.isti.cnr.it/labse/tools/glimpse/public/main
  * 
  * Copyright (C) 2011  Software Engineering Laboratory - ISTI CNR - Pisa - Italy
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
*/
package it.cnr.isti.labsedc.glimpse.rules;

import java.util.Collection;

import org.apache.commons.net.ntp.TimeStamp;
import org.drools.KnowledgeBase;
import org.drools.RuntimeDroolsException;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.io.ResourceFactory;
import org.w3c.dom.DOMException;

import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;
import it.cnr.isti.labsedc.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labsedc.glimpse.exceptions.UnknownMethodCallRuleException;
import it.cnr.isti.labsedc.glimpse.exceptions.UnknownRuleException;
import it.cnr.isti.labsedc.glimpse.rules.RulesManager;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;

public class DroolsRulesManager extends RulesManager {

	private KnowledgeBuilder kbuilder;
	private KnowledgeBase kbase;
	
	public DroolsRulesManager(Object knowledgeBuilder, Object knowledgeBase, Object knowledgeSession) {
		super(knowledgeBuilder, knowledgeBase, knowledgeSession);
		kbuilder = (KnowledgeBuilder) knowledgeBuilder;
		kbase = (KnowledgeBase) knowledgeBase;

		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	}

	@Override
	public void insertRule(final String rule, final String ruleName) throws IncorrectRuleFormatException, UnknownMethodCallRuleException {

		/*System.out.println("Elenco knowledgePackages presenti dentro il kbase");	
			
		Collection<KnowledgePackage> ass = kbase.getKnowledgePackages();
		Object esd[] = ass.toArray();
		for (int j = 0; j<esd.length; j++) {
			KnowledgePackage kp = (KnowledgePackage)esd[j];
			System.out.println("knowledgePackage presente nel kbase prima del load regola: " + kp.getName());
			
			Collection<Rule> qwe = kp.getRules();
			Object gg[] = qwe.toArray();
			System.out.println("All'interno di questo knowledgePackage sono presenti " + gg.length + " regole");
			for (int k = 0; k<gg.length; k++){
				Rule ru = (Rule) gg[k];
				System.out.println("Rule: " + k + " " + ru.getName());
			}
		}*/
		
		try {
			System.out.println();System.out.println();
		kbuilder.add(ResourceFactory.newByteArrayResource(rule.trim().getBytes()), ResourceType.DRL);
		} catch (RuntimeDroolsException droolsExceptionOnLoading) {
			throw new UnknownMethodCallRuleException();
		}

		if (kbuilder.getErrors().size() > 0)
			 throw new IncorrectRuleFormatException(); 
	}

	@Override
	public void deleteRule(final String ruleName) throws UnknownRuleException {
		
		final Collection<KnowledgePackage> pkg = kbase.getKnowledgePackages();
		final Object[] pkgArray = pkg.toArray();
		final KnowledgePackage pkgPd = (org.drools.definition.KnowledgePackage)pkgArray[0];
		kbase.removeRule(pkgPd.getName(), ruleName);
		DebugMessages.line();
		DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Rule " + ruleName + " successfully deleted");
		DebugMessages.line();
	}

	void startRule(final String ruleName) throws UnknownRuleException {
		
	}

	void stopRule(final String ruleName) throws UnknownRuleException {
		
	}

	void restartRule(final String ruleName) throws UnknownRuleException {
		
	}
	
	public Object[] loadRules(final ComplexEventRuleActionType rules) throws IncorrectRuleFormatException {
		
		final ComplexEventRuleType[] insertRules = rules.getInsertArray();
		for(int i = 0; i < insertRules.length; i++)
		{
			try {
				insertRule(insertRules[i].getRuleBody(),insertRules[i].getRuleName());
			} catch (final DOMException e) {
				e.printStackTrace();
			} catch (UnknownMethodCallRuleException e) {
				// TODO Auto-generated catch block
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
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(), "Listing rules loaded into the knowledgeBases");
		Collection<KnowledgePackage> ass = kbase.getKnowledgePackages();
		Object esd[] = ass.toArray();
		for (int i = 0; i<esd.length; i++) {
			
			KnowledgePackage kp = (KnowledgePackage)esd[i];
			Collection<Rule> rls = kp.getRules();
			Object r[] = rls.toArray();
			DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(), "KnowledgeBase name: " + kp.getName());
			if (r.length == 0) {
				DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(), "--! No rules loaded into: " + kp.getName() + " knowledgeBase");
			}
			else
			{
				for (int j = 0; j<r.length;j++) {
					Rule gg = (Rule) r[j];
					DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(), "RuleName: " + gg.getName());	
					}
			}
			DebugMessages.line();
		}
		
		return kbase.getKnowledgePackages().toArray();
	}
	
	public int getLoadedKnowledgePackageCardinality() {
		return this.kbase.getKnowledgePackages().size();
	}
	
	public void getLoadedRulesInfo()
	{
		//Collection<KnowledgePackage> pkg = kbase.getKnowledgePackages();
		System.out.println();
		Collection<KnowledgePackage> pkg = kbuilder.getKnowledgePackages();
		Object[] pkgArray = pkg.toArray();
		for (int i = 0; i<pkgArray.length; i++) {
			KnowledgePackage pkgPd = (org.drools.definition.KnowledgePackage)pkgArray[i];
			Collection<Rule> loadedRules = pkgPd.getRules();
			Object[] rlsArray = loadedRules.toArray();
			Rule rl;
			for(int j = 0; j<rlsArray.length; j++) {
				rl = (Rule) rlsArray[j];
				DebugMessages.println(TimeStamp.getCurrentTime(), this.getClass().getCanonicalName(),
						"Package: " + pkgPd.getName() + " - RuleName: " + rl.getName());
			}
		}
	}	
}