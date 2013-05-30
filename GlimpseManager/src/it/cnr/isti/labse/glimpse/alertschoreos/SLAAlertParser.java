package it.cnr.isti.labse.glimpse.alertschoreos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SLAAlertParser extends AbstractParser{

	String inputAlert;
	String processedAlert;
	
	String serviceName;
	
	private static final String NOTE = "note";
	private static final String NOTE_TAG_PATTERN = NOTE+">.*</[^>]*"+NOTE+">";
	private static final String SERVICENAME="serviceName";
	private static final String SERVICENAME_TAG_PATTERN = SERVICENAME+">.*</[^>]*"+SERVICENAME+">";
	
	public SLAAlertParser (){
		this("");
	}

	public SLAAlertParser (String alert){
		this.setAlert(alert);
		this.serviceName = null;
	}
	
	public void process (){		
		this.processedAlert = this.convertSpecialChars(this.inputAlert);
		String note = this.extractNote();	
		if (note != null)
			this.extractServiceName(note);
	}
	
	private void extractServiceName(String note) {
		 Pattern p = Pattern.compile(SERVICENAME_TAG_PATTERN);
		 Matcher m = p.matcher(note);
		 if (m.find()){
			 String tmpServiceName = m.group(); 
			 tmpServiceName = tmpServiceName.replaceAll(SERVICENAME, "");
			 tmpServiceName = tmpServiceName.replaceAll(">", "").replaceAll("<.*", "");
			 this.serviceName = tmpServiceName;
		 }
	}

	private String extractNote() {
		 Pattern p = Pattern.compile(NOTE_TAG_PATTERN);
		 Matcher m = p.matcher(this.processedAlert);
		 if (m.find()){
			 return m.group();
		 }
		return null;
	}
	
	public void setAlert(String alert){
		this.inputAlert = alert;
	}
	
	public String getProcessedAlert(){
		return this.processedAlert;
	}

	public String getProcessedServiceName(){
		return this.serviceName;
	}

	public static void main (String args[]){
		String input = "<wscap:alert xmlns:admin=\"http://com.petalslink.easyesb/admin/model/datatype/1.0\" xmlns:bsoap11=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:cad=\"http://com.petalslink.common.alerting.definition/1.0\" xmlns:cadsoap=\"http://com.petalslink.common.alerting.definition.protocol/soap-notification/1.0\" xmlns:capnote=\"http://com.petalslink.common.alerting.protocol.note/1.0\" xmlns:exch=\"http://com.petalslink.easyesb/exchange/1.0\" xmlns:ns11=\"http://docs.oasis-open.org/wsdm/muws2-2.xsd\" xmlns:ns12=\"http://docs.oasis-open.org/wsdm/muws1-2.xsd\" xmlns:plnk=\"http://docs.oasis-open.org/wsbpel/2.0/plnktype\" xmlns:qml=\"http://upmc.fr/ns/ws-qml\" xmlns:rawreport=\"http://com.petalslink/esrawreport/1.0\" xmlns:soa=\"http://com.petalslink.easyesb/soa/model/datatype/1.0\" xmlns:vprop=\"http://docs.oasis-open.org/wsbpel/2.0/varprop\" xmlns:wsaddr=\"http://www.w3.org/2005/08/addressing\" xmlns:wsagr=\"http://schemas.ggf.org/graap/2007/03/ws-agreement\" xmlns:wscap=\"urn:oasis:names:tc:emergency:cap:1.2\" xmlns:wsdl11=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsdm=\"http://docs.oasis-open.org/wsdm/mows-2.xsd\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><wscap:identifier>ad23d18d-a6b0-4950-a868-61a293577e81</wscap:identifier>  <wscap:sent>2012-11-13T13:22:53.250+01:00</wscap:sent><wscap:msgType>Alert</wscap:msgType><wscap:note><?xml version=\"1.0\" encoding=\"UTF-8\"?><capnote:alertNoteDefinition xmlns:admin=\"http://com.petalslink.easyesb/admin/model/datatype/1.0\" xmlns:bsoap11=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:cad=\"http://com.petalslink.common.alerting.definition/1.0\" xmlns:cadsoap=\"http://com.petalslink.common.alerting.definition.protocol/soap-notification/1.0\" xmlns:capnote=\"http://com.petalslink.common.alerting.protocol.note/1.0\" xmlns:exch=\"http://com.petalslink.easyesb/exchange/1.0\" xmlns:ns11=\"http://docs.oasis-open.org/wsdm/muws2-2.xsd\" xmlns:ns12=\"http://docs.oasis-open.org/wsdm/muws1-2.xsd\" xmlns:plnk=\"http://docs.oasis-open.org/wsbpel/2.0/plnktype\" xmlns:qml=\"http://upmc.fr/ns/ws-qml\" xmlns:rawreport=\"http://com.petalslink/esrawreport/1.0\" xmlns:soa=\"http://com.petalslink.easyesb/soa/model/datatype/1.0\" xmlns:vprop=\"http://docs.oasis-open.org/wsbpel/2.0/varprop\" xmlns:wsaddr=\"http://www.w3.org/2005/08/addressing\" xmlns:wsagr=\"http://schemas.ggf.org/graap/2007/03/ws-agreement\" xmlns:wscap=\"urn:oasis:names:tc:emergency:cap:1.2\" xmlns:wsdl11=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsdm=\"http://docs.oasis-open.org/wsdm/mows-2.xsd\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><capnote:message>latency violation: current latency(3104) is not &amp;lt; to 2000 (Confirmed)</capnote:message><capnote:responderIdentifier><capnote:namespaceURI>http://securitycompany.services.scn2.wp6.thalesgroup.choreos.ow2.org/</capnote:namespaceURI><capnote:serviceName>SecurityCompanyService</capnote:serviceName><capnote:endpointName>SecurityCompanyPort</capnote:endpointName></capnote:responderIdentifier><capnote:initiatorIdentifier><capnote:namespaceURI>http://securitycompany.services.scn2.wp6.thalesgroup.choreos.ow2.org/</capnote:namespaceURI><capnote:endpointName>SecurityCompanyPortClientProxyEndpoint</capnote:endpointName></capnote:initiatorIdentifier></capnote:alertNoteDefinition></wscap:note><wscap:info><wscap:category>Infra</wscap:category><wscap:event>latency violation: current latency(3104) is not < to 2000 (Confirmed)</wscap:event><wscap:urgency>Unknown</wscap:urgency><wscap:severity>Severe</wscap:severity><wscap:certainty>Observed</wscap:certainty></wscap:info></wscap:alert>";
		SLAAlertParser parser = new SLAAlertParser(input);
		parser.process();
		System.out.println("Alert: " + parser.processedAlert);
		System.out.println("ServiceName: " + parser.serviceName);
	}
	
}
