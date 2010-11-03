package it.cnr.isti.labse.glimpse;

import it.cnr.isti.labse.glimpse.consumer.ConsumerManager;
import it.cnr.isti.labse.glimpse.consumer.SimpleConsumer;
import it.cnr.isti.labse.glimpse.probe.TestProbe;
import it.cnr.isti.labse.glimpse.settings.DebugMessages;
import it.cnr.isti.labse.glimpse.settings.Manager;

import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DemoLauncher {

	private static Properties systemProps = new Properties();
	
	//start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String PROBE1PARAMETERSFILE;
	protected static String PROBE2PARAMETERSFILE;
	protected static String CONSUMER1PARAMETERSFILE;
	protected static String CONSUMER2PARAMETERSFILE;
	protected static String DROOLSPARAMETERFILE;
	protected static String MANAGERPARAMETERFILE;
	//end settings
	
	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static boolean initProps()
	{
		try
		{			
			systemProps = Manager.Read(System.getProperty("user.dir")  + "/src/it/cnr/isti/labse/glimpse/settings/configFiles/systemSettings");
			
			ENVIRONMENTPARAMETERSFILE = systemProps.getProperty("ENVIRONMENTPARAMETERSFILE");
			PROBE1PARAMETERSFILE = systemProps.getProperty("PROBE1PARAMETERSFILE");
			PROBE2PARAMETERSFILE = systemProps.getProperty("PROBE2PARAMETERSFILE");
			CONSUMER1PARAMETERSFILE = systemProps.getProperty("CONSUMER1PARAMETERSFILE");
			CONSUMER2PARAMETERSFILE = systemProps.getProperty("CONSUMER2PARAMETERSFILE");
			DROOLSPARAMETERFILE = systemProps.getProperty("DROOLSPARAMETERFILE");
			MANAGERPARAMETERFILE = systemProps.getProperty("MANAGERPARAMETERFILE");
			return true;
		}
		catch(Exception asd)
		{
			return false;
		}
	}
	
	public static void main(String[] args) {
		
		if (DemoLauncher.initProps() && DemoLauncher.init())
		{
			
			//TestProbe testingProbe1 = new TestProbe(Manager.Read(PROBE1PARAMETERSFILE), connFact, initConn);
			//testingProbe1.start();
			
			//TestProbe testingProbe2 = new TestProbe(Manager.Read(PROBE2PARAMETERSFILE), connFact, initConn);
			//testingProbe2.start();
					
			ConsumerManager manager = new ConsumerManager(Manager.Read(MANAGERPARAMETERFILE), connFact, initConn);
			manager.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//SimpleConsumer testingConsumer1 = new SimpleConsumer(Manager.Read(CONSUMER1PARAMETERSFILE), connFact, initConn);
			//testingConsumer1.start();
	
			/*try {
				Thread.sleep(12000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			SimpleConsumer testingConsumer2 = new SimpleConsumer(Manager.Read(CONSUMER1PARAMETERSFILE), connFact, initConn);
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
