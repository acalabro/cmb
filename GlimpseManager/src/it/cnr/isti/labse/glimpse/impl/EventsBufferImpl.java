package it.cnr.isti.labse.glimpse.impl;

import it.cnr.isti.labse.glimpse.buffer.EventsBuffer;
import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;

import java.util.ArrayList;


public class EventsBufferImpl <T> implements EventsBuffer<T> {

	/**
	 * @uml.property  name="myBuffer"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="it.cnr.isti.labse.glimpse.event.ConnectBaseEvent"
	 */
	public ArrayList<GlimpseBaseEvent<T>> myBuffer = new ArrayList<GlimpseBaseEvent<T>>(); 
	
	public void add(GlimpseBaseEvent<T> evt)
	{
		myBuffer.add(evt);
		System.out.println(this.getClass().getSimpleName() + ": New element add to the buffer. New buffer size = " + myBuffer.size());
	}
	
	public void remove(GlimpseBaseEvent<T> evt)
	{
		myBuffer.remove(evt);
	}
	
	public GlimpseBaseEvent<T> getElementAt(int index)
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
