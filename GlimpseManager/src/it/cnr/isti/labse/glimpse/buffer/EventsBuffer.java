package it.cnr.isti.labse.glimpse.buffer;

import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;

public interface EventsBuffer <T>{
	
	public void add(GlimpseBaseEvent<T> evt);
	public void remove(GlimpseBaseEvent<T> evt);
	public GlimpseBaseEvent<T> getElementAt(int index);
	public void removeElementAt(int index);
	public void clear();
	public void setDimension(int newSize);
	public int getSize();	
}
