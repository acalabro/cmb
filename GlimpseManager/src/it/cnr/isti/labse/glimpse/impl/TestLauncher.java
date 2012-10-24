package it.cnr.isti.labse.glimpse.impl;


import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

public class TestLauncher {
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		ServiceLocatorImpl esd = new ServiceLocatorImpl("http://wsf.cdyne.com/WeatherWS/weather.asmx?op=GetCityForecastByZIP",
//				"", new ServiceRegistryImpl());
//		esd.triggeredCheck();
//	}

	
	
	public static void main (String[] args) {
		  
		QName serviceName = new QName("http://ws.cdyne.com/WeatherWS/",
					"GetCityForecastByZIP ");
		URL wsdlLocation;
		Service service;
		
		try {
		
			wsdlLocation = new URL("http://wsf.cdyne.com/WeatherWS/weather.asmx?WSDL");
			ServiceFactory factory;
			factory = ServiceFactory.newInstance();
			service = factory.createService(wsdlLocation,serviceName);
			
		} catch (ServiceException e) {
			e.printStackTrace();
			}
		catch (MalformedURLException e) {
			e.printStackTrace();
			}
	}	
}
