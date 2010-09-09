package it.cnr.isti.labse.cmb;

import it.cnr.isti.labse.cmb.consumer.ConsumerManager;
import it.cnr.isti.labse.cmb.consumer.SimpleConsumer;
import it.cnr.isti.labse.cmb.probe.TestProbe;
import it.cnr.isti.labse.cmb.settings.DebugMessages;
import it.cnr.isti.labse.cmb.settings.Manager;
import it.cnr.isti.labse.cmb.settings.SplashScreen;

import java.util.Properties;


import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class DemoLauncher {

	//start settings
	protected static String ENVIRONMENTPARAMETERSFILE = "/home/antonello/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/environmentFile";
	protected static String PROBEPARAMETERSFILE1 = 		"/home/antonello/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/probeFile1";
	protected static String PROBEPARAMETERSFILE2 = 		"/home/antonello/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/probeFile2";
	protected static String CONSUMERPARAMETERSFILE = 	"/home/antonello/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/consumerFile";
	protected static String DROOLSPARAMETERFILE = 		"/home/antonello/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/droolsFile";
	protected static String MANAGERPARAMETERFILE = 		"/home/antonello/workspace/ConnectDemo/src/it/cnr/isti/labse/cmb/settings/managerFile";
	//end settings
	
	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static void main(String[] args) {
		
		if (DemoLauncher.init())
		{
			
			TestProbe testingProbe1 = new TestProbe(Manager.Read(PROBEPARAMETERSFILE1), connFact, initConn);
			testingProbe1.start();
			
			TestProbe testingProbe2 = new TestProbe(Manager.Read(PROBEPARAMETERSFILE2), connFact, initConn);
			testingProbe2.start();
					
			ConsumerManager manager = new ConsumerManager(Manager.Read(MANAGERPARAMETERFILE), connFact, initConn);
			
			SimpleConsumer testingConsumer = new SimpleConsumer(Manager.Read(CONSUMERPARAMETERSFILE), connFact, initConn);
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
