package org.Connect.Buffer;

import java.util.ArrayList;

import org.Connect.Event.ConnectBaseEvent;

public class MyBuffer implements EventsBuffer {

	public ArrayList<ConnectBaseEvent<String>> myBuffer = new ArrayList<ConnectBaseEvent<String>>(); 
	
	public void add(ConnectBaseEvent<String> evt)
	{
		myBuffer.add(evt);
		System.out.println("EVENTSBUFFER: Aggiunto al buffer, dimensione buffer = " + myBuffer.size());
	}
	
	public void remove(ConnectBaseEvent<String> evt)
	{
		myBuffer.remove(evt);
	}
	
	public ConnectBaseEvent<String> getElementAt(int index)
	{
		return myBuffer.get(index);
	}
	
	public void removeElementAt(int index)
	{
		myBuffer.remove(index);
	}
	
	public void clear()
	{
		myBuffer.clear();
	}
	
	public void setDimension(int newSize)
	{
		//
	}
	
	public int getSize()
	{
		return myBuffer.size();
	}
}
