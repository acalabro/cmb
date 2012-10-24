package it.cnr.isti.labse.glimpse.services;

import java.net.InetAddress;

import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPMessage;

/**
 * @author Antonello Calabr&ograve;
 *
 */
public abstract class ServiceLocator extends Thread {

	protected abstract SOAPConnection createConnectionToService();
	protected abstract String readSoapRequest(String SoapRequestXMLFilePath);
	protected abstract SOAPMessage messageCreation(String soapMessageStringFile);
	protected abstract SOAPMessage messageSendingAndGetResponse(SOAPConnection soapConnection, SOAPMessage soapMessage, String serviceWsdl);
	
	public abstract InetAddress getMachineIPLocally(String serviceName, String serviceType, String serviceRole); //check if the mapping between service and machine has been already done
	public abstract InetAddress getMachineIPQueryingDSB(String serviceName, String serviceType, String serviceRole); //if local check fails, this method will starts a procedure to query the dsb to get machine name
	
	public abstract ComplexEventRuleActionListDocument generateNewRuleToInjectInKnowledgeBase(InetAddress machineName); //after getting service name and machine name, a new rule for monitoring complex event should be created
	public abstract int insertRule(ComplexEventRuleActionListDocument newRuleToInsert); //this method call the RulesManager.loadRules method in order to insert a new rule into the knowledge base. An identifier of the inserted rule must be returned.
	public abstract int unloadRule(int ruleInsertionID); //it unload the rule specified by the insertionID
}
