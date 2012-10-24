package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.rules.RulesManager;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;

import java.net.InetAddress;

import org.apache.commons.net.ntp.TimeStamp;

public class RuleTemplateManager {

	public static String localDroolsRequestTemplatesFilePath;
	public static RuleTemplateManager instance = null;
	
	public RuleTemplateManager(String droolsRequestTemplatesFilePath) {
		
		RuleTemplateManager.localDroolsRequestTemplatesFilePath = droolsRequestTemplatesFilePath;
	}
	
	public static synchronized RuleTemplateManager getSingleton() {
        if (instance == null) 
            instance = new RuleTemplateManager(localDroolsRequestTemplatesFilePath);
        return instance;
    }

	public String setBody(InetAddress machineIP, RuleTemplateEnum templateType ) {
		String ruleSelected;
		switch(templateType) {
	      case EVENTAAFTEREVENTB:
	        ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePath);
	        break;
	     
	      case EVENTABETWEENEVENTB:
	       ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePath);
	       break;
	       
	      default:
	    	  ruleSelected = "";
	    	  break;
	    }
		return ruleSelected;
	}
	
	
	public ComplexEventRuleActionListDocument generateNewRuleToInjectInKnowledgeBase(
			InetAddress machineName, RuleTemplateEnum ruleTemplateType) {
		
		ComplexEventRuleActionListDocument ruleDoc;			
		ruleDoc = ComplexEventRuleActionListDocument.Factory.newInstance();
		ComplexEventRuleActionType ruleActions = ruleDoc.addNewComplexEventRuleActionList();
		ComplexEventRuleType ruleType = ruleActions.addNewInsert();
		ruleType.setRuleName(machineName.getHostAddress());
		ruleType.setRuleType("drools");
		
		ruleType.setRuleBody(setBody(machineName,ruleTemplateType));
		
		return ruleDoc;
	}

	public int insertRule(ComplexEventRuleActionListDocument newRuleToInsert) {
		try {
			RulesManager rulesManager = ServiceLocatorImpl.anEngine.getRuleManager();
			DebugMessages.println(TimeStamp.getCurrentTime(), ServiceLocatorImpl.class.getCanonicalName(), "Updating knowledgeBase");
			rulesManager.loadRules(newRuleToInsert.getComplexEventRuleActionList());
			DebugMessages.println(TimeStamp.getCurrentTime(), ServiceLocatorImpl.class.getCanonicalName(), "KnowledgeBase updated");
		} catch (IncorrectRuleFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public int unloadRule(int ruleInsertionID) {
		// TODO Auto-generated method stub
		return 0;
	}
}
