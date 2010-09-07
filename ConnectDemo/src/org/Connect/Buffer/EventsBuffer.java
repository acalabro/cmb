package org.Connect.Buffer;

import org.Connect.Event.ConnectBaseEvent;

public interface EventsBuffer {
	
	public void add(ConnectBaseEvent<String> evt);
	public void remove(ConnectBaseEvent<String> evt);
	public ConnectBaseEvent<String> getElementAt(int index);
	public void removeElementAt(int index);
	public void clear();
	public void setDimension(int newSize);
	public int getSize();	
}
