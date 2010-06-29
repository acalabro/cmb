package it.cnr.isti.connect.cmb.eventprocessing;

import it.cnr.isti.connect.cmb.events.SimpleEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;

public class EventProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			EventProcessor evtProc = new EventProcessor();
			KnowledgeBase kb = evtProc.loadKnowledgeBase("rules.drl");
			
			KnowledgeSessionConfiguration conf = new SessionConfiguration();
			((SessionConfiguration) conf).setClockType(ClockType.PSEUDO_CLOCK);
			
			StatefulKnowledgeSession session = kb.newStatefulKnowledgeSession(conf, null);
			
			//ArrayList<SimpleEvent> results = new ArrayList<SimpleEvent>();
			
			//session.setGlobal("results", results);
			
			
			SimpleEvent se1 = new SimpleEvent();
			SimpleEvent se2 = new SimpleEvent("custom data");
			
			WorkingMemoryEntryPoint entryPoint = (WorkingMemoryEntryPoint) session.getWorkingMemoryEntryPoint("EventStream");
			
			entryPoint.insert(se1);
			entryPoint.insert(se2);
			
			session.fireAllRules();
			
			
			
		} catch (DroolsParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Creates a KnowledgeBase from a rule file
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws DroolsParserException
	 */
	private KnowledgeBase loadKnowledgeBase(final String fileName)
		throws IOException, DroolsParserException
	{
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		kbuilder.add(ResourceFactory.newClassPathResource(fileName, getClass()), ResourceType.DRL );
		
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		System.err.println(kbase.toString());
		return kbase;
		
	}

}
