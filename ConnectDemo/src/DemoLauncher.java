import java.util.Properties;

import org.Connect.Buffer.EventsBuffer;
import org.Connect.Consumer.MyConsumer;
import org.Connect.Listener.DroolsListener;
import org.Connect.Probe.MyProbe;
import org.Connect.Settings.Manager;
import org.Connect.Settings.SplashScreen;

import javax.jms.JMSException;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class DemoLauncher {

	//start settings
	protected static String ENVIRONMENTPARAMETERSFILE = "/home/antonello/workspace/ConnectDemo/src/environmentFile";
	protected static String PROBEPARAMETERSFILE1 = 		"/home/antonello/workspace/ConnectDemo/src/probeFile1";
	protected static String PROBEPARAMETERSFILE2 = 		"/home/antonello/workspace/ConnectDemo/src/probeFile2";
	protected static String CONSUMERPARAMETERSFILE = 	"/home/antonello/workspace/ConnectDemo/src/consumerFile";
	protected static String RULEPATH = 					"org/Connect/Rules/FirstRule.drl";
	protected static String DROOLSPARAMETERFILE = 		"/home/antonello/workspace/ConnectDemo/src/droolsFile";
	//end settings
	
	private static KnowledgeBase kbase;
	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;
	private static StatefulKnowledgeSession ksession;

	public static void main(String[] args) {
		
		if (DemoLauncher.init())
		{
			
			MyProbe testingProbe1 = new MyProbe(Manager.Read(PROBEPARAMETERSFILE1), connFact, initConn);
			MyProbe testingProbe2 = new MyProbe(Manager.Read(PROBEPARAMETERSFILE2), connFact, initConn);
			
			EventsBuffer buffer = new EventsBuffer();
			
			DroolsListener listener = new DroolsListener(Manager.Read(DROOLSPARAMETERFILE), connFact, initConn, buffer);
			
			MyConsumer testingConsumer = new MyConsumer(Manager.Read(CONSUMERPARAMETERSFILE), connFact, initConn);
			
			testingProbe1.start();
			testingProbe2.start();
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
			
			System.out.print("Reading knowledge base ");
			kbase = readKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			System.out.println("				[ OK ]");
			System.out.println("-----------------------------------------------------");
			System.out.println();
			successfullInit = true;

		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
			successfullInit = false;
		} catch (Exception e) {
			e.printStackTrace();
			successfullInit = false;
		}
		return successfullInit;
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
	
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		kbuilder.add(ResourceFactory.newClassPathResource(RULEPATH, DemoLauncher.class.getClassLoader()), ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}
	
}
