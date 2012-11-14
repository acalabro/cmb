package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.rules.RulesManager;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;


import org.apache.commons.net.ntp.TimeStamp;

public class RuleTemplateManager {

	public static String localDroolsRequestTemplatesFilePath;
	public static RuleTemplateManager instance = null;
	private String finalString;
	private int startReplace;
	
	public RuleTemplateManager(String droolsRequestTemplatesFilePath) {
		
		RuleTemplateManager.localDroolsRequestTemplatesFilePath = droolsRequestTemplatesFilePath;
	}
	
	public static synchronized RuleTemplateManager getSingleton() {
        if (instance == null) 
            instance = new RuleTemplateManager(localDroolsRequestTemplatesFilePath);
        return instance;
    }

	public String setBody(String machineIP, String serviceName, RuleTemplateEnum templateType ) {
		String ruleSelected;
		switch(templateType) {
	      case EVENTAAFTEREVENTB: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePath);
	    	  ruleSelected.replaceAll("$$MACHINEIP$$", machineIP);
	    	  ruleSelected.replaceAll("$$SERVICENAME$$", serviceName);
	      }
	        break;
	     
	      case EVENTABETWEENEVENTB: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePath);
	    	  ruleSelected.replaceAll("$$MACHINEIP$$", machineIP);
	    	  ruleSelected.replaceAll("$$SERVICENAME$$", serviceName);
	      }
	       break;
	       
	      case INFRASTRUCTUREVIOLATION: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePath);
		      startReplace = ruleSelected.indexOf("MACHINE_IP");
		      finalString = ruleSelected.substring(0, startReplace) +
		    		  machineIP +
		    		  ruleSelected.substring(startReplace+10,ruleSelected.length());
		      startReplace = finalString.indexOf("SERVICE_NAME");
		      ruleSelected = finalString.substring(0, startReplace) +
		    		  serviceName 
		    		  + finalString.substring(startReplace+12,finalString.length());
	    	  }
	    	 break;

	      default:
	    	  ruleSelected = "";
	    	  break;
	    }
		return ruleSelected;
	}
	
	
	public ComplexEventRuleActionListDocument generateNewRuleToInjectInKnowledgeBase(
			String machineIP, String serviceName, RuleTemplateEnum ruleTemplateType) {
		
		ComplexEventRuleActionListDocument ruleDoc;			
		ruleDoc = ComplexEventRuleActionListDocument.Factory.newInstance();
		ComplexEventRuleActionType ruleActions = ruleDoc.addNewComplexEventRuleActionList();
		ComplexEventRuleType ruleType = ruleActions.addNewInsert();
		ruleType.setRuleName(machineIP);
		ruleType.setRuleType("drools");
		
		ruleType.setRuleBody(setBody(machineIP, serviceName, ruleTemplateType));
		return ruleDoc;
	}

	public int insertRule(ComplexEventRuleActionListDocument newRuleToInsert) {
		try {
			RulesManager rulesManager = ServiceLocatorImpl.anEngine.getRuleManager();
			DebugMessages.println(TimeStamp.getCurrentTime(), ServiceLocatorImpl.class.getCanonicalName(), "Updating knowledgeBase " +
			rulesManager.getLoadedKnowledgePackageCardinality());
			rulesManager.getLoadedRulesInfo();
			rulesManager.loadRules(newRuleToInsert.getComplexEventRuleActionList());
			DebugMessages.println(TimeStamp.getCurrentTime(), ServiceLocatorImpl.class.getCanonicalName(), "KnowledgeBase updated");
		} catch (IncorrectRuleFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int unloadRule(int ruleInsertionID) {
		return 0;
	}
}
