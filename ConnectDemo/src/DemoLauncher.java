import java.util.Properties;

import org.Connect.Consumer.ConsumerManager;
import org.Connect.Consumer.MyConsumer;
import org.Connect.Probe.MyProbe;
import org.Connect.Settings.Manager;
import org.Connect.Settings.SplashScreen;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class DemoLauncher {

	//start settings
	protected static String ENVIRONMENTPARAMETERSFILE = "/home/antonello/workspace/ConnectDemo/src/environmentFile";
	protected static String PROBEPARAMETERSFILE1 = 		"/home/antonello/workspace/ConnectDemo/src/probeFile1";
	protected static String PROBEPARAMETERSFILE2 = 		"/home/antonello/workspace/ConnectDemo/src/probeFile2";
	protected static String CONSUMERPARAMETERSFILE = 	"/home/antonello/workspace/ConnectDemo/src/consumerFile";
	protected static String DROOLSPARAMETERFILE = 		"/home/antonello/workspace/ConnectDemo/src/droolsFile";
	protected static String MANAGERPARAMETERFILE = 		"/home/antonello/workspace/ConnectDemo/src/managerFile";
	//end settings
	
	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	public static void main(String[] args) {
		
		if (DemoLauncher.init())
		{
			
			MyProbe testingProbe1 = new MyProbe(Manager.Read(PROBEPARAMETERSFILE1), connFact, initConn);
			testingProbe1.start();
			
			MyProbe testingProbe2 = new MyProbe(Manager.Read(PROBEPARAMETERSFILE2), connFact, initConn);
			testingProbe2.start();
					
			ConsumerManager manager = new ConsumerManager(Manager.Read(MANAGERPARAMETERFILE), connFact, initConn);
			
			MyConsumer testingConsumer = new MyConsumer(Manager.Read(CONSUMERPARAMETERSFILE), connFact, initConn);
			
		}
	}
	
	public static boolean init()
	{
		
		boolean successfullInit = false;
		
		try 
		{
			SplashScreen.Show();
			Properties environmentParameters = org.Connect.Settings.Manager.Read(ENVIRONMENTPARAMETERSFILE);
			initConn = new InitialContext(environmentParameters);
			
			System.out.print("Setting up TopicConnectionFactory ");
			connFact = (TopicConnectionFactory)initConn.lookup("TopicCF");
			System.out.println("		[ OK ]");
			
			System.out.println("------------------------------------------------------");
			System.out.println();
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
