package it.cnr.isti.labse.glimpse.osgi;

import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.event.GlimpseBaseEventImpl;
import it.cnr.isti.labse.glimpse.impl.ComplexEventProcessorImpl;
import it.cnr.isti.labse.glimpse.impl.EventsBufferImpl;
import it.cnr.isti.labse.glimpse.manager.GlimpseManager;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.utils.SplashScreen;

import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static Properties systemProps = new Properties();

	// start settings
	protected static String ENVIRONMENTPARAMETERSFILE;
	protected static String DROOLSPARAMETERFILE;
	protected static String MANAGERPARAMETERFILE;
	// end settings

	private static TopicConnectionFactory connFact;
	private static InitialContext initConn;

	/**
	 * This method reads parameters from text files
	 * Parameters are relative to environment, cep engine
	 * 
	 * @param systemSettings
	 * @return true if operations are completed correctly
	 */
	public static boolean initProps(String systemSettings) {
		try {
			systemProps = Manager
					.Read(systemSettings);

            //systemProps.load(new FileInputStream(systemSettings));
            //systemProps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(systemSettings));
            ENVIRONMENTPARAMETERSFILE = systemProps
					.getProperty("ENVIRONMENTPARAMETERSFILE");
			DROOLSPARAMETERFILE = systemProps
					.getProperty("DROOLSPARAMETERFILE");
			MANAGERPARAMETERFILE = systemProps
					.getProperty("MANAGERPARAMETERFILE");
			System.out.println(ENVIRONMENTPARAMETERSFILE);
			return true;
		} catch (Exception asd) {
			asd.printStackTrace();
		    System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");
			return false;
		}
	}

	/**
	 * Read the properties and init the connections to the enterprise service bus
	 * 
	 * @param is the systemSettings file
	 **/
	
	public static boolean init()
	{
		boolean successfullInit = false;
		
		try 
		{
			//the connection are initialized
			Properties environmentParameters = Manager.Read(ENVIRONMENTPARAMETERSFILE);
			
			initConn = new InitialContext(environmentParameters);
			DebugMessages.print(Activator.class.getSimpleName(),"Setting up TopicConnectionFactory");
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

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		try{
			//TODO:FIX THE LINE BELOW
            if (Activator.initProps("/home/acalabro/Desktop/systemSettings") && Activator.init()) {
            //if (Activator.initProps("ConfGlimpse/glimpseSettings.properties") && Activator.init()) {

				SplashScreen.Show();
				//the buffer where the events are stored to be analyzed
				EventsBuffer<GlimpseBaseEventImpl> buffer = new EventsBufferImpl<GlimpseBaseEventImpl>();
	
				//The complex event engine that will be used (in this case drools)
				ComplexEventProcessor engine = new ComplexEventProcessorImpl(
						Manager.Read(MANAGERPARAMETERFILE), buffer, connFact,
						initConn);
				engine.start();
	
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	
				//the manager of all the architecture
				GlimpseManager manager = new GlimpseManager(
						Manager.Read(MANAGERPARAMETERFILE), connFact, initConn,
						engine.getRuleManager());
				manager.start();
			}
		} catch (Exception e) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
