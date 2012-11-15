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

	public static String localDroolsRequestTemplatesFilePathOne;
	public static String localDroolsRequestTemplatesFilePathTwo;
	public static RuleTemplateManager instance = null;
	public static RulesManager rulesManager;
	private String finalString;
	private int startReplace;
	
	public RuleTemplateManager(String droolsRequestTemplatesFilePathOne, String droolsRequestTemplatesFilePathTwo) {
		
		RuleTemplateManager.localDroolsRequestTemplatesFilePathOne = droolsRequestTemplatesFilePathOne;
		RuleTemplateManager.localDroolsRequestTemplatesFilePathTwo = droolsRequestTemplatesFilePathTwo;
	}
	
	public static synchronized RuleTemplateManager getSingleton() {
        if (instance == null) 
            instance = new RuleTemplateManager(localDroolsRequestTemplatesFilePathOne,localDroolsRequestTemplatesFilePathTwo);
        return instance;
    }

	public String setBody(String machineIP, String serviceName, RuleTemplateEnum templateType, long timeStamp) {
		String ruleSelected;
		switch(templateType) {
	      case EVENTAAFTEREVENTB: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePathOne);
	    	  ruleSelected.replaceAll("$$MACHINEIP$$", machineIP);
	    	  ruleSelected.replaceAll("$$SERVICENAME$$", serviceName);
	      }
	        break;
	     
	      case EVENTABETWEENEVENTB: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePathOne);
	    	  ruleSelected.replaceAll("$$MACHINEIP$$", machineIP);
	    	  ruleSelected.replaceAll("$$SERVICENAME$$", serviceName);
	      }
	       break;
	       
	      case NOEVENTFROMINFRASTRUCTURE: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePathTwo);
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
	      
	      case INFRASTRUCTUREVIOLATION: {
	    	  ruleSelected = Manager.ReadTextFromFile(localDroolsRequestTemplatesFilePathOne);
		      startReplace = ruleSelected.indexOf("MACHINE_IP");
		      finalString = ruleSelected.substring(0, startReplace) +
		    		  machineIP +
		    		  ruleSelected.substring(startReplace+10,ruleSelected.length());
		      startReplace = finalString.indexOf("SERVICE_NAME");
		      ruleSelected = finalString.substring(0, startReplace) +
		    		  serviceName 
		    		  + finalString.substring(startReplace+12,finalString.length());
		      startReplace = ruleSelected.indexOf("_TIMESTAMP_");
		      ruleSelected = ruleSelected.substring(0,startReplace) + String.valueOf(timeStamp) +
		    		  ruleSelected.substring(startReplace+11,ruleSelected.length());
	    	  }
	    	 break;

	      default:
	    	  ruleSelected = "";
	    	  break;
	    }
		return ruleSelected;
	}
	
	
	public ComplexEventRuleActionListDocument generateNewRuleToInjectInKnowledgeBase(
			String machineIP, String serviceName, RuleTemplateEnum ruleTemplateType, Long timeStamp) {
		
		ComplexEventRuleActionListDocument ruleDoc;			
		ruleDoc = ComplexEventRuleActionListDocument.Factory.newInstance();
		ComplexEventRuleActionType ruleActions = ruleDoc.addNewComplexEventRuleActionList();
		ComplexEventRuleType ruleType = ruleActions.addNewInsert();
		ruleType.setRuleName(machineIP);
		ruleType.setRuleType("drools");
		
		ruleType.setRuleBody(setBody(machineIP, serviceName, ruleTemplateType, timeStamp));
		return ruleDoc;
	}

	public int insertRule(ComplexEventRuleActionListDocument newRuleToInsert) {
		try {
			rulesManager = ServiceLocatorImpl.anEngine.getRuleManager();
			DebugMessages.println(TimeStamp.getCurrentTime(), ServiceLocatorImpl.class.getCanonicalName(), "Updating knowledgeBase " +
			rulesManager.getLoadedKnowledgePackageCardinality());

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
