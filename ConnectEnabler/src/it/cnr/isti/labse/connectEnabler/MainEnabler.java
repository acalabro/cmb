package it.cnr.isti.labse.connectEnabler;

import it.cnr.isti.labse.connectEnabler.core.GenericConnectEnabler;
import it.cnr.isti.labse.connectEnabler.utils.DebugMessages;
import it.cnr.isti.labse.connectEnabler.utils.Manager;

import java.util.List;
import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MainEnabler {

	private static Properties systemProps = new Properties();

	// start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String ENABLER1PARAMETERSFILE;
	// end settings

	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static boolean initProps(String systemPropertiesPath) {
		try {
			systemProps = Manager
					.Read(systemPropertiesPath);
			
			ENVIRONMENTPARAMETERSFILE = systemProps
					.getProperty("ENVIRONMENTPARAMETERSFILE");
			ENABLER1PARAMETERSFILE = systemProps
					.getProperty("ENABLER1PARAMETERSFILE");
			return true;
			
		} catch (Exception asd) {
			return false;
		}
	}

	public static void main(String[] args) {

		try {
			if (MainEnabler.initProps(args[0]) && MainEnabler.init()) {
				
				GenericConnectEnabler testingConsumer1 = new GenericConnectEnabler(
						Manager.Read(ENABLER1PARAMETERSFILE), connFact, initConn);
				testingConsumer1.start();
			}
		}
		catch(Exception e)
		{
			System.out.println("USAGE: java -jar MainEnabler.jar \"systemPropertiesFile\"");
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
			
			DebugMessages.print(MainEnabler.class.getSimpleName(),"Setting up TopicConnectionFactory");
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
