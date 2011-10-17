 /*
  * GLIMPSE: A generic and flexible monitoring infrastructure.
  * For further information: http://labsewiki.isti.cnr.it/labse/tools/glimpse/public/main
  * 
  * Copyright (C) 2011  Software Engineering Laboratory - ISTI CNR - Pisa - Italy
  * 
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  * 
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  * 
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
  * 
*/
package it.cnr.isti.labse.glimpse;

import it.cnr.isti.labse.glimpse.impl.EventsBufferImpl;
import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.manager.GlimpseManager;
import it.cnr.isti.labse.glimpse.cep.ComplexEventProcessor;
import it.cnr.isti.labse.glimpse.impl.ComplexEventProcessorImpl;
import it.cnr.isti.labse.glimpse.utils.DebugMessages;
import it.cnr.isti.labse.glimpse.utils.Manager;
import it.cnr.isti.labse.glimpse.utils.SplashScreen;

import java.util.Properties;

import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Antonello Calabr&ograve;
 * 
 * This class is the launcher of the glimpse infrastructure
 * It setup the environment. It is possible to configure
 * different engine for complex event recognition.
 * 
 * It also provide the setup of all the connection to the ESB
 */

public class MainMonitoring {

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

			ENVIRONMENTPARAMETERSFILE = systemProps
					.getProperty("ENVIRONMENTPARAMETERSFILE");
			DROOLSPARAMETERFILE = systemProps
					.getProperty("DROOLSPARAMETERFILE");
			MANAGERPARAMETERFILE = systemProps
					.getProperty("MANAGERPARAMETERFILE");
			return true;
		} catch (Exception asd) {
			System.out.println("USAGE: java -jar MainMonitoring.jar \"systemSettings\"");
			return false;
		}
	}

	/**
	 * Read the properties and init the connections to the enterprise service bus
	 * 
	 * @param is the systemSettings file
	 */
	public static void main(String[] args) {
		try{
			if (MainMonitoring.initProps(args[0]) && MainMonitoring.init()) {
	
				SplashScreen.Show();
				//the buffer where the events are stored to be analyzed
				EventsBuffer<GlimpseBaseEvent<?>> buffer = new EventsBufferImpl<GlimpseBaseEvent<?>>();
	
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
	
	public static boolean init()
	{
		boolean successfullInit = false;
		
		try 
		{
			//the connection are initialized
			Properties environmentParameters = Manager.Read(ENVIRONMENTPARAMETERSFILE);
			initConn = new InitialContext(environmentParameters);
			
			DebugMessages.print(MainMonitoring.class.getSimpleName(),"Setting up TopicConnectionFactory");
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
