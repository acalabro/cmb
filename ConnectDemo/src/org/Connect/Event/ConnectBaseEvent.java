package org.Connect.Event;

public interface ConnectBaseEvent <T> {
	public T getData();
	public void setData(T t);
	public Long getTimestamp();
	public void setTimestamp(Long ts);
}
