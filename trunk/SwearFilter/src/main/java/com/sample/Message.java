package com.sample;

import java.util.Calendar;

public class Message {

	private static String textMessage;
	private static long timeStamp;

	public Message(String text, long tms)
	{
		Message.textMessage = text;
		Message.timeStamp = tms;
	}
	
	public Message()
	{
	}
	
	public static String getMessage() {
		return textMessage;
	}
	
	public static long getTimeStamp()
	{
		return timeStamp;
	}
	
	public static long getTimeNow()
	{
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public void setMessage(String msg) {
		this.textMessage = msg;
	}
	
	public void setTimeStamp(long tms)
	{
		this.timeStamp = tms;
	}
}