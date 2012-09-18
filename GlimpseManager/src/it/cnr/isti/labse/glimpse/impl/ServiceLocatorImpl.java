package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.services.ChoreosService;
import it.cnr.isti.labse.glimpse.services.ServiceLocator;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.messaging.URLEndpoint;

import org.apache.commons.net.ntp.TimeStamp;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ServiceLocatorImpl extends ServiceLocator {

	private String serviceWsdl;
	private String soapRequestFilePath;
	private ServiceRegistryImpl dataSetForCollectedInformation;
		
	public ServiceLocatorImpl(String serviceWsdl,
			String soapRequestFilePath,
			ServiceRegistryImpl dataSetForCollectedInformation) {
				
		this.serviceWsdl = serviceWsdl;
		this.soapRequestFilePath = soapRequestFilePath;
		this.dataSetForCollectedInformation = dataSetForCollectedInformation;
	}
	
	public void run() {
		
		while (this.getState() == State.RUNNABLE) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			triggeredCheck();
		}
	}
	
	protected void triggeredCheck() {
		
		analyzeEasyESBResponse(
				messageSendingAndGetResponse(createConnectionToService(),
						messageCreation(
								readSoapRequest(soapRequestFilePath)),
								serviceWsdl));
		
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
		SOAPPart soapPart;
		SOAPEnvelope soapEnvelope;
		SOAPBody soapBody;
		SOAPElement element;
		try {
			
			//begin Message creation
			mf = MessageFactory.newInstance();        
			soapMessage = mf.createMessage();
			soapPart = soapMessage.getSOAPPart();
	         
            //TODO: Here we should put the message that must be sent to easyESB
            //readSoapRequest(thePathOfTheStringWhereTheRequestToEasyESBisSaved);

	        soapEnvelope = soapPart.getEnvelope();
	        soapEnvelope.addNamespaceDeclaration("http://roseindia.net", "HelloWorldServiceHttpSoap11Endpoint");
	        soapEnvelope.addNamespaceDeclaration("xsd", "http://roseindia.net");
        
		    soapBody = soapEnvelope.getBody();
		    element = soapBody.addChildElement("ciao");
                   
	        element.addTextNode("Welcome to SunOne Web Services!");
	        soapMessage.saveChanges();

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
			 return resp;
		} catch (SOAPException e) {
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
	

}