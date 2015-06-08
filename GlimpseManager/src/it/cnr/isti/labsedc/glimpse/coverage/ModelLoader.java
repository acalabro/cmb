package it.cnr.isti.labsedc.glimpse.coverage;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ModelLoader {
	
	public static Document READMODEL(String modelURI) {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
		try {
			
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			dom = docBuilder.parse(modelURI);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dom;
	}
	
	
	public static void main (String[] args) {
		
		Document dom = ModelLoader.READMODEL(args[0]);
		Element el = dom.getDocumentElement();
		NodeList nl = el.getElementsByTagName("process");
		for(int i = 0 ; i < nl.getLength();i++) {
			//get the employee element
			Element el2 = (Element)nl.item(i);
			System.out.println(el2.getAttribute("id"));
		}		
	}
}
