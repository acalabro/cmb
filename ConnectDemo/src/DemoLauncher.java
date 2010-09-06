import java.util.Properties;

import org.Connect.Probe.MyProbe;
import org.Connect.Settings.Manager;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class DemoLauncher {

	//start settings
	protected static String ENVIRONMENTPARAMETERSFILE = "/home/acalabro/workspace/ConnectDemo/src/environmentFile";
	protected static String PROBEPARAMETERSFILE = 		"/home/acalabro/workspace/ConnectDemo/src/probeFile";
	//end settings
	
	private static KnowledgeBase kbase;
	private static TopicConnection connection;
	private static TopicSession publishSession;
	private static TopicSession subscribeSession;
	private static InitialContext initConn;
	private static TopicPublisher tPubb;
	private static StatefulKnowledgeSession ksession;
	
	
	
	public static void main(String[] args) {
		
		if (DemoLauncher.init())
		{
			MyProbe testingProbe = new MyProbe(Manager.Read(PROBEPARAMETERSFILE));
		}
	}
	
	public static boolean init()
	{
		
		boolean successfullInit = false;
		
		try 
		{
			Properties environmentParameters = org.Connect.Settings.Manager.Read(ENVIRONMENTPARAMETERSFILE);
			initConn = new InitialContext(environmentParameters);
			
			System.out.print("Setting up TopicConnectionFactory ");
			TopicConnectionFactory connFact = (TopicConnectionFactory)initConn.lookup("TopicCF");
			connection = connFact.createTopicConnection();
			System.out.println("	[ OK ]");
			
			System.out.print("Reading knowledge base ");
			kbase = readKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			System.out.println("			[ OK ]");
			
			System.out.print("Creating public session object ");
			publishSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			System.out.println("         [ OK ]");
			
			System.out.print("Creating subscribe session object ");
			subscribeSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			System.out.println("      [ OK ]");
			
			successfullInit = true;

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			successfullInit = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			successfullInit = false;
		}
		return successfullInit;
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
	
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		kbuilder.add(ResourceFactory.newClassPathResource("org/Connect/Rules/FirstRule.drl", DemoLauncher.class.getClassLoader()), ResourceType.DRL);
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
