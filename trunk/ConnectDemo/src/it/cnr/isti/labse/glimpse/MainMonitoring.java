package it.cnr.isti.labse.glimpse;

import it.cnr.isti.labse.glimpse.buffer.DroolsEventsBuffer;
import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.manager.GlimpseManager;
import it.cnr.isti.labse.glimpse.event.SimpleEvent;
import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.cep.DroolsComplexEventProcessor;
import it.cnr.isti.labse.glimpse.settings.DebugMessages;
import it.cnr.isti.labse.glimpse.settings.Manager;

import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MainMonitoring {

	private static Properties systemProps = new Properties();

	// start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String DROOLSPARAMETERFILE;
	protected static String MANAGERPARAMETERFILE;
	// end settings

	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static boolean initProps(String systemSettings) {
		try {
			systemProps = Manager
					.Read(systemSettings);

			ENVIRONMENTPARAMETERSFILE = systemProps
					.getProperty("ENVIRONMENTPARAMETERSFILE");
			DROOLSPARAMETERFILE = systemProps
					.getProperty("DROOLSPARAMETERFILE");
			MANAGERPARAMETERFILE = systemProps
					.getProperty("MANAGERPARAMETERFILE");
			return true;
		} catch (Exception asd) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");
			return false;
		}
	}

	public static void main(String[] args) {
		try{
			if (MainMonitoring.initProps(args[0]) && MainMonitoring.init()) {
	
				EventsBuffer<SimpleEvent> buffer = new DroolsEventsBuffer<SimpleEvent>();
	
				ComplexEventProcessor engine = new DroolsComplexEventProcessor(
						Manager.Read(MANAGERPARAMETERFILE), buffer, connFact,
						initConn);
				engine.start();
	
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
				GlimpseManager manager = new GlimpseManager(
						Manager.Read(MANAGERPARAMETERFILE), connFact, initConn,
						engine.getRuleManager());
				manager.start();
			}
		} catch (Exception e) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");			
		}
	}
	
	public static boolean init()
	{
		boolean successfullInit = false;
		
		try 
		{
			//SplashScreen.Show();
			Properties environmentParameters = Manager.Read(ENVIRONMENTPARAMETERSFILE);
			initConn = new InitialContext(environmentParameters);
			
			DebugMessages.print(MainMonitoring.class.getSimpleName(),"Setting up TopicConnectionFactory");
			connFact = (TopicConnectionFactory)initConn.lookup("TopicCF");
			DebugMessages.ok();
			DebugMessages.line();
			successfullInit = true;
		} catch (NamingException e) {
			e.printStackTrace();
			successfullInit = false;
		} catch (Exception e) {
			e.printStackTrace();
			successfullInit = false;
		}
		return successfullInit;
	}
	
}
