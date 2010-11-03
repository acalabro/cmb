package it.cnr.isti.labse.glimpse.buffer;

import it.cnr.isti.labse.glimpse.event.ConnectBaseEvent;

public interface EventsBuffer <T>{
	
	public void add(ConnectBaseEvent<T> evt);
	public void remove(ConnectBaseEvent<T> evt);
	public ConnectBaseEvent<T> getElementAt(int index);
	public void removeElementAt(int index);
	public void clear();
	public void setDimension(int newSize);
	public int getSize();	
}
