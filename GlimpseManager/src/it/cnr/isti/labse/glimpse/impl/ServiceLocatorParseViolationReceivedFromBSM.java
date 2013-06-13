package it.cnr.isti.labse.glimpse.impl;

import java.util.Properties;

import it.cnr.isti.labse.glimpse.alertschoreos.SLAAlertParser;
import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.services.HashMapManager;
import it.cnr.isti.labse.glimpse.services.ServiceLocator;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.net.ntp.TimeStamp;

public class ServiceLocatorParseViolationReceivedFromBSM extends ServiceLocator {
	
	public static ServiceRegistryImpl dataSetForCollectedInformation;
	public static RuleTemplateManager localRuleTemplateManager;
	public static String localRegexPatternFilePath;
	
	public static HashMapManager theHashMapManager;
	
	public Properties regexPatternProperties;

	public ServiceLocatorParseViolationReceivedFromBSM(
			ComplexEventProcessor engine,
			RuleTemplateManager ruleTemplateManager, String regexPatternFilePath) {
		
		DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Starting ServiceLocator component ");

		ServiceLocatorParseViolationReceivedFromBSM.localRuleTemplateManager = ruleTemplateManager;
		ServiceLocatorParseViolationReceivedFromBSM.localRegexPatternFilePath = regexPatternFilePath;
	
		theHashMapManager = new HashMapManager();
		regexPatternProperties = Manager.Read(regexPatternFilePath);		
		DebugMessages.ok();

	}
	
	public void run() {
		
		while (this.getState() == State.RUNNABLE) {
			try {
				Thread.sleep(90000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
public static void GetMachineIP(String senderName, String serviceType, String serviceRole, RuleTemplateEnum ruleTemplateType, String payload, Long timeStamp) {
		
		DebugMessages.println(TimeStamp.getCurrentTime(),ServiceLocatorImpl.class.getCanonicalName(), "getMachineIP method called");
		
		try{
			SLAAlertParser slaParser = new SLAAlertParser(payload);
			slaParser.process();
			
			String alertServiceName = slaParser.getProcessedServiceName();
			String machineIP = slaParser.getProcessedMachineIP();
			
			DebugMessages.println(TimeStamp.getCurrentTime(),ServiceLocatorImpl.class.getCanonicalName(), "IP retrieved: " + machineIP);

						
			//generate the new rule to monitor
			ComplexEventRuleActionListDocument newRule = localRuleTemplateManager
					.generateNewRuleToInjectInKnowledgeBase(machineIP, alertServiceName, ruleTemplateType, timeStamp);
			
			DebugMessages.println(TimeStamp.getCurrentTime(),
					ServiceLocatorImpl.class.getName(),
					newRule.getComplexEventRuleActionList().xmlText());
			
			//insert new rule into the knowledgeBase
			localRuleTemplateManager.insertRule(newRule);
		}
		catch(IndexOutOfBoundsException e) {
			DebugMessages.println(TimeStamp.getCurrentTime(),ServiceLocatorImpl.class.getName(),"Not an SLA Alert");
		}	 
	}

	@Override
	protected SOAPConnection createConnectionToService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String readSoapRequest(String SoapRequestXMLFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SOAPMessage messageCreation(String soapMessageStringFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SOAPMessage messageSendingAndGetResponse(
			SOAPConnection soapConnection, SOAPMessage soapMessage,
			String serviceWsdl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMachineIPLocally(String serviceName, String serviceType,
			String serviceRole) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMachineIPQueryingDSB(String serviceName,
			String serviceType, String serviceRole) {
		// TODO Auto-generated method stub
		return null;
	}

}
