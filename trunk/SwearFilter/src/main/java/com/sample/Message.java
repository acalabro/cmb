package com.sample;

public class Message {

	private static String textMessage;

	public Message(String text)
	{
		this.textMessage = text;
	}
	public Message()
	{
		this.textMessage = "NOT SET";
	}
	
	public static String getMessage() {
		return textMessage;
	}

	public void setMessage(String msg) {
		this.textMessage = msg;
	}
	
}