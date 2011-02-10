package it.cnr.isti.labse.connectProbe;

import it.cnr.isti.labse.connectProbe.core.DummyConnectorProbe;
import it.cnr.isti.labse.connectProbe.utils.DebugMessages;
import it.cnr.isti.labse.connectProbe.utils.Manager;

import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MainProbe {

	private static Properties systemProps = new Properties();

	// start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String PROBE1PARAMETERSFILE;
	// end settings

	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static boolean initProps(String systemPropertiesPath) {
		try {
			systemProps = Manager
					.Read(systemPropertiesPath);

			ENVIRONMENTPARAMETERSFILE = systemProps
					.getProperty("ENVIRONMENTPARAMETERSFILE");
			PROBE1PARAMETERSFILE = systemProps
					.getProperty("PROBE1PARAMETERSFILE");
			return true;
		} catch (Exception asd) {
			return false;
		}
	}

	public static void main(String[] args) {

		try {
			if (MainProbe.initProps(args[0]) && MainProbe.init()) {
				
				DummyConnectorProbe testingProbe1 = new DummyConnectorProbe(
						Manager.Read(PROBE1PARAMETERSFILE), connFact, initConn);
				testingProbe1.start();
			}
		}
		catch(Exception e)
		{
			System.out.println("USAGE: java -jar MainProbe.jar \"systemPropertiesFile\"");
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
			
			DebugMessages.print(MainProbe.class.getSimpleName(),"Setting up TopicConnectionFactory");
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

