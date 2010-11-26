package it.cnr.isti.labse.glimpse;

import it.cnr.isti.labse.glimpse.buffer.DroolsEventsBuffer;
import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.manager.ConnectEnablersManager;
import it.cnr.isti.labse.glimpse.enabler.SimpleConnectEnabler;
import it.cnr.isti.labse.glimpse.event.SimpleEvent;
import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.cep.DroolsComplexEventProcessor;
import it.cnr.isti.labse.glimpse.probe.DummyConnector;
import it.cnr.isti.labse.glimpse.settings.DebugMessages;
import it.cnr.isti.labse.glimpse.settings.Manager;

import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DemoLauncher {

	private static Properties systemProps = new Properties();

	// start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String CONNECTOR1PARAMETERSFILE;
	protected static String CONNECTOR2PARAMETERSFILE;
	protected static String ENABLER1PARAMETERSFILE;
	protected static String ENABLER2PARAMETERSFILE;
	protected static String DROOLSPARAMETERFILE;
	protected static String MANAGERPARAMETERFILE;
	// end settings

	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static boolean initProps() {
		try {
			systemProps = Manager
					.Read(System.getProperty("user.dir")
							+ "/src/it/cnr/isti/labse/glimpse/settings/configFiles/systemSettings");

			ENVIRONMENTPARAMETERSFILE = systemProps
					.getProperty("ENVIRONMENTPARAMETERSFILE");
			CONNECTOR1PARAMETERSFILE = systemProps
					.getProperty("CONNECTOR1PARAMETERSFILE");
			CONNECTOR2PARAMETERSFILE = systemProps
					.getProperty("CONNECTOR2PARAMETERSFILE");
			ENABLER1PARAMETERSFILE = systemProps
					.getProperty("ENABLER1PARAMETERSFILE");
			ENABLER2PARAMETERSFILE = systemProps
					.getProperty("ENABLER2PARAMETERSFILE");
			DROOLSPARAMETERFILE = systemProps
					.getProperty("DROOLSPARAMETERFILE");
			MANAGERPARAMETERFILE = systemProps
					.getProperty("MANAGERPARAMETERFILE");
			return true;
		} catch (Exception asd) {
			return false;
		}
	}

	public static void main(String[] args) {

		if (DemoLauncher.initProps() && DemoLauncher.init()) {

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

			ConnectEnablersManager manager = new ConnectEnablersManager(
					Manager.Read(MANAGERPARAMETERFILE), connFact, initConn,
					engine.getRuleManager());
			manager.start();
/*
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			DummyConnector testingProbe1 = new DummyConnector(
					Manager.Read(CONNECTOR1PARAMETERSFILE), connFact, initConn);
			testingProbe1.start();
			
			DummyConnector testingProbe2 = new DummyConnector(
					Manager.Read(CONNECTOR2PARAMETERSFILE), connFact, initConn);
			testingProbe2.start();
			/*
			
			SimpleConnectEnabler testingConsumer1 = new SimpleConnectEnabler(
					Manager.Read(ENABLER1PARAMETERSFILE), connFact, initConn);
			testingConsumer1.start();
			
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			SimpleConnectEnabler testingConsumer2 = new SimpleConnectEnabler(
					Manager.Read(ENABLER2PARAMETERSFILE), connFact, initConn);
			testingConsumer2.start();*/
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
			
			DebugMessages.print(DemoLauncher.class.getSimpleName(),"Setting up TopicConnectionFactory");
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
