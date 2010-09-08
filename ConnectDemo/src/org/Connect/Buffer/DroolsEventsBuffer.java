package org.Connect.Buffer;

import java.util.ArrayList;

import org.Connect.Event.ConnectBaseEvent;

public class DroolsEventsBuffer <T> implements EventsBuffer<T> {

	public ArrayList<ConnectBaseEvent<T>> myBuffer = new ArrayList<ConnectBaseEvent<T>>(); 
	
	public void add(ConnectBaseEvent<T> evt)
	{
		myBuffer.add(evt);
		System.out.println("EVENTSBUFFER: Aggiunto al buffer, dimensione buffer = " + myBuffer.size());
	}
	
	public void remove(ConnectBaseEvent<T> evt)
	{
		myBuffer.remove(evt);
	}
	
	public ConnectBaseEvent<T> getElementAt(int index)
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
