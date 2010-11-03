package it.cnr.isti.labse.glimpse.buffer;

import it.cnr.isti.labse.glimpse.event.ConnectBaseEvent;

import java.util.ArrayList;


public class DroolsEventsBuffer <T> implements EventsBuffer<T> {

	public ArrayList<ConnectBaseEvent<T>> myBuffer = new ArrayList<ConnectBaseEvent<T>>(); 
	
	public void add(ConnectBaseEvent<T> evt)
	{
		myBuffer.add(evt);
		System.out.println(this.getClass().getSimpleName() + ": New element add to the buffer. New buffer size = " + myBuffer.size());
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
