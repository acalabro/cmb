package org.Connect.Buffer;

import java.util.Vector;

import org.Connect.Event.ConnectBaseEvent;
import org.Connect.Event.MyEvent;

public class EventsBuffer {

	private Vector<ConnectBaseEvent<String>> myBuffer;
	
	public EventsBuffer()
	{
		myBuffer = new Vector<ConnectBaseEvent<String>>();
	}
	
	public void add(ConnectBaseEvent<String> evt)
	{
		myBuffer.add(evt);
		System.out.println("EVENTSBUFFER: Aggiunto al buffer, dimensione buffer = " + myBuffer.size());
	}
	
	public void remove(MyEvent evt)
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
		myBuffer.setSize(newSize);
	}
	
	public int getSize()
	{
		return myBuffer.size();
	}
	
}
