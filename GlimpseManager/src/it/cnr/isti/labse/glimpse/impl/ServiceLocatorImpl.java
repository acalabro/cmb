package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;

import it.cnr.isti.labse.glimpse.services.HashMapManager;
import it.cnr.isti.labse.glimpse.services.ServiceLocator;

import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;

import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import javax.xml.messaging.URLEndpoint;

import org.apache.commons.net.ntp.TimeStamp;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ServiceLocatorImpl extends ServiceLocator {

	public static String localSoapRequestFilePath;
	public static ServiceRegistryImpl dataSetForCollectedInformation;
	public static RuleTemplateManager localRuleTemplateManager;
	public static String localBsmWsdlUriFilePath;
	public static String localRegexPatternFilePath;
	
	public static ServiceLocatorImpl instance = null;
	public static HashMapManager theHashMapManager;
	public static ComplexEventProcessor anEngine = null;
	
	public Properties regexPatternProperties;
	
	public static synchronized ServiceLocatorImpl getSingleton() {
        if (instance == null) 
            instance = new ServiceLocatorImpl(anEngine,
            		localSoapRequestFilePath,
            		RuleTemplateManager.getSingleton(),
            		localBsmWsdlUriFilePath,
            		localRegexPatternFilePath);
        return instance;
    }
	
	public ServiceLocatorImpl(ComplexEventProcessor engine,
			String soapRequestFilePath,
			RuleTemplateManager ruleTemplateManager,
			String bsmWsdlUriFilePath,
			String regexPatternFilePath) {
		
		DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Starting ServiceLocator component ");

		ServiceLocatorImpl.instance = this;
		ServiceLocatorImpl.anEngine = engine;
		ServiceLocatorImpl.localSoapRequestFilePath = soapRequestFilePath;
		ServiceLocatorImpl.localRuleTemplateManager = ruleTemplateManager;
		ServiceLocatorImpl.localBsmWsdlUriFilePath = bsmWsdlUriFilePath;
		ServiceLocatorImpl.localRegexPatternFilePath = regexPatternFilePath;
	
		theHashMapManager = new HashMapManager();
		regexPatternProperties = Manager.Read(regexPatternFilePath);
		
		DebugMessages.ok();

	}
	
	public void run() {
		
		while (this.getState() == State.RUNNABLE) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Updating service map cache ");
			triggeredCheck();
			DebugMessages.ok();
		}
	}
	
	protected void triggeredCheck() {
		
		
		analyzeBSMResponse(
				messageSendingAndGetResponse(createConnectionToService(),
						messageCreation(
								localSoapRequestFilePath),
								localBsmWsdlUriFilePath));
		
	}
	
	protected void analyzeBSMResponse(SOAPMessage messageFromBSM) {
		
		//TODO:analyzeResponse
		try {
//			String receivedMessage = messageFromBSM.getSOAPBody().getElementsByTagName(name);
//			receivedMessage
//				Pattern regex = null;
//				Matcher regexMatcher = regex.matcher(regexPatternProperties.getProperty("serviceName"));
//				
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	protected SOAPConnection createConnectionToService() {
		
		SOAPConnectionFactory connectionFactory;
		SOAPConnection soapConnection;
		
		try {
			connectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = connectionFactory.createConnection();            
            return soapConnection;
		
        } catch (SOAPException soape) {
        	DebugMessages.println(TimeStamp.getCurrentTime(), this.getClass().getCanonicalName(), "error creating connection");
        	soape.printStackTrace();
        	return null;
        }
	}
	
	protected SOAPMessage messageCreation(String soapMessageStringFilePath) {
		
		 	SOAPMessage message = null;
			try {
				message = MessageFactory.newInstance().createMessage(); 
	        SOAPPart soapPart = message.getSOAPPart();  
	        soapPart.setContent(
	        		new StreamSource(
	        				new FileInputStream(soapMessageStringFilePath)));  
	        message.saveChanges();  
	        return message;  
			} catch (SOAPException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return message; 
	}
	
	protected SOAPMessage messageSendingAndGetResponse(SOAPConnection soapConnection, SOAPMessage soapMessage, String serviceWsdlFilePath) {
		SOAPMessage resp;
        URLEndpoint endpoint = new URLEndpoint (Manager.ReadTextFromFile(serviceWsdlFilePath));
        
        try {
			 resp = soapConnection.call(soapMessage, endpoint);
			 soapConnection.close();
			 System.out.println(resp.getSOAPBody().getTextContent());
			 return resp;
		}

        catch (SOAPException e) {
			DebugMessages.println(TimeStamp.getCurrentTime(), this.getClass().getCanonicalName(), "error during messageSendingAndGetResponse");		
			e.printStackTrace();
			return null;
		}
	}
	
	protected String readSoapRequest(String SoapRequestXMLFilePath) {
		
		File SoapRequestXMLFile = new File(SoapRequestXMLFilePath);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc;
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(SoapRequestXMLFile);
			doc.getDocumentElement().normalize();
		} catch (SAXException e) {
			DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(),"error during SoapRequestXMLFile reading - SaxException");
			return null;
		} catch (IOException e) {
			DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(),"error during SoapRequestXMLFile reading - IOException");
			return null;
		} catch (ParserConfigurationException e1) {
			DebugMessages.println(TimeStamp.getCurrentTime(),this.getClass().getCanonicalName(),"error during SoapRequestXMLFile reading - ParserConfigurationException");
			return null;
		}
		return doc.getTextContent();
	}

	//this method is the only one that can/will be called by the rule
	//it will generate a new rule that will be injected into the knowledge
	//base. This new rule will check if there are (will be) infrastructure
	//violation from the machine on which the service that call this method is running.
	public static void GetMachineIP(String serviceName, String serviceType, String serviceRole, RuleTemplateEnum ruleTemplateType) {
		
		DebugMessages.println(TimeStamp.getCurrentTime(),ServiceLocatorImpl.class.getCanonicalName(), "getMachineIP method called");
		ServiceLocatorImpl theLocator = ServiceLocatorImpl.getSingleton();
		
		InetAddress machineIP = theLocator.getMachineIPLocally(serviceName, serviceType, serviceRole);
		if (machineIP == null) {
			machineIP = theLocator.getMachineIPQueryingDSB(serviceName, serviceType, serviceRole);
		}
		
		
		//generate the new rule to monitor
		ComplexEventRuleActionListDocument newRule = localRuleTemplateManager.generateNewRuleToInjectInKnowledgeBase(machineIP, serviceName, ruleTemplateType);
		
		//insert new rule into the knowledgeBase
		localRuleTemplateManager.insertRule(newRule);
		
		//update the localTable with new information.
		theHashMapManager.insertLocalTable(newRule.getComplexEventRuleActionList().getInsertArray(0).getRuleName().hashCode(),
							serviceName,
							machineIP,
							serviceType,
							serviceRole);		 
	}
	
	@Override
	public InetAddress getMachineIPLocally(String serviceName, String serviceType, String serviceRole) {
		return theHashMapManager.getMachine(serviceName,serviceType,serviceRole);
	}
	
	@Override
	public InetAddress getMachineIPQueryingDSB(String serviceName, String serviceType, String serviceRole) {
		// TODO Auto-generated method stub
		return null;
	}
}