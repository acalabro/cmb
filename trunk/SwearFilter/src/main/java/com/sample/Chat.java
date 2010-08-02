package com.sample;

import javax.jms.*;
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

public class Chat implements MessageListener {

	private String chatTopic;
	private TopicConnection conn;
	private TopicSession publishSession;
	private TopicSession subscribeSession;
	private InitialContext initConn;
	private TopicPublisher tPubb;
	private StatefulKnowledgeSession ksession;
	private KnowledgeBase kbase;
	
	public Chat(String topicFactory, String topicName, String chatUserName) throws NamingException, JMSException
	{
		this.chatTopic = topicName;
		initConn = new InitialContext();

		System.out.print("Setting up TopicConnectionFactory ");
		
		TopicConnectionFactory connFact = (TopicConnectionFactory)initConn.lookup(topicFactory);
		try {
			conn = connFact.createTopicConnection();
			System.out.println("	[ OK ]");

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			System.out.print("Reading knowledge base ");
			// load up the knowledge base
			kbase = readKnowledgeBase();
			ksession = kbase.newStatefulKnowledgeSession();
			System.out.println("			[ OK ]");
			//KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
		} 
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
		
		try {
			System.out.print("Creating public session object ");
			publishSession = conn.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			System.out.println("         [ OK ]");
			
			System.out.print("Creating subscribe session object ");
			subscribeSession = conn.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			System.out.println("      [ OK ]");

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Topic chatT = (Topic)initConn.lookup(chatTopic);
		
		tPubb = publishSession.createPublisher(chatT);
		TopicSubscriber tSub = subscribeSession.createSubscriber(chatT, null, true);
		
		tSub.setMessageListener(this);
		
		conn.start();
	}
	
	
	public void onMessage(Message arg0) {
		TextMessage msg = (TextMessage) arg0; 
		ConnectBaseEvent<String>  evt = new MyEvent();
		try {
			evt.setTimestamp(msg.getJMSTimestamp());
			evt.setData(msg.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ksession.insert(evt);
		ksession.fireAllRules();
	}
	
	protected void WriteMessage(String msg)
	{
		try {
			TextMessage sendMessage = publishSession.createTextMessage();
			sendMessage.setText(msg);
			tPubb.publish(sendMessage);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("Sample.drl"), ResourceType.DRL);
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
