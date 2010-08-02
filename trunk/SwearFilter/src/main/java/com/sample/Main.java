package com.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.*;
import javax.naming.NamingException;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Chat localChat;
		try {
			localChat = new Chat("TopicCF","topic1", "userName");
			
		BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("\n> Ready - write a message and press Return to evaluate <");

		while(true)
			localChat.WriteMessage(commandLine.readLine());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NamingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} 
		catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
