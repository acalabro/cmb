package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labse.glimpse.rules.RulesManager;
import it.cnr.isti.labse.glimpse.services.ChoreosService;
import it.cnr.isti.labse.glimpse.services.ServiceLocator;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleActionType;
import it.cnr.isti.labse.glimpse.xml.complexEventRule.ComplexEventRuleType;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.messaging.URLEndpoint;

import net.sf.saxon.trans.RuleManager;

import org.apache.commons.net.ntp.TimeStamp;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ServiceLocatorImpl extends ServiceLocator {

	public static String localSoapRequestFilePath;
	public static String localDroolsRequestTemplateFilePath;
	public static ServiceRegistryImpl dataSetForCollectedInformation;
	
	public static ServiceLocatorImpl instance = null;
	public static HashMapManager theHashMapManager;
	public static ComplexEventProcessor anEngine = null;
	
	public static synchronized ServiceLocatorImpl getSingleton() {
        if (instance == null) 
            instance = new ServiceLocatorImpl(anEngine,localSoapRequestFilePath, localDroolsRequestTemplateFilePath);
        return instance;
    }
	
	public ServiceLocatorImpl(ComplexEventProcessor engine, String soapRequestFilePath, String droolsRuleRequestTemplateFile) {
		DebugMessages.print(TimeStamp.getCurrentTime(), this.getClass().getSimpleName(), "Starting ServiceLocator component ");

		ServiceLocatorImpl.instance = this;
		ServiceLocatorImpl.anEngine = engine;
		ServiceLocatorImpl.localSoapRequestFilePath = soapRequestFilePath;
		ServiceLocatorImpl.localDroolsRequestTemplateFilePath = droolsRuleRequestTemplateFile;
	
		theHashMapManager = new HashMapManager();
		
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
		
		
//		messageSendingAndGetResponse(
//				createConnectionToService(),
//				messageCreation(""),
//				serviceWsdl);
//		analyzeEasyESBResponse(
//				messageSendingAndGetResponse(createConnectionToService(),
//						messageCreation(
//								readSoapRequest(soapRequestFilePath)),
//								serviceWsdl));
		
	}
	
	protected void analyzeEasyESBResponse(SOAPMessage messageFromEasyESB) {
		
		/*
		 * HERE WE MUST SETUP THE ANALYSIS OF THE MESSAGE 
		 * COMING FROM EASYESB
		 */
		dataSetForCollectedInformation.add(new ChoreosService(null,null));
		
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
	
	protected SOAPMessage messageCreation(String soapMessageStringFile) {
		
		MessageFactory mf;
		SOAPMessage soapMessage;
		SOAPElement request;
		
		try {
			
			//begin Message creation
			mf = MessageFactory.newInstance();
			
			soapMessage = mf.createMessage();

			request = soapMessage.getSOAPBody().addChildElement("http://wsf.cdyne.com/WeatherWS/weather.asmx?op=GetCityForecastByZIP","http://wsf.cdyne.com/WeatherWS/weather.asmx?op=GetCityForecastByZIP","http://www.w3.org/2001/XMLSchema-instance");

						
            //TODO: Here we should put the message that must be sent to easyESB
            //readSoapRequest(thePathOfTheStringWhereTheRequestToEasyESBisSaved);

	        //end Message creation
			return soapMessage;
	        
		} catch (SOAPException e) {
			e.printStackTrace();
			DebugMessages.println(TimeStamp.getCurrentTime(), this.getClass().getCanonicalName(), "error during messageCreation");
			return null;
		}
	}
	
	protected SOAPMessage messageSendingAndGetResponse(SOAPConnection soapConnection, SOAPMessage soapMessage, String serviceWsdl) {
		SOAPMessage resp;
        URLEndpoint endpoint = new URLEndpoint (serviceWsdl);
        
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
	public static void GetMachineIP(String serviceName, String serviceType, String serviceRole) {
		
		DebugMessages.println(TimeStamp.getCurrentTime(),ServiceLocatorImpl.class.getCanonicalName(), " getMachineIP method called");
		ServiceLocatorImpl theLocator = ServiceLocatorImpl.getSingleton();
		
		InetAddress machineIP = theLocator.getMachineIPLocally(serviceName, serviceType, serviceRole);
		if (machineIP == null) {
			machineIP = theLocator.getMachineIPQueryingDSB(serviceName, serviceType, serviceRole);
		}
		
		//generate the new rule to monitor
		ComplexEventRuleActionListDocument newRule = theLocator.generateNewRuleToInjectInKnowledgeBase(machineIP);
		
		
		//insert new rule into the knowledgeBase
		theLocator.insertRule(newRule);
		
		
		//update the localTable with new information.
		theHashMapManager.insertLocalTable(newRule.getComplexEventRuleActionList().getInsertArray().hashCode(),
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

	@Override
	public ComplexEventRuleActionListDocument generateNewRuleToInjectInKnowledgeBase(
			InetAddress machineName) {
		
		ComplexEventRuleActionListDocument ruleDoc;			
		ruleDoc = ComplexEventRuleActionListDocument.Factory.newInstance();
		ComplexEventRuleActionType ruleActions = ruleDoc.addNewComplexEventRuleActionList();
		ComplexEventRuleType ruleType = ruleActions.addNewInsert();
		ruleType.setRuleName(machineName.getHostAddress());
		ruleType.setRuleType("drools");
		ruleType.setRuleBody(Manager.ReadTextFromFile(localDroolsRequestTemplateFilePath));
		
		return ruleDoc;
	}

	@Override
	public int insertRule(ComplexEventRuleActionListDocument newRuleToInsert) {
		try {
			RulesManager rulesManager = ServiceLocatorImpl.anEngine.getRuleManager();
			rulesManager.loadRules(newRuleToInsert.getComplexEventRuleActionList());
			DebugMessages.println(TimeStamp.getCurrentTime(), ServiceLocatorImpl.class.getCanonicalName(), "KnowledgeBase updated");
		} catch (IncorrectRuleFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int unloadRule(int ruleInsertionID) {
		// TODO Auto-generated method stub
		return 0;
	}
}