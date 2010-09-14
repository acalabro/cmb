package it.cnr.isti.labse.cmb;

import it.cnr.isti.labse.cmb.consumer.ConsumerManager;
import it.cnr.isti.labse.cmb.consumer.SimpleConsumer;
import it.cnr.isti.labse.cmb.probe.TestProbe;
import it.cnr.isti.labse.cmb.settings.DebugMessages;
import it.cnr.isti.labse.cmb.settings.Manager;

import java.util.Properties;


import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class DemoLauncher {

	//start settings
	protected static String ENVIRONMENTPARAMETERSFILE = "/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/environmentFile";
	protected static String PROBE1PARAMETERSFILE = 		"/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/probeFile1";
	protected static String PROBE2PARAMETERSFILE = 		"/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/probeFile2";
	protected static String CONSUMER1PARAMETERSFILE = 	"/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/consumerFile1";
	protected static String CONSUMER2PARAMETERSFILE = 	"/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/consumerFile2";
	protected static String DROOLSPARAMETERFILE = 		"/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/droolsFile";
	protected static String MANAGERPARAMETERFILE = 		"/home/acalabro/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/managerFile";
	//end settings
	
	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static void main(String[] args) {
		
		if (DemoLauncher.init())
		{
			
			TestProbe testingProbe1 = new TestProbe(Manager.Read(PROBE1PARAMETERSFILE), connFact, initConn);
			testingProbe1.start();
			
			TestProbe testingProbe2 = new TestProbe(Manager.Read(PROBE2PARAMETERSFILE), connFact, initConn);
			testingProbe2.start();
					
			ConsumerManager manager = new ConsumerManager(Manager.Read(MANAGERPARAMETERFILE), connFact, initConn);
			manager.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			SimpleConsumer testingConsumer1 = new SimpleConsumer(Manager.Read(CONSUMER1PARAMETERSFILE), connFact, initConn);
			testingConsumer1.start();
	
			//SimpleConsumer testingConsumer2 = new SimpleConsumer(Manager.Read(CONSUMER1PARAMETERSFILE), connFact, initConn);
			//testingConsumer2.start();
		}
	}
	
	public static boolean init()
	{
		
		boolean successfullInit = false;
		
		try 
		{
			//SplashScreen.Show();
			Properties environmentParameters = it.cnr.isti.labse.cmb.settings.Manager.Read(ENVIRONMENTPARAMETERSFILE);
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
