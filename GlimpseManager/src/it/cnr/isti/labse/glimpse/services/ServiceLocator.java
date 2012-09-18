/**
 * 
 */
package it.cnr.isti.labse.glimpse.services;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPMessage;

/**
 * @author acalabro
 *
 */
public abstract class ServiceLocator extends Thread {

	protected abstract SOAPConnection createConnectionToService();
	protected abstract String readSoapRequest(String SoapRequestXMLFilePath);
	protected abstract SOAPMessage messageCreation(String soapMessageStringFile);
	protected abstract SOAPMessage messageSendingAndGetResponse(SOAPConnection soapConnection, SOAPMessage soapMessage, String serviceWsdl);


}
